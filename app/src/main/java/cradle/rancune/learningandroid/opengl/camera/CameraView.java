package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.filter.camera.CameraFilter;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private KitkatCamera mCamera;
    private SurfaceTexture mSurfaceTexture;

    private int mTextureId;
    private CameraFilter mFilter;

    private Context mContext;
    private float[] mTextureTransformMatrix = new float[16];

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
        mCamera.setTargetFacing(ICamera.FACING.FACING_FRONT);
        ICamera.Config cnf = new ICamera.Config();
        cnf.mWidth = 1280;
        cnf.mHeight = 720;
        mCamera.setTargetConfig(cnf);

        mFilter = new CameraFilter(mContext);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = GLHelper.loadOESTexture();
        mFilter.performCreate();
        mFilter.setTextureId(mTextureId);
        mSurfaceTexture = new SurfaceTexture(mTextureId);
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
            mSurfaceTexture.getTransformMatrix(mTextureTransformMatrix);
            mFilter.setTextureMatrix(mTextureTransformMatrix);
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mFilter.performDraw();
    }

    public void resume() {
        onResume();
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public void pasuse() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        onPause();
    }

    public void switchCamera() {
        ICamera.FACING target;
        if (mCamera.getFacing() == ICamera.FACING.FACING_FRONT) {
            target = ICamera.FACING.FACING_BACK;
        } else {
            target = ICamera.FACING.FACING_FRONT;
        }
        mCamera.setTargetFacing(target);
        mCamera.startPreview();
    }
}
