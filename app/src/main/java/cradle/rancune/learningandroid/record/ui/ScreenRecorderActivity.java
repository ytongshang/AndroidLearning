package cradle.rancune.learningandroid.record.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import cradle.rancune.commons.util.IOUtils;
import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.Constants;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.record.RecordCallback;
import cradle.rancune.learningandroid.record.audio.AudioConfig;
import cradle.rancune.learningandroid.record.audio.AudioWorker;
import cradle.rancune.learningandroid.record.muxer.EncodedData;
import cradle.rancune.learningandroid.record.muxer.MutexCallback;
import cradle.rancune.learningandroid.record.muxer.ScreenCaptureMuxer;
import cradle.rancune.learningandroid.record.video.MediaProjectionHolder;
import cradle.rancune.learningandroid.record.video.ScreenCapture;
import cradle.rancune.learningandroid.record.video.VideoConfig;
import cradle.rancune.learningandroid.record.video.VideoRecorder;
import cradle.rancune.learningandroid.record.video.VideoWorker;

/**
 * Created by Rancune@126.com 2018/8/9.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecorderActivity extends BaseActivity implements View.OnClickListener {

    // audio
    private AudioConfig mAudioConfig;
    private AudioWorker mAudioWorker;

    // video
    private MediaProjection mMediaProjection;
    private ScreenCapture mScreenCapture;
    private VideoRecorder mVideoRecorder;
    private VideoConfig mVideoConfig;
    private VideoWorker mVideoWorker;

    // muxer
    private ScreenCaptureMuxer mMuxer;

    @Override
    public void initView() {
        setContentView(R.layout.activity_record_screen);
    }

    @Override
    public void initData() {
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                stop();
                start();
                break;
            }
            case R.id.btn_stop: {
                stop();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean result = MediaProjectionHolder.getInstance().resolveMediaProjection(resultCode, data);
        if (!result) {
            Toast.makeText(this, R.string.toast_record_screen_permission_denied, Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjection = MediaProjectionHolder.getInstance().getMediaProjection();

        setupRecod();
    }

    private void start() {
        boolean result = MediaProjectionHolder.getInstance().requestMediaProjection(this);
        if (!result) {
            Toast.makeText(this, R.string.toast_record_screen_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void stop() {
        if (mVideoWorker != null) {
            mVideoWorker.stop();
            mVideoWorker = null;
        }
        if (mAudioWorker != null) {
            mAudioWorker.stop();
            mAudioWorker = null;
        }
    }

    private void setupRecod() {
        // Muxer
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.VIDEO_CAHCE);
        IOUtils.ensureDirectory(dir);
        File videofile = new File(dir, System.currentTimeMillis() + ".mp4");
        try {
            mMuxer = new ScreenCaptureMuxer(videofile.getAbsolutePath(), new MutexCallback() {
                @Override
                public void onState(int state) {

                }

                @Override
                public void onError(int error, Throwable e) {

                }
            });
        } catch (IOException e) {
            mMuxer = null;
        }
        if (mMuxer == null) {
            Toast.makeText(this, R.string.toast_record_screen_muxer_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        // audio
        mAudioConfig = new AudioConfig();
        mAudioWorker = new AudioWorker(mAudioConfig, new RecordCallback() {
            @Override
            public void onState(int state) {

            }

            @Override
            public void onError(int code, Throwable e) {

            }

            @Override
            public void onFormatChanged(MediaFormat format) {
                if (mMuxer != null) {
                    mMuxer.onAudioFormatChanged(format);
                }

            }

            @Override
            public void onOutputAvailable(MediaCodec.BufferInfo info, ByteBuffer buffer) {
                if (mMuxer != null) {
                    mMuxer.onRececiveAudioData(new EncodedData(info, buffer));
                }
            }
        });

        // video
        mVideoConfig = new VideoConfig(this);
        mScreenCapture = new ScreenCapture(mMediaProjection);
        mVideoRecorder = new VideoRecorder(mScreenCapture, null, null);
        mVideoWorker = new VideoWorker(mVideoConfig, mVideoRecorder, new RecordCallback() {
            @Override
            public void onState(int state) {

            }

            @Override
            public void onError(int code, Throwable e) {

            }

            @Override
            public void onFormatChanged(MediaFormat format) {
                if (mMuxer != null) {
                    mMuxer.onVideoFormatChanged(format);
                }
            }

            @Override
            public void onOutputAvailable(MediaCodec.BufferInfo info, ByteBuffer buffer) {
                if (mMuxer != null) {
                    mMuxer.onRececiveVideoData(new EncodedData(info, buffer));
                }
            }
        });

        mAudioWorker.start();
        mVideoWorker.start();
    }
}
