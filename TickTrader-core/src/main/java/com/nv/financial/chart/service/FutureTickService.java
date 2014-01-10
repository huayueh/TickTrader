package com.nv.financial.chart.service;

import com.nv.financial.chart.concurrent.JobExecutor;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.provider.IMemQuoteProvider;
import com.nv.financial.chart.quote.provider.SettleProvider;
import com.nv.financial.chart.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.Observable;

/**
 * Get tick from feed server
 * Prime observers are EventService, IndicatorService, QuoteProvider
 */
public class FutureTickService extends Observable implements IMarketTickService,Runnable{
    private static final Logger logger = LogManager.getLogger(FutureTickService.class);
    private IQuoteService quoteService;
    SettleProvider stProvider = SettleProvider.getInstance();
    private long start;
    private long end;
    private String baseFolder = "E:\\Tick\\Future_Tick\\";

    public FutureTickService(long start, long end) {
        this.start = start;
        this.end = end;
        quoteService = QuoteService.getInstance();
    }

    @Override
    public void onTick(final Tick tick) {
        logger.debug(tick);
        setChanged();
        notifyObservers(tick);
    }

    @Override
    public void run() {
        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(start);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(end);

        while (start < end){
            //Daily_2013_01_02.rpt
            int year = calStart.get(Calendar.YEAR);
            String month = "" + (calStart.get(Calendar.MONTH)+1);
            String day = "" + calStart.get(Calendar.DAY_OF_MONTH);
            String path = baseFolder + year + File.separator;
            month = (month.length()==1)?"0"+month:month;
            day = (day.length()==1)?"0"+day:day;
            String fileName = "Daily_" + year + "_" + month + "_" + day + ".rpt";
            path += fileName;
            final File file = new File(path);
            if (file.exists()){
                JobExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        parse(file);
                    }
                });
                logger.info(file.getName());
            }
            calStart.add(Calendar.DATE,1);
            start = calStart.getTimeInMillis();
        }
        logger.info("done");
    }

    private void parse(File file){
        String line;
        LineIterator it = null;

        try {
            it = FileUtils.lineIterator(file,"Big5");
            while(it.hasNext()){
                line = it.nextLine();
                String[] ary = StringUtils.split(line, ",");
                //交易日期,商品代號,交割年月,成交時間,成交價格,成交數量(B+S)
                if (ary.length > 5 && !line.startsWith("交易日期")) {
                    String date = ary[0].trim();
                    String product = ary[1].trim();
                    String contract = ary[2].trim();
                    String time = ary[3].trim();
                    time = time.substring(0,6);
                    String price = ary[4].trim();
                    String qty = ary[5].trim();
                    long ltime = Utils.formatTimeStamp(date + time);

                    Tick tick = new Tick();
                    tick.setTime(ltime);
                    tick.setPrice(NumberUtils.toDouble(price));
                    if(product.equals("FIMTX") || product.equals("MXF")){
                        product = "MTX";
                    }
                    tick.setProductId(product);
                    tick.setContract(contract);
                    tick.setQty(NumberUtils.toInt(qty));

                    if (!product.equals("MTX")){
                        continue;
                    }

                    if (!quoteService.isExist(contract, product)){
                        quoteService.creatQuoteProvider(contract, product);
                        for(IMemQuoteProvider provider : quoteService.getAllMemProvider()){
                            this.addObserver(provider);
                        }
                    }
                    if (stProvider.currentContract(ltime).equals(contract)){
                        onTick(tick);
                    }
                }
            }
            logger.info(file.getName() + " finish");
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            LineIterator.closeQuietly(it);
        }
    }
}
