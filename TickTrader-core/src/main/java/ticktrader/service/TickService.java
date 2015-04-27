package ticktrader.service;

import ticktrader.dto.Tick;
import ticktrader.dto.Topic;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public interface TickService extends Runnable {
    void addTopic(Topic topic);
    void removeTopic(Topic topic);
    void onTick(Tick tick);
}
