package com;

import java.util.concurrent.TimeUnit;

/**
 * User: Harvey
 * Date: 2014/1/29
 * Time: 下午 12:14
 */
public class DaemonTest {
    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    System.out.println("T");
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
