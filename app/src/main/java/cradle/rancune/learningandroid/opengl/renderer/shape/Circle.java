package cradle.rancune.learningandroid.opengl.renderer.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/6.
 */
public class Circle extends SimpleRenderer {
    private static final int SLASH = 256;

    private FloatBuffer mVertexBuffer;

    private final float[] mColor = {
            1.0f, 1.0f, 1.0f, 1.0f
    };
    private final float[] mVertices;

    private GLProgram mGLProgram;

    private int mColorPosition;
    private int mVetexPosition;
    private int mMatrixPosition;

    private float[] mMatrix = new float[16];

    public Circle(Context context) {
        super(context);
        mVertices = createPositions(0.5f, SLASH);
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
        GLES20.glClearColor(0.0f, 0, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        GLES20.glViewport(0, 0, width, height);
        float ratio = width > height ? width / (float)height : (float)height / width;
        if (width > height) {
            Matrix.orthoM(mMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
        } else {
            Matrix.orthoM(mMatrix, 0, -1, 1, -ratio, ratio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        draw();
    }

    @Override
    public void draw() {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mGLProgram.getProgram());

        GLES20.glUniform4fv(mColorPosition, 1, mColor, 0);
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);
        GLES20.glEnableVertexAttribArray(mVetexPosition);
        GLES20.glVertexAttribPointer(
                mVetexPosition,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mVertexBuffer
        );
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertices.length / 3);

        GLES20.glDisableVertexAttribArray(mVetexPosition);
    }

    private float[] createPositions(float radius, int n) {
        List<Float> list = new ArrayList<>();
        list.add(0.0f);
        list.add(0.0f);
        float ang = (float) (360 / n);
        for (int i = 0; i < 360 + ang; i += ang) {
            list.add((float) (radius * Math.cos(i * Math.PI / 180)));
            list.add((float) (radius * Math.sin(i * Math.PI / 180)));
        }
        float[] f = new float[list.size()];
        for (int i = 0; i < f.length; ++i) {
            f[i] = list.get(i);
        }
        return f;
    }
}
