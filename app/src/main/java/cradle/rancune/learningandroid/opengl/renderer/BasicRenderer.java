package cradle.rancune.learningandroid.opengl.renderer;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.SimpleRenderer;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class BasicRenderer extends SimpleRenderer{

    public BasicRenderer(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glClearColor(1.0f, 0, 0f, 0.8f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
                GL10.GL_DEPTH_BUFFER_BIT);
    }
}
