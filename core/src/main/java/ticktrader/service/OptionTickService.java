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
 * Date: 2015/4/24
 */
public class OptionTickService extends AbstractTickService {

    public OptionTickService(String baseFolder, int year, Strategy ob) {
        super(baseFolder, year, ob);
    }

    @Override
    protected Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        String date = ary[0].trim();
        //交易日期,商品代號,履約價格,到期月份(週別),買賣權別,成交時間,成交價格,成交數量(B or S),開盤集合競價
        //交易日期,商品代號,履約價格,到期月份(週別),買賣權別,成交時間,成交價格,成交數量(B or S)
        if (ary.length >= 8 && NumberUtils.isDigits(date)) {
            String symbol = ary[1].trim();
            String exPrice = ary[2].trim();
            String contract = ary[3].trim();
            String putCall = ary[4].trim();
            FutureType pc = "P".equals(putCall)? FutureType.PUT: FutureType.CALL;
            String time = ary[5].trim();
            time = time.substring(0, 6);
            String price = ary[6].trim();
            String qty = ary[7].trim();
            LocalDateTime ltime = LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            tick = new Tick();
            tick.setTime(ltime);
            tick.setPrice(NumberUtils.toDouble(price));
            tick.setSymbol(symbol);
            tick.setContract(contract);
            tick.setExPrice(NumberUtils.toInt(exPrice));
            tick.setFutureType(pc);
            tick.setQty(NumberUtils.toInt(qty));
        }
        return tick;
    }
}
