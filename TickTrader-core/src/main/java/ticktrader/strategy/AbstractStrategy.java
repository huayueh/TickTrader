package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public abstract class AbstractStrategy implements Observer {
    protected Map<String, Position> positions = new HashMap<>();

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass().isAssignableFrom(Tick.class)){
            onTick((Tick)arg);
        }
    }

    abstract protected void onTick(Tick arg);
}
