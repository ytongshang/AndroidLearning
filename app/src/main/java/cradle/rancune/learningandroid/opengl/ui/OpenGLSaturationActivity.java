package cradle.rancune.learningandroid.opengl.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.WindowManager;
import android.widget.SeekBar;

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.renderer.image.SaturationRender;

/**
 * Created by Rancune@126.com 2018/7/18.
 */
public class OpenGLSaturationActivity extends BaseActivity{

    GLSurfaceView mSurfaceView;
    Bitmap mBitmap;
    float mSaturation;
    SaturationRender mRender;
    AppCompatSeekBar mSeekbar;

    @Override
    public void initView() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lenna, options);
        mSaturation = 0;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_saturation);
        mSurfaceView = findViewById(R.id.gl_surfaceview);

        mSeekbar = findViewById(R.id.seekbar_saturation);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mRender != null) {
                    mRender.setSaturation(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initData() {
        mSurfaceView.setEGLContextClientVersion(2);
        mRender = new SaturationRender(mContext);
        mRender.setBitmap(mBitmap);
        mRender.setSaturation(mSaturation);
        mSurfaceView.setRenderer(mRender);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }
}
