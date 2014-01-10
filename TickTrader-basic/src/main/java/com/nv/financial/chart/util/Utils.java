package com.nv.financial.chart.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final Pattern IPV4_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("([0-9]*)\\.([0-9]*)");
    private static final Pattern INT_PATTERN = Pattern.compile("([0-9]*)");
    private static final String TIME_STAMP_FORMAT = "yyyyMMddHHmmss";
    public static final String INDICATOR_DELIMITER = "_";
    public static final String FILE_DELIMITER = "_";
    public static final String PRODUCT_DELIMITER = "_";
    public static final String CMD_DELIMITER = ",";

    public static String getMBeanServerId(final MBeanServer aMBeanServer) {
        String serverId = null;
        final String SERVER_DELEGATE = "JMImplementation:type=MBeanServerDelegate";
        final String MBEAN_SERVER_ID_KEY = "MBeanServerId";
        try {
            ObjectName delegateObjName = new ObjectName(SERVER_DELEGATE);
            serverId = (String) aMBeanServer.getAttribute(delegateObjName,
                    MBEAN_SERVER_ID_KEY);
        } catch (MalformedObjectNameException malformedObjectNameException) {
            System.err.println("Problems constructing MBean ObjectName: "
                    + malformedObjectNameException.getMessage());
        } catch (AttributeNotFoundException noMatchingAttrException) {
            System.err.println("Unable to find attribute " + MBEAN_SERVER_ID_KEY
                    + " in MBean " + SERVER_DELEGATE + ": "
                    + noMatchingAttrException);
        } catch (MBeanException mBeanException) {
            System.err.println("Exception thrown by MBean's (" + SERVER_DELEGATE
                    + "'s " + MBEAN_SERVER_ID_KEY + ") getter: "
                    + mBeanException.getMessage());
        } catch (ReflectionException reflectionException) {
            System.err.println("Exception thrown by MBean's (" + SERVER_DELEGATE
                    + "'s " + MBEAN_SERVER_ID_KEY + ") setter: "
                    + reflectionException.getMessage());
        } catch (InstanceNotFoundException noMBeanInstance) {
            System.err.println("No instance of MBean " + SERVER_DELEGATE
                    + " found in MBeanServer: "
                    + noMBeanInstance.getMessage());
        }
        return serverId;
    }

    public static boolean isIpAddress(String ip) {
        Matcher matcher = IPV4_PATTERN.matcher(ip);
        return matcher.matches();
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

    public static long formatTimeStamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException ex) {
            logger.error("", ex);
        }
        return 0;
    }

    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (IOException ex) {
            logger.error("", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("", ex);
        }
        return obj;
    }

    public static byte[] getBytes(Object obj) throws java.io.IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();
        return data;
    }

    public static String formatTimeStamp(long timestamp) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sim.format(new java.util.Date(timestamp));
    }

    public static boolean isDouble(String s) {
        return DOUBLE_PATTERN.matcher(s).matches();
    }

    public static boolean isInt(String s) {
        return INT_PATTERN.matcher(s).matches();
    }
}
