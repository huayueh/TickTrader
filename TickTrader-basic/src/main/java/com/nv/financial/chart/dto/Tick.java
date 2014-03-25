package com.nv.financial.chart.dto;

import com.nv.financial.chart.util.Utils;

import java.io.Serializable;

/**
 * User: Harvey
 * Date: 2013/10/21
 * Time: 上午 11:54
 */
public class Tick implements Serializable {
    private double price;
    private int qty;
    private String productId;
    private String contract;
    private long time;

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(Utils.formatTimeStamp(time)).append(",");
        sb.append(productId).append(",");
        sb.append(contract).append(",");
        sb.append(price).append(",");
        sb.append(qty);

        return sb.toString();
    }
}
