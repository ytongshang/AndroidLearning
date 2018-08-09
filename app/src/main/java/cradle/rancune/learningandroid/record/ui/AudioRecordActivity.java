package cradle.rancune.learningandroid.record.ui;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.commons.util.IOUtils;
import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.Constants;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.record.RecordCallback;
import cradle.rancune.learningandroid.record.audio.AudioConfig;
import cradle.rancune.learningandroid.record.audio.AudioWorker;
import cradle.rancune.learningandroid.record.util.ADTSUtils;

/**
 * Created by Rancune@126.com 2018/7/27.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioRecordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AudioRecordActivity";

    private AudioConfig mConfig;
    private AudioWorker mWorker;
    private OutputStream mOutputStream;

    private boolean mIsRecording = false;

    @Override
    public void initView() {
        setContentView(R.layout.activity_record_audio);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    public void initData() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.AUDIO_CAHCE);
        IOUtils.ensureDirectory(dir);
        File temp = new File(dir, "test.aac");
        if (temp.exists()) {
            temp.delete();
        }
        try {
            mOutputStream = new BufferedOutputStream(new FileOutputStream(temp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mConfig = new AudioConfig();
        mWorker = new AudioWorker(mConfig, new RecordCallback() {

            @Override
            public void onState(int state) {
                switch (state) {
                    case RecordCallback.STATE_AUDIO_START: {
                        mIsRecording = true;
                        break;
                    }
                    case RecordCallback.STATE_AUDIO_STOP: {
                        mIsRecording = false;
                        break;
                    }
                }
            }

            @Override
            public void onError(int code, Throwable e) {
                Logger.e(TAG, "", e);
            }

            @Override
            public void onFormatChanged(MediaFormat format) {

            }

            @Override
            public void onOutputAvailable(MediaCodec.BufferInfo info, ByteBuffer buffer) {
                if (mIsRecording && mOutputStream != null) {
                    //7为ADTS头部的大小
                    int chunkSize = info.size + 7;

                    // 加入ADTS的文件头
                    byte[] chunk = new byte[chunkSize];
                    ADTSUtils.addADTStoPacket(chunk, mConfig.getSampleRate(), chunkSize);

                    // 读入原始的数据
                    buffer.position(info.offset);
                    buffer.limit(info.offset + info.size);
                    buffer.get(chunk, 7, info.size);

                    try {
                        mOutputStream.write(chunk, 0, chunkSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mWorker != null) {
            mWorker.stop();
            mWorker = null;
        }
        IOUtils.closeQuietly(mOutputStream);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                mWorker.start();
                break;
            }
            case R.id.btn_resume: {
                mWorker.resume();
                break;
            }
            case R.id.btn_pause: {
                mWorker.pause();
                break;
            }
            case R.id.btn_stop: {
                mWorker.stop();
                break;
            }
        }
    }
}
