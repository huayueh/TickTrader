package ticktrader.recorder;

import ticktrader.dto.Position;

import java.nio.file.Path;

/**
 * Author: huayueh
 * Date: 2015/5/28
 */
public class FileReportRecorder extends AbstractFileRecorder<Position> {
    private Position maxWin;
    private Position maxLoss;
    private Position maxDrawDownStart;
    private Position maxDrawDownEnd;
    private Position maxDrawUpStart;
    private Position maxDrawUpEnd;
    private Position curMaxDrawStart;
    private Position lastPosition;
    private double maxDrawdown;
    private double maxDrawup;
    private double curMaxDrawdown;
    private double curMaxDrawup;
    private double winningRate;
    private int cnt;
    private int winCnt;

    public FileReportRecorder(Path path) {
        super(path);
    }

    @Override
    public void record(Position position) {
        cnt++;
        if (position.getPnl() > 0){
            winCnt++;
            winningRate = winCnt / cnt;
        }

        if (maxWin != null){
            if (position.getPnl() > maxWin.getPnl())
                maxWin = position;
        }

        if (maxLoss != null){
            if (position.getPnl() < maxLoss.getPnl())
                maxLoss = position;
        }

        if (lastPosition != null){
            if (lastPosition.getPnl() > 0 && position.getPnl() > 0){
                // max draw up
                curMaxDrawup += position.getPnl();
            } else if(lastPosition.getPnl() < 0 && position.getPnl() < 0){
                // max draw down
                curMaxDrawdown += position.getPnl();
            } else {
                // different side count current
                if (curMaxDrawdown < maxDrawdown){
                    maxDrawdown = curMaxDrawdown;
                    maxDrawDownStart = curMaxDrawStart;
                    maxDrawDownEnd = position;
                }
                if (curMaxDrawup > maxDrawup){
                    maxDrawup = curMaxDrawup;
                    maxDrawUpStart = curMaxDrawStart;
                    maxDrawUpEnd = position;
                }

                curMaxDrawStart = null;
                curMaxDrawdown = 0;
                curMaxDrawup = 0;
            }
        }

        if (curMaxDrawStart == null){
            curMaxDrawStart = position;
            if (position.getPnl() > 0){
                curMaxDrawup = position.getPnl();
            } else {
                curMaxDrawdown = position.getPnl();
            }
        }

        if (maxWin == null)
            maxWin = position;

        if (maxLoss == null)
            maxLoss = position;

        lastPosition = position;
    }

    @Override
    public void done() {
        write("WinningRate");
        write(System.lineSeparator());
        write(Double.toString(winningRate));

        write(System.lineSeparator());
        write("MaxWin");
        write(System.lineSeparator());
        write(maxWin.toString());

        write(System.lineSeparator());
        write("MaxLoss");
        write(System.lineSeparator());
        write(maxLoss.toString());

        write(System.lineSeparator());
        write("MaxDrawUp");
        write(System.lineSeparator());
        write(Double.toString(maxDrawup));

        write(System.lineSeparator());
        write("MaxDrawUpStart");
        write(System.lineSeparator());
        write(maxDrawUpStart.toString());

        write(System.lineSeparator());
        write("MaxDrawUpEnd");
        write(System.lineSeparator());
        write(maxDrawUpEnd.toString());

        write(System.lineSeparator());
        write("MaxDrawDown");
        write(System.lineSeparator());
        write(Double.toString(maxDrawdown));

        write(System.lineSeparator());
        write("MaxDrawDownStart");
        write(System.lineSeparator());
        write(maxDrawDownStart.toString());

        write(System.lineSeparator());
        write("MaxDrawDownEnd");
        write(System.lineSeparator());
        write(maxDrawDownEnd.toString());
    }
}
