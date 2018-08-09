package cradle.rancune.learningandroid.record.video;

import android.graphics.Bitmap;
import android.opengl.EGLContext;
import android.view.Surface;

/**
 * Created by Rancune@126.com 2018/8/3.
 */
public class VideoRecorder {
    private static final String TAG = "VideoRecorder";

    private ScreenCapture mScreenCapture;
    private EGLContext mEGLContext;
    private Bitmap mWatermark;

    public VideoRecorder(ScreenCapture screenCapture, EGLContext eglContext, Bitmap watermark) {
        mScreenCapture = screenCapture;
        mEGLContext = eglContext;
        mWatermark = watermark;
    }

    public void onStart(VideoConfig config, Surface surface) {
        if (mScreenCapture == null) {
            return;
        }
        mScreenCapture.init(
                config.getWidth(),
                config.getHeight(),
                config.getDpi(),
                config.getFrameRate(),
                mEGLContext,
                surface,
                mWatermark);
    }

    public void onFrame(boolean endOfStream, long lastEncodeNs) throws Exception {
        mScreenCapture.processFrame(lastEncodeNs);
    }

    public void onStop() {
        if (mScreenCapture != null) {
            mScreenCapture.destroy();
            mScreenCapture = null;
        }
    }
}
