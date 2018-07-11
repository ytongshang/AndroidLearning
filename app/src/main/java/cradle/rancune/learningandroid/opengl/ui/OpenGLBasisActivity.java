package cradle.rancune.learningandroid.opengl.ui;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.renderer.BasicRenderer;
import cradle.rancune.learningandroid.opengl.renderer.RendererWrapper;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.renderer.shape.Circle;
import cradle.rancune.learningandroid.opengl.renderer.shape.ColorTrangle;
import cradle.rancune.learningandroid.opengl.renderer.shape.DrawElements;
import cradle.rancune.learningandroid.opengl.renderer.shape.Square;
import cradle.rancune.learningandroid.opengl.renderer.shape.Triangle;
import cradle.rancune.learningandroid.opengl.renderer.texture.Sample2D;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/3.
 */
public class OpenGLBasisActivity extends BaseActivity {

    public static final int TYPE_BASIS = 1;
    public static final int TYPE_TRIANGLE = 2;
    public static final int TYPE_COLORTRIANGLE = 3;
    public static final int TYPE_SQUARE = 4;
    public static final int TYPE_CIRCLE = 5;
    public static final int TYPE_DRAWELEMENTS = 6;
    public static final int TYPE_SAMPLE2D = 7;

    GLSurfaceView mSurfaceView;

    RendererWrapper mRenderer;

    public static Intent RendererOf(Context context, int type) {
        Intent intent = new Intent(context, OpenGLBasisActivity.class);
        intent.putExtra("rendererType", type);
        return intent;
    }

    @Override
    public void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_opengl01);
        mSurfaceView = findViewById(R.id.gl_surfaceview);
    }

    @Override
    public void initData() {
        if (GLHelper.hasGLES20(this)) {
            mSurfaceView.setEGLContextClientVersion(2);
            Intent intent = getIntent();
            int rendererType = intent.getIntExtra("rendererType", TYPE_BASIS);
            mRenderer = new RendererWrapper(mContext, getRendererClass(rendererType));
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

    private Class<? extends SimpleRenderer> getRendererClass(int type) {
        switch (type) {
            case TYPE_BASIS: {
                return BasicRenderer.class;
            }
            case TYPE_TRIANGLE: {
                return Triangle.class;
            }
            case TYPE_COLORTRIANGLE: {
                return ColorTrangle.class;
            }
            case TYPE_SQUARE: {
                return Square.class;
            }
            case TYPE_CIRCLE: {
                return Circle.class;
            }
            case TYPE_DRAWELEMENTS: {
                return DrawElements.class;
            }
            case TYPE_SAMPLE2D: {
                return Sample2D.class;
            }
            default: {
                return BasicRenderer.class;
            }
        }
    }
}
