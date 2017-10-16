package com.willc.collector.lib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.willc.collector.lib.map.ActiveView;
import com.willc.collector.lib.map.IActiveView;
import com.willc.collector.lib.map.IMap;
import com.willc.collector.lib.map.Map;
import com.willc.collector.lib.map.event.ContentChangedListener;
import com.willc.collector.lib.tools.ITool;
import com.willc.collector.lib.tools.ZoomPan;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import srs.Geometry.Envelope;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.Layer.IElementContainer;
import srs.Layer.IGPSContainer;
import srs.Layer.TileLayer;
import srs.Layer.wmts.ImageDownLoader;

/**
 * Created by stg on 17/10/14.
 */
public class MapView extends BaseControl implements ContentChangedListener {
    private static final String TAG = MapView.class.getSimpleName();

    private IActiveView mActiveView;
    private ITool mZoomPan = null;
    //private ITool mGPSTool = null;
    private ITool mDrawTool = null;

    private DisplayMetrics displayMetrics;
    private int densityDpi;
    private int mwidthold = 0;
    private int mheightold = 0;

    private ProgressBar mProgressBar;
    //private TextView mTVRules;
    private Handler myHandler;

    private Paint mPaint = new Paint();
    public Bitmap mBitScreen = null;

    public boolean misFirst = true;
    private boolean IsDrawTrack = false;
    private IEnvelope menv = null;


    public MapView(Context context) {
        super(context);
        this.init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    @SuppressLint({"HandlerLeak"})
    private void init() {
        this.mActiveView = new ActiveView();
        this.mZoomPan = new ZoomPan();
        this.mZoomPan.setBuddyControl(this);

        this.displayMetrics = this.getResources().getDisplayMetrics();
        this.densityDpi = this.displayMetrics.densityDpi;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.mProgressBar = new ProgressBar(this.getContext());
        this.addView(this.mProgressBar, params);

        this.myHandler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    super.handleMessage(msg);
                    switch(msg.arg1) {
                        case 0:
                        case 5:
                        case 1:
                        case 2:
                            MapView.this.drawTrack();
                            ImageDownLoader.cancelTask();
                            ImageDownLoader.StopThread();
                            MapView.this.mProgressBar.setVisibility(View.GONE);
                            Log.i(TAG, "handleMessage, msg=" + msg.arg1);
                            Log.i(TAG, "MapView刷新完成, 进度条消失");
                            break;
                        case 3:
                            Log.i(TAG, "handleMessage, msg=" + msg.arg1+" and drawTrackLayer()");
                            MapView.this.drawTrackLayer();
                            break;
                        case 4:

                            /*String e = msg.getData().getString("KEY");
                            Log.i("LEVEL-ROW-COLUMN", "MSG = 4:MapControl.myHandler 绘制瓦片：" + e);
                            if(e != null) {
                                Log.i("LEVEL-ROW-COLUMN", "MapControl.myHandler 绘制瓦片：" + e);
                                MapView.this.DrawTileImage(e, MapView.this.myHandler);
                                MapView.this.drawTrackLayer();
                            }*/

                            if(TileLayer.IsDrawnEnd()) {
                                Log.i(TAG, "handleMessage, msg=4 and drawTrackLayer()");
                                MapView.this.drawTrackLayer();
                                Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + "绘制瓦片已经绘制完成，绘制下一层map.drawLayer");
                                ++Map.INDEXDRAWLAYER;
                                MapView.this.mActiveView.FocusMap().drawLayer(MapView.this.myHandler);
                            }
                            break;
                        case 6:
                            Log.i(TAG, "handleMessage, msg=6 and drawTrackLayer()");
                            MapView.this.drawTrackLayer();
                            Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + "已经绘制完成，绘制下一层map.drawLayer");
                            ++Map.INDEXDRAWLAYER;
                            MapView.this.mActiveView.FocusMap().drawLayer(MapView.this.myHandler);
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "MapView myHandler handleMessage()" + ex.getMessage());
                }
            }
        };
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, String.format("onFinishInflate(). the width is %s, the height is %s", this.getWidth(), this.getHeight()));

        this.mActiveView.FocusMap(new Map(new Envelope(-180.0D, -90.0D, 90.0D, 180.0D)));
        this.mActiveView.getContentChanged().addListener(this);
        System.gc();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;
        Log.i(TAG, String.format("onLayout(). the width is %s, the height is %s", width, height));

        if(this.misFirst && changed && this.mwidthold != width && this.mheightold != height) {
            this.mwidthold = width;
            this.mheightold = height;
            Log.i("MapControl。onLayout", "width:" + this.mwidthold + ";height:" + this.mheightold + ";");
            IMap mMap = this.mActiveView.FocusMap();
            mMap.setDeviceExtent(new Envelope(0.0D, 0.0D, (double)this.mwidthold, (double)this.mheightold));
            this.misFirst = false;
            this.refresh();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, String.format("onMeasure(). the widthMeasureSpec is %s, the heightMeasureSpec is %s", widthMeasureSpec, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw(). 重画屏幕");

        /*try {
            if(this.mZoomPan != null && ((ZoomPan)this.mZoomPan).isMAGNIFY()) {
                super.onDraw(canvas);
                ((ZoomPan)this.mZoomPan).drawMagnify(canvas);
                return;
            }

            if(this.IsDrawTrack) {
                if(this.mBitScreen == null) {
                    this.mBitScreen = this.mActiveView.FocusMap().ExportMap(false).copy(Bitmap.Config.RGB_565, true);
                    Log.d("mBitScreen", "" + this.mBitScreen);
                }

                canvas.drawBitmap(this.mBitScreen, 0.0F, 0.0F, this.mPaint);
                this.IsDrawTrack = false;
                if(this.mBitScreen != null && !this.mBitScreen.isRecycled()) {
                    this.mBitScreen = null;
                    Log.d("mBitScreen", "" + this.mBitScreen);
                }
            }
        } catch (Exception var3) {
            System.out.println("终于抓到你了！");
            var3.printStackTrace();
        }*/
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.mDrawTool != null && this.mDrawTool.getEnable().booleanValue()) {
            boolean end = this.mDrawTool.onTouch(v, event);
            if(end) {
                return end;
            }
        }

        if(this.mZoomPan != null) {
            return this.mZoomPan.onTouch(v, event);
        } else {
            return true;
        }
    }

    public IActiveView getActiveView() {
        return this.mActiveView;
    }

    public void setActiveView(IActiveView value) {
        if(!this.mActiveView.equals(value)) {
            this.mActiveView = value;
            this.mActiveView.getContentChanged().addListener(this);
        }
    }

    public IMap getMap() {
        return this.mActiveView.FocusMap();
    }

    public void setMap(IMap value) {
        if(value != null && this.mActiveView.FocusMap() != value) {
            try {
                this.mActiveView.FocusMap().dispose();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            this.mActiveView.FocusMap(value);
            this.misFirst = true;

            try {
                ((ZoomPan)this.mZoomPan).dispose();
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            this.mZoomPan = new ZoomPan();
            this.mZoomPan.setBuddyControl(this);
        }
    }

    public ITool getDrawTool() {
        return this.mDrawTool != null?this.mDrawTool:null;
    }

    public void setDrawTool(ITool value) {
        if(value != null) {
            this.mDrawTool = value;
            this.mDrawTool.setBuddyControl(this);
            this.mDrawTool.setEnable(Boolean.valueOf(true));
        } else {
            this.mDrawTool = null;
        }
    }

    public void ClearDrawTool() {
        this.mDrawTool = null;
    }

    public Bitmap getBitmap() {
        return this.mActiveView.FocusMap().ExportMap(false);
    }

    public IElementContainer getElementContainer() {
        return this.mActiveView.FocusMap().getElementContainer();
    }

    public void refresh() {
        try {
            /*this.setDrawingCacheEnabled(true);

            try {
                if(!this.misFirst && this.mActiveView.FocusMap().getHasWMTSBUTTOM()) {
                    this.mBitScreen = this.getDrawingCache().copy(Bitmap.Config.RGB_565, false);
                    Log.i("RECYCLE", "通过getDrawingCache获取了控件的截图，并copy后赋值给mBitScreen" + this.mBitScreen);
                }
                this.setDrawingCacheEnabled(false);
            } catch (Exception var3) {
                Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 490" + var3.getMessage());
            }*/
            Log.i(TAG, "refresh(). MapView全刷新");
            if(this.mBitScreen != null && !this.mBitScreen.isRecycled() && this.mActiveView.FocusMap().getHasWMTSBUTTOM()) {
                this.mActiveView.FocusMap().Refresh(this.myHandler, this.mBitScreen);
            } else {
                this.mActiveView.FocusMap().Refresh(this.myHandler, null);
            }
        } catch (InterruptedException var4) {
            Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 507 InterruptedException" + var4.getMessage());
            var4.printStackTrace();
        } catch (Exception var5) {
            Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 510" + var5.getMessage());
            Message message = new Message();
            message.arg1 = 2;
            this.myHandler.sendMessage(message);
        }
    }

    public void partialRefresh() {
        try {
            Log.i(TAG, "partialRefresh(). MapView部分刷新");
            this.mActiveView.FocusMap().PartialRefresh();
            this.drawTrack();
        } catch (Exception var2) {
            var2.printStackTrace();
            System.out.println(var2.getMessage());
        }
    }

    public void drawTrackLayer() {
        BitmapDrawable bd = new BitmapDrawable(this.getResources(), this.mActiveView.FocusMap().ExportMapLayer());
        this.setBackgroundDrawable(bd);
        Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + " 绘制过程中,将部分图层缓存绘于屏幕 MapView.drawTrackLayer");
    }

    public void drawTrack() {
        Bitmap bmp = this.mActiveView.FocusMap().ExportMap(false).copy(Bitmap.Config.RGB_565, true);
        BitmapDrawable bd = new BitmapDrawable(this.getResources(), bmp);
        this.setBackgroundDrawable(bd);
        bmp = null;
        Log.i(TAG, "地图刷新完成,将画布底图绘于屏幕 MapView.drawTrack");
    }

    /*public void drawTrack(Bitmap bit) {
        if(bit != null && bit != this.mActiveView.FocusMap().ExportMap(false)) {
            BitmapDrawable bd = new BitmapDrawable(this.getResources(), bit);
            this.setBackgroundDrawable(bd);
            bit = null;
        }
    }*/

    public IPoint ToWorldPoint(PointF point) {
        return this.mActiveView.FocusMap().ToMapPoint(point);
    }

    public PointF FromWorldPoint(IPoint point) {
        return this.mActiveView.FocusMap().FromMapPoint(point);
    }

    public double FromWorldDistance(double worldDistance) {
        return this.mActiveView.FocusMap().FromMapDistance(worldDistance);
    }

    public double ToWorldDistance(double deviceDistance) {
        return this.mActiveView.FocusMap().ToMapDistance(deviceDistance);
    }

    private IEnvelope getAllSelectEnvelope(List<IEnvelope> envs) {
        this.menv = this.getActiveView().FocusMap().getExtent();
        if(envs != null && envs.size() != 0) {
            Iterator itenvs = envs.iterator();
            if(itenvs.hasNext()) {
                this.menv = (IEnvelope)itenvs.next();
            }

            double minx = this.menv.XMin();
            double miny = this.menv.YMin();
            double maxx = this.menv.XMax();
            double maxy = this.menv.YMax();

            while(itenvs.hasNext()) {
                IEnvelope env = (IEnvelope)itenvs.next();
                if(env.XMin() < minx) {
                    minx = env.XMin();
                }

                if(env.YMin() < miny) {
                    miny = env.YMin();
                }

                if(env.XMax() > maxx) {
                    maxx = env.XMax();
                }

                if(env.YMax() > maxy) {
                    maxy = env.YMax();
                }
            }

            this.menv = new Envelope(minx - (maxx - minx) * 0.1D, miny - (maxy - miny) * 0.1D, maxx + (maxx - minx) * 0.1D, maxy + (maxy - miny) * 0.1D);
            return this.menv;
        } else {
            return this.menv;
        }
    }

    public void dispose() throws Exception {
        if(this.mBitScreen != null && !this.mBitScreen.isRecycled()) {
            this.mBitScreen.recycle();
            this.mBitScreen = null;
        }

        this.mBitScreen = null;
        this.myHandler = null;
        this.mProgressBar = null;
        this.mPaint = null;
        //this.menv = null;
        //this.fLayer = null;
        //this.mfieldID = null;
        this.mActiveView.dispose();
        this.mActiveView = null;
        //this.mTVRules = null;
        ((ZoomPan)this.mZoomPan).dispose();
        this.mZoomPan = null;
        //this.mGPSTool = null;
        this.mDrawTool = null;
    }

    public void doEvent(EventObject event) {
        int width = this.getWidth();
        int height = this.getHeight();
        Log.i(TAG, String.format("ContentChangedListener doEvent().  The width is %s and the height is %s .", width, height));

        if(width != 0 && height != 0) {
            this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, (double)width, (double)height));
        } else {
            this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, 60.0D, 60.0D));
        }
    }

    public IGPSContainer getGPSContainer() {
        return this.mActiveView.FocusMap().getGPSContainer();
    }

    public void StopDraw() {
        ImageDownLoader.cancelTask();
    }

    public void EditRefresh() {
    }

    public void Copy(BaseControl targetControl) {
        if(targetControl.getActiveView().FocusMap() == null) {
            targetControl.getActiveView().FocusMap(this.mActiveView.FocusMap());
        }

        if(!targetControl.getActiveView().equals(this.mActiveView)) {
            this.mActiveView = targetControl.getActiveView();
            this.mActiveView.getContentChanged().addListener(this);
        }

        this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, (double)this.getWidth(), (double)this.getHeight()));
    }
}
