package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;
import ticktrader.strategy.Strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Author: huayueh
 * Date: 2015/4/17
 */
public class FutureTickService extends AbstractTickService {

    public FutureTickService(String baseFolder, int year, Strategy ob) {
        super(baseFolder, year, ob);
    }

    @Override
    protected Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        String date = ary[0].trim();
        if (ary.length > 5 && NumberUtils.isDigits(date)) {
            String symbol = ary[1].trim();
            String contract = ary[2].trim();
            String time = ary[3].trim();
            time = time.substring(0, 6);
            String price = ary[4].trim();
            String qty = ary[5].trim();
            LocalDateTime ltime = LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            tick = new Tick();
            tick.setTime(ltime);
            tick.setPrice(NumberUtils.toDouble(price));

            // FIMTX 2007, MTF 2008
            if (symbol.equals("FIMTX") || symbol.equals("MXF")) {
                symbol = "MTX";
            }

            // FITX 2007, TXF 2008
            if (symbol.equals("FITX") || symbol.equals("TXF")) {
                symbol = "TX";
            }

            tick.setSymbol(symbol);
            tick.setContract(contract);
            tick.setQty(NumberUtils.toInt(qty));
            //for option
            tick.setFutureType(FutureType.FUTURE);
            tick.setExPrice(0);
        }
        return tick;
    }
}

