package com.willc.collector.lib.tools;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by stg on 17/10/15.
 */

public abstract class BaseTool extends BaseCommand implements ITool {
    protected float mRate = 1.0F;

    public BaseTool() {
    }

    protected void setRate() {
        int sdkId = Integer.valueOf(Build.VERSION.SDK).intValue();
        if(sdkId >= 15) {
            this.mRate = 1.0F;
        } else if(sdkId >= 12) {
            this.mRate = 1.0F;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void drawAgain() {

    }

    @Override
    public void saveResault() {

    }
}

