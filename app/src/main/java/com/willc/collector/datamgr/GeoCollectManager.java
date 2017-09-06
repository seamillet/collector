/**
 * 
 */
package com.willc.collector.datamgr;

import com.willc.collector.data.collect.GeoCollector;
import com.willc.collector.data.collect.PointCollector;
import com.willc.collector.data.collect.PolyLineCollector;
import com.willc.collector.data.collect.PolygonCollector;

import java.io.IOException;

import srs.Geometry.srsGeometryType;
import srs.tools.MapControl;

/**
 * @author keqian
 * 
 */
public final class GeoCollectManager {

	private static GeoCollector mCollector = null;
	private static MapControl mMapControl = null;
	private static srsGeometryType mType = null;

	public static GeoCollector getCollector() {
		if (mCollector == null) {
			switch (mType) {
			case Point:
				mCollector = new PointCollector();
				break;
			case Polyline:
				mCollector = new PolyLineCollector();
				break;
			case Polygon:
				mCollector = new PolygonCollector();
				break;
			default:
				break;
			}
		}
		mCollector.setMapControl(mMapControl);
		return mCollector;
	}

	/**
	 * 设置对当前操作MapControl对象的引用
	 * 
	 * @param mapControl
	 *            当前操作的MapControl实例
	 */
	public static void setMapControl(MapControl mapControl) {
		mMapControl = mapControl;
	}

	/**
	 * 设置当前采集要素类型
	 * 
	 * @param geoType
	 *            当前采集要素类型
	 */
	public static void setGeometryType(srsGeometryType geoType) {
		mType = geoType;
	}

	/**
	 * 清空显示，并释放资源
	 * 
	 * @throws IOException
	 */
	public static void dispose() throws IOException {
		// 清空Elements,并刷新显示
		if (null != mCollector) {
			mCollector.clearElements();
			mCollector = null;
		}
		mType = null;
		mMapControl = null;
	}
}
