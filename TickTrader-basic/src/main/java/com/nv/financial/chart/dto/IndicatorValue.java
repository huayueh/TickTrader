package com.nv.financial.chart.dto;

import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class IndicatorValue implements Serializable {
    private String contract;
    private String product;
    private String idcId;//name with parameter ex: EMA_5
    private TimePeriod period;
    private long time;
    private List<String> name;
    private List<Double> value;

    public IndicatorValue(long time){
        this.time = time;
        name = new ArrayList<String>();
        value = new ArrayList<Double>();
    }

    public IndicatorValue(){
        name = new ArrayList<String>();
        value = new ArrayList<Double>();
    }

    public void put(String name, double value){
        this.name.add(name);
        this.value.add(value);
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return this.time;
    }

    public double getValue(int idx){
        if(idx > name.size() || name.isEmpty())
            return 0;
        return value.get(idx);
    }

    public String getName(int idx){
        if(idx > name.size())
            return "";
        return name.get(idx);
    }

    public String[] getName(){
        final String ary[] = new String[name.size()];
        int idx = 0;
        for(String str : name){
            ary[idx] = str;
            idx++;
        }
        return ary;
    }

    public double[] getValue(){
        final double ary[] = new double[value.size()];
        int idx = 0;
        for(double val : value){
            ary[idx] = val;
            idx++;
        }
        return ary;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();

        sb.append("time=").append(Utils.formatTimeStamp(time)).append(",");
        if(period != null)
            sb.append(period.name()).append(",");
        for(int idx = 0; idx < name.size(); idx++){
            sb.append(name.get(idx)+"=").append(value.get(idx)).append(",");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }

//    public String toJSONString(){
//        final StringBuilder sb = new StringBuilder();
//        sb.append("{").append(toString()).append("}");
//        return sb.toString();
//    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    public TimePeriod getPeriod() {
        return period;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getIdcId() {
        return idcId;
    }

    public void setIdcId(String idcId) {
        this.idcId = idcId;
    }
}
