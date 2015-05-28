package ticktrader.recorder;

import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/28
 */
public class PrintTickRecorder implements Recorder<Tick> {

    @Override
    public void record(Tick tick) {
        System.out.println(tick);
    }

    @Override
    public void done() {

    }
}
