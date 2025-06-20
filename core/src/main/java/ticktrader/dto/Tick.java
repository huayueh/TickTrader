package ticktrader.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
//TODO: immutable
public class Tick {
    private double price;
    private int qty;
    private String symbol;
    private String contract;
    private LocalDateTime localDateTime;
    private int exPrice;
    private FutureType futureType = FutureType.FUTURE;

    public Tick(){}

    public Tick(String symbol, String contract, int exPrice, FutureType futureType, double price) {
        this.symbol = symbol;
        this.contract = contract;
        this.exPrice = exPrice;
        this.futureType = futureType;
        this.price = price;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDateTime getTime() {
        return localDateTime;
    }

    public void setTime(LocalDateTime time) {
        this.localDateTime = time;
    }

    public void setExPrice(int exPrice) {
        this.exPrice = exPrice;
    }

    public void setFutureType(FutureType futureType) {
        this.futureType = futureType;
    }

    public int getExPrice() {
        return exPrice;
    }

    public FutureType getFutureType() {
        return futureType;
    }

//    public Contract getTopic(){
//        return new Contract(symbol, contract, exPrice, putOrCall);
//    }

    public boolean samePrice(Tick t) {
        return new EqualsBuilder()
                .append(price, t.price)
                .append(symbol, t.symbol)
                .append(contract, t.contract)
                .append(exPrice, t.exPrice)
                .append(futureType, t.futureType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, qty, symbol, contract, localDateTime, exPrice, futureType);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).
                append(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).
                append(symbol).
                append(contract).
                append(price).
                append(qty).
                append(futureType);
        if (!futureType.equals(FutureType.FUTURE)){
            builder.append(exPrice);
        }
        return builder.build();
    }
}
