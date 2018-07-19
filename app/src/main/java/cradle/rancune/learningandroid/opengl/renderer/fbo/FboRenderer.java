package cradle.rancune.learningandroid.opengl.renderer.fbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.filter.image.SaturationFilter;
import cradle.rancune.learningandroid.opengl.interfaces.Callback;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/19.
 */
public class FboRenderer implements GLSurfaceView.Renderer {
    private int mSourceTextureId = 0;
    private int mDstTextureId = 0;

    private int mFrameBufferId = 0;
    private int mRenderBufferId = 0;

    private final SaturationFilter mFilter;

    private Bitmap mBitmap;

    private ByteBuffer mBuffer;

    private Callback<ByteBuffer> mCallback;

    public FboRenderer(Context context) {
        mFilter = new SaturationFilter(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFilter.performCreate();
        createEnv();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mBitmap == null) {
            return;
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mDstTextureId, 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mRenderBufferId);
        GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mFilter.setTextureId(mSourceTextureId);
        mFilter.onDraw();
        if (mBuffer == null) {
            mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
        }
        GLES20.glReadPixels(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, mBuffer);
        if (mCallback != null) {
            mCallback.onCallback(mBuffer);
        }
        release();
    }

    public void release() {
        if (mFrameBufferId > 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{mFrameBufferId}, 0);
            mFrameBufferId = 0;
        }
        if (mRenderBufferId > 0) {
            GLES20.glDeleteRenderbuffers(1, new int[mRenderBufferId], 0);
            mRenderBufferId = 0;
        }

        if (mSourceTextureId > 0) {
            GLES20.glDeleteTextures(2, new int[]{mSourceTextureId, mDstTextureId}, 0);
            mSourceTextureId = 0;
            mDstTextureId = 0;
        }

    }

    private void createEnv() {
        release();

        // create Framebbuffer
        int[] fbo = new int[1];
        GLES20.glGenFramebuffers(1, fbo, 0);
        GLHelper.checkGlError("glGenFramebuffers");
        mFrameBufferId = fbo[0];

        // create Renderbuffer
        int[] render = new int[1];
        GLES20.glGenRenderbuffers(1, render, 0);
        GLHelper.checkGlError("glGenRenderbuffers");
        mRenderBufferId = render[0];

        // depth attachment
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRenderBufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                mBitmap.getWidth(), mBitmap.getHeight());
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mRenderBufferId);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        // create texture
        int[] textures = new int[2];
        GLES20.glGenTextures(2, textures, 0);
        GLHelper.checkGlError("glGenTextures");
        for (int i = 0; i < 2; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            if (i == 0) {
                // source
                mSourceTextureId = textures[0];
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            } else {
                // dst
                mDstTextureId = textures[1];
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                        mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA,
                        GLES20.GL_UNSIGNED_BYTE, null);
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mFilter.setBitmap(bitmap);
        mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
    }

    public void setSaturation(float saturation) {
        mFilter.setSaturation(saturation);
    }

    public void setCallback(Callback<ByteBuffer> callback) {
        mCallback = callback;
    }
}
