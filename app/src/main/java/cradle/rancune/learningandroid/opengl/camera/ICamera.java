package cradle.rancune.learningandroid.opengl.camera;

import android.graphics.SurfaceTexture;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public interface ICamera {
    enum FACING {
        FACING_FRONT,
        FACING_BACK
    }

    void setFacing(FACING facing);

    void setConfig(Config config);

    void setPreviewTexture(SurfaceTexture texture);

    void setPreviewFrameCallback(PreviewFrameCallback callback);

    void startPreview();

    void release();

    class Config {
        int mWidth;
        int mHeight;
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
}
