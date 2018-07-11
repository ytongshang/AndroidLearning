package cradle.rancune.learningandroid.opengl.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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
public class GLHelper {
    private static final String TAG = "GLHelper";

    private GLHelper() {

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

    public static int loadTexture(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            Logger.d(TAG, "load texture failed, bitmap is invalid");
            return -1;
        }
        final int[] textureObjectids = new int[1];
        GLES20.glGenTextures(1, textureObjectids, 0);
        if (textureObjectids[0] != GLES20.GL_TRUE) {
            Logger.d(TAG, "glGenTextures failed");
            return -1;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectids[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectids[0];
    }
}
