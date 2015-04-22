package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

import java.util.*;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public abstract class AbstractStrategy implements Observer {
    protected Map<String, Queue<Position>> positions = new HashMap<>();

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass().isAssignableFrom(Tick.class)){
            cntPnl((Tick) arg);
        }
    }

    protected void cntPnl(Tick tick){
        String key = tick.getSymbol() + tick.getContract();
        Queue<Position> queue = positions.get(key);

        for (Position pos : queue){
            // concept: over 0 profit
            if (pos.getSide() == Position.Side.Buy){
                pos.setPnl((tick.getPrice() - pos.getPrice()) * pos.getQty());
            } else {
                pos.setPnl((pos.getPrice() - tick.getPrice()) * pos.getQty());
            }
        }
        onTick(tick);
    }

    abstract protected void onTick(Tick tick);
}
