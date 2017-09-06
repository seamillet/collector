/**
 * 
 */
package com.willc.collector.data.split;

import java.util.ArrayList;
import java.util.List;

import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.Point;
import srs.Geometry.SpatialOp;

/**
 * @author keqian
 */
public abstract class Splitter {

	protected List<LinkedPoint> mGeoPoints = null;
	protected List<LinkedPoint> mInterPoints = null;
	protected boolean isFstPtInPolygon = false;
	protected boolean isOutSameEdge = false;
	protected boolean isSeparable = false;

	protected Splitter() {
		// 将要裁剪的Geometry的点集合封装成LinkedPoint集合
		mGeoPoints = new ArrayList<LinkedPoint>();
		// 初始化交点的LinkedPoint集合
		mInterPoints = new ArrayList<LinkedPoint>();
	}

	/**
	 * 设置裁剪的几何对象的LinkedPoint集合
	 * 
	 * @param geoPoints
	 *            Geome的LinkedPoint集合
	 */
	public void setLinkedGeoPoints(List<LinkedPoint> geoPoints) {
		mGeoPoints = geoPoints;
	}

	public boolean isSeparable() {
		return isSeparable;
	}

	public boolean isPointInPolygon(IPoint point) {
		IPoint[] points = new Point[mGeoPoints.size() + 1];
		for (int i = 0; i < mGeoPoints.size(); i++) {
			points[i] = mGeoPoints.get(i).getPoint();
		}
		points[mGeoPoints.size()] = mGeoPoints.get(0).getPoint();

		if (SpatialOp.Point_In_Polygon(point, points)) {
			isFstPtInPolygon = true;
		}
		return isFstPtInPolygon;
	}

	public void checkSeparable(IPoint pStart, IPoint pEnd, int pointIndex) {
		for (int i = 0; i < mGeoPoints.size(); i++) {
			LinkedPoint lp1 = mGeoPoints.get(i);
			LinkedPoint lp2 = mGeoPoints.get(lp1.getNext());
			if (isSegsIntersect(pStart, pEnd, lp1.getPoint(), lp2.getPoint())) {
				IPoint intersectPt = getIntersection(pStart, pEnd,
						lp1.getPoint(), lp2.getPoint());

				LinkedPoint intersectLP = new LinkedPoint();
				intersectLP.setPoint(intersectPt);
				intersectLP.setIndex(pointIndex);
				intersectLP.setPre(i);
				intersectLP.setNext((i + 1) % mGeoPoints.size());
				intersectLP.setIsIntersection(true);
				mInterPoints.add(intersectLP);

				mGeoPoints.get(i).setIsNextIntersection(true);
				mGeoPoints.get(i).setNextIntsectIndex(mInterPoints.size() - 1);
				mGeoPoints.get((i + 1) % mGeoPoints.size())
						.setIsPreIntersection(true);
				mGeoPoints.get((i + 1) % mGeoPoints.size()).setPreIntsectIndex(
						mInterPoints.size() - 1);
			} else {
				continue;
			}
		}

		if (isFstPtInPolygon) {
			isSeparable = mInterPoints.size() >= 3 ? true : false;
		} else {
			isSeparable = mInterPoints.size() >= 2 ? true : false;
		}
	}

	/**
	 * 裁剪(分割)Geometry对象
	 * 
	 * @param linePoints
	 *            裁剪线(分割线)点的集合
	 * @return 返回分割后的Geometry对象集合
	 */
	public abstract List<IGeometry> split(List<IPoint> linePoints);

	protected int addGeoPts(IPart part, int geoStartIndex, int geoEndIndex) {
		if (geoEndIndex == -1) {
			// 结束标志为遇到交点
			int next = geoStartIndex;
			while (true) {
				LinkedPoint lpStart = mGeoPoints.get(next);
				// add first point
				part.AddPoint(lpStart.getPoint());
				if (lpStart.isNextIntersection()) {
					break;
				} else {
					next = lpStart.getNext();
				}
			}
			return next;
		} else {
			int next = geoStartIndex;
			while (true) {
				LinkedPoint lpStart = mGeoPoints.get(next);
				// add first point
				part.AddPoint(lpStart.getPoint());
				if (next == geoEndIndex) {
					break;
				} else {
					next = lpStart.getNext();
				}
			}
			return -1;
		}
	}

	protected int addLinePts(IPart part, int next, List<IPoint> linePts) {
		LinkedPoint lp = mGeoPoints.get(next);
		LinkedPoint startLPt = mInterPoints.get(lp.getNextIntsectIndex());
		LinkedPoint endLPt = null;
		if (lp.getNextIntsectIndex() == mInterPoints.size() - 1) {
			endLPt = mInterPoints.get(lp.getNextIntsectIndex() - 1);
		} else {
			endLPt = mInterPoints.get(lp.getNextIntsectIndex() + 1);
		}

		if (startLPt.getIndex() == endLPt.getIndex()) {
			// 直线切割
			part.AddPoint(startLPt.getPoint());
			part.AddPoint(endLPt.getPoint());
		} else {
			// 折线切割
			part.AddPoint(startLPt.getPoint());
			if (startLPt.getIndex() > endLPt.getIndex()) {
				for (int i = startLPt.getIndex(); i >= endLPt.getIndex() + 1; i--) {
					part.AddPoint(linePts.get(i));
				}
			} else {
				for (int i = startLPt.getIndex() + 1; i <= endLPt.getIndex(); i++) {
					part.AddPoint(linePts.get(i));
				}
			}
			part.AddPoint(endLPt.getPoint());
		}

		if (startLPt.getPre() == endLPt.getPre()) {
			isOutSameEdge = true;
		}

		return endLPt.getNext();
	}

	/**
	 * 求两线段的交点(直线方程算法) Line1: 方程:y = k1x + b1; Line2: 方程:y = k2x + b2
	 * 根据四个点的坐标，可以推导出来k1,k2和b1,b2
	 * 
	 * @param p1
	 * @param p2
	 *            p1p2为一条线段
	 * @param p3
	 * @param p4
	 *            p3p4为另一条线段
	 * @return 交点IPoint对象
	 */
	protected IPoint getIntersection(IPoint p1, IPoint p2, IPoint p3, IPoint p4) {
		IPoint interPoint = new Point();
		double k1, k2, b1, b2;
		if (p1.X() != p2.X() && p3.X() != p4.X()) {
			k1 = (p2.Y() - p1.Y()) / (p2.X() - p1.X());
			k2 = (p4.Y() - p3.Y()) / (p4.X() - p3.X());
			b1 = p1.Y() - (p2.Y() - p1.Y()) / (p2.X() - p1.X()) * p1.X();
			b2 = p3.Y() - (p4.Y() - p3.Y()) / (p4.X() - p3.X()) * p3.X();
			interPoint.X((b2 - b1) / (k1 - k2));
			interPoint.Y((b2 - b1) / (k1 - k2) * k1 + b1);
		} else if (p1.X() == p2.X()) {
			k2 = (p4.Y() - p3.Y()) / (p4.X() - p3.X());
			b2 = p3.Y() - (p4.Y() - p3.Y()) / (p4.X() - p3.X()) * p3.X();
			interPoint.X(p1.X());
			interPoint.Y(k2 * interPoint.X() + b2);
		} else if (p3.X() == p4.X()) {
			k1 = (p2.Y() - p1.Y()) / (p2.X() - p1.X());
			b1 = p1.Y() - (p2.Y() - p1.Y()) / (p2.X() - p1.X()) * p1.X();
			interPoint.X(p3.X());
			interPoint.Y(k1 * interPoint.X() + b1);
		}
		return interPoint;
	}

	/**
	 * 判断两线段是否想交(包括相交在端点处)
	 * 
	 * @param p1
	 * @param p2
	 *            p1p2为一条线段
	 * @param q1
	 * @param q2
	 *            q1q2为另一条线段
	 * @return 相交返回True,否则返回False
	 */
	protected boolean isSegsIntersect(final IPoint p1, final IPoint p2,
			final IPoint q1, final IPoint q2) {
		// 排斥实验
		boolean isRectCross = Math.min(p1.X(), p2.X()) <= Math.max(q1.X(),
				q2.X())
				&& Math.min(q1.X(), q2.X()) <= Math.max(p1.X(), p2.Y())
				&& Math.min(p1.Y(), p2.Y()) <= Math.max(q1.Y(), q2.Y())
				&& Math.min(q1.Y(), q2.Y()) <= Math.max(p1.Y(), p2.Y());
		// 跨立实验
		// 若P1P2跨立Q1Q2，则矢量(P1-Q1)和(P2-Q1)位于矢量(Q2-Q1)的两侧，
		// 即( P1 - Q1 ) × ( Q2 - Q1 ) * ( P2 - Q1 ) × ( Q2 - Q1 ) < 0。
		// 若Q1Q2跨立P1P2，则矢量(Q1-P1)和(Q2-P1)位于矢量(P2-P1)的两侧，
		// 即( Q1 - P1 ) × ( P2 - P1 ) * ( Q2 - P1 ) × ( P2 - P1 ) < 0。
		boolean isSegsCross = crossMult(p1, q2, q1) * crossMult(p2, q2, q1) < 0
				&& crossMult(q1, p2, p1) * crossMult(q2, p2, p1) < 0;
		return isRectCross && isSegsCross;
	}

	/**
	 * 计算(sp-op)*(ep-op)的叉积 r>0:ep在矢量opsp的逆时针方向； r=0：opspep三点共线；
	 * r<0:ep在矢量opsp的顺时针方向
	 * 
	 * @param sp
	 * @param ep
	 * @param op
	 * @return
	 */
	protected double crossMult(final IPoint sp, final IPoint ep, final IPoint op) {
		double result = (sp.X() - op.X()) * (ep.Y() - op.Y())
				- (ep.X() - op.X()) * (sp.Y() - op.Y());
		return result;
	}

}
