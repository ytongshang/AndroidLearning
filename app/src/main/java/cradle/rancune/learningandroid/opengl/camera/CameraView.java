package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.filter.CameraFilter;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private KitkatCamera mCamera;
    private SurfaceTexture mSurfaceTexture;

    private CameraFilter mFilter;

    private Context mContext;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCamera = new KitkatCamera(getContext());
        mCamera.setFacing(ICamera.FACING.FACING_FRONT);
        ICamera.Config cnf = new ICamera.Config();
        cnf.mWidth = 720;
        cnf.mHeight = 1080;
        mCamera.setConfig(cnf);

        mFilter = new CameraFilter(mContext);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFilter.performCreate();
        mSurfaceTexture = new SurfaceTexture(mFilter.mOesTexture);
        mCamera.setPreviewTexture(mSurfaceTexture);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCamera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mFilter.setPreviewSize(mCamera.getPreviewWidth(), mCamera.getPreviewHeight());
        mFilter.onSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
        mFilter.performDraw();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDetachedFromWindow();
    }


}
