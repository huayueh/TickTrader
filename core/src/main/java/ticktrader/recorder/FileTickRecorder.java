package ticktrader.recorder;

import ticktrader.dto.Tick;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class FileTickRecorder extends AbstractFileRecorder<Tick> {


    public FileTickRecorder(Path path) {
        super(path);
    }

    @Override
    public void record(Tick tick) {
        StringBuffer sb = new StringBuffer();
        sb.append(tick.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append(",");
        sb.append(tick.getSymbol()).append(",");
        sb.append(tick.getContract()).append(",");
        sb.append(tick.getPrice()).append(",");
        sb.append(tick.getQty());
        sb.append(System.lineSeparator());
        write(sb.toString());
    }
}
