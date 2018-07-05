package cradle.rancune.learningandroid.opengl.ui;

import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.shader.Triangle;

/**
 * Created by Rancune@126.com 2018/7/3.
 */
public class OpenGL02Activity extends OpenGLBaseActivity {

    Triangle mTriangle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTriangle = new Triangle(mContext);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mTriangle.draw();
    }
}
