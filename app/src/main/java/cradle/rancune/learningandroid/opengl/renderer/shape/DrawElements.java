package cradle.rancune.learningandroid.opengl.renderer.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/10.
 */
public class DrawElements extends SimpleRenderer {

    private final float[] mVertices = {
            -1.0f, 1.0f,
            -0.5f, 0.0f,
            0.0f, 1.0f,
            0.5f, 0.0f,
            1.0f, 1.0f,
    };
    private FloatBuffer mVertexBuffer;

    private final float[] mColor = {
            1.0f, 1.0f, 1.0f, 1.0f
    };

    private byte[] mIndices = {
            0, 1, 2, 3, 4,
    };
    private ByteBuffer mIndiceBuffer;

    private GLProgram mGLProgram;

    private int mColorPosition;
    private int mVetexPosition;
    private int mMatrixPosition;

    private float[] mMatrix = new float[16];

    public DrawElements(Context context) {
        super(context);
        mIndiceBuffer = (ByteBuffer) ByteBuffer.allocateDirect(mIndices.length).put(mIndices).position(0);
        mVertexBuffer = GLHelper.createFloatBuffer(mVertices);
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
        GLES20.glClearColor(0.f, 0.f, 0.f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        draw();
    }

    @Override
    public void draw() {
        super.draw();
        GLES20.glUseProgram(mGLProgram.getProgram());

        GLES20.glUniform4fv(mColorPosition, 1, mColor, 0);
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);

        GLES20.glEnableVertexAttribArray(mVetexPosition);
        GLES20.glVertexAttribPointer(
                mVetexPosition,
                2,
                GLES20.GL_FLOAT,
                false,
                2*4,
                mVertexBuffer
        );

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLE_STRIP,
                mIndices.length,
                GLES20.GL_UNSIGNED_BYTE,
                mIndiceBuffer
        );

        GLES20.glDisableVertexAttribArray(mVetexPosition);
    }
}
