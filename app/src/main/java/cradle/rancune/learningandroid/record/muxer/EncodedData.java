package cradle.rancune.learningandroid.record.muxer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.os.Build;

import java.nio.ByteBuffer;

/**
 * Created by Rancune@126.com 2018/8/9.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class EncodedData {
    public final MediaCodec.BufferInfo info;
    public final ByteBuffer buffer;

    public int track = -1;

    public EncodedData(MediaCodec.BufferInfo info, ByteBuffer buffer) {
        this.info = info;
        this.buffer = buffer;
    }

    public EncodedData(int track, MediaCodec.BufferInfo info, ByteBuffer buffer) {
        this.track = track;
        this.info = info;
        this.buffer = buffer;
    }
}
