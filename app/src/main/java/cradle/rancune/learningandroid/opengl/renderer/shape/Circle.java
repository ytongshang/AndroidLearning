package cradle.rancune.learningandroid.opengl.renderer.shape;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLUtils;

/**
 * Created by Rancune@126.com 2018/7/6.
 */
public class Circle extends SimpleRenderer {
    private static final int SLASH = 60;

    private FloatBuffer mVertexBuffer;

    private final float[] mColor = {
            1.0f, 1.0f, 1.0f, 1.0f
    };
    private FloatBuffer mColorBuffer;

    private final float[] mVertices;

    private GLProgram mGLProgram;

    public Circle(Context context) {
        super(context);
        mVertices = createPositions(0.5f, SLASH);
        mVertexBuffer = GLUtils.createFloatBuffer(mVertices);
        mColorBuffer = GLUtils.createFloatBuffer(mColor);
        mGLProgram = GLProgram.of(GLUtils.readFromAssets(context, "shader/basic.vert"),
                GLUtils.readFromAssets(context, "shader/basic.frag"));
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

        int colorIndex = mGLProgram.getUniformLocation("vColor");
        GLES20.glUniform4fv(colorIndex, 1, mColorBuffer);

        int positionIndex = mGLProgram.getAttributeLocation("vPosition");
        GLES20.glEnableVertexAttribArray(positionIndex);
        GLES20.glVertexAttribPointer(
                positionIndex,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertices.length / 3);
    }

    private float[] createPositions(float radius, int n) {
        List<Float> list = new ArrayList<>();
        list.add(0.0f);
        list.add(0.0f);
        list.add(0.0f);
        float ang = (float) (360 / n);
        for (int i = 0; i < 360 + ang; i += ang) {
            list.add((float) (radius * Math.cos(i * Math.PI / 180)));
            list.add((float) (radius * Math.sin(i * Math.PI / 180)));
            list.add(0.0f);
        }
        float[] f = new float[list.size()];
        for (int i = 0; i < f.length; ++i) {
            f[i] = list.get(i);
        }
        return f;
    }
}
