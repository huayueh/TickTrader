package com;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * User: Harvey
 * Date: 2014/1/28
 */
public class GoThroughAllTickPerf {
    private static final Logger logger = LogManager.getLogger(GoThroughAllTickPerf.class);
    private static final Logger tag = LogManager.getLogger("Tag");

    public static void main(String arg[]){
//        String baseFolder = "E:\\NetBeansProjects\\TickTrader\\logs\\TickTrader.log.bk";
        String baseFolder = "E:\\NetBeansProjects\\TickTrader\\Tick\\MTX.csv";
        String line;
        File file = new File(baseFolder);
        LineIterator it = null;
        tag.info("start perf");
        try {
            it = FileUtils.lineIterator(file, "Big5");
            while (it.hasNext()) {
                line = it.nextLine();
//                System.out.println(line);
                logger.debug(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tag.info("end perf");
    }

}
