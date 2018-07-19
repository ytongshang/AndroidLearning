package cradle.rancune.learningandroid.opengl.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES11Ext;
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
public class GLHelper {
    private static final String TAG = "GLHelper";

    private GLHelper() {

    }

    public static boolean hasGLES20(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Logger.w(TAG, msg);
            throw new RuntimeException(msg);
        }
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

    public static int[] load2DTexture(int size) {
        final int[] textureObjectids = new int[size];
        GLES20.glGenTextures(size, textureObjectids, 0);
        checkGlError("glGenTextures");
        for (int i = 0; i < size; ++i) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectids[i]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        return textureObjectids;
    }

    public static int load2DTexture() {
        return load2DTexture(1)[0];
    }

    public static int loadOESTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        checkGlError("glGenTextures");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

}
