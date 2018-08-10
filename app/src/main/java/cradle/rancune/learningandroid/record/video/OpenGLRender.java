package cradle.rancune.learningandroid.record.video;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.view.Surface;

import java.nio.FloatBuffer;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.opengl.egl.EglHelper;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/26.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class OpenGLRender {
    private static final String TAG = "OpenGLRender";

    private static final String VERTEX =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoordinate;\n" +
                    "uniform mat4 uMatrix;\n" +
                    "uniform mat4 uTextureCoordMatrix;\n" +
                    "varying vec2 vTextureCoordinate;\n" +
                    "void main() {\n" +
                    "    gl_Position=uMatrix*aPosition;\n" +
                    "    vTextureCoordinate=(uTextureCoordMatrix*vec4(aTextureCoordinate,0,1)).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_2D =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "varying vec2 vTextureCoordinate;\n" +
                    "void main() {\n" +
                    "  gl_FragColor=texture2D(uTexture,vTextureCoordinate);\n" +
                    "}\n";

    private static final String FRAGMENT_OES =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform samplerExternalOES uOESTexture;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "uniform bool uFlag2DTexture;\n" +
                    "varying vec2 vTextureCoordinate;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = uFlag2DTexture ? texture2D(uTexture, vTextureCoordinate):texture2D(uOESTexture, vTextureCoordinate);\n" +
                    "}\n";

    private static final float[] VERTICES = {
            -1.0f, 1.0f, // 左上
            -1.0f, -1.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, -1.0f, // 右下
    };

    private static final float[] TEXTURECOORDS = {
            0.0f, 0.0f, // 左上
            0.0f, 1.0f, // 左下
            1.0f, 0.0f, // 右上
            1.0f, 1.0f, // 右下
    };

    private static final float[] CAMERACOORDS = {
            0.0f, 1.0f, // 左上
            0.0f, 0.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, 0.0f, // 右下
    };

    private static final float[] IDENTITY_MATRIX;

    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    private static final int TEXTURE_INDEX_OES = 0;
    private static final int TEXTURE_INDEX_WATERMARK_2D = 1;
    private static final int TEXTURE_INDEX_FOREGROUND = 2;

    private EglHelper mEglHelper;

    private int mProgram;
    private int mVetexCoordsHandle;
    private int mTextureCoordsHandle;
    private int mMvpMatrixHandle;
    private int mTextureMatrixHandle;
    private int mTextureHandle;
    private int mOESTextureHandle;
    private int mFlag2DHandle;

    private int[] mTextures = null;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureCoordBuffer;

    private int mRenderWidth;
    private int mRenderHeight;

    private Bitmap mWatermark;
    private boolean mIsWatermarkEnabled = false;

    private boolean mShow2DTexture = false;

    private boolean mIsInited = false;

    public OpenGLRender() {
        mVertexBuffer = GLHelper.createFloatBuffer(VERTICES);
        mTextureCoordBuffer = GLHelper.createFloatBuffer(CAMERACOORDS);
    }

    public boolean init(int renderWidth, int renderHeight, Surface surface, Bitmap watermark) {
        if (mIsInited) {
            return true;
        }
        mRenderWidth = renderWidth;
        mRenderHeight = renderHeight;

        // create egl context
        try {
            mEglHelper = new EglHelper(EglHelper.getDefaultChooser());
            mEglHelper.init();
        } catch (Exception e) {
            Logger.e(TAG, "OpenGLRender create egl failed", e);
            return false;
        }

        // add surface
        try {
            mEglHelper.resumeWindowSurface(surface);
        } catch (Exception e) {
            Logger.e(TAG, "OpenGLRender resumeWindowSurface failed", e);
            return false;
        }

        // create gl program
        int vertex = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, VERTEX);
        int fragment = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_OES);
        if (vertex < 0 || fragment < 0) {
            Logger.e(TAG, "OpenGLRender create shader failed");
            destroy();
            return false;
        }
        mProgram = GLHelper.linkProgram(vertex, fragment);
        if (mProgram < 0) {
            Logger.e(TAG, "OpenGLRender linked program failed");
            destroy();
            return false;
        }

        // attriblocation uniform location
        mVetexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMatrix");
        mTextureMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uTextureCoordMatrix");
        mOESTextureHandle = GLES20.glGetUniformLocation(mProgram, "uOESTexture");
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        mFlag2DHandle = GLES20.glGetUniformLocation(mProgram, "uFlag2DTexture");

        // create texture
        try {
            mTextures = createTextures();
        } catch (Exception e) {
            Logger.e(TAG, "OpenGLRender createTextures failed", e);
            return false;
        }

        //watermark
        if (mWatermark != null && !mWatermark.isRecycled()) {
            mWatermark = watermark;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[TEXTURE_INDEX_WATERMARK_2D]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mWatermark, 0);
        }

        mIsInited = true;
        return true;
    }

    public void destroy() {
        mIsInited = false;
        releaseGl();
        releaseEgl();
        mRenderHeight = 0;
        mRenderWidth = 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void doRender(float[] mvpMatrix, float[] textureMatrix, long presentationTime) {
        if (!mIsInited || mvpMatrix == null) {
            return;
        }

        GLES20.glViewport(0, 0, mRenderWidth, mRenderHeight);

        GLES20.glUseProgram(mProgram);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(mTextureMatrixHandle, 1, false, textureMatrix, 0);
        // oes texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextures[TEXTURE_INDEX_OES]);
        GLES20.glUniform1i(mOESTextureHandle, 0);
        // foreground texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[TEXTURE_INDEX_FOREGROUND]);
        GLES20.glUniform1i(mTextureHandle, 1);
        GLES20.glUniform1i(mFlag2DHandle, mShow2DTexture ? 1 : 0);

        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mvpMatrix, 0);
        // vetex
        GLES20.glEnableVertexAttribArray(mVetexCoordsHandle);
        GLES20.glVertexAttribPointer(mVetexCoordsHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);
        // texture
        GLES20.glEnableVertexAttribArray(mTextureCoordsHandle);
        GLES20.glVertexAttribPointer(mTextureCoordsHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTextureCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVetexCoordsHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordsHandle);

        mEglHelper.setPresentationTime(presentationTime);
        mEglHelper.swapBuffers();
    }

    public int getOESTexture() {
        return mTextures[TEXTURE_INDEX_OES];
    }

    private int[] createTextures() {
        int[] textures = new int[3];

        GLES20.glGenTextures(3, textures, 0);
        GLHelper.checkGlError("glGenTextures");

        // oes
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[TEXTURE_INDEX_OES]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // watermark
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[TEXTURE_INDEX_WATERMARK_2D]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // foreground
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[TEXTURE_INDEX_FOREGROUND]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return textures;
    }


    private void releaseGl() {
        if (mTextures != null) {
            int[] texture = mTextures;
            mTextures = null;
            GLES20.glDeleteTextures(texture.length, texture, 0);
        }
    }

    private void releaseEgl() {
        if (mEglHelper != null) {
            mEglHelper.destroy();
            mEglHelper = null;
        }
    }
}
