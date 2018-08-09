package cradle.rancune.learningandroid.record;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;


/**
 * Created by Rancune@126.com 2018/7/27.
 */
public interface RecordCallback {
    // AUDIO
    int ERROR_AUDIO_CREATE_RECORD = 1;
    int ERROR_AUDIO_CREATE_ENCODER = 2;
    int ERROR_AUDIO_START_RECORD = 3;
    int ERROR_AUDIO_START_ENCODER = 4;
    int ERROR_AUDIO_ENCODER_OFFER = 5;
    int ERROR_AUDIO_ENCODER_CONSUMER = 6;
    int ERROR_AUDIO_STOP_RECORD = 7;
    int ERROR_AUDIO_STOP_ENCODER = 8;

    int STATE_AUDIO_START = 11;
    int STATE_AUDIO_STOP = 12;

    // VIDEO
    int ERROR_VIDEO_CREATE_ENCODER = 101;
    int ERROR_VIDEO_START_ENCODER = 102;
    int ERROR_VIDEO_RECORDER_FRAME = 103;
    int ERROR_VIDEO_ENCODER_FRAME = 104;

    int STATE_VIDEO_START = 111;
    int STATE_VIDEO_START_SUCCESS = 112;
    int STATE_VIDEO_STOP = 113;


    void onState(int state);

    void onError(int code, Throwable e);

    void onFormatChanged(MediaFormat format);

    void onOutputAvailable(MediaCodec.BufferInfo info, ByteBuffer buffer);
}
