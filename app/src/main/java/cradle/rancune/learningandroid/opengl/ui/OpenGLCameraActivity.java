package cradle.rancune.learningandroid.opengl.ui;

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.camera.CameraView;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class OpenGLCameraActivity extends BaseActivity {

    private CameraView mCameraView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_opengl_camera);
        mCameraView = findViewById(R.id.camera_view);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraView != null) {
            mCameraView.onResume();
        }
    }

    @Override
    protected void onStop() {
        if (mCameraView != null) {
            mCameraView.onPause();
        }
        super.onStop();
    }
}
