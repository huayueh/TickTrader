package com.nv.financial.chart.indicator;

import com.tictactec.ta.lib.Core;

/**
 * User: Harvey
 * Date: 2013/11/12
 * Time: 下午 2:41
 */
public class TaLib {
    private static Core core = new Core();

    public static Core getCore(){
        return core;
    }
}
