package cradle.rancune.learningandroid.opengl.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.egl.EglHelper;
import cradle.rancune.learningandroid.opengl.filter.image.SaturationFilter;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/23.
 */
public class OpenGLEglActivity extends BaseActivity implements SurfaceHolder.Callback {
    private HandlerThread mThread;
    private Handler mHandler;
    private EglHelper mEglHelper;

    private ImageView mImageView;

    private float mSaturation;
    private SaturationFilter mFilter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_opengl_egl);
        mImageView = findViewById(R.id.image_view);
        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void initData() {
        // egl
        mEglHelper = new EglHelper();

        // filter
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lenna, options);
        mSaturation = 0.5f;
        mFilter = new SaturationFilter(OpenGLEglActivity.this);
        mFilter.setBitmap(bitmap);
        mFilter.setSaturation(mSaturation);

        // render thread
        mThread = new HandlerThread("Egl_Thread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    protected void onDestroy() {
        if (mThread != null) {
            mThread.quit();
            mThread = null;
        }
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEglHelper == null || mFilter == null) {
                    return;
                }
                mEglHelper.init(null, EglHelper.FLAG_TRY_GLES3 | EglHelper.FLAG_RECORDABLE);
            }
        });
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEglHelper == null || mFilter == null) {
                    return;
                }
                // egl make current
                mEglHelper.createWindowSurface(holder.getSurface());

                int texture = GLHelper.load2DTexture();
                mFilter.setTextureId(texture);
                mFilter.performCreate();

                // viewport
                GLES20.glViewport(0, 0, width, height);
                mFilter.onSizeChanged(width, height);

                // draw
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                mFilter.performDraw();
                mEglHelper.swapBuffers();
            }
        });

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEglHelper != null) {
                    mEglHelper.destroy();
                }
                mHandler.removeCallbacksAndMessages(null);
            }
        });
    }
}
