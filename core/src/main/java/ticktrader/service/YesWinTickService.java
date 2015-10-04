package ticktrader.service;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;
import ticktrader.dto.Contract;
import ticktrader.recorder.PrintPositionRecorder;
import ticktrader.strategy.PrintStrategy;
import ticktrader.strategy.Strategy;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class YesWinTickService extends AbstractTickService {
    private static final Logger logger = LoggerFactory.getLogger(YesWinTickService.class);
    private static final String SERVICE = "YES";
    private static final String TOPIC = "DQ";
    final CountDownLatch eventDisconnect = new CountDownLatch(1);
    private DDEClientConversation conversation;

    public YesWinTickService(Strategy ob) {
        super(ob);
        conversation = new DDEClientConversation();
        conversation.setEventListener(new DDEClientEventListener() {
            public void onDisconnect() {
                logger.debug("onDisconnect()");
                eventDisconnect.countDown();
            }

            public void onItemChanged(String topic, String item, String data) {
                logger.debug("onItemChanged(" + topic + "," + item + "," + data + ")");
                Tick tick = wrapTick(item + "," + data);
                onTick(tick);
            }
        });
    }

    @Override
    public void run() {
        try {
            logger.debug("Connecting...");
            conversation.connect(SERVICE, TOPIC);

            logger.debug("Waiting event...");
            eventDisconnect.await();

            logger.debug("Disconnecting...");
            conversation.disconnect();
            logger.debug("Exit from thread");
        } catch (DDEMLException e) {
            logger.error("DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " " + e.getMessage());
        } catch (DDEException e) {
            logger.error("DDEClientException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
    }

    @Override
    protected Tick wrapTick(String line) {
        Tick tick = null;
        String[] ary = StringUtils.split(line, ",");
        //TXO09900Q5.Price,107.0
        if (ary.length == 2){
            String item = ary[0];
            String symbol = item.substring(0, 3);
            String exPrice = item.substring(3, item.indexOf(".")-2);
            String pc = item.substring(item.indexOf(".")-2, item.indexOf("."));
            FutureType futureType = "E5".equals(pc)? FutureType.PUT: FutureType.CALL;

            tick = new Tick();
            tick.setSymbol(symbol);
            tick.setExPrice(NumberUtils.toInt(exPrice));
            tick.setTime(LocalDateTime.now());
            tick.setPrice(NumberUtils.toDouble(ary[1]));
            tick.setFutureType(futureType);
            tick.setContract("");
        }

        return tick;
    }

    @Override
    public void addContract(Contract contract) {
        super.addContract(contract);
        String pc = FutureType.PUT.equals(contract.getFutureType()) ? "Q5" : "E5";
        String exPrice = (contract.getExPrice() < 10000)?"0"+ contract.getExPrice():""+ contract.getExPrice();
        String item = contract.getSymbol() + exPrice + pc + ".Price";
        try {
            conversation.startAdvice(item);
        } catch (DDEException e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void removeContract(Contract contract) {
        super.removeContract(contract);
        String pc = FutureType.PUT.equals(contract.getFutureType()) ? "Q5" : "E5";
        String exPrice = (contract.getExPrice() < 10000)?"0"+ contract.getExPrice():""+ contract.getExPrice();
        String item = contract.getSymbol() + exPrice + pc + ".Price";
        try {
            conversation.stopAdvice(item);
        } catch (DDEException e) {
            logger.error("{}", e);
        }
    }

    public static void main(String arg[]) throws InterruptedException {
        TickService tickService = new YesWinTickService(new PrintStrategy(new PrintPositionRecorder()));
        new Thread(tickService).start();
        TimeUnit.SECONDS.sleep(2);
        tickService.addContract(new Contract("TXO", "", 10000, FutureType.PUT));
        tickService.addContract(new Contract("TXO", "", 10000, FutureType.CALL));
        tickService.addContract(new Contract("TXO", "", 9900, FutureType.PUT));
        tickService.addContract(new Contract("TXO", "", 9900, FutureType.CALL));
        tickService.addContract(new Contract("TXO", "", 9800, FutureType.PUT));
        tickService.addContract(new Contract("TXO", "", 9800, FutureType.CALL));
    }
}
