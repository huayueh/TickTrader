package ticktrader.service;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.PutOrCall;
import ticktrader.dto.Tick;
import ticktrader.dto.Topic;
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
            PutOrCall putOrCall = "E5".equals(pc)?PutOrCall.PUT:PutOrCall.CALL;

            tick = new Tick();
            tick.setSymbol(symbol);
            tick.setExPrice(NumberUtils.toInt(exPrice));
            tick.setTime(LocalDateTime.now());
            tick.setPrice(NumberUtils.toDouble(ary[1]));
            tick.setPutOrCall(putOrCall);
            tick.setContract("");
        }

        return tick;
    }

    @Override
    public void addTopic(Topic topic) {
        super.addTopic(topic);
        String pc = PutOrCall.PUT.equals(topic.getPutOrCall()) ? "Q5" : "E5";
        String exPrice = (topic.getExPrice() < 10000)?"0"+topic.getExPrice():""+topic.getExPrice();
        String item = topic.getSymbol() + exPrice + pc + ".Price";
        try {
            conversation.startAdvice(item);
        } catch (DDEException e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void removeTopic(Topic topic) {
        super.removeTopic(topic);
        String pc = PutOrCall.PUT.equals(topic.getPutOrCall()) ? "Q5" : "E5";
        String exPrice = (topic.getExPrice() < 10000)?"0"+topic.getExPrice():""+topic.getExPrice();
        String item = topic.getSymbol() + exPrice + pc + ".Price";
        try {
            conversation.stopAdvice(item);
        } catch (DDEException e) {
            logger.error("{}", e);
        }
    }

    public static void main(String arg[]) throws InterruptedException {
        TickService tickService = new YesWinTickService(new PrintStrategy());
        new Thread(tickService).start();
        TimeUnit.SECONDS.sleep(2);
        tickService.addTopic(new Topic("TXO", "", 10000, PutOrCall.PUT));
        tickService.addTopic(new Topic("TXO", "", 10000, PutOrCall.CALL));
        tickService.addTopic(new Topic("TXO", "", 9900, PutOrCall.PUT));
        tickService.addTopic(new Topic("TXO", "", 9900, PutOrCall.CALL));
        tickService.addTopic(new Topic("TXO", "", 9800, PutOrCall.PUT));
        tickService.addTopic(new Topic("TXO", "", 9800, PutOrCall.CALL));
    }
}
