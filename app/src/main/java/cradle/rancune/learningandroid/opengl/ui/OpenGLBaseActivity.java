package cradle.rancune.learningandroid.opengl.ui;

import android.opengl.GLSurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.renderer.RendererWrapper;
import cradle.rancune.learningandroid.opengl.util.GLUtils;

/**
 * Created by Rancune@126.com 2018/7/4.
 */
public abstract class OpenGLBaseActivity extends BaseActivity {

    GLSurfaceView mSurfaceView;

    RendererWrapper mRenderer;

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
            mRenderer = new RendererWrapper(mContext, createRenderer());
            mSurfaceView.setRenderer(mRenderer);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract Class<? extends SimpleRenderer> createRenderer();
}
