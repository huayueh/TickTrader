package ticktrader.service;

import ticktrader.dto.Tick;
import com.nv.financial.chart.quote.provider.IMemQuoteProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Get tick from feed server
 * Prime observers are EventService, IndicatorService, QuoteProvider
 */
public class SingleFutureTickService extends FutureTickService {
    private static final Logger logger = LogManager.getLogger(SingleFutureTickService.class);
    private static final Logger tag = LogManager.getLogger("Tag");

    public SingleFutureTickService(long start, long end) {
        super(start, end);
    }

    @Override
    public void run() {
        String baseFolder = "E:\\Tick\\Future_Tick\\MTX.txt";
        String line;
        File file = new File(baseFolder);
        LineIterator it = null;
        tag.info("start perf");
        try {
            it = FileUtils.lineIterator(file, "Big5");
            while (it.hasNext()) {
                line = it.nextLine();
                Tick tick = new Tick();
                String[] ary = StringUtils.split(line, ",");
                //交易日期,商品代號,交割年月,成交時間,成交價格,成交數量(B+S)
                //2014-01-23-13:44:53,201412,MTX,8229.0,2
                if (ary.length == 5) {
                    String time = ary[0].trim();
                    String product = ary[2].trim();
                    String contract = ary[1].trim();
                    String price = ary[3].trim();
                    String qty = ary[4].trim();
                    long ltime = formatTimeStamp(time);

                    tick.setTime(ltime);
                    tick.setPrice(NumberUtils.toDouble(price));
                    tick.setProductId(product);
                    tick.setContract(contract);
                    tick.setQty(NumberUtils.toInt(qty));
                }

                if (stProvider.currentContract(tick.getTime()).equals(tick.getContract())) {
                    tick.setContract("CUR");
                    this.addObserver(quoteService);
                    onTick(tick);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tag.info("end perf");
    }

    private long formatTimeStamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException ex) {
            logger.error("", ex);
        }
        return 0;
    }
}
