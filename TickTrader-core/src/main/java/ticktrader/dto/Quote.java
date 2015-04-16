package ticktrader.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ticktrader.util.Utils;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class Quote {
    private String group;
    private String product;
	private long time;
    private long lastTickTS;
	private double open, high, low, close;
    private double avgPrice;
    private double custPrice;
    private int tickCnt;
    private double volume;
    private TimePeriod period;


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
        double close, int volume) {
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setTickCnt(int tickCnt) {
        this.tickCnt = tickCnt;
    }

    public long getLastTickTS() {
        return lastTickTS;
    }

    public void setLastTickTS(long lastTickTS) {
        this.lastTickTS = lastTickTS;
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

	public double getVolume() {
		return volume;
	}

    public double getCustPrice() {
        return custPrice;
    }

	public void setClose(double close) {
		this.close = close;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public synchronized void addVolume(double volume) {
		this.volume += volume;
	}

    public void setCustPrice(double custPrice) {
        this.custPrice = custPrice;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
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

        sb.append(group).append(",");
        sb.append(product).append(",");
        if(period != null)
            sb.append(period.name()).append(",");
        sb.append(Utils.formatTimeStamp(time)).append(",");
        sb.append(open).append(",");
        sb.append(high).append(",");
        sb.append(low).append(",");
        sb.append(close).append(",");
        sb.append(volume);

		return sb.toString();
	}

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(time).
                append(group).
                append(product).
                append(period).
            append(open).append(high).append(low).append(close).
                append(volume).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Quote))
            return false;

        Quote qt = (Quote) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(time, qt.time).
                append(group, qt.group).
                append(product, qt.product).
                append(period, qt.period).
                append(open, qt.open).
                append(high, qt.high).
                append(low, qt.low).
                append(close, qt.close).
                append(volume, qt.volume).
                isEquals();
    }

    public TimePeriod getPeriod() {
        return period;
    }

    public void setPeriod(TimePeriod period) {
        this.period = period;
    }

    public int getTickCnt() {
        return tickCnt;
    }

    public double getAvgPrice() {
        return avgPrice;
    }
}
