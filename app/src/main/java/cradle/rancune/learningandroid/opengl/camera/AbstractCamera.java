package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class AbstractCamera implements ICamera {
    protected PreviewFrameCallback mPreviewFrameCallback;
    protected FACING mTargetFacing;
    protected SurfaceTexture mSurfaceTexture;
    protected Config mConfig;

    protected Context mContext;

    protected FACING mFacing;
    protected int mDisplayOrientation = 0;
    protected int mCameraWidth;
    protected int mCameraHeight;

    public AbstractCamera(Context context) {
        mContext = context;
    }

    @Override
    public void setFacing(FACING facing) {
        mTargetFacing = facing;
    }

    @Override
    public void setConfig(Config config) {
        mConfig = config;
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

    }

    @Override
    public void release() {

    }

    public int getDisplayOrientation() {
        return mDisplayOrientation;
    }
}
