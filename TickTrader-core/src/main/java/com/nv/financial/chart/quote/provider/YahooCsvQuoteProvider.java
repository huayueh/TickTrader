package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.util.Utils;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;

/**
 * For Validate indicator.
 */
public class YahooCsvQuoteProvider implements IQuoteProvider {
    private static final Logger logger = LogManager.getLogger(YahooCsvQuoteProvider.class);
    private List<Quote> his = new ArrayList<Quote>();
    private Map<Long, Integer> hisIdx = new HashMap<Long, Integer>();
    private String product = "";

    public YahooCsvQuoteProvider(String path) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        Scanner scan;
        String line;
        product = StringUtils.replace(path,".csv","");

        try {
            scan = new Scanner(is);
            int idx = 0;
            while (scan.hasNext()) {
                line = scan.next();
                String[] ary = StringUtils.split(line, ",");
                //Date,Open,High,Low,Close,Volume,Adj Close
                if (ary.length == 7 && !line.startsWith("Date")) {
                    String date = ary[0].trim();
                    String open = ary[1].trim();
                    String high = ary[2].trim();
                    String low = ary[3].trim();
                    String close = ary[4].trim();
                    String vol = ary[5].trim();
                    String adjClose = ary[6].trim();
                    Date time = Utils.formatDate(date);
                    Quote quote = new Quote(time.getTime());
                    quote.setOpen(NumberUtils.toDouble(open));
                    quote.setHigh(NumberUtils.toDouble(high));
                    quote.setLow(NumberUtils.toDouble(low));
                    quote.setClose(NumberUtils.toDouble(close));
                    quote.setVolume(NumberUtils.toLong(vol));
                    his.add(quote);
                    hisIdx.put(time.getTime(), idx);
                    idx++;
//                    logger.debug(quote);
                }
            }
            logger.debug("parse success");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Override
    public Quote getQuote(long time) {
        int idx = hisIdx.get(time);
        return his.get(idx);
    }

    @Override
    public Quote latestQuote() {
        return his.get(his.size()-1);
    }

    @Override
    public List<Quote> getQuotes(long fromTime, long toTime) {
        int idxStart = hisIdx.get(fromTime);
        int idxEnd = hisIdx.get(toTime);
        List<Quote> list = his.subList(idxStart, idxEnd);
        Collections.reverse(list);
        return list;
    }

    @Override
    public List<Quote> getQuotesBefore(int nQuote, long toTime) {
        List<Quote> ret = new ArrayList<Quote>();
        int idx = hisIdx.get(toTime);
        try {
            //all data
            if (Integer.MAX_VALUE == nQuote) {
                while (idx < his.size()) {
                    ret.add(his.get(idx));
                    idx++;
                }
            }else{
                //partial data
                for (int i = 0; i < nQuote; i++) {
                    ret.add(his.get(idx + i));
                }
            }
            Collections.reverse(ret);
        } catch (Exception ex) {
            logger.error("Can't get " + nQuote + " quotes before " + toTime);
        }

        return ret;
    }

    @Override
    public List<Quote> getQuotesAfter(int nQuote, long fromTime) {
        List<Quote> ret = new ArrayList<Quote>();
        int idx = hisIdx.get(fromTime);
        try {
            //all data
            if (Integer.MAX_VALUE == nQuote) {
                while (idx > 0) {
                    ret.add(his.get(idx));
                    idx--;
                }
            }else{
                //partial data
                for (int i = 0; i < nQuote; i++) {
                    ret.add(his.get(idx - i));
                }
            }
            Collections.reverse(ret);
        } catch (Exception ex) {
            logger.error("Can't get " + nQuote + " quotes after " + fromTime);
        }

        return ret;
    }

    @Override
    public List<Long> getTimesBefore(int nTime, long toTime) {
        List<Long> ret = new ArrayList<Long>();
        int idx = hisIdx.get(toTime);

        for (int i = 0; i < nTime; i++) {
            ret.add(his.get(idx + i).getTime());
        }
        Collections.reverse(ret);
        return ret;
    }

    @Override
    public List<Long> getTimesAfter(int nTime, long fromTime) {
        List<Long> ret = new ArrayList<Long>();

        List<Quote> qtList = getQuotesAfter(nTime, fromTime);
        for (Quote qt : qtList){
            ret.add(qt.getTime());
        }

        return ret;
    }

    @Override
    public String getProductName() {
        return product;
    }

    @Override
    public String getContract() {
        return "Yahoo Finance";
    }

    public TimePeriod getTimePeriod() {
        return TimePeriod.DAY;
    }

    @Override
    public long latestQuoteTime() {
        return 0;
    }

    public long getNextTime(long time) {
        int idx = hisIdx.get(time);
        idx--;
        Quote quote = his.get(idx);
        return quote.getTime();
    }
}
