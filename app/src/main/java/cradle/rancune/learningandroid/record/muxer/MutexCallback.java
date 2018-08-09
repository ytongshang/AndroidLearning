package cradle.rancune.learningandroid.record.muxer;

/**
 * Created by Rancune@126.com 2018/8/9.
 */
public interface MutexCallback {
    int STATE_START = 1;
    int STATE_STOP = 2;


    int ERROR_WRITE_AUDIO = 11;
    int ERROR_WRITE_VIDEO = 12;

    void onState(int state);

    void onError(int error, Throwable e);
}
