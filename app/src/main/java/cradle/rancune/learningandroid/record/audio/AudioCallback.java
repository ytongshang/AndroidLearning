package cradle.rancune.learningandroid.record.audio;

import android.media.MediaCodec;

import java.nio.ByteBuffer;


/**
 * Created by Rancune@126.com 2018/7/27.
 */
public interface AudioCallback {
    int ERROR_CREATE_RECORD = 1;
    int ERROR_CREATE_ENCODER = 2;
    int ERROR_START_RECORD = 3;
    int ERROR_START_ENCODER = 4;
    int ERROR_ENCODER_OFFER = 5;
    int ERROR_ENCODER_CONSUMER = 6;
    int ERROR_STOP_RECORD = 7;
    int ERROR_STOP_ENCODER = 8;

    int STATE_START = 21;
    int STATE_STOP = 22;

    void onState(int state);

    void onError(int code, Throwable e);

    void onOutputAvailable(MediaCodec.BufferInfo info, ByteBuffer buffer);
}
