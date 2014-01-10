package com.nv.financial.chart.indicator;

import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.nv.financial.chart.quote.provider.YahooCsvQuoteProvider;
import com.nv.financial.chart.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class IndicatorFactory {
    private static final Logger logger = LogManager.getLogger(IndicatorFactory.class);
    private static final String PACKAGE = "com.nv.financial.chart.indicator.";

    /**
     * @param quoteProvider instance of IQuoteProvider
     * @param indicator ClassName_param1_param2_...
     * */
    public static IIndicator create(IQuoteProvider quoteProvider, String indicator){
        if(quoteProvider == null || indicator == null){
            logger.error("can't create indicator instance with null input");
            return null;
        }
        String[] ary = StringUtils.split(indicator,"_");
        String className = ary[0];
        Class[] clsAry = new Class[ary.length];
        Object[] args = new Object[ary.length];

        //parse constructor type from parameter
        clsAry[0] = IQuoteProvider.class;
        args[0] = quoteProvider;

        for (int i=1 ; i<ary.length ; i++){
            if(Utils.isDouble(ary[i])){
                args[i] = NumberUtils.toDouble(ary[i]);
                clsAry[i] = double.class;
            }else if(Utils.isInt(ary[i])){
                args[i] = NumberUtils.toInt(ary[i]);
                clsAry[i] = int.class;
            }else{
                args[i] = ary[i];
                clsAry[i] = String.class;
            }
        }

        //try to create instance
        try {
            Class cls = Class.forName(PACKAGE + className);
            Constructor constructor = cls.getConstructor(clsAry);
            return (IIndicator) constructor.newInstance(args);
        } catch (ClassNotFoundException e) {
            logger.error("", e);
        } catch (NoSuchMethodException e) {
            logger.error("", e);
        } catch (InvocationTargetException e) {
            logger.error("", e);
        } catch (InstantiationException e) {
            logger.error("", e);
        } catch (IllegalAccessException e) {
            logger.error("", e);
        }
        logger.error("can't create instance of indicator : " + indicator);
        return null;
    }

    public static void main(String arg[]){
        IIndicator idx = IndicatorFactory.create(new YahooCsvQuoteProvider("E:\\workspace\\Indicator\\twii.csv"),"EMA_15");
        logger.debug(idx);
    }
}
