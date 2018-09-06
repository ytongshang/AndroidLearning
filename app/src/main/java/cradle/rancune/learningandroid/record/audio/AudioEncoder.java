package cradle.rancune.learningandroid.record.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.record.RecordCallback;

/**
 * Created by Rancune@126.com 2018/7/24.
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class AudioEncoder {
    private static final String TAG = "AudioEncoder";

    private static final int SEC2MICROSEC = 1000000;

    private AudioConfig mConfig;
    private MediaCodec mEncoder;
    private RecordCallback mCallback;

    /**
     * 每秒钟采集的原始PCM数据大小
     */
    private final int mSampleByteSizeInSec;

    private long mLastPresentationTimeUs = 0;
    private long mPresentationInterval = 0;
    private long mLastFrameEncodeEndTimeUs = 0;
    private boolean mUnExpectedEndOfStream = false;

    public AudioEncoder(AudioConfig config, RecordCallback callback) throws Exception {
        mCallback = callback;
        String mineType = config.getMime();
        MediaFormat format = MediaFormat.createAudioFormat(mineType, config.getSampleRate(),
                config.getSizeOfChannel());
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.getBitRate());
        mEncoder = MediaCodec.createEncoderByType(mineType);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSampleByteSizeInSec = config.getSampleRate() * config.getSizeOfChannel() * config.getByteOfFormat();
    }

    public void setCallback(RecordCallback callback) {
        mCallback = callback;
    }

    public void start() {
        mEncoder.start();
    }

    public void stop() {
        mEncoder.stop();
        mEncoder.release();
        mEncoder = null;
    }

    public void offer(AudioRecord record, boolean endOfStream) {
        try {
            int buffIndex = mEncoder.dequeueInputBuffer(0);
            if (buffIndex >= 0) {
                // get an input buffer
                ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
                ByteBuffer buffer = inputBuffers[buffIndex];
                buffer.clear();

                // read from audioRecord
                int readSize = record.read(buffer, buffer.remaining());
                if (readSize == AudioRecord.ERROR
                        || readSize == AudioRecord.ERROR_BAD_VALUE
                        || readSize == AudioRecord.ERROR_INVALID_OPERATION
                        || readSize == AudioRecord.ERROR_DEAD_OBJECT) {
                    Logger.w(TAG, "read from AudioRecord failed, error=" + readSize);
                } else {
                    int flag = endOfStream ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0;
                    mEncoder.queueInputBuffer(buffIndex, 0, readSize, mLastPresentationTimeUs, flag);
                    // 根据采集数据的大小确定实际经过的时间
                    mPresentationInterval = (int) (((float) readSize / mSampleByteSizeInSec) * SEC2MICROSEC);
                    mLastPresentationTimeUs += mPresentationInterval;
                    mLastFrameEncodeEndTimeUs += mPresentationInterval;
                }
            } else if (endOfStream) {
                mUnExpectedEndOfStream = true;
            }
        } catch (Exception e) {
            Logger.e(TAG, "AudioEncoder offer error", e);
            if (mCallback != null) {
                mCallback.onError(RecordCallback.ERROR_AUDIO_ENCODER_OFFER, e);
            }
        }
    }

    public void consume(boolean endOfStream) {
        try {
            while (true) {
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int buffIndex = mEncoder.dequeueOutputBuffer(info, 0);
                ByteBuffer[] buffers = mEncoder.getOutputBuffers();

                if (buffIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (!endOfStream || mUnExpectedEndOfStream) {
                        break;
                    }
                } else if (buffIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // 输出格式发生了变化
                    MediaFormat format = mEncoder.getOutputFormat();
                    if (mCallback != null) {
                        mCallback.onFormatChanged(format);
                    }
                } else if (buffIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // 输出的buffer数组发生了变化
                    buffers = mEncoder.getOutputBuffers();
                } else if (buffIndex < 0) {
                    // 一般也不会发生
                    Logger.w(TAG, "Mediacodec dequeueOutputBuffer, buffIndex < 0");
                } else {
                    ByteBuffer buffer = buffers[buffIndex];

                    if (mCallback != null) {
                        mCallback.onOutputAvailable(info, buffer);
                    }
                    mEncoder.releaseOutputBuffer(buffIndex, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfStream) {
                            Logger.w(TAG, "AudioEncoder.consume : reached end of stream unexpectedly");
                        } else {
                            Logger.d(TAG, "AudioEncoder.consume : end of stream reached");
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "AudioEncoder consumer error", e);
            if (mCallback != null) {
                mCallback.onError(RecordCallback.ERROR_AUDIO_ENCODER_OFFER, e);
            }
        }
    }

}
