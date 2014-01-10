package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Settle;
import com.nv.financial.chart.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * User: Harvey
 * Date: 2014/1/10
 * Time: 下午 12:30
 */
public class SettleProvider {
    private static final Logger logger = LogManager.getLogger(SettleProvider.class);
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
                    logger.debug(settle);
                }
            }
            logger.debug("parse success");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public static SettleProvider getInstance() {
        return instance;
    }

    /**
     * 差一點避免拉高壓低結算
     * */
    public String currentContract(long time){
        long key = his.ceilingKey(time);
        Settle settle = his.get(key);
        return settle.getContract();
    }
}
