package cradle.rancune.learningandroid.opengl.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import cradle.rancune.commons.base.Charsets;
import cradle.rancune.commons.logging.Logger;
import cradle.rancune.commons.util.IOUtils;

/**
 * Created by Rancune@126.com 2018/7/3.
 */
public class GLUtils {
    private static final String TAG = "GLUtils";

    private GLUtils() {

    }

    public static boolean hasGLES20(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public static int getMaxVertexAttribute() {
        int[] params = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, params, 0);
        return params[0];
    }

    public static FloatBuffer createFloatBuffer(float[] vertices) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(vertices);
        fb.position(0);
        return fb;
    }

    public static ShortBuffer createShortBuffer(short[] indices) {
        ByteBuffer bb = ByteBuffer.allocateDirect(indices.length * 2);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer fb = bb.asShortBuffer();
        fb.put(indices);
        fb.position(0);
        return fb;
    }

    public static String readFromAssets(Context context, String path) {
        InputStream in = null;
        try {
            in = context.getAssets().open(path);
            Reader reader = new InputStreamReader(in, Charsets.UTF_8);
            StringBuilder builder = new StringBuilder();
            char[] buf = new char[1024];
            int len;
            while ((len = reader.read(buf)) != -1) {
                builder.append(buf, 0, len);
            }
            return builder.toString().replaceAll("\\r\\n", "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return "";
    }

    public static int buildProgram(String vertexShaderCode, String fragmentShaderCode) {
        int vertextShader = compileVertexShader(vertexShaderCode);
        int fragmentShader = compileFragmentShader(fragmentShaderCode);
        return linkProgram(vertextShader, fragmentShader);
    }

    private static int compileVertexShader(String code) {
        return compileShader(GLES20.GL_VERTEX_SHADER, code);
    }

    private static int compileFragmentShader(String code) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, code);
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
}
