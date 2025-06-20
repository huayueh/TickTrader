package ticktrader.provider;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;

public class TickPriceContractProvider implements ContractProvider {
    private static final Logger logger = LoggerFactory.getLogger(SettleContractProvider.class);
    protected ConcurrentNavigableMap<LocalDate, Tick> calls = new ConcurrentSkipListMap<>();
    protected ConcurrentNavigableMap<LocalDate, Tick> puts = new ConcurrentSkipListMap<>();
    protected ConcurrentNavigableMap<LocalDate, String> lines = new ConcurrentSkipListMap<>();

    public TickPriceContractProvider(String file) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        Scanner scan;
        String line;

        try {
            scan = new Scanner(is);

            while (scan.hasNext()) {
                line = scan.next();

                if (line.startsWith("_time"))
                    continue;

                String[] ary = StringUtils.split(line, ",");
                //_time,call_contract,call_contract_price,call_high,put_contract,put_contract_price,put_high
                String date = ary[0].trim();

                String callContract = ary[1].trim();
                int callContractPrice = NumberUtils.toInt(ary[2].trim());
                double callPrice = NumberUtils.toDouble(ary[3].trim());

                String putContract = ary[4].trim();
                int putContractPrice = NumberUtils.toInt(ary[5].trim());
                double putPrice = NumberUtils.toDouble(ary[6].trim());

                LocalDate ldate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                calls.put(ldate, new Tick("TXO", callContract, callContractPrice, FutureType.CALL, callPrice));
                puts.put(ldate, new Tick("TXO", putContract, putContractPrice, FutureType.PUT, putPrice));
                lines.put(ldate, line);
            }
            logger.info("{} is ready", this.getClass().getSimpleName());
        } catch (Exception ex) {
            logger.error("", ex);
        }

    }

    public Tick getPutTick(LocalDate day) {
        return puts.get(day);
    }

    public Tick getCallTick(LocalDate day) {
        return calls.get(day);
    }

    @Override
    public String closestContract(LocalDate time) {
        return lines.get(time);
    }
}
