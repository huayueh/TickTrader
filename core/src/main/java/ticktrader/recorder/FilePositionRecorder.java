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
        // Do not append lineSeparator to the builder, as it might add an extra comma before it.
        // Instead, build the string and then append the separator.
        String output = builder.build();
        // ToStringBuilder with SIMPLE_STYLE might add a trailing comma if the last field was appended.
        // Let's check if it does and remove it if necessary, though ideally, it shouldn't for the last element.
        // A quick check: SIMPLE_STYLE usually just joins with commas. If Pnl is the last, no comma should follow.
        // The issue might be that builder.append(System.lineSeparator()) was acting like another field.

        write(output + System.lineSeparator());
    }
}
