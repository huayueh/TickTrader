package ticktrader.dto;


import java.util.Calendar;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public enum TimePeriod {
    THIRTY_SEC{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.SECOND)%30;
            cal.set(Calendar.SECOND,cal.get(Calendar.SECOND)+30-mod);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.SECOND)%30;
            cal.set(Calendar.SECOND,cal.get(Calendar.SECOND)-mod);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    ONE_MIN{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)+1);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    FIVE_MIN{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%5;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)+5-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%5;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    TEN_MIN{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%10;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)+10-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%10;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    FIFTEEN_MIN{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%15;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)+15-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%15;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    HALF_HOUR{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%30;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)+30-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }

        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int minMod = cal.get(Calendar.MINUTE)%30;
            cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)-minMod);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    ONE_HOUR{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)+1);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    TWO_HOUR{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%2;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)+2-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%2;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    FOUR_HOUR{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%4;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)+4-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%4;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    EIGHT_HOUR{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%8;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)+8-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int mod = cal.get(Calendar.HOUR_OF_DAY)%8;
            cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)-mod);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    DAY{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.DATE,cal.get(Calendar.DATE)+1);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    WEEK{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            boolean isSunday = cal.getFirstDayOfWeek()== Calendar.SUNDAY;
            cal.setTimeInMillis(time);
            int mod = isSunday?cal.get(Calendar.DAY_OF_WEEK)-1:cal.get(Calendar.DAY_OF_WEEK);
            cal.set(Calendar.DATE,cal.get(Calendar.DATE)+7-mod+1);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    },
    MONTH{
        public long getNextOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.DAY_OF_MONTH,1);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            cal.add(Calendar.MONTH,1);
            return cal.getTimeInMillis();
        }
        public long getStartOn(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.DAY_OF_MONTH,1);
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        }
    };

    public abstract long getNextOn(long time);
    public abstract long getStartOn(long time);

}
