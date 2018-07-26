package cradle.rancune.learningandroid.record.audio;

import android.media.AudioFormat;

/**
 * Created by Rancune@126.com 2018/7/24.
 * https://www.jianshu.com/p/c398754e5984
 */
public class AudioConfig {
    /**
     * 音频采样频率，单位：赫兹（Hz），常用采样频率：8000，12050，22050，44100等
     */
    private int sampleRate = 44100;
    /**
     * 声道
     */
    private int channel = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 每个声音采样点用16bit表示
     */
    private int format = AudioFormat.ENCODING_PCM_16BIT;
    private int samplePerFrame = 2048;
    private int minBufferSize = samplePerFrame;

    private String mime = "audio/mp4a-latm";
    /**
     * 音频码率，单位：比特每秒（bit/s），常用码率：64k，128k，192k，256k，320k等。
     */
    private int bitRate = 64000;

    public int getSizeOfChannel() {
        if (channel == AudioFormat.CHANNEL_IN_MONO) {
            return 1;
        } else if (channel == AudioFormat.CHANNEL_IN_STEREO) {
            return 2;
        }

        return 1;
    }

    public int getByteOfFormat() {
        if (format == AudioFormat.ENCODING_PCM_16BIT) {
            return 2;
        } else if (format == AudioFormat.ENCODING_PCM_8BIT) {
            return 1;
        }
        return 1;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public AudioConfig setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    public int getChannel() {
        return channel;
    }

    public AudioConfig setChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getFormat() {
        return format;
    }

    public AudioConfig setFormat(int format) {
        this.format = format;
        return this;
    }

    public int getSamplePerFrame() {
        return samplePerFrame;
    }

    public AudioConfig setSamplePerFrame(int samplePerFrame) {
        this.samplePerFrame = samplePerFrame;
        return this;
    }

    public int getMinBufferSize() {
        return minBufferSize;
    }

    public AudioConfig setMinBufferSize(int minBufferSize) {
        this.minBufferSize = minBufferSize;
        return this;
    }

    public String getMime() {
        return mime;
    }

    public AudioConfig setMime(String mime) {
        this.mime = mime;
        return this;
    }

    public int getBitRate() {
        return bitRate;
    }

    public AudioConfig setBitRate(int bitRate) {
        this.bitRate = bitRate;
        return this;
    }
}
