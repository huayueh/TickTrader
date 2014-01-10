package com.nv.financial.chart.service;

import com.nv.financial.chart.dto.Tick;

/**
 *
 */
public interface IMarketTickService {
    public void onTick(Tick quote);
}
