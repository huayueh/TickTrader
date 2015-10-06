package ticktrader.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public class Position {
    private final Order order;
    private final LocalDateTime openTime;
    private LocalDateTime closeTime;
    private double closePrice;
    private double pnl;
    private double netPnl;

    public Position(Order order, LocalDateTime openTime) {
        this.order = order;
        this.openTime = openTime;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getNetPnl() {
        return netPnl;
    }

    public void setNetPnl(double pnl) {
        this.netPnl = pnl;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        ToStringBuilder builder =  new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append(openTime).
                append(closePrice).
                append(closeTime);
        return builder.build();
    }

    public void fillAllQuantity(double price, LocalDateTime time) {
        closeTime = time;
        closePrice = price;
    }

}
