package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import cradle.rancune.commons.logging.Logger;


/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class KitkatCamera implements ICamera, Camera.PreviewCallback {
    private static final String TAG = "KitkatCamera";

    private static final int PREVIEW_BUFFER_COUNT = 3;

    protected Context mContext;
    private FACING mTargetFacing;
    private Config mTargetConfig;
    private PreviewFrameCallback mPreviewFrameCallback;
    private SurfaceTexture mSurfaceTexture;

    private FACING mFacing;
    private int mCameraWidth;
    private int mCameraHeight;
    private int mDisplayOrientation = 0;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private byte[][] mPreviewCallbackBuffer;

    private boolean mIsPreviewing = false;

    public KitkatCamera(Context context) {
        mContext = context;
    }

    @Override
    public void setTargetFacing(FACING facing) {
        mTargetFacing = facing;
    }

    @Override
    public void setTargetConfig(Config targetConfig) {
        mTargetConfig = targetConfig;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        mSurfaceTexture = texture;
    }

    @Override
    public void setPreviewFrameCallback(PreviewFrameCallback callback) {
        mPreviewFrameCallback = callback;
    }

    @Override
    public void startPreview() {
        if (mCamera != null) {
            release();
        }
        if (mSurfaceTexture == null) {
            Logger.d(TAG, "startPreview, but the SurfaceTexture is not set");
            return;
        }

        // 1. 找到目标摄像头
        mCameraInfo = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == translateFacing(mTargetFacing)) {
                mCamera = Camera.open(i);
                mFacing = mTargetFacing;
                break;
            }
        }
        if (mCamera == null) {
            Logger.d(TAG, "camera facing=" + mTargetFacing + " not found");
            return;
        }

        // 2. 调整摄像头展示方向
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            Logger.d(TAG, "get windowManager failed");
            return;
        }
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mDisplayOrientation = result;
        mCamera.setDisplayOrientation(mDisplayOrientation);

        // 3. 设置自动对焦
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        // 4. 设置预览大小
        mCameraWidth = mTargetConfig.mWidth;
        mCameraHeight = mTargetConfig.mHeight;
        Camera.Size size = parameters.getPreferredPreviewSizeForVideo();
        if (size != null) {
            mCameraWidth = size.width;
            mCameraHeight = size.height;
        }
        for (Camera.Size s : parameters.getSupportedPreviewSizes()) {
            if (s.width == mTargetConfig.mWidth && s.height == mTargetConfig.mHeight) {
                mCameraWidth = mTargetConfig.mWidth;
                mCameraHeight = mTargetConfig.mHeight;
                break;
            }
        }
        parameters.setPreviewSize(mCameraWidth, mCameraHeight);
        mCamera.setParameters(parameters);

        // 5. 开始预览
        if (mPreviewCallbackBuffer == null) {
            mPreviewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
        }
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++) {
            mCamera.addCallbackBuffer(mPreviewCallbackBuffer[i]);
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            Logger.e(TAG, "get windowManager failed", e);
        }
        mCamera.startPreview();

        mIsPreviewing = true;
    }

    @Override
    public void stopPreview() {
        release();
    }

    @Override
    public void release() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mFacing = FACING.FACING_FRONT;
        mCameraInfo = null;
        mDisplayOrientation = 0;
        mCameraWidth = 0;
        mCameraHeight = 0;
        mIsPreviewing = false;
    }

    @Override
    public FACING getFacing() {
        return mFacing;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewFrameCallback != null) {
            mPreviewFrameCallback.onPreviewFrame(data, mCameraWidth, mCameraHeight);
        }
        if (mCamera != null) {
            mCamera.addCallbackBuffer(data);
        }
    }

    public int getPreviewWidth() {
        return mCameraWidth;
    }

    public int getPreviewHeight() {
        return mCameraHeight;
    }

    public int getDisplayOrientation() {
        return mDisplayOrientation;
    }

    private static int translateFacing(FACING facing) {
        if (facing == FACING.FACING_BACK) {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        return Camera.CameraInfo.CAMERA_FACING_FRONT;
    }
}
