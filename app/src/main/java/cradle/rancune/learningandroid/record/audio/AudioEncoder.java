package cradle.rancune.learningandroid.record.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;

import cradle.rancune.commons.logging.Logger;

/**
 * Created by Rancune@126.com 2018/7/24.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class AudioEncoder {
    private static final String TAG = "AudioEncoder";

    private static final int SEC2MICROSEC = 1000000;

    private AudioConfig mConfig;
    private MediaCodec mEncoder;

    /**
     * 每秒钟采集的数据大小
     */
    private final int mSampleByteSizeInSec;

    private long mLastPresentationTimeUs;
    private long mPresentationInterval = 0;
    private long mLastFrameEncodeEndTimeUs = 0;

    private boolean mUnExpectedEndOfStream = false;

    public AudioEncoder(AudioConfig config) throws IOException {
        String mineType = config.getMime();
        MediaFormat format = MediaFormat.createAudioFormat(mineType, config.getSampleRate(),
                config.getSizeOfChannel());
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.getBitRate());
        mEncoder = MediaCodec.createEncoderByType(mineType);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mSampleByteSizeInSec = config.getSampleRate() * config.getSizeOfChannel() * config.getByteOfFormat();
    }

    public void start() {
        mEncoder.start();
    }

    public void stop() {
        mEncoder.stop();
        mEncoder.release();
        mEncoder = null;
    }

    public void push(AudioRecord record, boolean endOfStream) {
        int buffIndex = mEncoder.dequeueInputBuffer(0);
        if (buffIndex >= 0) {
            ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
            // get an input buffer
            ByteBuffer bufferCache = inputBuffers[buffIndex];
            // clear the buffer
            bufferCache.clear();
            // read from audio record
            int readSize = record.read(bufferCache, bufferCache.remaining());
            if (readSize == AudioRecord.ERROR_INVALID_OPERATION
                    || readSize == AudioRecord.ERROR_BAD_VALUE) {
                Logger.w(TAG, "audiorecord read failed, error=" + readSize);
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
    }

    public void pop(boolean endOfStream) {
        while (true) {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int buffIndex = mEncoder.dequeueOutputBuffer(info, 0);
            ByteBuffer[] buffers = mEncoder.getOutputBuffers();

            if (buffIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            } else if (buffIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                buffers = mEncoder.getOutputBuffers();
            } else if (buffIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream || mUnExpectedEndOfStream) {
                    break;
                }
            } else if (buffIndex < 0) {
                Logger.w(TAG, "Mediacodec dequeueOutputBuffer, buffIndex < 0");
            } else {
                ByteBuffer buffer = buffers[buffIndex];
                if (buffer != null) {
                    buffer.position(info.offset);
                    buffer.limit(info.offset + info.size);
                    info.offset = 0;
                }

                // todo 消费掉编码好的数据
                mEncoder.releaseOutputBuffer(buffIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Logger.w(TAG, "AudioEncoder.pop : reached end of stream unexpectedly");
                    } else {
                        Logger.d(TAG, "AudioEncoder.pop : end of stream reached");
                    }
                    break;
                }
            }
        }
    }

}
