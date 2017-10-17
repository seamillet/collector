/**
 * 
 */
package com.willc.collector.interoperation;

import com.willc.collector.datamgr.GeoSplitManager;
import com.willc.collector.interoperation.acticity.CollectActivity;
import com.willc.collector.interoperation.event.OnAreaBackListener;
import com.willc.collector.interoperation.event.OnAreaSaveListener;
import com.willc.collector.interoperation.event.OnCollectBackListener;
import com.willc.collector.interoperation.event.OnCollectSaveListener;
import com.willc.collector.interoperation.event.OnEditBackListener;
import com.willc.collector.interoperation.event.OnEditSaveListener;
import com.willc.collector.interoperation.event.OnShearBackListener;
import com.willc.collector.interoperation.event.OnShearSaveListener;
import com.willc.collector.lib.map.IMap;

import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import srs.Geometry.IGeometry;
import srs.Geometry.srsGeometryType;
import srs.Layer.IFeatureLayer;

/**
 * @author keqian 点、线、面要素采集交互接口类，用以初始化要素采集设置
 */
public class CollectInteroperator {

	private static boolean mIsNew = true;
	public static IMap mMap = null;
	public static IGeometry mEditGeometry = null;
	private static List<IGeometry> mShearGeometries = null;
	public static srsGeometryType mGeometryType = null;
	private static double mCaArea = 0.0;

	/**
	 * 初始化设置(新建)
	 * 
	 * @param map
	 *            要素采集的map对象
	 * @param geometryType
	 *            采集要素的类型
	 */
	public static void init(IMap map, srsGeometryType geometryType) {
		mIsNew = true;
		mMap = map;
		mGeometryType = geometryType;
	}

	/**
	 * 初始化设置，几何计算
	 * @param map
	 * @param geometryType
	 * @param title
	 */
	public static void init(IMap map, srsGeometryType geometryType,String title) {
		mIsNew = false;
		mMap = map;
		mGeometryType = geometryType;
	}

	/**
	 * 初始化设置(编辑)
	 * 
	 * @param map
	 *            要素编辑的map对象
	 * @param geometry
	 *            选择编辑的geometry对象
	 */
	public static void init(IMap map, IGeometry geometry) {
		mIsNew = false;
		mMap = map;
		mEditGeometry = geometry;
		mGeometryType = geometry.GeometryType();
	}

	/**
	 * 初始化设置(裁剪),剪切后保存为shp格式
	 * 
	 * @param map
	 *            要素编辑的map对象
	 */
	public static void init(IMap map, IFeatureLayer layer, List<Integer> fids) {
		mIsNew = false;
		mMap = map;
		mGeometryType = srsGeometryType.Polygon;
		for (Integer fid : fids) {
			try {
				mShearGeometries.add(layer.getFeatureClass().getGeometry(fid));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化设置(裁剪),剪切后保存为WKT格式
	 * 
	 * @param map
	 *            要素编辑的map对象
	 * @param geometrys
	 *            选择裁剪的List<IGeometry>对象
	 */
	public static void init(IMap map, List<IGeometry> geometrys) {
		mIsNew = false;
		mMap = map;
		mShearGeometries = geometrys;
		mGeometryType = srsGeometryType.Polygon;
	}

	public static boolean isNew() {
		return mIsNew;
	}

	public static IMap getMap() {
		return mMap;
	}

	public static IGeometry getEditGeometry() {
		return mEditGeometry;
	}

	public static List<IGeometry> getShearGeometries() {
		return mShearGeometries;
	}

	public static double getMCaArea(){
		String area = CollectActivity.Area_value;
		mCaArea = Double.valueOf(area);
		return mCaArea;
	}

	public static srsGeometryType getGeometryType() {
		return mGeometryType;
	}

	public static void dispose() {
		mIsNew = true;
		mMap = null;
		mEditGeometry = null;
		mShearGeometries = null;
		mGeometryType = null;
		CollectEventManager.dispose();
	}

	/**
	 * 事件管理内部类，返回、保存操作与外界交互的接口；调用CollectActivity时，通过设置setOnCollectBackListener、
	 * setOnCollectSaveListener来分别处理返回和保存动作
	 * 
	 * @author keqian
	 */
	public static class CollectEventManager {

		private static OnCollectBackListener collectBackListener = null;
		private static OnCollectSaveListener collectSaveListener = null;
		private static OnEditBackListener editBackListener = null;
		private static OnEditSaveListener editSaveListener = null;
		private static OnShearBackListener shearBackListener = null;
		private static OnShearSaveListener shearSaveListener = null;
		private static OnAreaBackListener areaBackListener = null;
		private static OnAreaSaveListener areaSaveListener = null;
	
		
		/**
		 * 设置面积几何计算返回监听
		 * 
		 * @param listener the areaBackListener to set
		 */
		public static void setOnAreaBackListener(OnAreaBackListener listener) {
			areaBackListener = listener;
		}

		/**
		 * 设置面积几何计算返回事件监听
		 * 
		 * @param listener
		 */
		public static void setOnAreaSaveListener(OnAreaSaveListener listener) {
			areaSaveListener = listener;
		}

		/**
		 * 设置采集返回事件监听
		 * 
		 * @param listener
		 *            OnCollectBackListener
		 */
		public static void setOnCollectBackListener(OnCollectBackListener listener) {
			collectBackListener = listener;
		}

		/**
		 * 设置采集保存事件监听
		 * 
		 * @param listener
		 *            OnCollectSaveListener
		 */
		public static void setOnCollectSaveListener(OnCollectSaveListener listener) {
			collectSaveListener = listener;
		}

		/**
		 * 设置编辑返回事件监听
		 * 
		 * @param listener
		 *            OnEditBackListener
		 */
		public static void setOnEditBackListener(OnEditBackListener listener) {
			editBackListener = listener;
		}

		/**
		 * 设置编辑保存事件监听
		 * 
		 * @param listener
		 *            OnEditSaveListener
		 */
		public static void setOnEditSaveListener(OnEditSaveListener listener) {
			editSaveListener = listener;
		}

		/**
		 * 设置裁剪返回事件监听
		 * 
		 * @param listener
		 *            OnShearBackListener
		 */
		public static void setOnShearBackListener(OnShearBackListener listener) {
			shearBackListener = listener;
		}

		/**
		 * 设置裁剪保存事件监听
		 * 
		 * @param listener
		 *            OnShearSaveListener
		 */
		public static void setOnShearSaveListener(OnShearSaveListener listener) {
			shearSaveListener = listener;
		}

		
		/**
		 * 触发AreaBack事件
		 */
		public static boolean fireAreaBack(EventObject event) {
			if (areaBackListener == null)
				return false;
			return areaBackListener.areaBack(event);
		}

		/**
		 * 触发AreaSave事件
		 */
		public static boolean fireAreaSave(EventObject event) {
			if (areaSaveListener == null)
				return false;
			return areaSaveListener.areaSave(event);
		}
		/**
		 * 触发CollectBack事件
		 */
		public static boolean fireCollectBack(EventObject event) {
			if (collectBackListener == null)
				return false;
			return collectBackListener.collectBack(event);
		}

		/**
		 * 触发CollectSave事件
		 */
		public static boolean fireCollectSave(EventObject event) {
			if (collectSaveListener == null)
				return false;
			return collectSaveListener.collectSave(event,getMCaArea());
		}

		/**
		 * 触发EditBack事件
		 */
		public static boolean fireEditBack(EventObject event) {
			if (editBackListener == null)
				return false;
			return editBackListener.editBack(event);
		}

		/**
		 * 触发EditSave事件
		 */
		public static boolean fireEditSave(EventObject event) {
			if (editSaveListener == null)
				return false;
			return editSaveListener.editSave(event);
		}

		/**
		 * 触发ShearBack事件
		 */
		public static boolean fireShearBack(EventObject event) {
			if (shearBackListener == null)
				return false;
			return shearBackListener.shearBack(event);
		}

		/**
		 * 触发ShearSave事件
		 */
		public static boolean fireShearSave(EventObject event) {
			if (shearSaveListener == null)
				return false;
			return shearSaveListener.shearSave(event,GeoSplitManager.Instance().areas);
		}

		public static void dispose() {
			areaBackListener = null;
			areaSaveListener = null;
			collectBackListener = null;
			collectSaveListener = null;
			editBackListener = null;
			editSaveListener = null;
			shearBackListener = null;
			shearSaveListener = null;
		}
	}
}
