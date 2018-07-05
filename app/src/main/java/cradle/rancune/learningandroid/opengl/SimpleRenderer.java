package cradle.rancune.learningandroid.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/7/5.
 */
public class SimpleRenderer implements GLSurfaceView.Renderer {
    final String TAG = getClass().getSimpleName();

    protected Context mContext;

    public SimpleRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.d(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Logger.d(TAG, "onSurfaceChanged, width=" + width + ",height=" + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    public void draw() {

    }
}
