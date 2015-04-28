package ticktrader.recorder;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public interface Recorder {
    void record(Position position);
    void record(Tick position);
}
