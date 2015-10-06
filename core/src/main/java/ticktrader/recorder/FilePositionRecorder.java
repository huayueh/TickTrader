package ticktrader.recorder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ticktrader.dto.FutureType;
import ticktrader.dto.Position;

import java.nio.file.Path;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class FilePositionRecorder extends AbstractFileRecorder<Position> {

    public FilePositionRecorder(Path path) {
        super(path);
        write("Symbol,Contract,Open Time,Open Price,Qty,Side,Close Price,Close Time,Future Type, ExPrice,Net PNL, PNL");
        write(System.lineSeparator());
    }

    @Override
    public void record(Position position) {
        ToStringBuilder builder = new ToStringBuilder(position, ToStringStyle.SIMPLE_STYLE).
                append(position.getOrder().getSymbol()).
                append(position.getOrder().getContract()).
                append(position.getOpenTime()).
                append(position.getOrder().getPrice()).
                append(position.getOrder().getQty()).
                append(position.getOrder().getSide().name()).
                append(position.getClosePrice()).
                append(position.getCloseTime()).
                append(position.getOrder().getFutureType()).
                append(position.getOrder().getExPrice()).
                append(position.getNetPnl()).
                append(position.getPnl());
        builder.append(System.lineSeparator());
        write(builder.build());
    }
}
