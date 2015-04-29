package ticktrader.recorder;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public interface Recorder<T> {
    void record(T t);
}
