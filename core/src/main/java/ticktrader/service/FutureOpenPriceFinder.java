package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class FutureOpenPriceFinder {
    private String baseFolder;

    public FutureOpenPriceFinder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    //TODO: collect this to file
    public Optional<Tick> find(LocalDate date, String symbol, String contract) {
        //2014_01_02
        String fileName = date.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        Path path = Paths.get(baseFolder + File.separator + date.getYear() + File.separator + "Daily_" + fileName + ".rpt");
        try (Stream<String> stream = Files.lines(path, Charset.defaultCharset())) {
            return stream.map(line -> wrapTick(line)).
                    filter(tick -> tick != null && tick.getSymbol().equals(symbol) && tick.getContract().equals(contract)).
                    findFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Tick wrapTick(String line) {
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
            tick.setFutureType(FutureType.FUTURE);
            tick.setExPrice(0);
        }
        return tick;
    }
}
