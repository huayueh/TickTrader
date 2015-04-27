package ticktrader.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public class Position {
    private final String symbol;
    private final String contract;
    private final double price;
    private final double qty;
    private final Side side;
    private final LocalDateTime openTime;
    private final PutOrCall putOrCall;
    private final int exPrice;
    private LocalDateTime closeTime;
    private double closePrice;
    private double pnl;

    public enum Side {
        Buy,
        Sell
    }

    public Position(Builder builder) {
        this.symbol = builder.symbol;
        this.contract = builder.contract;
        this.side = builder.side;
        this.price = builder.price;
        this.qty = builder.qty;
        this.openTime = builder.openTime;
        this.putOrCall = builder.putOrCall;
        this.exPrice = builder.exPrice;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getContract() {
        return contract;
    }

    public Side getSide() {
        return side;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getQty() {
        return qty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append(symbol).
                append(contract).
                append(openTime).
                append(price).
                append(qty).
                append(side.name()).
                append(closePrice).
                append(closeTime).build();
    }

    //    public void fillQuantity(double qty) {
//        qtyRemain -= qty;
//    }
//
    public void fillAllQuantity(double price, LocalDateTime time) {
        closeTime = time;
        closePrice = price;
    }

    public static class Builder {
        private String symbol;
        private String contract;
        private double price;
        private double qty;
        private Side side;
        private LocalDateTime openTime;
        private PutOrCall putOrCall = PutOrCall.NONE;
        private int exPrice;

        public Builder exercisePrice(int exPrice) {
            this.exPrice = exPrice;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder contract(String contract) {
            this.contract = contract;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder qty(double qty) {
            this.qty = qty;
            return this;
        }

        public Builder side(Side side) {
            this.side = side;
            return this;
        }

        public Builder openTime(LocalDateTime openTime) {
            this.openTime = openTime;
            return this;
        }

        public Builder putOrCall(PutOrCall putOrCall) {
            this.putOrCall = putOrCall;
            return this;
        }

        public Position build(){
            //TODO: check
            return new Position(this);
        }
    }
}
