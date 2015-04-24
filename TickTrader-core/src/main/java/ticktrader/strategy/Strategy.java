package ticktrader.strategy;

import ticktrader.dto.Tick;

import java.util.Observer;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public interface Strategy extends Observer {
    void onTick(Tick tick);
    double getPnl();
}
