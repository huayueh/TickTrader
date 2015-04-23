package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Settle;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class SettleProvider {
    private static final Logger logger = LoggerFactory.getLogger(SettleProvider.class);
    private static SettleProvider instance = new SettleProvider();
    protected ConcurrentNavigableMap<LocalDate, Settle> his = new ConcurrentSkipListMap<>();

    private SettleProvider() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("settle.csv");
        Scanner scan;
        String line;

        try {
            scan = new Scanner(is);

            while (scan.hasNext()) {
                line = scan.next();
                String[] ary = StringUtils.split(line, ",");
                //Date,contract,price
                if (ary.length == 3) {
                    String date = ary[0].trim();
                    String contract = ary[1].trim();
                    String price = ary[2].trim();
                    LocalDate ldate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/M/d"));
                    //remove weekly contract
                    if (!contract.contains("W")){
                        Settle settle = new Settle(ldate, contract, NumberUtils.toDouble(price));
                        his.put(ldate, settle);
                        logger.debug("{}", settle);
                    }
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

    public String currentContract(LocalDate time){
        LocalDate key = his.ceilingKey(time);
        Settle settle = his.get(key);
        return settle.getContract();
    }
}
