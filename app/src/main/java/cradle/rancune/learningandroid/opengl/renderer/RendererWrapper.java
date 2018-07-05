package cradle.rancune.learningandroid.opengl.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.lang.reflect.Constructor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.SimpleRenderer;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class RendererWrapper implements GLSurfaceView.Renderer {

    private Context mContext;
    private Class<? extends SimpleRenderer> mClass;

    private SimpleRenderer mRenderer;

    public RendererWrapper(Context context, Class<? extends SimpleRenderer> clazz) {
        mContext = context;
        mClass = clazz;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        try {
            Constructor<? extends SimpleRenderer> constructor = mClass.getDeclaredConstructor(Context.class);
            mRenderer = constructor.newInstance(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            mRenderer = new BasicRenderer(mContext);
        }
        mRenderer.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mRenderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
                GL10.GL_DEPTH_BUFFER_BIT);
        mRenderer.onDrawFrame(gl);
    }
}
