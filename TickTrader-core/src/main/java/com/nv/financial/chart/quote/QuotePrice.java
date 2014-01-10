package com.nv.financial.chart.quote;

import com.nv.financial.chart.dto.Quote;

/**
 */
public enum QuotePrice {
    OPEN{
        public double getPrice(Quote quote){
            return quote.getOpen();
        }
    },
    HIGH{
        public double getPrice(Quote quote){
            return quote.getHigh();
        }
    },
    LOW{
        public double getPrice(Quote quote){
            return quote.getLow();
        }
    },
    CLOSE{
        public double getPrice(Quote quote){
            return quote.getClose();
        }
    },
    TYPICAL{
        public double getPrice(Quote quote){
            double high = quote.getHigh();
            double low = quote.getLow();
            double close = quote.getClose();
            return (high + low + close)/3;
        }
    },
    HL_DIF{
        public double getPrice(Quote quote){
            return quote.getDifpoint();
        }
    },
    AVG{
        public double getPrice(Quote quote){
            return (quote.getHigh()+quote.getLow())/2;
        }
    },
    CUST_PRICE{
        public double getPrice(Quote quote){
            return quote.getAmplitude();
        }
    };

    public abstract double getPrice(Quote quote);
}
