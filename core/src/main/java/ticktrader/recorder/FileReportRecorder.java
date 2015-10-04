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
    private double totalPnl;
    private double totalWin;
    private double totalLose;
    private int cnt;
    private int winCnt;
    private double curMaxWinCnt = 1;
    private double curMaxLoseCnt = 1;
    private double maxWinCnt;
    private double maxLoseCnt;

    public FileReportRecorder(Path path) {
        super(path);
    }

    @Override
    public void record(Position position) {
        totalPnl += position.getPnl();
        if (position.getPnl() > 0){
            totalWin += position.getPnl();
        } else {
            totalLose += position.getPnl();
        }

        cnt++;
        if (position.getPnl() > 0){
            winCnt++;
        }
        winningRate = (double)winCnt / cnt;

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
                curMaxWinCnt++;
            } else if(lastPosition.getPnl() < 0 && position.getPnl() < 0){
                // max draw down
                curMaxDrawdown += position.getPnl();
                curMaxLoseCnt++;
            } else {
                // different side count current
                if (curMaxDrawdown < maxDrawdown){
                    maxDrawdown = curMaxDrawdown;
                    maxDrawDownStart = curMaxDrawStart;
                    maxDrawDownEnd = lastPosition;
                    maxLoseCnt = curMaxLoseCnt;
                }
                if (curMaxDrawup > maxDrawup){
                    maxDrawup = curMaxDrawup;
                    maxDrawUpStart = curMaxDrawStart;
                    maxDrawUpEnd = lastPosition;
                    maxWinCnt = curMaxWinCnt;
                }

                curMaxDrawStart = null;
                curMaxDrawdown = 0;
                curMaxDrawup = 0;
                curMaxWinCnt = 1;
                curMaxLoseCnt = 1;
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
        write("TotalPnl : " + Double.toString(totalPnl));

        write(System.lineSeparator());
        write("AverageWin : " + Double.toString(totalWin/winCnt));

        write(System.lineSeparator());
        write("AverageLose : " + Double.toString(totalLose/(cnt-winCnt)));

        write(System.lineSeparator());
        write("WinLoseRate : " + Double.toString((totalWin/winCnt)/ (totalWin/(cnt-winCnt))));

        write(System.lineSeparator());
        write("WinningRate : " + Double.toString(winningRate));

        write(System.lineSeparator());
        write("MaxWin : " + maxWin.toString() + maxWin.getPnl());

        write(System.lineSeparator());
        write("MaxLoss : " + maxLoss.toString() + maxLoss.getPnl());

        write(System.lineSeparator());
        write("MaxDrawUp : " + Double.toString(maxDrawup));

        write(System.lineSeparator());
        write("MaxDrawUpPeriod : " + maxDrawUpStart.getOpenTime() + " ~ " + maxDrawUpEnd.getCloseTime());

        write(System.lineSeparator());
        write("MaxWinCnt : " + Double.toString(maxWinCnt));

        write(System.lineSeparator());
        write("MaxDrawDown : " + Double.toString(maxDrawdown));

        write(System.lineSeparator());
        write("MaxLoseCnt : " + Double.toString(maxLoseCnt));

        write(System.lineSeparator());
        write("MaxDrawDownPeriod : " + maxDrawDownStart.getOpenTime() + " ~ " + maxDrawDownEnd.getCloseTime());

    }
}
