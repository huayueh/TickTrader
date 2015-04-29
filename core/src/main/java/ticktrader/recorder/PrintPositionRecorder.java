package ticktrader.recorder;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class PrintPositionRecorder implements Recorder<Position> {
    @Override
    public void record(Position position) {
        double pnl = position.getPnl();
        System.out.println(position + " " + pnl);
    }
}
