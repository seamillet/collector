package com.willc.collector.lib.map.event;

import com.willc.collector.lib.map.IMap;

import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */
public class LayerClearedEvent extends EventObject {
    private IMap _map;

    public LayerClearedEvent(Object source) {
        super(source);
        this._map = (IMap)source;
    }

    public IMap getMap() {
        return this._map;
    }
}
