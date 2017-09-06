package com.willc.collector.interoperation.acticity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.willc.collector.R;
import com.willc.collector.datamgr.GeoCollectManager;
import com.willc.collector.interoperation.CollectInteroperator;
import com.willc.collector.tools.DrawManually;
import com.willc.collector.tools.EditTools;
import com.willc.collector.tools.GPSUtil;

import java.io.IOException;
import java.util.EventObject;

import srs.Geometry.srsGeometryType;
import srs.tools.MapControl;

/**
 * 面积计算Activity
 */
public class AreaCalActivity extends Activity {
    // UI references.
    private LinearLayout actionBack = null;
    private LinearLayout actionSave = null;
    private LinearLayout actionGPS = null;
    private LinearLayout actionUndo = null;
    private LinearLayout actionDelpt = null;
    private LinearLayout actionClear = null;
    private TextView txtTitle = null;
    private MapControl mapControl = null;
    // The tool of collecting points Manually
    private DrawManually drawManually = null;
    // The geometry type of the current feature collecting
    private srsGeometryType mtype = null;
    private String mtitle = null;


    public OnPointListener p;

    private SharedPreferences shared;

    public void setOnPointListener(OnPointListener pointListener) {
        this.p = pointListener;
    }

    public interface OnPointListener {

        void pointListening(View view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        // Init and Set MapControl
        mapControl = (MapControl) findViewById(R.id.map_collect);
        mapControl.setMap(CollectInteroperator.getMap());
        mapControl.Refresh();
        GeoCollectManager.setMapControl(mapControl);

        // Set Geometry Type to GeoCollectManager
        mtype = CollectInteroperator.getGeometryType();
        GeoCollectManager.setGeometryType(mtype);

        // Edit settings
        if (!CollectInteroperator.isNew()) {
            try {
                GeoCollectManager.getCollector().setEditGeometry(
                        CollectInteroperator.getEditGeometry());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set title according to Geometry Type
        txtTitle = (TextView) findViewById(R.id.title);
        if (CollectInteroperator.getmTitle()!=null){
            txtTitle.setText(CollectInteroperator.getmTitle());
        }else {
            txtTitle.setText(getTitleDesc());
        }


        // Init DrawManually tool and Set it to BuddyControl
        if (drawManually == null) {
            drawManually = new DrawManually(this);
        }
        drawManually.OnCreate(mapControl);
        mapControl.setDrawTool(drawManually);

        // Initial action controls
        actionBack = (LinearLayout) findViewById(R.id.action_back);
        actionSave = (LinearLayout) findViewById(R.id.action_save);
        actionGPS = (LinearLayout) findViewById(R.id.action_gps);
        actionUndo = (LinearLayout) findViewById(R.id.action_undo);
        actionDelpt = (LinearLayout) findViewById(R.id.action_delpt);
        actionClear = (LinearLayout) findViewById(R.id.action_clear);

        if(AreaCalActivity.this.getIntent().getExtras()!=null)
        {
            String checkMap = AreaCalActivity.this.getIntent().getExtras().getString("check_map");
            if("check_map".equals(checkMap))
            {
                actionSave.setVisibility(View.INVISIBLE);
            }
        }

        // Set click event to them
        bindEventToActions();
    }

    public static String Area_value;

    private void bindEventToActions() {
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        actionSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeoCollectManager.getCollector().isGeometryValid(
                        AreaCalActivity.this)) {
                    boolean isSuccess = false;
                    if (CollectInteroperator.isNew()) {
                        isSuccess = CollectInteroperator.CollectEventManager
                                .fireAreaSave(new EventObject(v));
                    } else {
                        isSuccess = CollectInteroperator.CollectEventManager
                                .fireEditSave(new EventObject(v));
                    }
                    if (isSuccess) {
                        // 释放资源
                        try {
                            dispose();
                        } catch (IOException e) {
                            showToast(e.getMessage());
                        }
                        Intent intent = getIntent();

                        if(intent.getBooleanExtra("obtainArea", false))
                        {
                            shared = getSharedPreferences("Area", MODE_PRIVATE);
                            Area_value = shared.getString("AreaValue", "");
                            System.out.println(CollectInteroperator.getMCaArea() + "----------");
                            AreaCalActivity.this.setResult(601, intent);
                            finish();
                        }else
                        {
                            finish();
                        }
                    }
                }
            }
        });
        actionGPS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GPSUtil.addPointForCollecting(mapControl.getMap().getGeoProjectType());
                    drawManually.setValues();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
        actionUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditTools.undo();
                    drawManually.setValues();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
        if (mtype != srsGeometryType.Point) {
            actionDelpt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditTools.delpt();
                        drawManually.setValues();
                    } catch (Exception e) {
                        showToast(e.getMessage());
                    }
                }
            });
            actionClear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditTools.clear();
                        drawManually.setValues();
                    } catch (Exception e) {
                        showToast(e.getMessage());
                    }
                }
            });
        } else {
            actionDelpt.setVisibility(View.GONE);
            actionClear.setVisibility(View.GONE);
        }
    }

    private String getTitleDesc() {
        if (mtype != null) {
            switch (mtype) {
                case Point:
                    return "点要素采集";
                case Polyline:
                    return "线要素采集";
                case Polygon:
                    return "面要素采集";
                default:
                    return CollectInteroperator.getmTitle();
            }
        }
        return "量算";
    }

    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AreaCalActivity.this);
        builder.setTitle("是否放弃采集?");
        builder.setMessage("是否确定要放弃已采集的要素?");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 触发返回处理事件
                boolean isSuccess = false;
                if (CollectInteroperator.isNew()) {
                    isSuccess = CollectInteroperator.CollectEventManager
                            .fireCollectBack(new EventObject(
                                    AreaCalActivity.this));
                } else {
                    isSuccess = CollectInteroperator.CollectEventManager
                            .fireEditBack(new EventObject(AreaCalActivity.this));
                }
                if (isSuccess) {
                    // 回收资源
                    try {
                        dispose();
                    } catch (IOException e) {
                        showToast(e.getMessage());
                    }
                    // 关闭activity
                    finish();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 要素采集资源释放
     *
     * @throws IOException
     */
    private void dispose() throws IOException {
        mapControl.setDrawTool(null);
        drawManually = null;
        mtype = null;
        GeoCollectManager.dispose();
        CollectInteroperator.dispose();
        System.gc();
    }

    @SuppressLint("ShowToast")
    private void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }

}
