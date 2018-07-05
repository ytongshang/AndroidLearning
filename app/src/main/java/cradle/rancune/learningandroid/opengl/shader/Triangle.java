package cradle.rancune.learningandroid.opengl.shader;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cradle.rancune.learningandroid.opengl.util.GLUtils;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class Triangle {
    private static final int COORDS_PER_VERTEX = 3;

    private final float vertices[] = {
            0.0f, 0.5f, 0.0f, // top
            -0.5f, 0.0f, 0.0f, // bottom left
            0.5f, 0.0f, 0.0f  // bottom right
    };
    private final int vertexCount = vertices.length / COORDS_PER_VERTEX;

    private FloatBuffer vertexBuffer;

    private float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

    private int glProgram;
    private int glPositionHandle;
    private int glColorHandle;

    public Triangle(Context context) {
        vertexBuffer = GLUtils.createFloatBuffer(vertices);
        glProgram = GLUtils.buildProgram(
                GLUtils.readFromAssets(context, "basic_vertex_shader.glsl"),
                GLUtils.readFromAssets(context, "basic_fragment_shader.glsl"));
        glPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition");
        glColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor");
    }

    public void draw() {
        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(glPositionHandle);
        GLES20.glVertexAttribPointer(glPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, vertexBuffer);
        GLES20.glUniform4fv(glColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(glPositionHandle);
    }
}
