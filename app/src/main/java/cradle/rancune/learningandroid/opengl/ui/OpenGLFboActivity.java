package cradle.rancune.learningandroid.opengl.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;

import cradle.rancune.commons.util.ImageUtils;
import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.Constants;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.interfaces.Callback;
import cradle.rancune.learningandroid.opengl.renderer.fbo.FboRenderer;

/**
 * Created by Rancune@126.com 2018/7/19.
 */
public class OpenGLFboActivity extends BaseActivity {

    GLSurfaceView mSurfaceView;
    Bitmap mBitmap;
    float mSaturation;
    FboRenderer mRender;
    AppCompatSeekBar mSeekbar;

    @Override
    public void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_saturation);
        mSurfaceView = findViewById(R.id.gl_surfaceview);
        mSeekbar = findViewById(R.id.seekbar_saturation);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mRender != null) {
                    mRender.setSaturation(progress / 100f);
                    //mSurfaceView.requestRender();
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
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lenna);

        mSurfaceView.setEGLContextClientVersion(2);
        mRender = new FboRenderer(this);
        mRender.setBitmap(mBitmap);
        mRender.setSaturation(mSaturation);
        mSurfaceView.setRenderer(mRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mRender.setCallback(new Callback<ByteBuffer>() {
            @Override
            public void onCallback(final ByteBuffer data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveBitmap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.toast_save_picture_sucess, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        mSurfaceView.onPause();
        super.onPause();
    }

    private void saveBitmap(ByteBuffer data) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(data);
        String fileName = System.currentTimeMillis() + ".jpg";
        String file = Environment.getExternalStorageDirectory().getAbsolutePath()
                + Constants.IMAGE_CAHCE + fileName;
        ImageUtils.saveBitmapToFile(bitmap, new File(file), Bitmap.CompressFormat.JPEG, 100);
    }
}
