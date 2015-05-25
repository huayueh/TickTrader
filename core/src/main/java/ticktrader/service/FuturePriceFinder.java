package ticktrader.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class FuturePriceFinder {
    private Map<Key, Tick> openMap = new ConcurrentHashMap<>();
    private Map<Key, Tick> closeMap = new ConcurrentHashMap<>();

    public enum Type{
        OPEN,
        CLOSE
    }

    private class Key {
        private final LocalDate date;
        private final String symbol;

        private Key(LocalDate date, String symbol) {
            this.date = date;
            this.symbol = symbol;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                    // if deriving: appendSuper(super.hashCode()).
                    append(symbol).
                    append(date).
                    toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (!(obj instanceof Key))
                return false;

            Key key = (Key) obj;
            return new EqualsBuilder().
                    append(date, key.date).
                    append(symbol, key.symbol).
                    build();
        }
    }

    public FuturePriceFinder(Path openPath, Path closePath) {
        // open tick file
        try (Stream<String> stream = Files.lines(openPath, Charset.defaultCharset())) {
            stream.map(line -> wrapTick(line)).
                    filter(tick -> tick != null).
                    forEach(t -> openMap.put(new Key(t.getTime().toLocalDate(), t.getSymbol()), t));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close tick file
        try (Stream<String> stream = Files.lines(closePath, Charset.defaultCharset())) {
            stream.map(line -> wrapTick(line)).
                    filter(tick -> tick != null).
                    forEach(t -> closeMap.put(new Key(t.getTime().toLocalDate(), t.getSymbol()), t));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Tick> find(LocalDate date, String symbol, Type type) {
        Optional<Tick> ret = Optional.empty();
        if (date == null || symbol == null)
            return ret;
        switch (type){
            case OPEN:
                ret = Optional.of(openMap.get(new Key(date, symbol)));
                break;
            case CLOSE:
                ret = Optional.of(closeMap.get(new Key(date, symbol)));
                break;
            default:
                break;
        }
        return ret;
    }

    private Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        //2014-01-02T13:44:59,TX,201401,8618.0,2
        if (ary.length > 4) {
            String time = ary[0].trim();
            String symbol = ary[1].trim();
            String contract = ary[2].trim();
            String price = ary[3].trim();
            String qty = ary[4].trim();
            LocalDateTime ltime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            tick = new Tick();
            tick.setTime(ltime);
            tick.setPrice(NumberUtils.toDouble(price));
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
