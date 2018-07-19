package cradle.rancune.learningandroid.opengl.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

import cradle.rancune.commons.base.Objects;
import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LollipopCamera implements ICamera {
    private static final String TAG = "LollipopCamera";

    protected Context mContext;
    private FACING mTargetFacing;
    private Config mTargetConfig;
    private PreviewFrameCallback mPreviewFrameCallback;
    private SurfaceTexture mSurfaceTexture;

    private FACING mFacing;

    private CameraManager mManager;
    private CameraCharacteristics mCharacteristics;
    private CameraDevice mCameraDevice;
    private String mCameraId;
    private Surface mSurface;
    private CaptureRequest.Builder mRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;

    private Handler mHandler;

    public LollipopCamera(Context context, Handler handler) {
        mContext = context;
        if (handler == null) {
            Looper looper = Looper.myLooper();
            looper = looper == null ? Looper.getMainLooper() : looper;
            handler = new Handler(looper);
        }
        mHandler = handler;
        mManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
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

    @SuppressLint("MissingPermission")
    @Override
    public boolean startPreview() {
        if (mFacing != mTargetFacing) {
            release();
        }
        try {
            for (String cameraId : mManager.getCameraIdList()) {
                mCharacteristics = mManager.getCameraCharacteristics(cameraId);
                Integer f = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (f != null && f == translate(mTargetFacing)) {
                    mCameraId = cameraId;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Logger.e(TAG, "getCameraIdList failed", e);
        }
        if (Objects.isEmpty(mCameraId)) {
            Logger.d(TAG, "camera facing=" + mTargetFacing + " not found");
            return false;
        }
        try {
            mManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    createCameraPreviewSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                }
            }, mHandler);
            return true;
        } catch (CameraAccessException e) {
            Logger.e(TAG, "openCamera failed", e);
        }
        return false;
    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void release() {

    }

    @Override
    public FACING getFacing() {
        return null;
    }

    private int translate(FACING facing) {
        if (facing == FACING.FACING_BACK) {
            return CameraCharacteristics.LENS_FACING_BACK;
        }
        return CameraCharacteristics.LENS_FACING_FRONT;
    }

    private void createCameraPreviewSession() {
        if (mCameraDevice == null) {
            Logger.d(TAG, "createCameraPreviewSession failed, cameraDevice is null");
            return;
        }
        if (mSurfaceTexture == null) {
            Logger.d(TAG, "createCameraPreviewSession failed, SurfaceTexture is null");
            return;
        }

        try {
            mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mSurface = new Surface(mSurfaceTexture);
            mRequestBuilder.addTarget(mSurface);
            List<Surface> list = new ArrayList<>();
            list.add(mSurface);
            mCameraDevice.createCaptureSession(list, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (mCameraDevice == null) {
                        return;
                    }
                    mCameraCaptureSession = session;
                    mRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                    capture();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void capture() {
        try {
            mCameraCaptureSession.setRepeatingRequest(mRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                    super.onCaptureProgressed(session, request, partialResult);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                }

                @Override
                public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                    super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                }

                @Override
                public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                    super.onCaptureSequenceAborted(session, sequenceId);
                }

                @Override
                public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                    super.onCaptureBufferLost(session, request, target, frameNumber);
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
