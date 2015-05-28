package ticktrader.strategy;

import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public class PrintStrategy extends AbstractStrategy {
    public PrintStrategy(Recorder recorder) {
        super(recorder);
    }

    @Override
    public void onTick(Tick tick) {
        System.out.println(tick);
    }

    @Override
    public void onFirstTick(Tick tick) {

    }
}
