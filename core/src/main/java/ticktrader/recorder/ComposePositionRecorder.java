package ticktrader.recorder;

import ticktrader.dto.Position;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class ComposePositionRecorder implements Recorder<Position> {
    private List<Recorder<Position>> recorders;

    public ComposePositionRecorder(){
        recorders = new ArrayList<>();
        recorders.add(new PrintPositionRecorder());
        //TODO:
        recorders.add(new FilePositionRecorder(Paths.get("E:", "positions.csv")));
    }

    @Override
    public void record(Position position) {
        for (Recorder<Position> recorder : recorders){
            recorder.record(position);
        }
    }
}
