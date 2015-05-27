package ticktrader.strategy;

import ticktrader.dto.Tick;
import ticktrader.provider.ContractProvider;
import ticktrader.recorder.Recorder;

/**
 * Author: huayueh
 * Date: 2015/4/28
 */
public class RecordStrategy extends AbstractStrategy {

    public RecordStrategy(Recorder recorder) {
        super(recorder);
    }

    @Override
    public void onTick(Tick tick) {
        recorder.record(tick);
    }

    @Override
    public void onFirstTick(Tick tick) {

    }
}
