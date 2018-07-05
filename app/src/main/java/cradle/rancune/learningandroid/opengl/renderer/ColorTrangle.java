package cradle.rancune.learningandroid.opengl.renderer;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLUtils;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class ColorTrangle extends SimpleRenderer {
    private static final int COORDS_PER_VERTEX = 3;

    private final float vertices[] = {
            0.0f, 0.5f, 0.0f,
            -0.5f, 0.0f, 0.0f,
            0.5f, 0.0f, 0.0f,
    };
    private FloatBuffer vertexBuffer;

    private final float colors[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    private FloatBuffer colorBuffer;

    private GLProgram mProgram;

    public ColorTrangle(Context context) {
        super(context);
        vertexBuffer = GLUtils.createFloatBuffer(vertices);
        colorBuffer = GLUtils.createFloatBuffer(colors);
        mProgram = GLProgram.of(GLUtils.readFromAssets(context, "shader/colortriangle.vert"),
                GLUtils.readFromAssets(context, "shader/colortriangle.frag"));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        draw();
    }

    @Override
    public void draw() {
        GLES20.glUseProgram(mProgram.getProgram());

        int colorIndex = mProgram.getAttributeLocation("aColor");
        GLES20.glEnableVertexAttribArray(colorIndex);
        GLES20.glVertexAttribPointer(
                colorIndex,
                4,
                GLES20.GL_FLOAT,
                false,
                4 * 4,
                colorBuffer
        );

        int posIndex = mProgram.getAttributeLocation("vPosition");
        GLES20.glEnableVertexAttribArray(posIndex);
        GLES20.glVertexAttribPointer(
                posIndex,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                vertexBuffer);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
