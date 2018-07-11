package cradle.rancune.learningandroid.opengl.renderer.shape;

import android.content.Context;
import android.opengl.GLES20;

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
    private static final int COORDS_PER_VERTEX = 3;

    private final float[] vertices = {
            0.0f, 0.5f, 0.0f, // top
            -0.5f, 0.0f, 0.0f, // bottom left
            0.5f, 0.0f, 0.0f  // bottom right
    };
    private final int vertexCount = vertices.length / COORDS_PER_VERTEX;

    private FloatBuffer vertexBuffer;

    private float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

    private GLProgram mProgram;

    public Triangle(Context context) {
        super(context);
        vertexBuffer = GLHelper.createFloatBuffer(vertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        mProgram = GLProgram.of(GLHelper.readFromAssets(mContext, "shader/basic.vert"),
                GLHelper.readFromAssets(mContext, "shader/basic.frag"));
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

    public void draw() {
        GLES20.glUseProgram(mProgram.getProgram());
        int positionHandler = mProgram.getAttributeLocation("vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, vertexBuffer);
        int colorHandler = mProgram.getUniformLocation("vColor");
        if (colorHandler != -1) {
            GLES20.glUniform4fv(colorHandler, 1, color, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandler);
    }


}
