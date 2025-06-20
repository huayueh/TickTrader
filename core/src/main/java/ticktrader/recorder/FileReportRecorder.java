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
    private int curMaxWinCnt = 0; // Initialize to 0
    private int curMaxLoseCnt = 0; // Initialize to 0
    private int maxWinCnt = 0;     // Initialize to 0
    private int maxLoseCnt = 0;    // Initialize to 0

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
                // Continuing sequence
                if (position.getPnl() > 0) {
                    curMaxDrawup += position.getPnl();
                    curMaxWinCnt++;
                } else if (position.getPnl() < 0) {
                    curMaxDrawdown += position.getPnl();
                    curMaxLoseCnt++;
                }
                // If PNL is 0, counters don't increment, sequence effectively breaks on next non-zero PNL.
            } else {
                // Sequence broken or first trade after a zero PNL trade
                maxWinCnt = Math.max(maxWinCnt, curMaxWinCnt);
                maxLoseCnt = Math.max(maxLoseCnt, curMaxLoseCnt);

                if (curMaxDrawdown < maxDrawdown){
                    maxDrawdown = curMaxDrawdown;
                    maxDrawDownStart = curMaxDrawStart;
                    maxDrawDownEnd = lastPosition;
                }
                if (curMaxDrawup > maxDrawup){
                    maxDrawup = curMaxDrawup;
                    maxDrawUpStart = curMaxDrawStart;
                    maxDrawUpEnd = lastPosition;
                }

                curMaxDrawStart = position; // Start new sequence
                if (position.getPnl() > 0) {
                    curMaxDrawup = position.getPnl();
                    curMaxDrawdown = 0;
                    curMaxWinCnt = 1;
                    curMaxLoseCnt = 0;
                } else if (position.getPnl() < 0) {
                    curMaxDrawdown = position.getPnl();
                    curMaxDrawup = 0;
                    curMaxLoseCnt = 1;
                    curMaxWinCnt = 0;
                } else { // PNL is 0
                    curMaxDrawdown = 0;
                    curMaxDrawup = 0;
                    curMaxWinCnt = 0;
                    curMaxLoseCnt = 0;
                }
            }
        } else { // Very first position
            curMaxDrawStart = position;
            if (position.getPnl() > 0) {
                curMaxDrawup = position.getPnl();
                curMaxWinCnt = 1;
                curMaxLoseCnt = 0;
            } else if (position.getPnl() < 0) {
                curMaxDrawdown = position.getPnl();
                curMaxLoseCnt = 1;
                curMaxWinCnt = 0;
            } else { // PNL is 0
                curMaxWinCnt = 0;
                curMaxLoseCnt = 0;
                curMaxDrawup = 0;
                curMaxDrawdown = 0;
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
        // Finalize current drawdown/drawup sequence before reporting
        if (curMaxDrawStart != null) { // If there was any position
            if (curMaxDrawdown < maxDrawdown){
                maxDrawdown = curMaxDrawdown;
                maxDrawDownStart = curMaxDrawStart;
                maxDrawDownEnd = lastPosition; // last recorded position
            }
            if (curMaxDrawup > maxDrawup){
                maxDrawup = curMaxDrawup;
                maxDrawUpStart = curMaxDrawStart;
                maxDrawUpEnd = lastPosition; // last recorded position
            }
            // Finalize consecutive win/loss counts for the very last sequence
            maxWinCnt = Math.max(maxWinCnt, curMaxWinCnt);
            maxLoseCnt = Math.max(maxLoseCnt, curMaxLoseCnt);
        }

        write("TotalPnl : " + Double.toString(totalPnl));
        write(System.lineSeparator());

        write("AverageWin : " + (winCnt > 0 ? Double.toString(totalWin / winCnt) : "NaN"));
        write(System.lineSeparator());

        write("AverageLose : " + ((cnt - winCnt) > 0 ? Double.toString(totalLose / (cnt - winCnt)) : "NaN"));
        write(System.lineSeparator());

        double avgWin = (winCnt > 0) ? (totalWin / winCnt) : 0;
        double avgLoss = ((cnt - winCnt) > 0) ? (totalLose / (cnt - winCnt)) : 0;
        write("WinLoseRate : " + (avgLoss != 0 ? Double.toString(Math.abs(avgWin / avgLoss)) : "NaN")); // abs because avgLoss is negative
        write(System.lineSeparator());

        winningRate = (cnt > 0) ? (double)winCnt / cnt : 0.0; // Ensure cnt > 0 for winningRate
        write("WinningRate : " + Double.toString(winningRate));
        write(System.lineSeparator());

        write("MaxWin : " + (maxWin != null ? maxWin.toString() + maxWin.getPnl() : "N/A"));
        write(System.lineSeparator());

        write("MaxLoss : " + (maxLoss != null ? maxLoss.toString() + maxLoss.getPnl() : "N/A"));
        write(System.lineSeparator());

        write("MaxDrawUp : " + Double.toString(maxDrawup));
        write(System.lineSeparator());

        write("MaxDrawUpPeriod : " + (maxDrawUpStart != null && maxDrawUpEnd != null ? maxDrawUpStart.getOpenTime() + " ~ " + maxDrawUpEnd.getCloseTime() : "N/A"));
        write(System.lineSeparator());

        write("MaxWinCnt : " + Double.toString(maxWinCnt));
        write(System.lineSeparator());

        write("MaxDrawDown : " + Double.toString(maxDrawdown));
        write(System.lineSeparator());

        write("MaxLoseCnt : " + Double.toString(maxLoseCnt));
        write(System.lineSeparator());

        write("MaxDrawDownPeriod : " + (maxDrawDownStart != null && maxDrawDownEnd != null ? maxDrawDownStart.getOpenTime() + " ~ " + maxDrawDownEnd.getCloseTime() : "N/A"));
        // Assuming AbstractFileRecorder's write method appends a newline, or expecting each written string to be a full line.
        // The previous FilePositionRecorder added it explicitly after builder.build(). Let's be consistent.
        // However, the original FileReportRecorder does write(System.lineSeparator()) between entries.
        // So, the last entry does not need an extra one if each "write" implies a line.
        // If write just writes the string, then each should end with System.lineSeparator().
        // The current pattern is write("Key : Value"); write(System.lineSeparator());
        // The last one does not have a following write(System.lineSeparator()), which is fine.
    }
}
