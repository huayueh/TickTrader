package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Author: huayueh
 * Date: 2015/4/22
 */
public class DayTradeStrategy extends AbstractStrategy {
    private int tradeCnt;
    private boolean noPos = true;
    private double totalPnl = 0;
    private LocalDate date;

    @Override
    protected void onTick(Tick tick) {
        if (!tick.getTime().toLocalDate().equals(date)) {
            date = tick.getTime().toLocalDate();
            tradeCnt = 0;
        }

        if (tradeCnt == 1)
            return;

        LocalTime tickTime = tick.getTime().toLocalTime();

        if (tickTime.isAfter(LocalTime.of(8, 45, 00)) && noPos) {
            Position position = new Position(tick.getSymbol(), tick.getContract(), Position.Side.Sell, tick.getPrice(), 1, tick.getTime());
            Queue<Position> posQueue = new LinkedList<>();
            posQueue.offer(position);
            //TODO: place order function
            positions.put(tick.getSymbol(), posQueue);//TODO: contract will have W4
            noPos = false;
        }

        if (tickTime.isAfter(LocalTime.of(13, 44, 00)) && !noPos) {
            //TODO: settle order function
            Queue<Position> posQueue = positions.get(tick.getSymbol());//TODO: contract will have W4
            Position position = posQueue.poll();
            double pnl = position.getPnl();
            if (pnl < 1000){
                totalPnl += pnl;
            }
            System.out.println(pnl);
            noPos = true;
            tradeCnt++;
        }
    }

    public double getTotalPnl() {
        return totalPnl;
    }
}
