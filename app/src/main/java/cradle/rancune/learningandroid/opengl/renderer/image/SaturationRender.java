package cradle.rancune.learningandroid.opengl.renderer.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.filter.image.SaturationFilter;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/18.
 */
public class SaturationRender implements GLSurfaceView.Renderer {

    private final SaturationFilter mFilter;


    public SaturationRender(Context context) {
        mFilter = new SaturationFilter(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int textId = GLHelper.load2DTexture();
        mFilter.performCreate();
        mFilter.setTextureId(textId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mFilter.onSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mFilter.performDraw();
    }

    public void setBitmap(Bitmap bitmap) {
        mFilter.setBitmap(bitmap);
    }

    public void setSaturation(float saturation) {
        mFilter.setSaturation(saturation);
    }
}
