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
import com.willc.collector.lib.map.IMap;
import com.willc.collector.lib.map.Map;
import com.willc.collector.lib.view.MapView;

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

public class TestActivity extends Activity implements View.OnClickListener{
    private static final String TAG = TestActivity.class.getSimpleName();

    private final CharSequence[] items = {"Point", "Line", "Polygon"};
    private List<IGeometry> mGeometrys = null;

    private IMap map = null;

    private LinearLayout actionNew = null;
    private LinearLayout actionEdit = null;
    private LinearLayout actionShear = null;
    private MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);  //inflate views

        mGeometrys = new ArrayList<IGeometry>();
        mapView = (MapView) findViewById(R.id.map_view);
        actionNew = (LinearLayout) findViewById(R.id.action_new);
        actionEdit = (LinearLayout) findViewById(R.id.action_edit);
        actionShear = (LinearLayout)findViewById(R.id.action_shear);

        actionNew.setOnClickListener(this);  // 新建单击
        actionEdit.setOnClickListener(this);  // 编辑
        actionShear.setOnClickListener(this); // 剪切单击

        try {
            mapView.setMap(loadMap());
            //mapView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_new:
                actionNew();
                break;
            case R.id.action_edit:
                actionEdit();
                break;
            case R.id.action_shear:
                actionShear();
                break;
            default:
                break;
        }
    }

    private void actionNew() {
        try {
            CollectInteroperator.init(loadMap(), srsGeometryType.Polygon);
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
        Intent intent = new Intent(TestActivity.this, CollectActivity.class);
        intent.putExtra("obtainArea",true);
        startActivity(intent);
    }

    private void actionEdit() {
        try {
            CollectInteroperator.init(loadMap(), ((IFeatureLayer) loadMap().GetLayer(1)).getFeatureClass().getGeometry(1));
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
        Intent intent = new Intent(TestActivity.this, CollectActivity.class);
        intent.putExtra("obtainArea",true);
        startActivity(intent);
    }

    private void actionShear() {
        try {
            mGeometrys.add(((IFeatureLayer) loadMap().GetLayer(1)).getFeatureClass().getGeometry(1));
            CollectInteroperator.init(loadMap(), mGeometrys);
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
        Intent intent = new Intent(TestActivity.this, ShearActivity.class);
        startActivity(intent);
    }

    /**
     * 测试用 加载测试数据
     *
     * @throws Exception
     */
    public IMap loadMap() throws Exception {
        if (this.map == null) {
            this.map = new Map(new Envelope(0, 0, 100D, 100D));

            // 加载影像文件数据 /TestData/IMAGE/长葛10村.tif /test/辉县市/IMAGE/01.tif  /storage/emulated/0/FlightTarget/廊坊.tif
            /*final String tifPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/collector/长葛10村.tif";
            Log.i(TAG, tifPath);

            File tifFile = new File(tifPath);
            if (tifFile.exists()) {
                IRasterLayer layer = new RasterLayer(tifPath);
                if (layer != null) {
                    this.map.AddLayer(layer);
                }
            }*/

            // 加载shp矢量文件数据 /TestData/Data/调查村.shp /test/辉县市/TASK/村边界.shp
            /*final String shpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/collector/Data/调查村.shp";
            Log.d(TAG, shpPath);

            File shpFile = new File(shpPath);
            if (shpFile.exists()) {
                IFeatureLayer layer = new FeatureLayer(shpPath);
                if (layer != null) {
                    this.map.AddLayer(layer);
                }
            }

            this.map.setExtent(((IFeatureLayer) map.GetLayer(0)).getFeatureClass().getGeometry(1).Extent());
            this.map.setGeoProjectType(ProjCSType.ProjCS_WGS1984_Albers_BJ);*/
        }
        return this.map;
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
                        //CollectInteroperator.init(LoadMapTest(), type,"几何计算");
                        CollectInteroperator.init(loadMap(), type);
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
                }
            }
        });
        return builder.create();
    }
}
