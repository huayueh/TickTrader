package ticktrader.recorder;

import ticktrader.dto.Position;

import java.util.Collections;
import java.util.List;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class ComposePositionRecorder implements Recorder<Position> {
    private List<Recorder<Position>> recorders;

    public ComposePositionRecorder(List<Recorder<Position>> list){
        recorders = Collections.unmodifiableList(list);
    }

    @Override
    public void record(Position position) {
        for (Recorder<Position> recorder : recorders){
            recorder.record(position);
        }
    }

    @Override
    public void done() {
        for (Recorder<Position> recorder : recorders){
            recorder.done();
        }
    }
}
