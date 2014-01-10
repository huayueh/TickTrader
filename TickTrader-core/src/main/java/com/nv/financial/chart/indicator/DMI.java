package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.FuncUnstId;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Directional Movement Index. Base on
 * http://www.prosticks.com.hk/tech_indicators.asp?page=1
 * http://baike.baidu.com/view/189274.htm?fromId=348882
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:average_directional_index_adx
 */
public class DMI extends AbstractIndicator {
    private int[] unstablePeriod = new int[com.tictactec.ta.lib.FuncUnstId.All.ordinal()];
    public final static int DI_PLUS = 0;
    public final static int DI_MINUS = 1;
    public final static int DX_POSITION = 2;
    private static final Logger logger = LogManager.getLogger(DMI.class);
    //The weight omitted by stopping after k
    private final int k;
    private final int periodLength;

    {
        longName = "Directional Movement Index";
        shortName = "DMI";
        description = "unknow";
    }

    public DMI(IQuoteProvider quoteProvider, int periodLength) {
        super(quoteProvider);
        this.periodLength = periodLength;
//        this.k = (int)Math.ceil(3.45 * (periodLength + 1));
        this.k = Integer.MAX_VALUE;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] outPlusDI = new double[size];
        double[] outMinusDI = new double[size];
        double[] outdx = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            close[idx] = qt.getClose();
            idx++;
        }
        RetCode retCode = adx(0, size-1, high, low, close, periodLength, outBegin, outNBElement, outPlusDI, outMinusDI, outdx);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double diPlus = outPlusDI[outNBElement.value-1];
            double diMinus = outMinusDI[outNBElement.value-1];
            double dx = outdx[outNBElement.value-1];
            ret.put("DI_PLUS",diPlus);
            ret.put("DI_MINUS",diMinus);
            ret.put("DX",dx);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }

    public RetCode adx( int startIdx,
                        int endIdx,
                        double inHigh[],
                        double inLow[],
                        double inClose[],
                        int optInTimePeriod,
                        MInteger outBegIdx,
                        MInteger outNBElement,
                        double outPlusDI[],
                        double outMinusDI[],
                        double outReal[] )
    {
        int today, lookbackTotal, outIdx;
        double prevHigh, prevLow, prevClose;
        double prevMinusDM, prevPlusDM, prevTR;
        double tempReal, tempReal2, diffP, diffM;
        double minusDI = 0, plusDI = 0, sumDX, prevADX;
        int i;
        if( startIdx < 0 )
            return RetCode.OutOfRangeStartIndex ;
        if( (endIdx < 0) || (endIdx < startIdx))
            return RetCode.OutOfRangeEndIndex ;
        if( (int)optInTimePeriod == ( Integer.MIN_VALUE ) )
            optInTimePeriod = 14;
        else if( ((int)optInTimePeriod < 2) || ((int)optInTimePeriod > 100000) )
            return RetCode.BadParam ;
        lookbackTotal = (2*optInTimePeriod) + (unstablePeriod[FuncUnstId.Adx.ordinal()]) - 1;
        if( startIdx < lookbackTotal )
            startIdx = lookbackTotal;
        if( startIdx > endIdx )
        {
            outBegIdx.value = 0 ;
            outNBElement.value = 0 ;
            return RetCode.Success ;
        }
        outIdx = 0;
        outBegIdx.value = today = startIdx;
        prevMinusDM = 0.0;
        prevPlusDM = 0.0;
        prevTR = 0.0;
        today = startIdx - lookbackTotal;
        prevHigh = inHigh[today];
        prevLow = inLow[today];
        prevClose = inClose[today];
        i = optInTimePeriod-1;
        while( i-- > 0 )
        {
            today++;
            tempReal = inHigh[today];
            diffP = tempReal-prevHigh;
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = prevLow-tempReal;
            prevLow = tempReal;
            if( (diffM > 0) && (diffP < diffM) )
            {
                prevMinusDM += diffM;
            }
            else if( (diffP > 0) && (diffP > diffM) )
            {
                prevPlusDM += diffP;
            }
            { tempReal = prevHigh-prevLow; tempReal2 = Math.abs (prevHigh-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; tempReal2 = Math.abs (prevLow-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; } ;
            prevTR += tempReal;
            prevClose = inClose[today];
        }
        sumDX = 0.0;
        i = optInTimePeriod;
        while( i-- > 0 )
        {
            today++;
            tempReal = inHigh[today];
            diffP = tempReal-prevHigh;
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = prevLow-tempReal;
            prevLow = tempReal;
            prevMinusDM -= prevMinusDM/optInTimePeriod;
            prevPlusDM -= prevPlusDM/optInTimePeriod;
            if( (diffM > 0) && (diffP < diffM) )
            {
                prevMinusDM += diffM;
            }
            else if( (diffP > 0) && (diffP > diffM) )
            {
                prevPlusDM += diffP;
            }
            { tempReal = prevHigh-prevLow; tempReal2 = Math.abs (prevHigh-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; tempReal2 = Math.abs (prevLow-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; } ;
            prevTR = prevTR - (prevTR/optInTimePeriod) + tempReal;
            prevClose = inClose[today];
            if( ! (((- (0.00000000000001) )<prevTR)&&(prevTR< (0.00000000000001) )) )
            {
                minusDI = (100.0*(prevMinusDM/prevTR)) ;
                plusDI = (100.0*(prevPlusDM/prevTR)) ;
                tempReal = minusDI+plusDI;
                if( ! (((- (0.00000000000001) )<tempReal)&&(tempReal< (0.00000000000001) )) )
                    sumDX += (100.0 * ( Math.abs (minusDI-plusDI)/tempReal)) ;
            }
        }
        prevADX = (sumDX / optInTimePeriod) ;
        i = (unstablePeriod[FuncUnstId.Adx.ordinal()]) ;
        while( i-- > 0 )
        {
            today++;
            tempReal = inHigh[today];
            diffP = tempReal-prevHigh;
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = prevLow-tempReal;
            prevLow = tempReal;
            prevMinusDM -= prevMinusDM/optInTimePeriod;
            prevPlusDM -= prevPlusDM/optInTimePeriod;
            if( (diffM > 0) && (diffP < diffM) )
            {
                prevMinusDM += diffM;
            }
            else if( (diffP > 0) && (diffP > diffM) )
            {
                prevPlusDM += diffP;
            }
            { tempReal = prevHigh-prevLow; tempReal2 = Math.abs (prevHigh-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; tempReal2 = Math.abs (prevLow-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; } ;
            prevTR = prevTR - (prevTR/optInTimePeriod) + tempReal;
            prevClose = inClose[today];
            if( ! (((- (0.00000000000001) )<prevTR)&&(prevTR< (0.00000000000001) )) )
            {
                minusDI = (100.0*(prevMinusDM/prevTR)) ;
                plusDI = (100.0*(prevPlusDM/prevTR)) ;
                tempReal = minusDI+plusDI;
                if( ! (((- (0.00000000000001) )<tempReal)&&(tempReal< (0.00000000000001) )) )
                {
                    tempReal = (100.0*( Math.abs (minusDI-plusDI)/tempReal)) ;
                    prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod) ;
                }
            }
        }
        outReal[0] = prevADX;
        outIdx = 1;
        while( today < endIdx )
        {
            int idx;
            today++;
            tempReal = inHigh[today];
            diffP = tempReal-prevHigh;
            prevHigh = tempReal;
            tempReal = inLow[today];
            diffM = prevLow-tempReal;
            prevLow = tempReal;
            prevMinusDM -= prevMinusDM/optInTimePeriod;
            prevPlusDM -= prevPlusDM/optInTimePeriod;
            if( (diffM > 0) && (diffP < diffM) )
            {
                prevMinusDM += diffM;
            }
            else if( (diffP > 0) && (diffP > diffM) )
            {
                prevPlusDM += diffP;
            }
            { tempReal = prevHigh-prevLow; tempReal2 = Math.abs (prevHigh-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; tempReal2 = Math.abs (prevLow-prevClose); if( tempReal2 > tempReal ) tempReal = tempReal2; } ;
            prevTR = prevTR - (prevTR/optInTimePeriod) + tempReal;
            prevClose = inClose[today];
            if( ! (((- (0.00000000000001) )<prevTR)&&(prevTR< (0.00000000000001) )) )
            {
                minusDI = (100.0*(prevMinusDM/prevTR)) ;
                plusDI = (100.0*(prevPlusDM/prevTR)) ;
                tempReal = minusDI+plusDI;
                if( ! (((- (0.00000000000001) )<tempReal)&&(tempReal< (0.00000000000001) )) )
                {
                    tempReal = (100.0*( Math.abs (minusDI-plusDI)/tempReal)) ;
                    prevADX = (((prevADX*(optInTimePeriod-1))+tempReal)/optInTimePeriod) ;
                }
            }
            idx = outIdx++;
            outPlusDI[idx] = plusDI;
            outMinusDI[idx] = minusDI;
            outReal[idx] = tempReal;
        }
        outNBElement.value = outIdx;
        return RetCode.Success ;
    }
}
