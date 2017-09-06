/**
 * 
 */
package com.willc.collector.tools;

import com.willc.collector.datamgr.GeoCollectManager;
import com.willc.collector.datamgr.GeoSplitManager;

import srs.CoordinateSystem.ProjCSType;
import srs.GPS.GPSControl;
import srs.GPS.GPSConvert;
import srs.Geometry.IPoint;
import srs.Geometry.Point;

/**
 * @author keqian
 * 
 */
public class GPSUtil
{
	public static void addPointForCollecting(ProjCSType projType) throws Exception {
		GPSControl mGPSControl = GPSControl.getInstance();
		double[] xy = GPSConvert.GEO2PROJECT(mGPSControl.GPSLongitude,
				mGPSControl.GPSLatitude, projType);
		IPoint p = new Point(xy[0], xy[1]);
		GeoCollectManager.getCollector().addPoint(p);
		GeoCollectManager.getCollector().refresh();
	}
	
	public static void addPointForSplitting(ProjCSType projType) throws Exception {
		GPSControl mGPSControl = GPSControl.getInstance();
		double[] xy = GPSConvert.GEO2PROJECT(mGPSControl.GPSLongitude,
				mGPSControl.GPSLatitude, projType);
		IPoint p = new Point(xy[0], xy[1]);
		GeoSplitManager.Instance().addPoint(p);
		GeoSplitManager.Instance().refresh();
	}
}
