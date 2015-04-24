package ticktrader.strategy;

import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public class PrintStrategy extends AbstractStrategy {
    @Override
    public void onTick(Tick tick) {
        System.out.println(tick);
    }

    @Override
    public double getPnl() {
        return 0;
    }
}
