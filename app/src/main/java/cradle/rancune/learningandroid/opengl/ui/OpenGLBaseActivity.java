package cradle.rancune.learningandroid.opengl.ui;

import android.opengl.GLSurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.base.BaseActivity;
import cradle.rancune.learningandroid.opengl.util.GLUtils;

/**
 * Created by Rancune@126.com 2018/7/4.
 */
public abstract class OpenGLBaseActivity extends BaseActivity implements GLSurfaceView.Renderer {

    GLSurfaceView mSurfaceView;

    @Override
    public void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_opengl01);
        mSurfaceView = findViewById(R.id.gl_surfaceview);
    }

    @Override
    public void initData() {
        if (GLUtils.hasGLES20(this)) {
            Logger.d(TAG, "Max Vertex Attribute:" + GLUtils.getMaxVertexAttribute());
            mSurfaceView.setEGLContextClientVersion(2);
            mSurfaceView.setRenderer(this);
        } else {
            Toast.makeText(this, getString(R.string.opengl2_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSurfaceView != null) {
            mSurfaceView.onResume();
        }
    }

    @Override
    protected void onStop() {
        if (mSurfaceView != null) {
            mSurfaceView.onPause();
        }
        super.onStop();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.d(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Logger.d(TAG, "onSurfaceChanged, width=" + width + ",height=" + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }

}
