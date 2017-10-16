package com.willc.collector.lib.map.event;

import java.util.EventListener;

/**
 * Created by stg on 17/10/15.
 */

public interface LayerClearedListener extends EventListener {
    void doEvent(LayerClearedEvent var1);
}
