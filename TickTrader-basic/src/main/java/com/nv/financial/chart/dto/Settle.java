package com.nv.financial.chart.dto;

import com.nv.financial.chart.util.Utils;

/**
 * User: Harvey
 * Date: 2014/1/10
 * Time: 下午 12:35
 */
public class Settle {
    private String contract;
    private double price;

    public Settle(String contract, double price) {
        this.contract = contract;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(contract).append(",");
        sb.append(price);
        return sb.toString();
    }
}
