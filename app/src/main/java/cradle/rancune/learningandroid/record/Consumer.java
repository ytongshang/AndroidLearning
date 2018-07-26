package cradle.rancune.learningandroid.record;

/**
 * Created by Rancune@126.com 2018/7/25.
 */
public interface Consumer<T> {
    void accept(T value) throws Exception;
}
