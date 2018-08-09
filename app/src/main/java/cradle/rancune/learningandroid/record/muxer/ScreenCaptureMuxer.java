package cradle.rancune.learningandroid.record.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/8/9.
 */
@SuppressWarnings("UnusedReturnValue")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScreenCaptureMuxer implements Runnable {
    private static final String TAG = "ScreenCaptureMuxer";

    private volatile boolean mStopSingal;

    private String filePath;
    private MediaMuxer mMuxer;
    private Thread mThread;

    private int mVideoTrack;
    private volatile boolean mIsVideoAdded;
    private final BlockingQueue<EncodedData> mVideoData = new LinkedBlockingQueue<>();

    private int mAudioTrack;
    private volatile boolean mIsAudioAdded;
    private final BlockingQueue<EncodedData> mAudioData = new LinkedBlockingQueue<>();

    private long audioSize = 0;
    private long videoSize = 0;
    private long consumeAudio = 0;
    private long consumeVideo = 0;

    private MutexCallback mCallback;

    private volatile boolean mIsStarted;

    public ScreenCaptureMuxer(String filepath, MutexCallback callback) throws IOException {
        mMuxer = new MediaMuxer(filepath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mCallback = callback;
    }


    public void stop() {
        mStopSingal = true;
    }

    @Override
    public void run() {
        running();
        if (mCallback != null) {
            mCallback.onState(MutexCallback.STATE_START);
        }
    }

    public int onAudioFormatChanged(MediaFormat format) {
        mAudioTrack = mMuxer.addTrack(format);
        mIsAudioAdded = true;
        if (mIsVideoAdded && !mIsStarted) {
            start();
            mIsStarted = true;
        }
        return mAudioTrack;
    }

    public void onRececiveAudioData(EncodedData data) {
        if (data == null || data.info == null || data.buffer == null) {
            return;
        }
        if (data.track < 0) {
            data.track = mAudioTrack;
        }
        mAudioData.offer(data);
    }

    public int onVideoFormatChanged(MediaFormat format) {
        mVideoTrack = mMuxer.addTrack(format);
        mIsVideoAdded = true;
        if (mIsAudioAdded && !mIsStarted) {
            start();
            mIsStarted = true;
        }
        return mVideoTrack;
    }

    public void onRececiveVideoData(EncodedData data) {
        if (data == null || data.info == null || data.buffer == null) {
            return;
        }
        if (data.track < 0) {
            data.track = mVideoTrack;
        }
        mVideoData.offer(data);
    }

    private void start() {
        if (mCallback != null) {
            mCallback.onState(MutexCallback.STATE_START);
        }
        mStopSingal = false;
        mThread = new Thread(this, TAG);
        mThread.start();
    }

    private void running() {
        mMuxer.start();
        while (!mStopSingal) {
            EncodedData audio = mAudioData.poll();
            EncodedData video = mVideoData.poll();

            if (audio != null) {
                ByteBuffer buffer = audio.buffer;
                MediaCodec.BufferInfo info = audio.info;
                // MediaCodec 初始化等信息，并非音频数据
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    buffer = null;
                }
                if (buffer != null) {
                    buffer.position(info.offset);
                    buffer.limit(info.offset + info.size);
                    try {
                        Logger.d(TAG, "audio:" + info.presentationTimeUs);
                        mMuxer.writeSampleData(audio.track, buffer, info);
                        long pts = info.presentationTimeUs / 1000;
                        // 结束了
                        if (info.flags != MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            consumeAudio = pts;
                        }
                        audioSize += info.size;
                    } catch (Exception e) {
                        mStopSingal = true;
                        if (mCallback != null) {
                            mCallback.onError(MutexCallback.ERROR_WRITE_AUDIO, e);
                        }
                    }
                }
            }

            if (video != null) {
                ByteBuffer buffer = video.buffer;
                MediaCodec.BufferInfo info = video.info;
                // MediaCodec 初始化等信息，并非音频数据
                if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    buffer = null;
                }
                if (buffer != null) {
                    buffer.position(info.offset);
                    buffer.limit(info.offset + info.size);
                    try {
                        Logger.d(TAG, "video:" + info.presentationTimeUs);
                        mMuxer.writeSampleData(video.track, buffer, info);
                        long pts = info.presentationTimeUs / 1000;
                        // 结束了
                        if (info.flags != MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            consumeVideo = pts;
                        }
                        videoSize += info.size;
                    } catch (Exception e) {
                        mStopSingal = true;
                        if (mCallback != null) {
                            mCallback.onError(MutexCallback.ERROR_WRITE_VIDEO, e);
                        }
                    }
                }
            }

            int audioDataSize = mAudioData.size();
            int videoDataSize = mVideoData.size();
            if (audioDataSize == 0 && videoDataSize == 0) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    mStopSingal = true;
                    break;
                }
            }
        }

        try {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
