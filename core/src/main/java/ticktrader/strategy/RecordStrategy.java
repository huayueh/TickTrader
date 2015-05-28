package ticktrader.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;

/**
 * Author: huayueh
 * Date: 2015/4/28
 */
public class RecordStrategy extends AbstractStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RecordStrategy.class);

    public RecordStrategy(Recorder recorder) {
        super(recorder);
    }

    @Override
    public void onTick(Tick tick) {
        logger.debug("{}", tick);
        recorder.record(tick);
    }

    @Override
    public void onFirstTick(Tick tick) {

    }
}
