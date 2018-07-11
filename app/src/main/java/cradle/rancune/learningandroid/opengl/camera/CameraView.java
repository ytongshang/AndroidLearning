package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private KitkatCamera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private GLProgram mGLProgram;

    private int mHCoordMatrix;

    public static final float[] OM= new float[16];

    static {
        Matrix.setIdentityM(OM, 0);
    }

    //顶点坐标
    private float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord={
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };


    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLProgram = GLProgram.of(GLHelper.readFromAssets(getContext(), "shader/oes_base_vertex.vert"),
                GLHelper.readFromAssets(getContext(), "shader/oes_base_fragment.frag"));
        mHCoordMatrix = mGLProgram.getUniformLocation("vCoordMatrix");
        mCamera = new KitkatCamera(getContext());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCamera.setFacing(ICamera.FACING.FACING_FRONT);
        ICamera.Config cnf = new ICamera.Config();
        cnf.mWidth = 720;
        cnf.mHeight = 1080;
        mCamera.setConfig(cnf);
        int textureId = createTextureId();
        mSurfaceTexture = new SurfaceTexture(textureId);
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
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mGLProgram.getProgram());
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
    }

    private int createTextureId() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return texture[0];
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
