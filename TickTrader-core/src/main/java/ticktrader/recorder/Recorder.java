package ticktrader.recorder;

import ticktrader.dto.Position;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public interface Recorder {
    void record(Position position);
}
