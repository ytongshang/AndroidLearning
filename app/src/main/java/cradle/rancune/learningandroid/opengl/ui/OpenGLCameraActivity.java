package cradle.rancune.learningandroid.opengl.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.camera.CameraView;
import cradle.rancune.learningandroid.opengl.camera.ICamera;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class OpenGLCameraActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;

    private CameraView mCameraView;
    private WindowManager mWindowManager;

    private ICamera.FACING mFacing = ICamera.FACING.FACING_FRONT;

    @Override
    public void initView() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            setContentView(R.layout.activity_opengl_camera);
            mCameraView = findViewById(R.id.camera_view);
            findViewById(R.id.btn_get_rotation).setOnClickListener(this);
            findViewById(R.id.btn_switch_camera).setOnClickListener(this);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraView != null) {
            mCameraView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mCameraView != null) {
            mCameraView.pasuse();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_rotation: {
                int degree = getDisplayRotation();
                Toast.makeText(mContext, mContext.getString(R.string.toast_display_rotation, degree),
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_switch_camera: {
                mCameraView.switchCamera();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            setContentView(R.layout.activity_opengl_camera);
            mCameraView = findViewById(R.id.camera_view);
        }
    }

    private int getDisplayRotation() {
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
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
        return degrees;
    }
}
