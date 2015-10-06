package ticktrader.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Author: huayueh
 * Date: 2015/10/6
 */
public class Order {
    private final String symbol;
    private final String contract;
    private double price;
    private final double qty;
    private final Side side;
    private final FutureType futureType;
    private final int exPrice;

    public Order(Builder builder) {
        this.symbol = builder.symbol;
        this.contract = builder.contract;
        this.price = builder.price;
        this.qty = builder.qty;
        this.side = builder.side;
        this.futureType = builder.futureType;
        this.exPrice = builder.exPrice;
    }

    public enum Side {
        Buy,
        Sell
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

    public double getQty() {
        return qty;
    }

    public FutureType getFutureType() {
        return futureType;
    }

    public int getExPrice() {
        return exPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static class Builder {
        private String symbol;
        private String contract;
        private double price;
        private double qty;
        private Side side;
        private FutureType futureType = FutureType.FUTURE;
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

        public Builder putOrCall(FutureType futureType) {
            this.futureType = futureType;
            return this;
        }

        public Order build() {
            //TODO: check
            return new Order(this);
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder =  new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append(symbol).
                append(contract).
                append(price).
                append(qty).
                append(side.name());
        if (!futureType.equals(FutureType.FUTURE)){
            builder.append(futureType).append(exPrice);
        }
        return builder.build();
    }
}
