package com.willc.collector.lib.map.event;

import java.util.EventListener;


/**
 * Created by stg on 17/10/15.
 */
public interface LayerRemovedListener extends EventListener {
    void doEvent(LayerRemovedEvent var1);
}
