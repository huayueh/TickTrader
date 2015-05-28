package ticktrader.recorder;

import ticktrader.dto.Position;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class PrintPositionRecorder implements Recorder<Position> {
    private double totalPnl;

    @Override
    public void record(Position position) {
        double pnl = position.getPnl();
        totalPnl += pnl;
        System.out.println(position + " " + pnl);
    }

    @Override
    public void done() {
        System.out.println("Total pnl : " + totalPnl);
    }
}
