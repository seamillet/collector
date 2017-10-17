/**
 * 
 */
package com.willc.collector.data.collect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import com.willc.collector.settings.DrawPaintStyles;
import com.willc.collector.settings.ElementStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import srs.Element.IElement;
import srs.Element.ILineElement;
import srs.Element.IPointElement;
import srs.Element.LineElement;
import srs.Element.PointElement;
import srs.Geometry.IGeometry;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolyline;
import srs.Geometry.Part;
import srs.Geometry.Point;
import srs.Geometry.Polyline;
import srs.Geometry.srsGeometryType;

/**
 * @author keqian 缁捐儻顩︾槐鐘诲櫚闂嗭拷
 */
public class PolyLineCollector extends GeoCollector {

	public PolyLineCollector() {
		super();
		mMidPoints = new ArrayList<IPoint>();
		historyMidPoints = new Stack<List<IPoint>>();
	}

	@Override
	public void addPoint(IPoint point) {
		historyCurrIndex.push(currentPointIndex);
		historyActions.push("ADD");
		historyGeos.push(getGeometry());

		// 濞ｈ濮為柌鍥肠閻愶拷
		if (currentPointIndex == mPoints.size() - 1) {
			mPoints.add(point);
		} else {
			mPoints.add(currentPointIndex + 1, point);
		}
		currentPointIndex++;
		// 濞ｈ濮炴稉顓犲仯
		if (mPoints.size() > 1) {
			historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));

			if (currentPointIndex == mPoints.size() - 1) {
				mMidPoints.add(calcMidPoint(mPoints.get(currentPointIndex),
						mPoints.get(currentPointIndex - 1)));
			} else {
				IPoint midPointModified = calcMidPoint(
						mPoints.get(currentPointIndex),
						mPoints.get(currentPointIndex - 1));
				IPoint midPointAdd = calcMidPoint(
						mPoints.get(currentPointIndex),
						mPoints.get(currentPointIndex + 1));
				mMidPoints.remove(currentPointIndex - 1);
				mMidPoints.add(currentPointIndex - 1, midPointModified);
				mMidPoints.add(currentPointIndex, midPointAdd);
			}
		}
	}

	@Override
	public void addPointMid(IPoint point) {
		historyCurrIndex.push(currentPointIndex);
		historyActions.push("ADD");
		historyGeos.push(getGeometry());

		// 濞ｈ濮為柌鍥肠閻愶拷
		currentPointIndex = currentMidPointIndex + 1;
		mPoints.add(currentPointIndex, point);
		// 濞ｈ濮炴稉顓犲仯
		historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));
		IPoint midPointModified = calcMidPoint(mPoints.get(currentPointIndex),
				mPoints.get(currentPointIndex - 1));
		IPoint midPointAdd = calcMidPoint(mPoints.get(currentPointIndex),
				mPoints.get(currentPointIndex + 1));
		mMidPoints.remove(currentPointIndex - 1);
		mMidPoints.add(currentPointIndex - 1, midPointModified);
		mMidPoints.add(currentPointIndex, midPointAdd);
		currentMidPointIndex = -1;
	}

	@Override
	public void updatePoint(IPoint point) {
		historyCurrIndex.push(currentPointIndex);
		historyActions.push("UPDATE");
		historyGeos.push(getGeometry());

		mPoints.remove(currentPointIndex);
		mPoints.add(currentPointIndex, point);

		// 娣囶喗鏁兼稉顓犲仯閻ㄥ嫬锟斤拷
		updateMidPoints();
	}

	private void updateMidPoints() {
		historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));
		// 鐠у嘲顫愰悙鍦椽鏉堟埊绱濋弴瀛樻暭鐠у嘲顫愰悙鐟版嫲鐠у嘲顫愰悙鐟版倵娑擄拷閻愬湱娈戞稉顓犲仯娴ｅ秶鐤�
		if (currentPointIndex == 0) {
			mMidPoints.remove(0);
			mMidPoints.add(0, calcMidPoint(mPoints.get(0), mPoints.get(1)));
		}
		// 閺堫偄鐔悙鍦椽鏉堟埊绱濋弴瀛樻暭閺堫偄鐔悙鐟版嫲閺堫偄鐔悙鐟板娑擄拷閻愬湱娈戞稉顓犲仯娴ｅ秶鐤�
		else if (currentPointIndex == mPoints.size() - 1) {
			mMidPoints.remove(mMidPoints.size() - 1);
			mMidPoints.add(calcMidPoint(mPoints.get(currentPointIndex),
					mPoints.get(currentPointIndex - 1)));
		}
		// 娑擃參妫块悙鍦椽鏉堟埊绱濋弴瀛樻暭鐠囥儳鍋ｉ崪灞藉娑擄拷閻愰�涜厬閻愰�涗簰閸欏﹨顕氶悙鐟版嫲閸氬簼绔撮悙閫涜厬閻愰�涚秴缂冿拷
		else {
			IPoint midPointPre = calcMidPoint(mPoints.get(currentPointIndex),
					mPoints.get(currentPointIndex - 1));
			IPoint midPointLast = calcMidPoint(mPoints.get(currentPointIndex),
					mPoints.get(currentPointIndex + 1));
			mMidPoints.remove(currentPointIndex - 1);
			mMidPoints.add(currentPointIndex - 1, midPointPre);
			mMidPoints.remove(currentPointIndex);
			mMidPoints.add(currentPointIndex, midPointLast);
		}
	}

	@Override
	public void clear() throws Exception {
		if (mPoints != null && mPoints.size() > 0) {
			historyCurrIndex.push(currentPointIndex);
			historyActions.push("CLEAR");
			historyGeos.push(getGeometry());
			historyMidPoints.push(historyMidPoints.push(new ArrayList<IPoint>(
					mMidPoints)));

			mPoints.clear();
			mMidPoints.clear();
			currentPointIndex = -1;
			currentMidPointIndex = -1;
		}
		refresh();
	}

	@Override
	public void delpt() throws Exception {
		// 鐞涖劎銇氭稉顓犲仯鐞氼偊锟藉鑵戦幋鏍ㄧ梾閺堝鍣伴梿鍡氬Ν閻愮櫢绱濇稉宥堢箻鐞涘本鎼锋担锟�
		if (currentPointIndex != -1) {
			historyCurrIndex.push(currentPointIndex);
			historyActions.push("DELETE");
			historyGeos.push(getGeometry());
			historyMidPoints.push(new ArrayList<IPoint>(mMidPoints));

			if (mPoints.size() == 1) {
				mPoints.remove(0);
				currentPointIndex--;
			} else if (mPoints.size() > 1) {
				if (currentPointIndex == 0) {
					mPoints.remove(0);
					mMidPoints.remove(0);
				} else if (currentPointIndex == mPoints.size() - 1) {
					mPoints.remove(currentPointIndex);
					mMidPoints.remove(mMidPoints.size() - 1);
					currentPointIndex--;
				} else {
					// 閸掔娀娅庨懞鍌滃仯
					mPoints.remove(currentPointIndex);
					currentPointIndex--;
					// 鐠侊紕鐣婚懞鍌滃仯閸掔娀娅庨崥搴ｆ畱娑擃厾鍋ｆ担宥囩枂
					IPoint midPoint = calcMidPoint(
							mPoints.get(currentPointIndex),
							mPoints.get(currentPointIndex + 1));
					// 閸掔娀娅庢稉搴ゎ嚉閼哄倻鍋ｉ惄鎼佸仸閻ㄥ嫪琚辨稉顏冭厬閻愶拷
					mMidPoints.remove(currentPointIndex);
					mMidPoints.remove(currentPointIndex);
					// 濞ｈ濮炴稉濠囨桨鐠侊紕鐣婚惃鍕厬閻愶拷
					mMidPoints.add(currentPointIndex, midPoint);
				}
			}
		}
		refresh();
	}

	@Override
	public void undo() throws Exception {
		if (historyGeos.size() > 0) {
			CharSequence action = historyActions.pop();
			if (historyMidPoints.size() > 0) {
				if (action == "CLEAR") {
					historyMidPoints.pop();
				}
				mMidPoints.clear();
				mMidPoints.addAll(historyMidPoints.pop());
			}
			geometryToPoints(historyGeos.pop());
			int index = historyCurrIndex.pop();
			if (action == "ADD" && index < currentPointIndex) {
				currentPointIndex--;
			} else {
				currentPointIndex = index;
			}
		}
		refresh();
	}

	@Override
	public void setEditGeometry(IGeometry editGeometry) throws Exception {
		// 鐠佸墽鐤嗛柌鍥肠閻愶拷
		geometryToPoints(editGeometry);
		currentPointIndex = mPoints.size() - 1;
		// 鐠佸墽鐤嗘稉顓犲仯
		calcMidPoints();
		refresh();
	}

	private void calcMidPoints() {
		for (int i = 0; i < mPoints.size() - 1; i++) {
			mMidPoints.add(calcMidPoint(mPoints.get(i), mPoints.get(i + 1)));
		}
	}

	@Override
	public boolean isPointFocused(double x, double y) {
		double limits = mMapView.getMap()
				.getScreenDisplay().ToMapDistance(range);
		boolean flag = false;
		if (mPoints.size() > 0) {
			for (int i = 0; i < mPoints.size(); i++) {
				IPoint currP = mPoints.get(i);
				if (x > currP.X() - limits && x < currP.X() + limits
						&& y > currP.Y() - limits && y < currP.Y() + limits) {
					currentPointIndex = i;
					currentMidPointIndex = -1;
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public boolean isMidPointFocused(double x, double y) {
		double limits = mMapView.getMap()
				.getScreenDisplay().ToMapDistance(range);
		boolean flag = false;
		if (mMidPoints.size() > 0) {
			for (int i = 0; i < mMidPoints.size(); i++) {
				IPoint currP = mMidPoints.get(i);
				if (x > currP.X() - limits && x < currP.X() + limits
						&& y > currP.Y() - limits && y < currP.Y() + limits) {
					currentMidPointIndex = i;
					currentPointIndex = -1;
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public void drawPathOnCanvas(Canvas canvas, float x, float y) {
		Path path = new Path();
		if (currentPointIndex == 0) {
			path.moveTo(x, y);
		} else {
			PointF pf = mMap.FromMapPoint(mPoints.get(0));
			path.moveTo(pf.x, pf.y);
		}
		for (int i = 1; i < mPoints.size(); i++) {
			if (currentPointIndex == i) {
				path.lineTo(x, y);
			} else {
				PointF pointf = mMap.FromMapPoint(mPoints.get(i));
				path.lineTo(pointf.x, pointf.y);
			}
		}
		canvas.drawPath(path, DrawPaintStyles.linePaintPaint);
	}

	@Override
	public void drawPointsOnCanvas(Canvas canvas, float x, float y) {
		// 闁插洭娉﹂悙锟�
		for (int i = 0; i < mPoints.size(); i++) {
			if (i == currentPointIndex) {
				canvas.drawCircle(x, y, 7, DrawPaintStyles.PointFocusedPaint);
			} else {
				PointF pf = mMap.FromMapPoint(mPoints.get(i));
				canvas.drawCircle(pf.x, pf.y, 7,
						DrawPaintStyles.pointNoFocusedPaint);
			}
		}
		// 鐠侊紕鐣婚崝銊︼拷浣疯厬閻愮櫢绱濋獮璺虹殺娑擃厾鍋ｇ紒妯哄煑閸掔檲anvas
		if (currentPointIndex == 0) {
			PointF cMidPt = calcMidPoint(x, y,
					mMap.FromMapPoint(mPoints.get(currentPointIndex + 1)));
			canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
					DrawPaintStyles.midPointPaint);
			for (int i = 1; i < mMidPoints.size(); i++) {
				PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
				canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
			}
		}
		// 閺堫偄鐔悙鍦椽鏉堟埊绱濋弴瀛樻暭閺堫偄鐔悙鐟版嫲閺堫偄鐔悙鐟板娑擄拷閻愬湱娈戞稉顓犲仯娴ｅ秶鐤�
		else if (currentPointIndex == mPoints.size() - 1) {
			PointF cMidPt = calcMidPoint(x, y,
					mMap.FromMapPoint(mPoints.get(currentPointIndex - 1)));
			canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
					DrawPaintStyles.midPointPaint);
			for (int i = 0; i < mMidPoints.size() - 1; i++) {
				PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
				canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
			}
		}
		// 娑擃參妫块悙鍦椽鏉堟埊绱濋弴瀛樻暭鐠囥儳鍋ｉ崪灞藉娑擄拷閻愰�涜厬閻愰�涗簰閸欏﹨顕氶悙鐟版嫲閸氬簼绔撮悙閫涜厬閻愰�涚秴缂冿拷
		else {
			for (int i = 0; i < mMidPoints.size(); i++) {
				if (i == currentPointIndex - 1) {
					PointF cMidPt = calcMidPoint(x, y,
							mMap.FromMapPoint(mPoints
									.get(currentPointIndex - 1)));
					canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
							DrawPaintStyles.midPointPaint);
					continue;
				}
				if (i == currentPointIndex) {
					PointF cMidPt = calcMidPoint(x, y,
							mMap.FromMapPoint(mPoints
									.get(currentPointIndex + 1)));
					canvas.drawCircle(cMidPt.x, cMidPt.y, 4,
							DrawPaintStyles.midPointPaint);
					continue;
				}
				PointF pf = mMap.FromMapPoint(mMidPoints.get(i));
				canvas.drawCircle(pf.x, pf.y, 4, DrawPaintStyles.midPointPaint);
			}
		}
	}

	@Override
	public IGeometry getGeometry() {
		if (mPoints.size() > 1) {
			IPart part = new Part();
			for (int i = 0; i < mPoints.size(); i++) {
				IPoint p = new Point(mPoints.get(i).X(), mPoints.get(i).Y());
				part.AddPoint(p);
			}
			IPolyline polyline = new Polyline();
			polyline.AddPart(part);
			return polyline;
		} else if (mPoints.size() == 1) {
			return mPoints.get(0);
		}
		return null;
	}

	@Override
	public void refresh() throws Exception {
		mMap.getElementContainer().ClearElement();

		IGeometry geo = getGeometry();
		if (null != geo) {
			IElement element = null;
			switch (geo.GeometryType()) {
			case Point:
				element = new PointElement();
				((IPointElement) element)
						.setSymbol(ElementStyles.FocusedPointStyle);
				break;
			case Polyline:
				element = new LineElement();
				((ILineElement) element).setSymbol(ElementStyles.LineStyle);
				break;
			default:
				break;
			}

			element.setGeometry(geo);
			mMap.getElementContainer().AddElement(element);
			if (mPoints.size() > 1) {
				List<IElement> elements = new ArrayList<IElement>();
				elements.addAll(getEditPointElements());
				elements.addAll(getMidPointsElements());
				mMap.getElementContainer().AddElements(elements);
			}
		}
		mMapView.PartialRefresh();
	}

	@Override
	public boolean isGeometryValid(Context context) {
		// 缁炬寧婀侀弫鍫燂拷褍鍨介弬锟�
		if (mPoints.size() < 2) {
			generateAlertDialog(context, "闂囷拷鐟曚焦婀侀弫鍫㈡畱缁撅拷",
					"闁俺绻冮崡鏇炲毊閸︽澘娴橀幋鏍﹀▏閻€劌缍嬮崜宥勭秴缂冾喗娼甸柌鍥肠缁撅拷").show();
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
		IGeometry geo = getGeometry();
		if (geo != null) {
			if (geo.GeometryType() == srsGeometryType.Polyline) {
				if (((IPolyline) geo).Length() != 0) {
					return ((IPolyline) geo).Length();
				}
			}
		}
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
		IGeometry geo = getGeometry();
		if (geo != null) {
			if (geo.GeometryType() == srsGeometryType.Polyline) {
				if (geo.CenterPoint()!=null) {
					return geo.CenterPoint();
				}
			}
		}
		return null;
	}
}
