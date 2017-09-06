/**
 * 
 */
package com.willc.collector.tools;

import com.willc.collector.datamgr.GeoCollectManager;
import com.willc.collector.datamgr.GeoSplitManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import srs.DataSource.Table.IRecord;
import srs.DataSource.Table.ITable;
import srs.DataSource.Table.Record;
import srs.DataSource.Vector.Feature;
import srs.DataSource.Vector.IFeature;
import srs.DataSource.Vector.IFeatureClass;
import srs.Geometry.IGeometry;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IPolyline;
import srs.Layer.IFeatureLayer;
import srs.Operation.IFeatureEdit;

/**
 * 淇濆瓨瑕佺礌閲囬泦缁撴灉
 * 
 * @author keqian
 */
public class SaveGeometry {

	/**
	 * 瑕佺礌鏂板缓銆佺紪杈戜繚瀛橈紙shp鏍煎紡)
	 * 
	 * @param layer
	 *            FeatureLayer
	 * @param id
	 *            瑕佺礌鐨凢ID锛屽鏋滄柊寤鸿涓�-1
	 * @return true锛屼繚瀛樻垚鍔燂紱false,淇濆瓨澶辫触
	 * @throws Exception
	 */
	public static boolean saveAsShp(IFeatureLayer layer, int id)
			throws Exception {
		IGeometry geo = GeoCollectManager.getCollector().getGeometry();
		IFeatureClass fClass = layer.getFeatureClass();
		if (fClass.getGeometryType() != geo.GeometryType()) {
			throw new Exception("鐭㈤噺鍥惧眰绫诲瀷涓庨噰闆嗚绱犵被鍨嬩笉涓�鑷达紒");
		} else {
			((IFeatureEdit) fClass).StartEdit();
			try {
				// id绛変簬-1琛ㄧず鏂板缓
				if (id == -1) {
					IRecord newRecord = new Record(
							((ITable) fClass).getFields());
					newRecord.setFID(fClass.getFeatureCount());
					IFeature newFeature = new Feature();
					newFeature.setFID(fClass.getFeatureCount());
					newFeature.setRecord(newRecord);
					newFeature.setGeometry(geo);
					fClass.AddFeature(new IFeature[] { newFeature });
				} else {
					IFeature modifiedFeature = fClass.getFeature(id);
					modifiedFeature.setGeometry(geo);
					fClass.ModifyFeature(new IFeature[] { modifiedFeature });
				}
				((IFeatureEdit) fClass).SaveEdit();
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
			return true;
		}
	}

	/**
	 * 瑕佺礌鏂板缓銆佺紪杈戜繚瀛橈紙WKT鏍煎紡)
	 * 
	 * @return 瑕佺礌WKT瀛楃涓�;鑻ラ噰闆嗘垨鑰呯紪杈戝悗鐨凣eometry瀵硅薄涓篘ull,鍒欒繑鍥�""绌哄瓧绗︿覆
	 * @throws IOException
	 */
	public static String saveAsWKT() throws IOException {
		String resultString = "";
		byte[] bytes = null;

		IGeometry geo = GeoCollectManager.getCollector().getGeometry();
		if (null != geo) {
			switch (geo.GeometryType()) {
			case Point:
				bytes = srs.Geometry.FormatConvert.PointToWKB((IPoint) geo);
				break;
			case Polyline:
				bytes = srs.Geometry.FormatConvert
						.PolylineToWKB((IPolyline) geo);
				break;
			case Polygon:
				bytes = srs.Geometry.FormatConvert.PolygonToWKB((IPolygon) geo);
				break;
			default:
				break;
			}
		}
		if (null != bytes) {
			resultString = org.gdal.ogr.Geometry.CreateFromWkb(bytes)
					.ExportToWkt();
		}
		return resultString;
	}

	/**
	 * 瑕佺礌鏂板缓銆佺紪杈戜繚瀛橈紙shp鏍煎紡)
	 * 
	 * @param layer
	 *            FeatureLayer
	 * @param id
	 *            瑕佺礌鐨凢ID锛屽鏋滄柊寤鸿涓�-1
	 * @return true锛屼繚瀛樻垚鍔燂紱false,淇濆瓨澶辫触
	 * @throws Exception
	 */
	static boolean saveShearedGeosAsShp(IFeatureLayer layer, int id)
			throws Exception {
		IGeometry geo = GeoCollectManager.getCollector().getGeometry();
		IFeatureClass fClass = layer.getFeatureClass();
		if (fClass.getGeometryType() != geo.GeometryType()) {
			throw new Exception("鐭㈤噺鍥惧眰绫诲瀷涓庨噰闆嗚绱犵被鍨嬩笉涓�鑷达紒");
		} else {
			((IFeatureEdit) fClass).StartEdit();
			try {
				// id绛変簬-1琛ㄧず鏂板缓
				if (id == -1) {
					IRecord newRecord = new Record(
							((ITable) fClass).getFields());
					newRecord.setFID(fClass.getFeatureCount());
					IFeature newFeature = new Feature();
					newFeature.setFID(fClass.getFeatureCount());
					newFeature.setRecord(newRecord);
					newFeature.setGeometry(geo);
					fClass.AddFeature(new IFeature[] { newFeature });
				} else {
					IFeature modifiedFeature = fClass.getFeature(id);
					modifiedFeature.setGeometry(geo);
					fClass.ModifyFeature(new IFeature[] { modifiedFeature });
				}
				((IFeatureEdit) fClass).SaveEdit();
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
			return true;
		}
	}

	/**
	 * 瑕佺礌鍓淇濆瓨锛圵KT鏍煎紡)
	 * 
	 * @return 瑕佺礌鍓垏鍚嶹KT瀛楃涓查泦鍚堬紝鑻ラ噰闆嗘垨鑰呯紪杈戝悗鐨凣eometry瀵硅薄涓篘ull,鍒欒繑鍥�""绌哄瓧绗︿覆
	 * @throws IOException
	 */
	public static List<String> saveShearedGeosAsWKT() throws IOException {
		List<String> resultWKTs = new ArrayList<String>();
		List<IGeometry> geos = GeoSplitManager.Instance()
				.getShearedGeometries();
		for (IGeometry geo : geos) {
			String resultString = "";
			byte[] bytes = null;
			if (null != geo) {
				switch (geo.GeometryType()) {
				case Point:
					bytes = srs.Geometry.FormatConvert.PointToWKB((IPoint) geo);
					break;
				case Polyline:
					bytes = srs.Geometry.FormatConvert
							.PolylineToWKB((IPolyline) geo);
					break;
				case Polygon:
					bytes = srs.Geometry.FormatConvert
							.PolygonToWKB((IPolygon) geo);
					break;
				default:
					break;
				}
			}
			if (null != bytes) {
				resultString = org.gdal.ogr.Geometry.CreateFromWkb(bytes)
						.ExportToWkt();
			}
			resultWKTs.add(resultString);
		}
		return resultWKTs;
	}
}
