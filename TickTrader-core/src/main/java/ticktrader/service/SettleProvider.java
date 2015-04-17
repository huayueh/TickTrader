package ticktrader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Settle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ticktrader.util.Utils;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class SettleProvider {
    private static final Logger logger = LoggerFactory.getLogger(SettleProvider.class);
    private static SettleProvider instance = new SettleProvider();
    protected ConcurrentNavigableMap<Long, Settle> his = new ConcurrentSkipListMap<Long, Settle>();

    private SettleProvider() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("settle.csv");
        Scanner scan;
        String line;

        try {
            scan = new Scanner(is);
            int idx = 0;
            while (scan.hasNext()) {
                line = scan.next();
                String[] ary = StringUtils.split(line, ",");
                //Date,contract,price
                if (ary.length == 3) {
                    String date = ary[0].trim();
                    String contract = ary[1].trim();
                    String price = ary[2].trim();
                    date = StringUtils.replace(date, "/", "-");
                    Date time = Utils.formatDate(date);
                    Settle settle = new Settle(contract, NumberUtils.toDouble(price));
                    his.put(time.getTime(), settle);
                    logger.debug("{}", settle);
                }
            }
            logger.info("settle provider is ready");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public static SettleProvider getInstance() {
        return instance;
    }

    /**
     * 差一天避免拉高壓低結算
     * */
    public String currentContract(long time){
        long key = his.ceilingKey(time);
        Settle settle = his.get(key);
        return settle.getContract();
    }
}