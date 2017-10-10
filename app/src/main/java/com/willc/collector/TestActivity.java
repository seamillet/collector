package com.willc.collector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.willc.collector.interoperation.CollectInteroperator;
import com.willc.collector.interoperation.acticity.CollectActivity;
import com.willc.collector.interoperation.acticity.ShearActivity;
import com.willc.collector.interoperation.event.OnCollectBackListener;
import com.willc.collector.interoperation.event.OnCollectSaveListener;
import com.willc.collector.interoperation.event.OnEditBackListener;
import com.willc.collector.interoperation.event.OnEditSaveListener;
import com.willc.collector.interoperation.event.OnShearBackListener;
import com.willc.collector.interoperation.event.OnShearSaveListener;

import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import srs.CoordinateSystem.ProjCSType;
import srs.Geometry.Envelope;
import srs.Geometry.IGeometry;
import srs.Geometry.srsGeometryType;
import srs.Layer.FeatureLayer;
import srs.Layer.IFeatureLayer;
import srs.Layer.IRasterLayer;
import srs.Layer.RasterLayer;
import srs.Map.IMap;
import srs.Map.Map;
import srs.tools.MapControl;

public class TestActivity extends Activity {
    private final CharSequence[] items = {"Point", "Line", "Polygon"};
    private List<IGeometry> mGeometrys = null;

    private LinearLayout actionNew = null;
    private LinearLayout actionEdit = null;
    private LinearLayout actionShear = null;
    private MapControl mapControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);

        mGeometrys = new ArrayList<IGeometry>();
        mapControl = (MapControl) findViewById(R.id.map_main);
        actionNew = (LinearLayout) findViewById(R.id.action_new);
        actionEdit = (LinearLayout) findViewById(R.id.action_edit);
        actionShear = (LinearLayout)findViewById(R.id.action_shear);
        // 新建单击
        actionNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                generateDialog("新建").show();
            }
        });
        // 编辑
        actionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CollectInteroperator.init(LoadMapTest(), ((IFeatureLayer) LoadMapTest().GetLayer(1))
                            .getFeatureClass().getGeometry(1));
                    CollectInteroperator.CollectEventManager
                            .setOnEditBackListener(new OnEditBackListener() {
                                @Override
                                public boolean editBack(EventObject event) {
                                    return true;
                                }
                            });
                    CollectInteroperator.CollectEventManager
                            .setOnEditSaveListener(new OnEditSaveListener() {
                                @Override
                                public boolean editSave(EventObject event) {
                                    return true;
                                }
                            });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent intent = new Intent(TestActivity.this,
                        CollectActivity.class);
                intent.putExtra("obtainArea",true);
                startActivity(intent);
                //generateDialog("编辑").show();
            }
        });
        // 剪切单击
        actionShear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IMap map = LoadMapTest();
                    mGeometrys.add(((IFeatureLayer) map.GetLayer(1))
                            .getFeatureClass().getGeometry(1));
                    CollectInteroperator.init(map, mGeometrys);
                    CollectInteroperator.CollectEventManager
                            .setOnShearBackListener(new OnShearBackListener() {
                                @Override
                                public boolean shearBack(EventObject event) {
                                    // TODO Auto-generated method stub
                                    return true;
                                }
                            });

                    CollectInteroperator.CollectEventManager
                            .setOnShearSaveListener(new OnShearSaveListener() {
                                @Override
                                public boolean shearSave(EventObject event,double[] area) {
                                    return true;
                                }
                            });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent intent = new Intent(TestActivity.this,
                        ShearActivity.class);
                startActivity(intent);
            }
        });

        try {
            mapControl.setMap(LoadMapTest());
            mapControl.Refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private AlertDialog generateDialog(final CharSequence title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                srsGeometryType type = null;
                switch (which) {
                    case 0:
                        type = srsGeometryType.Point;
                        break;
                    case 1:
                        type = srsGeometryType.Polyline;
                        break;
                    case 2:
                        type = srsGeometryType.Polygon;
                        break;
                    default:
                        break;
                }
                if (title.equals("新建")) {
                    try {
                        CollectInteroperator.init(LoadMapTest(), type,"几何计算");
                        CollectInteroperator.CollectEventManager
                                .setOnCollectBackListener(new OnCollectBackListener() {
                                    @Override
                                    public boolean collectBack(EventObject event) {
                                        // TODO Auto-generated method stub
                                        return true;
                                    }
                                });
                        CollectInteroperator.CollectEventManager
                                .setOnCollectSaveListener(new OnCollectSaveListener() {
                                    @Override
                                    public boolean collectSave(EventObject event,double area) {
                                        // TODO Auto-generated method stub
                                        return true;
                                    }
                                });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(TestActivity.this,
                            CollectActivity.class);
                    intent.putExtra("obtainArea",true);
                    startActivity(intent);
                } /*else if (title.equals("编辑")) {

                }*/
            }
        });
        return builder.create();
    }

    /**
     * 测试用 加载测试数据
     *
     * @throws Exception
     */
    public Map LoadMapTest() throws Exception {
//        Map map = new Map(new Envelope(0, 0, mapControl.getWidth(),
//                mapControl.getHeight()));
        Map map = new Map(new Envelope(0, 0, 100D, 100D));
        String tifPath = "";
        String shpPath = "";
        // 根据文件，读取影像数据 xian.tif
        tifPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/FlightTarget/廊坊.tif";
        Log.d("Main_Activity", tifPath);
        // /TestData/IMAGE/长葛10村.tif /test/辉县市/IMAGE/01.tif  /storage/emulated/0/FlightTarget/廊坊.tif
        File tifFile = new File(tifPath);
        if (tifFile.exists()) {
            IRasterLayer layer = new RasterLayer(tifPath);
            if (layer != null) {
                map.AddLayer(layer);
            }
        }

        // 根据文件，加载shp矢量图层
        shpPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/FlightTarget/目标.shp";
        Log.d("Main_Activity", shpPath);
        // /TestData/Data/调查村.shp /test/辉县市/TASK/村边界.shp
        File shpFile = new File(shpPath);
        if (shpFile.exists()) {
            IFeatureLayer layer = new FeatureLayer(shpPath);
            if (layer != null) {
                map.AddLayer(layer);
            }
        }
        map.setExtent(((IFeatureLayer)map.GetLayer(0)).getFeatureClass().getGeometry(1).Extent());
        map.PartialRefresh();
        map.setGeoProjectType(ProjCSType.ProjCS_WGS1984_Albers_BJ);
        return map;
    }

}
