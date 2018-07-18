package cradle.rancune.learningandroid.opengl.renderer.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class Triangle extends SimpleRenderer {
    private final float[] vertices = {
            0.0f, 0.5f, // top
            -0.5f, 0.0f, // bottom left
            0.5f, 0.0f  // bottom right
    };
    private final float[] mColor = {
            0.0f, 0.0f, 0.0f, 1.0f
    };

    private FloatBuffer vertexBuffer;

    private float[] mMatrix = new float[16];

    private GLProgram mGLProgram;

    private int mColorPosition;
    private int mVetexPosition;
    private int mMatrixPosition;

    public Triangle(Context context) {
        super(context);
        vertexBuffer = GLHelper.createFloatBuffer(vertices);
        mGLProgram = GLProgram.of(GLHelper.readFromAssets(context, "shader/basic.vert"),
                GLHelper.readFromAssets(context, "shader/basic.frag"));
        mVetexPosition = mGLProgram.getAttributeLocation("aPosition");
        mColorPosition = mGLProgram.getUniformLocation("uColor");
        mMatrixPosition = mGLProgram.getUniformLocation("uMatrix");
        Matrix.setIdentityM(mMatrix, 0);
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
        draw();
    }

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mGLProgram.getProgram());

        GLES20.glUniform4fv(mColorPosition, 1, mColor, 0);
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);

        GLES20.glEnableVertexAttribArray(mVetexPosition);
        GLES20.glVertexAttribPointer(mVetexPosition, 2, GLES20.GL_FLOAT, false,
                2 * 4, vertexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/2);

        GLES20.glDisableVertexAttribArray(mVetexPosition);
    }


}
