package ticktrader.recorder;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class PrintRecorder implements Recorder {
    @Override
    public void record(Position position) {
        double pnl = position.getPnl();
        System.out.println(position + " " + pnl);
    }

    @Override
    public void record(Tick tick) {
        System.out.println(tick);
    }
}
