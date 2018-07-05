package cradle.rancune.learningandroid.opengl;

import android.opengl.GLES20;

import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class GLProgram {
    private static final String TAG = "GLProgram";

    private int mProgram;

    private int mVertexShader;

    private int mFragmentShaer;

    public static GLProgram of(String vertexCode, String fragmentCode) {
        return new GLProgram(vertexCode, fragmentCode);
    }

    private GLProgram(String vertexShaderCode, String fragmentShaderCode) {
        mVertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        mFragmentShaer = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = linkProgram(mVertexShader, mFragmentShaer);
    }

    public int getAttributeLocation(String name) {
        int location = GLES20.glGetAttribLocation(mProgram, name);
        if (location == -1) {
            Logger.d(TAG, "Attribute :" + name + " not found");
        }
        return location;
    }

    public int getUniformLocation(String name) {
        int location = GLES20.glGetUniformLocation(mProgram, name);
        if (location == -1) {
            Logger.d(TAG, "uniform :" + name + " not found");
        }
        return location;
    }

    public void setIntUniform(String name, int value) {
        if (mProgram == -1) {
            return;
        }
        int location = getUniformLocation(name);
        if (location != -1) {
            GLES20.glUniform1i(location, value);
        }
    }

    public void setFloatUniform(String name, float value) {
        if (mProgram == -1) {
            return;
        }
        int location = GLES20.glGetUniformLocation(mProgram, name);
        if (location != -1) {
            GLES20.glUniform1f(location, value);
        }
    }

    private static int compileShader(int type, String code) {
        if (code == null || code.isEmpty()) {
            Logger.d(TAG, "Shader code is empty");
            return -1;
        }
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            Logger.d(TAG, "Can not create shader");
            return -1;
        }
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            Logger.d(TAG, "Can not compile shader");
            GLES20.glDeleteShader(shader);
            return -1;
        }
        return shader;
    }

    private static int linkProgram(int vertexShader, int fragmentShader) {
        if (vertexShader <= 0 && fragmentShader <= 0) {
            return -1;
        }
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Logger.d(TAG, "Can not create program");
            return -1;
        }
        if (vertexShader > 0) {
            GLES20.glAttachShader(program, vertexShader);
        }
        if (fragmentShader > 0) {
            GLES20.glAttachShader(program, fragmentShader);
        }
        GLES20.glLinkProgram(program);
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Logger.d(TAG, "Can not link program");
            GLES20.glDeleteProgram(program);
            return -1;
        }
        return program;
    }


    public int getProgram() {
        return mProgram;
    }

    public int getVertexShader() {
        return mVertexShader;
    }

    public int getFragmentShaer() {
        return mFragmentShaer;
    }
}
