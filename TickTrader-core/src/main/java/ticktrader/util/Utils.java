package ticktrader.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd-HH:mm:ss";
    public static final String FILE_DELIMITER = "_";

    public static long formatTimeStamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException ex) {
            logger.error("", ex);
        }
        return 0;
    }

    public static String formatTimeStamp(long timestamp) {
        SimpleDateFormat sim = new SimpleDateFormat(TIME_STAMP_FORMAT);
        return sim.format(new java.util.Date(timestamp));
    }

    public static Date formatDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            logger.error("", ex);
        }
        return null;
    }
}
