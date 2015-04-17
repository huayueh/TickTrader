package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;
import ticktrader.util.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Author: huayueh
 * Date: 2015/4/17
 */
public abstract class AbstractFutureTickService extends Observable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FutureTickService.class);
    protected Observer observer;
    protected long start;
    protected long end;
    protected String baseFolder = "E:\\Tick\\Future_Tick\\";

    public AbstractFutureTickService(long start, long end, Observer ob) {
        this.start = start;
        this.end = end;
        observer = ob;
    }

    public void onTick(final Tick tick) {
        logger.info("{}", tick);
        setChanged();
        notifyObservers(tick);
    }


    protected Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        //交易日期,商品代號,交割年月,成交時間,成交價格,成交數量(B+S)
        if (ary.length > 5 && !line.startsWith("交易日期")) {
            String date = ary[0].trim();
            String product = ary[1].trim();
            String contract = ary[2].trim();
            String time = ary[3].trim();
            time = time.substring(0, 6);
            String price = ary[4].trim();
            String qty = ary[5].trim();
            long ltime = Utils.formatTimeStamp(date + time);

            tick = new Tick();
            tick.setTime(ltime);
            tick.setPrice(NumberUtils.toDouble(price));
            if (product.equals("FIMTX") || product.equals("MXF")) {
                product = "MTX";
            }
            tick.setProductId(product);
            tick.setContract(contract);
            tick.setQty(NumberUtils.toInt(qty));
        }
        return tick;
    }
}
