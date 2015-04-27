package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;

import java.util.*;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public abstract class AbstractStrategy implements Strategy {
    private final Recorder recorder;
    protected Map<String, Queue<Position>> positions = new HashMap<>();
    protected double totalPnl = 0;

    public AbstractStrategy(Recorder recorder){
        this.recorder = recorder;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass().isAssignableFrom(Tick.class)) {
            cntPnl((Tick) arg);
            onTick((Tick) arg);
        }
    }

    protected void placePosition(Position position) {
        Queue<Position> posQueue = positions.get(position.getSymbol() + position.getContract());
        if (posQueue == null) {
            posQueue = new LinkedList<>();
        }
        posQueue.offer(position);
        positions.put(position.getSymbol() + position.getContract(), posQueue);
    }

    protected void settleAllPosition(Tick tick) {
        Queue<Position> posQueue = positions.get(tick.getSymbol() + tick.getContract());
        while (posQueue != null && !posQueue.isEmpty()) {
            Position position = posQueue.poll();
            position.fillAllQuantity(tick.getPrice(), tick.getTime());
            totalPnl += position.getPnl();
            recorder.record(position);
        }
    }

    protected void cntPnl(Tick tick) {
        String key = tick.getSymbol() + tick.getContract();
        Queue<Position> queue = positions.get(key);

        if (queue == null)
            return;

        for (Position pos : queue) {
            // concept: over 0 profit
            if (pos.getSide() == Position.Side.Buy) {
                pos.setPnl((tick.getPrice() - pos.getPrice()) * pos.getQty());
            } else {
                pos.setPnl((pos.getPrice() - tick.getPrice()) * pos.getQty());
            }
        }
        onTick(tick);
    }

    @Override
    public double getPnl() {
        return totalPnl;
    }
}
