package com.nv.financial.chart;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Keep global setting of chart service.
 * Put customize configuration under "config/chartservice.properties".
 */
public class ChartSetting {
    private static final Logger logger = LogManager.getLogger(ChartSetting.class);
    private static String[] eventPub;
    private static String[] eventRep;

    private static int workerNum;
    private static int maxInMem;

    private static String[] eventSub;
    private static final int eventServerNum;
    private static final String[] eventReq;

    private static final int storageNum;
    private static final String[] storage;

    private static int heartBeatPeriod;
    private static int clientId;
    private static int serverId;

    private static final int maxActive;
    private static final int maxWait;
    private static final int recvTimeOut;

    private static final String keyspace;

    static {
        Properties prop = new Properties();

        try {
            File file = new File("config/chartservice.properties");
            if (file.exists()){
                logger.info("use setting file " + file.getAbsolutePath());
                FileInputStream fis = new FileInputStream(file);
                prop.load(fis);
            }else {
                logger.info("chartservice.properties does not exist use default setting");
                prop.load(ChartSetting.class.getClassLoader().getResourceAsStream("chartservice.properties"));
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        serverId = NumberUtils.toInt(prop.getProperty("server.id"));
        heartBeatPeriod = NumberUtils.toInt(prop.getProperty("chart.server.event.heartbeat.period", "5000"));

        workerNum = NumberUtils.toInt(prop.getProperty("chart.worker.thread.size", "1"));
        maxInMem = NumberUtils.toInt(prop.getProperty("chart.history.max.memory.size", "0"));

        eventServerNum = NumberUtils.toInt(prop.getProperty("chart.server.event.num"));
        eventReq = new String[eventServerNum];
        eventRep = new String[eventServerNum];
        eventSub = new String[eventServerNum];
        eventPub = new String[eventServerNum];

        for (int i=0 ; i<eventServerNum ; i++){
            eventReq[i] = prop.getProperty("chart.client.event.req." + i);
            eventRep[i] = prop.getProperty("chart.server.event.rep." + i);
            eventSub[i] = prop.getProperty("chart.client.event.sub." + i);
            eventPub[i] = prop.getProperty("chart.server.event.pub." + i);
        }
        clientId = NumberUtils.toInt(prop.getProperty("client.id"));

        storageNum = NumberUtils.toInt(prop.getProperty("chart.server.storage.num"));
        keyspace = prop.getProperty("chart.server.storage.name");
        storage = new String[storageNum];
        for (int i=0 ; i<storageNum ; i++){
            storage[i] = prop.getProperty("chart.server.storage." + i);
        }

        //socket pool
        maxActive = NumberUtils.toInt(prop.getProperty("chart.channel.pool.maxActive", "10"));
        maxWait = NumberUtils.toInt(prop.getProperty("chart.channel.pool.maxWait", "5000"));
        recvTimeOut = NumberUtils.toInt(prop.getProperty("chart.channel.pool.recv.timeout", "5000"));
    }

    public static int getWorkerNum() {
        return workerNum;
    }

    public static int getMaxInMem() {
        return maxInMem;
    }

    public static String getEventRep() {
        return eventRep[serverId];
    }

    public static String getEventPub() {
        return eventPub[serverId];
    }

    public static String getEventReq(int idx) {
        return eventReq[idx];
    }

    public static String getEventSub(int idx) {
        return eventSub[idx];
    }

    public static String getStorage(int idx) {
        return storage[idx];
    }

    public static int getEventServerNum() {
        return eventServerNum;
    }

    public static int getRecvTimeOut() {
        return recvTimeOut;
    }

    public static int getMaxActive() {
        return maxActive;
    }

    public static int getMaxWait() {
        return maxWait;
    }

    public static int getClientId() {
        return clientId;
    }

    public static int getServerId() {
        return serverId;
    }

    public static void setServerId(int serverId) {
        ChartSetting.serverId = serverId;
    }

    public static int getHeartBeatPeriod() {
        return heartBeatPeriod;
    }

    public static String getKeyspace() {
        return keyspace;
    }

    public static int getStorageNum() {
        return storageNum;
    }
}
