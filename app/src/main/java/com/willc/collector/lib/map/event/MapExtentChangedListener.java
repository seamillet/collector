package com.willc.collector.lib.map.event;

import java.util.EventListener;

/**
 * Created by stg on 17/10/15.
 */

public interface MapExtentChangedListener extends EventListener {
    void doEvent(MapExtentChangedEvent var1);
}
