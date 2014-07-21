package ticktrader.storage;

import ticktrader.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Harvey
 * Date: 2014/7/21
 * Time: 下午 06:01
 */
public class TickCsvStorage extends AdvCsvStorage {
    ExecutorService exc = Executors.newSingleThreadExecutor();

    public TickCsvStorage(String contract, String product, TimePeriod period) {
        super(contract, product, period);
    }

    @Override
    public void save(final Tick tick) {
//            logger.debug("save " + tick);
        exc.execute(new Runnable() {
            @Override
            public void run() {
                String line = tick.toString() + LINE_SEPARATOR;
                byte[] in = line.getBytes();
                if ((tickBuffer.limit() + in.length) > BUF_SIZE) {
                    try {
                        tickBuffer.flip();
                        tickFChannel.write(tickBuffer);
                        tickBuffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                tickBuffer.put(in);
            }
        });
    }
}
