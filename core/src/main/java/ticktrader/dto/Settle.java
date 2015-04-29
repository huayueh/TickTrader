package ticktrader.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class Settle {
    private final LocalDate date;
    private final String contract;
    private final double price;

    public Settle(LocalDate date, String contract, double price) {
        this.date = date;
        this.contract = contract;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getContract() {
        return contract;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append(date).
                append(contract).
                append(price).build();
    }
}
