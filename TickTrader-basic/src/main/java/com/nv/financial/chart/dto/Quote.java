package com.nv.financial.chart.dto;

import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.util.Utils;

import java.io.Serializable;

/**
 *
 */
public class Quote implements Serializable {
    private String contract;
    private String product;
    private boolean isNew;
	private long time;
	private double open, high, low, close;
    private double avgPrice;
    private double amplitude;
    private int tickCnt;
	private long volume;
    private TimePeriod period;

    /**
     * @deprecated only for kryo deserialize
     */
    public Quote() {
        tickCnt = 1;
    }

    /**
	 * This constructor used to create a new real time quote whose open, high, low, shutdown values are
	 * the same as the last completed bar.
	 */
	public Quote(long time) {
        this.time = time;
	}

	/**
	 * This constructor used to create a new real time quote
	 */
	public Quote(long time, double open, double high, double low,
                 double close, long volume) {
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public double getClose() {
		return close;
	}

	public long getTime() {
		return time;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getMidpoint() {
		return (low + high) / 2;
	}

    public double getDifpoint() {
        return high - low;
    }

	public double getOpen() {
		return open;
	}

	public long getVolume() {
		return volume;
	}

    public double getAmplitude() {
        amplitude = (high-low)/low;
        return amplitude;
    }

	public void setClose(double close) {
		this.close = close;
	}

//	public void setTime(long time) {
//		this.time = time;
//	}

	public void setHigh(double high) {
		this.high = high;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getContract() {
        return this.contract;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return this.product;
    }

    @Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

        sb.append(contract).append(",");
        sb.append(product).append(",");
        if(period != null)
            sb.append(period.name()).append(",");
        sb.append(Utils.formatTimeStamp(time)).append(",");
        sb.append(open).append(",");
        sb.append(high).append(",");
        sb.append(low).append(",");
        sb.append(close).append(",");
        sb.append(volume).append(",");
        sb.append(getAmplitude());

		return sb.toString();
	}

    public TimePeriod getPeriod() {
        return period;
    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getTickCnt() {
        return tickCnt;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void cntAvgPrice(double avgPrice) {
        this.avgPrice = (this.avgPrice*tickCnt+avgPrice)/(tickCnt+1);
        this.tickCnt++;
    }
}
