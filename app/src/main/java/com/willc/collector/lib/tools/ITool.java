package com.willc.collector.lib.tools;

import android.view.View;

/**
 * Created by stg on 17/10/15.
 */

public interface ITool extends ICommand,View.OnTouchListener {
    void drawAgain();

    void saveResault();
}