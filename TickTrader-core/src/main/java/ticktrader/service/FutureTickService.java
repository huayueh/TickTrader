package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ticktrader.dto.PutOrCall;
import ticktrader.dto.Tick;
import ticktrader.strategy.Strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Author: huayueh
 * Date: 2015/4/17
 */
public class FutureTickService extends AbstractTickService {

    public FutureTickService(String baseFolder, Strategy strategy) {
        super(baseFolder, strategy);
    }

    @Override
    protected Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        //交易日期,商品代號,到期月份(週別),成交時間,成交價格,成交數量(B+S),近月價格,遠月價格,開盤集合競價
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
            if (symbol.equals("FIMTX") || symbol.equals("MXF")) {
                symbol = "MTX";
            }
            tick.setSymbol(symbol);
            tick.setContract(contract);
            tick.setQty(NumberUtils.toInt(qty));
            //for option
            tick.setPutOrCall(PutOrCall.NONE);
            tick.setExPrice(0);
        }
        return tick;
    }
}

