package cradle.rancune.learningandroid.record.video;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.opengl.EGLContext;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.util.concurrent.TimeUnit;

/**
 * Created by Rancune@126.com 2018/7/26.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCapture implements SurfaceTexture.OnFrameAvailableListener {
    private static final String DEFAULT_DISPLAY_NAME = "rancune_display";
    private static final String CAPTURE_DISPLAY_NAME = "rancune_capture_display";

    public static final int LOCATION_LEFT_TOP = 0;
    public static final int LOCATION_LEFT_BOTTOM = 1;
    public static final int LOCATION_RIGHT_TOP = 2;
    public static final int LOCATION_RIGHT_BOTTOM = 3;
    private int mWaterMarkLocation = LOCATION_RIGHT_TOP;

    private static final long ONE_SECOND_IN_NS = TimeUnit.SECONDS.toNanos(1);

    private boolean mIsInited = false;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private int mTargetWidth;
    private int mTargetHeight;
    private int mDpi;
    private int mFps = 30;

    // oes
    private OpenGLRender mGLRender;
    private SurfaceTexture mGLSurfaceTexture;
    private float[] mMvpMatrix = new float[16];
    private float[] mTextureMatrix = new float[16];
    private Surface mGLSurface;

    private boolean mFrameReady = false;
    private long mReceivedFrameCount = 0;
    private long mRenderedFrameCount = 0;
    private long mDroppedFrameCount = 0;

    private long mStartPtsNs = -1;
    private long mLastRenderPtsNs = -1;

    public ScreenCapture(MediaProjection mediaProjection) {
        mMediaProjection = mediaProjection;
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mFrameReady = true;
        mReceivedFrameCount++;
    }

    public boolean init(int targetWidth,
                        int targetHeight,
                        int dpi,
                        int fps,
                        EGLContext shareContext,
                        Surface surface,
                        Bitmap watermark) {
        if (mIsInited) {
            return true;
        }
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
        mDpi = dpi;
        mFps = fps;

        // create egl and opengl
        mGLRender = new OpenGLRender();
        // 将mediacodec的surface当作egl的输出的surface
        boolean flag = mGLRender.init(mTargetWidth, mTargetHeight, surface, watermark);
        if (!flag) {
            return false;
        }

        //surface texture
        int surfaceTexture = mGLRender.getOESTexture();
        mGLSurfaceTexture = new SurfaceTexture(surfaceTexture);
        mGLSurfaceTexture.setDefaultBufferSize(mTargetWidth, mTargetHeight);
        mGLSurfaceTexture.setOnFrameAvailableListener(this);

        // gl surface
        // 将截屏的内容绘制到通过opengl生成的surface中间去
        mGLSurface = new Surface(mGLSurfaceTexture);
        createVirtualDisplay(mGLSurface);

        mIsInited = true;
        return true;
    }

    public void destroy() {
        mIsInited = false;

        if (mGLRender != null) {
            mGLRender.destroy();
            mGLRender = null;
        }
    }

    public void processFrame(long lasEncodeNs) {
        if (!mIsInited) {
            return;
        }
        openglOES(lasEncodeNs);
    }


    private void createVirtualDisplay(Surface surface) {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                DEFAULT_DISPLAY_NAME,
                mTargetWidth,
                mTargetHeight,
                mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                surface, new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        super.onPaused();
                    }

                    @Override
                    public void onResumed() {
                        super.onResumed();
                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                        destroy();
                    }
                }, null);
    }

    private void openglOES(long lastEncodeNs) {
        if (mLastRenderPtsNs < 0) {
            if (!mFrameReady) {
                return;
            }
        }

        boolean isNewFrame = false;
        boolean isNewFrameRendered = false;

        long timeStart = System.currentTimeMillis();

        if (mFrameReady) {
            mFrameReady = false;
            mGLSurfaceTexture.updateTexImage();
            mGLSurfaceTexture.getTransformMatrix(mTextureMatrix);

            isNewFrame = true;
        }


        long timespanNs = ONE_SECOND_IN_NS / mFps;
        // 第一帧的开始时刻
        if (mStartPtsNs < 0) {
            mStartPtsNs = System.nanoTime();
        }
        // 视频的时间戳
        long ptsNs = System.nanoTime() - mStartPtsNs;
        if (isNewFrame) {
            if (mLastRenderPtsNs < 0) {
                // 第一帧,不用管直接绘制
                mLastRenderPtsNs = ptsNs;
                mGLRender.doRender(mMvpMatrix, mTextureMatrix, mLastRenderPtsNs);
                mRenderedFrameCount++;
                isNewFrameRendered = true;
            } else if (ptsNs - mLastRenderPtsNs >= timespanNs) {
                // 防止录制产生的数据产生过快
                // 因为只用保持30桢，所以只有距离上一桢至少有1s/30后，才会绘制下一桢
                if (ptsNs - mLastRenderPtsNs > (timespanNs * 4)) {
                    mLastRenderPtsNs = ptsNs;
                    Log.e("ScreenCapture", "abnormal timespan happens 222");
                } else {
                    mLastRenderPtsNs = mLastRenderPtsNs + timespanNs;
                }
                mGLRender.doRender(mMvpMatrix, mTextureMatrix, mLastRenderPtsNs);
                mRenderedFrameCount++;
                isNewFrameRendered = true;
            }
        }

        // 判断是否是由于编码太慢导致的
        boolean isConsumerLagging = (mLastRenderPtsNs - lastEncodeNs) > timespanNs * 4;
        // 编码太慢不插帧，其它情况则进行插帧
        while (!isConsumerLagging && ((ptsNs - mLastRenderPtsNs) >= timespanNs)) {
            if (ptsNs - mLastRenderPtsNs > (timespanNs * 4)) {
                mLastRenderPtsNs = ptsNs;
                Log.e("ScreenCapture", "abnormal timespan happens 222");
            } else {
                mLastRenderPtsNs = mLastRenderPtsNs + timespanNs;
            }
            mGLRender.doRender(mMvpMatrix, mTextureMatrix, mLastRenderPtsNs);
            mRenderedFrameCount++;
            isNewFrameRendered = true;
        }

        if (isNewFrame && !isNewFrameRendered) {
            // 丢帧
            mDroppedFrameCount++;
        }
    }

}
