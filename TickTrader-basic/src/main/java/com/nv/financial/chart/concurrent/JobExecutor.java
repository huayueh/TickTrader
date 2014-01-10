package com.nv.financial.chart.concurrent;

import com.nv.financial.chart.ChartSetting;

import java.util.concurrent.*;

/**
 * Through thread pool control thread number.
 */
public class JobExecutor {
    private static ExecutorService executor;
    private static ThreadFactory threadFactory = new NamedThreadFactory("ChartService");

    static {
        executor = Executors.newFixedThreadPool(ChartSetting.getWorkerNum(),threadFactory);
//        executor = Executors.newFixedThreadPool(ChartApiSetting.getWorkerNum());
    }

    public static void execute(Runnable runnable){
        executor.execute(runnable);
    }

    public static Future submit(Callable task){
        return executor.submit(task);
    }

    public static void close(){
        executor.shutdown();
    }
}
