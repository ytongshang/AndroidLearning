package cradle.rancune.learningandroid.record.video;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import java.nio.ByteBuffer;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.record.RecordCallback;

/**
 * Created by Rancune@126.com 2018/7/24.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoEncoder {
    private static final String TAG = "VideoEncoder";

    private VideoConfig mConfig;
    private RecordCallback mCallback;

    private MediaCodec mEncoder;
    private Surface mSurface;

    public VideoEncoder(VideoConfig config, RecordCallback callback) throws Exception {
        mConfig = config;
        mCallback = callback;
        String mine = config.getMime();
        mEncoder = MediaCodec.createEncoderByType(mine);
        MediaFormat format = MediaFormat.createVideoFormat(mine, config.getWidth(), config.getHeight());
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, config.getColorFormat());
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.getBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, config.getFrameRate());
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, config.getiFrameRate());
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
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

    public Surface getSurface() {
        return mSurface;
    }

    public long consume(boolean endOfStream) throws Exception {
        if (endOfStream) {
            mEncoder.signalEndOfInputStream();
        }
        long lastEncoderPtsNs = 0;
        while (true) {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int bufferIndex = mEncoder.dequeueOutputBuffer(info, 0);
            ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();
            if (bufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mEncoder.getOutputBuffers();
            } else if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break;
                } else {
                    Logger.d(TAG, "VideoEncoder.consume : wait for eos");
                }
            } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat mediaFormat = mEncoder.getOutputFormat();
                if (mCallback != null) {
                    mCallback.onFormatChanged(mediaFormat);
                }
            } else if (bufferIndex < 0) {
                // 一般也不会发生
                Logger.w(TAG, "Mediacodec dequeueOutputBuffer, buffIndex < 0");
            } else {
                ByteBuffer buffer = outputBuffers[bufferIndex];
                if (mCallback != null) {
                    mCallback.onOutputAvailable(info, buffer);
                }

                lastEncoderPtsNs = info.presentationTimeUs * 1000;
                mEncoder.releaseOutputBuffer(bufferIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Logger.w(TAG, "VideoEncoder.consume : reached end of stream unexpectedly");
                    } else {
                        Logger.d(TAG, "VideoEncoder.consume : end of stream reached");
                    }
                    break;
                }
            }
        }

        return lastEncoderPtsNs;
    }
}
