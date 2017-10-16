package com.willc.collector.lib.map.event;

import com.willc.collector.lib.map.IMap;

import java.util.EventObject;

/**
 * Created by stg on 17/10/15.
 */

public class MapExtentChangedEvent extends EventObject {
    private IMap _Map;
    private Object _EventArgs;

    public MapExtentChangedEvent(Object source, Object e) {
        super(source);
        this._Map = (IMap)source;
        this._EventArgs = e;
    }

    public IMap getMap() {
        return this._Map;
    }

    public Object getEventArgs() {
        return this._EventArgs;
    }
}
