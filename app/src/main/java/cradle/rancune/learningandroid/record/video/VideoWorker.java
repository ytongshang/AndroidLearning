package cradle.rancune.learningandroid.record.video;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.record.RecordCallback;

/**
 * Created by Rancune@126.com 2018/8/3.
 */
public class VideoWorker implements Runnable {
    private static final String TAG = "VideoWorker";

    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_FRAME = 3;

    private final Object mLock = new Object();

    private boolean mRunning = false;
    private boolean mReady = false;
    private boolean mStopped = false;

    private VideoConfig mConfig;
    private VideoRecorder mRecorder;
    private VideoEncoder mEncoder;
    private RecordCallback mCallback;

    private VideoHandler mHandler;

    private long mLastEncodeNs;

    public VideoWorker(VideoConfig config, VideoRecorder recorder, RecordCallback callback) {
        mConfig = config;
        mRecorder = recorder;
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
        Process.setThreadPriority(Process.THREAD_PRIORITY_MORE_FAVORABLE);
        Looper.prepare();
        synchronized (mLock) {
            Looper looper = Looper.myLooper();
            mHandler = new VideoHandler(this);
            mReady = true;
            mLock.notifyAll();
        }
        Looper.loop();
        synchronized (mLock) {
            mReady = mRunning = mStopped = false;
        }
    }

    private void performStart() {
        if (mCallback != null) {
            mCallback.onState(RecordCallback.STATE_VIDEO_START);
        }

        // create video encoder
        try {
             mEncoder = new VideoEncoder(mConfig, mCallback);
        } catch (Exception e) {
            Logger.e(TAG, "VideoEncoder create failed", e);
            if (mCallback != null) {
                mCallback.onError(RecordCallback.ERROR_VIDEO_CREATE_ENCODER, e);
            }
            return;
        }

        // start encoder
        if (mRecorder != null) {
            mRecorder.onStart(mConfig, mEncoder.getSurface());
        }

        // start encode raw data
        try {
            mEncoder.start();
        } catch (Exception e) {
            Logger.e(TAG, "VideoEncoder create failed", e);
            if (mCallback != null) {
                mCallback.onError(RecordCallback.ERROR_VIDEO_START_ENCODER, e);
            }
            return;
        }

        if (mCallback != null) {
            mCallback.onState(RecordCallback.STATE_VIDEO_START_SUCCESS);
        }

        mLastEncodeNs = 0;

        mHandler.sendEmptyMessage(MSG_FRAME);
    }

    private void performFrame(boolean endOfStream) {
        if (mRecorder != null) {
            try {
                mRecorder.onFrame(endOfStream, mLastEncodeNs);
            } catch (Exception e) {
                if (mCallback != null) {
                    mCallback.onError(RecordCallback.ERROR_VIDEO_RECORDER_FRAME, e);
                }
                return;
            }
        }

        try {
            long result = mEncoder.consume(endOfStream);
            if (result > 0) {
                mLastEncodeNs = result;
            } else {
                TimeUnit.MILLISECONDS.sleep(2);
            }
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.onError(RecordCallback.ERROR_VIDEO_ENCODER_FRAME, e);
            }
            return;
        }

        synchronized (mLock) {
            if (mStopped) {
                return;
            }
        }
        mHandler.sendEmptyMessage(MSG_FRAME);
    }

    private void performStop() {
        if (mRecorder != null) {
            mRecorder.onStop();
        }

        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCallback != null) {
            mCallback.onState(RecordCallback.STATE_VIDEO_STOP);
        }
    }

    private static class VideoHandler extends Handler {
        private WeakReference<VideoWorker> mRef;

        VideoHandler(VideoWorker worker) {
            mRef = new WeakReference<>(worker);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoWorker worker = mRef.get();
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
