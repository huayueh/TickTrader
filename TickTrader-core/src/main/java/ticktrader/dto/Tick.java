package ticktrader.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private PutOrCall putOrCall = PutOrCall.NONE;

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

    public void setPutOrCall(PutOrCall putOrCall) {
        this.putOrCall = putOrCall;
    }

    public int getExPrice() {
        return exPrice;
    }

    public PutOrCall getPutOrCall() {
        return putOrCall;
    }

//    public Topic getTopic(){
//        return new Topic(symbol, contract, exPrice, putOrCall);
//    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append(localDateTime).
                append(symbol).
                append(contract).
                append(price).
                append(qty);
        if (!putOrCall.equals(PutOrCall.NONE)){
            builder.append(putOrCall).append(exPrice);
        }
        return builder.build();
    }
}
