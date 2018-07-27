package cradle.rancune.learningandroid.record.audio;


import android.annotation.TargetApi;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.lang.ref.WeakReference;

import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/7/25.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioWorker implements Runnable {
    private static final String TAG = "AudioWorker";

    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_FRAME = 3;

    private final Object mLock = new Object();

    private AudioConfig mConfig;
    private AudioHandler mHandler;
    private boolean mRunning = false;
    private boolean mReady = false;
    private boolean mPaused = false;
    private boolean mStopped = false;

    private AudioRecord mRecord;
    private AudioEncoder mEncoder;
    private AudioCallback mCallback;

    public AudioWorker(AudioConfig config) {
        if (config == null) {
            config = new AudioConfig();
        }
        mConfig = config;
    }

    public void setCallback(AudioCallback callback) {
        mCallback = callback;
    }

    public void start() {
        synchronized (mLock) {
            if (mRunning) {
                return;
            }
            mRunning = true;
            new Thread(this, TAG).start();

            while (!mReady) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mHandler.sendEmptyMessage(MSG_START);
    }

    public void resume() {
        synchronized (mLock) {
            if (!mRunning) {
                return;
            }
            if (!mPaused) {
                return;
            }
        }
        mPaused = false;
        mHandler.sendEmptyMessage(MSG_FRAME);
    }

    public void pause() {
        synchronized (mLock) {
            if (!mRunning) {
                return;
            }
            if (mPaused) {
                return;
            }
        }
        mPaused = false;
        mHandler.removeMessages(MSG_FRAME);
    }

    public void stop() {
        synchronized (mLock) {
            if (!mRunning) {
                return;
            }
            if (mStopped) {
                return;
            }
        }
        mStopped = true;

        mHandler.sendEmptyMessage(MSG_STOP);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        Looper.prepare();
        synchronized (mLock) {
            Looper looper = Looper.myLooper();
            mHandler = new AudioHandler(this);
            mReady = true;
            mLock.notifyAll();
        }
        Looper.loop();
        synchronized (mLock) {
            mReady = mRunning = mStopped = false;
        }
    }

    private void performStart() {
        int sampleRate = mConfig.getSampleRate();
        int channelConfig = mConfig.getChannel();
        int format = mConfig.getFormat();
        int samplePerFrame = mConfig.getSamplePerFrame();

        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, format);
        int bufferSize = (int) Math.ceil((float) minBufferSize / samplePerFrame) * samplePerFrame;

        // create audioRecord
        try {
            mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, format, bufferSize);
        } catch (Exception e) {
            Logger.e(TAG, "AudioRecord create failed", e);
            if (mCallback != null) {
                mCallback.onError(AudioCallback.ERROR_CREATE_RECORD, e);
            }
            return;
        }
        mConfig.setMinBufferSize(bufferSize);

        // create audioEncoder
        try {
            mEncoder = new AudioEncoder(mConfig);
            mEncoder.setCallback(mCallback);
        } catch (Exception e) {
            Logger.e(TAG, "AudioEncoder create failed", e);
            if (mCallback != null) {
                mCallback.onError(AudioCallback.ERROR_CREATE_ENCODER, e);
            }
            return;
        }

        // start recording
        try {
            mRecord.startRecording();
        } catch (IllegalStateException e) {
            Logger.e(TAG, "AudioRecord startRecording failed", e);
            if (mCallback != null) {
                mCallback.onError(AudioCallback.ERROR_START_RECORD, e);
            }
            return;
        }

        try {
            mEncoder.start();
        } catch (Exception e) {
            Logger.e(TAG, "AudioEncoder start failed", e);
            if (mCallback != null) {
                mCallback.onError(AudioCallback.ERROR_START_ENCODER, e);
            }
            return;
        }

        if (mCallback != null) {
            mCallback.onState(AudioCallback.STATE_START);
        }
        mHandler.sendEmptyMessage(MSG_FRAME);
    }

    private void performStop() {
        if (mRecord != null) {
            try {
                mRecord.stop();
                mRecord.release();
                mRecord = null;
            } catch (Exception e) {
                Logger.e(TAG, "AudioRecord stop failed", e);
                if (mCallback != null) {
                    mCallback.onError(AudioCallback.ERROR_STOP_RECORD, e);
                }
            }
        }

        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder = null;
            } catch (Exception e) {
                Logger.e(TAG, "AudioEncoder stop failed", e);
                if (mCallback != null) {
                    mCallback.onError(AudioCallback.ERROR_STOP_ENCODER, e);
                }
            }
        }

        if (mCallback != null) {
            mCallback.onState(AudioCallback.STATE_STOP);
        }
    }

    private void performFrame(boolean endOfStream) {
        // 原始音频数据写入MediaCodec
        mEncoder.offer(mRecord, endOfStream);

        // 消费编码好的音频数据
        mEncoder.consume(endOfStream);

        synchronized (mLock) {
            if (mStopped || mPaused) {
                return;
            }
        }
        mHandler.sendEmptyMessage(MSG_FRAME);
    }

    private static class AudioHandler extends Handler {
        private WeakReference<AudioWorker> mRef;

        AudioHandler(AudioWorker worker) {
            mRef = new WeakReference<>(worker);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AudioWorker worker = mRef.get();
            if (worker == null) {
                return;
            }
            switch (msg.what) {
                case MSG_START: {
                    worker.performStart();
                    break;
                }
                case MSG_FRAME: {
                    worker.performFrame(false);
                    break;
                }
                case MSG_STOP: {
                    worker.performFrame(true);
                    worker.performStop();
                    getLooper().quit();
                    break;
                }
            }
        }
    }
}
