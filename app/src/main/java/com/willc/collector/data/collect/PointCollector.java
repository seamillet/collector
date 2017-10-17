/**
 *
 */
package com.willc.collector.data.collect;

import android.content.Context;
import android.graphics.Canvas;

import com.willc.collector.settings.DrawPaintStyles;
import com.willc.collector.settings.ElementStyles;

import srs.CoordinateSystem.ProjCSType;
import srs.Element.IPointElement;
import srs.Element.PointElement;
import srs.GPS.GPSConvert;
import srs.Geometry.IGeometry;
import srs.Geometry.IPoint;
import srs.Geometry.Point;

/**
 * @author keqian 点要素采集
 */
public class PointCollector extends GeoCollector {

    public PointCollector() {
        super();
    }

    @Override
    public void addPoint(IPoint point) {
        historyGeos.push(getGeometry());
        historyActions.push("ADD");
        mPoints.add(point);
        currentPointIndex++;
    }

    @Override
    public void updatePoint(IPoint point) {
        historyGeos.push(getGeometry());
        historyActions.push("UPDATE");
        mPoints.remove(currentPointIndex);
        mPoints.add(point);
    }

    @Override
    public void undo() throws Exception {
        if (historyGeos.size() > 0) {
            IPoint point = (IPoint) historyGeos.pop();
            if (historyActions.pop() == "ADD") {
                mPoints.remove(currentPointIndex);
                currentPointIndex--;
            } else {
                mPoints.remove(currentPointIndex);
                mPoints.add(point);
            }
            refresh();
        }
    }

    @Override
    public void clear() throws Exception {
        if (mPoints != null && mPoints.size() > 0) {
            historyActions.push("CLEAR");
            historyGeos.push(getGeometry());

            mPoints.clear();
            currentPointIndex = -1;
            refresh();
        }
    }

    @Override
    public void delpt() throws Exception {
        if (mPoints != null && mPoints.size() > 0) {
            historyActions.push("DELETE");
            historyGeos.push(getGeometry());

            mPoints.remove(currentPointIndex);
            currentPointIndex--;
        }
        refresh();
    }

    @Override
    public boolean isPointFocused(double x, double y) {
        double limits = mMapView.getMap()
                .getScreenDisplay().ToMapDistance(range);
        boolean flag = false;
        if (mPoints.size() > 0) {
            IPoint currF = mPoints.get(currentPointIndex);
            if (x > currF.X() - limits && x < currF.X() + limits
                    && y > currF.Y() - limits && y < currF.Y() + limits) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void drawPointsOnCanvas(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, 10, DrawPaintStyles.PointFocusedPaint);
    }

    @Override
    public IGeometry getGeometry() {
        if (mPoints.size() > 0) {
            return mPoints.get(currentPointIndex);
        } else {
            return null;
        }
    }

    @Override
    public void refresh() throws Exception {
        mMap.getElementContainer().ClearElement();

        IGeometry geo = getGeometry();
        if (geo != null) {
            IPointElement element = new PointElement();
            element.setSymbol(ElementStyles.FocusedPointStyle);
            element.setGeometry(geo);
            mMap.getElementContainer().AddElement(element);
        }
        mMapView.PartialRefresh();
    }

    @Override
    public boolean isGeometryValid(Context context) {
        // 点有效性判断
        if (mPoints.size() < 1) {
            generateAlertDialog(context, "需要有效的位置", "通过使用当前位置或单击地图来采集位置")
                    .show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double getArea() {
        return -1;
    }

    @Override
    public double getLength() {
        return -1;
    }

    @Override
    public double[] getLastSideLength() {
        return new double[0];
    }

    @Override
    public double[] getEachSideLength() {
        return new double[0];
    }

    @Override
    public double[] getEachSideAngle() {
        return new double[0];
    }

    @Override
    public double getAngle() {
        return 0;
    }

    @Override
    public IPoint getPosition() {
        if (mPoints != null && mPoints.size() > 0) {
            IPoint p = mPoints.get(mPoints.size() - 1);
            double[] xy = GPSConvert.PROJECT2GEO(p.X(), p.Y(), ProjCSType.ProjCS_WGS1984_Albers_BJ);
            IPoint pr = new Point(xy[0], xy[1]);
            return pr;
        }
        return null;
    }
}
