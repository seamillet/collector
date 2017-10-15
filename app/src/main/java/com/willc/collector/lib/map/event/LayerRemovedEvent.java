package com.willc.collector.lib.map.event;

import com.willc.collector.lib.map.IMap;

import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */

public class LayerRemovedEvent extends EventObject {
    private IMap _Map;
    private LayerEventArgs _LayerEventArgs;

    public LayerRemovedEvent(Object source, LayerEventArgs e) {
        super(source);
        this._Map = (IMap)source;
        this._LayerEventArgs = e;
    }

    public IMap getMap() {
        return this._Map;
    }

    public LayerEventArgs getLayerEventArgs() {
        return this._LayerEventArgs;
    }
}
