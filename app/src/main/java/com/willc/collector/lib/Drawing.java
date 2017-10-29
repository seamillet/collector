package com.willc.collector.lib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import srs.Display.FromMapPointDelegate;
import srs.Display.IFromMapPointDelegate;
import srs.Display.Symbol.IFillSymbol;
import srs.Display.Symbol.ILineSymbol;
import srs.Display.Symbol.IPointSymbol;
import srs.Display.Symbol.ISimpleFillSymbol;
import srs.Display.Symbol.ISimpleLineSymbol;
import srs.Display.Symbol.ISimplePointSymbol;
import srs.Display.Symbol.ITextSymbol;
import srs.Display.Symbol.SimplePointStyle;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPart;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Geometry.IPolyline;
import srs.Geometry.Part;
import srs.Geometry.Point;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/28.
 */
public class Drawing {
    public static float HollowLineWidth = 2.0F;

    private static Paint defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private IFromMapPointDelegate mFromMapPointDelegate;
    private Canvas mCanvas;
    private float mtextrate;

    public Drawing(Canvas canvas, FromMapPointDelegate delegate) throws sRSException {
        this(canvas);
        if (delegate == null) {
            throw new sRSException("00300001");
        } else {
            this.mFromMapPointDelegate = delegate;
        }
    }

    public Drawing(Canvas vaule) {
        this.mtextrate = 1.0F;
        this.mCanvas = vaule;
    }

    public Canvas getCanvas() {
        return this.mCanvas;
    }


    public static void drawPoint(Canvas canvas, IPoint point, IPointSymbol symbol, FromMapPointDelegate delegate) {
        drawPoint(canvas, delegate.FromMapPoint(point), symbol);
    }

    public static void drawPoint(Canvas canvas, PointF pointF, IPointSymbol symbol) {
        if (symbol instanceof ISimplePointSymbol) {
            defaultPaint.reset();
            defaultPaint.setAntiAlias(true);
            defaultPaint.setAlpha(symbol.getTransparent());
            defaultPaint.setColor(symbol.getColor());
            defaultPaint.setStyle(Paint.Style.FILL);

            Path path = null;
            float offset = 0.0f;
            switch (((ISimplePointSymbol) symbol).getStyle()) {
                case Circle:
                    drawPointCircle(canvas, pointF, symbol, defaultPaint);
                    break;
                case Square:
                    drawPointSquare(canvas, pointF, symbol, defaultPaint);
                    break;
                case Cross:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(symbol.getSize() / 2.0f);
                    drawPointCross(canvas, pointF, symbol, defaultPaint);
                    break;
                case X:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(symbol.getSize() / 2.0f);
                    drawPointX(canvas, pointF, symbol, defaultPaint);
                    break;
                case Diamond:
                    drawPointDiamond(canvas, pointF, symbol, defaultPaint);
                    break;
                case Triangle:
                    drawPointTriangle(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowCircle:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointCircle(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowSquare:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointSquare(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowDiamond:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointDiamond(canvas, pointF, symbol, defaultPaint);
                    break;
                case HollowTriangle:
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setStrokeWidth(HollowLineWidth);
                    drawPointTriangle(canvas, pointF, symbol, defaultPaint);
                    break;
            }
        }
    }

    private static void drawPointCircle(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        canvas.drawCircle(pointF.x, pointF.y, symbol.getSize(), paint);
    }

    private static void drawPointSquare(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        canvas.drawRect(pointF.x - offset, pointF.y - offset, pointF.x + offset, pointF.y + offset, paint);
    }

    private static void drawPointCross(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pLeft = new PointF(pointF.x - offset, pointF.y);
        PointF pRight = new PointF(pointF.x + offset, pointF.y);
        PointF pTop = new PointF(pointF.x, pointF.y - offset);
        PointF pBottom = new PointF(pointF.x, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pLeft.x, pLeft.y); //左-->右
        path.lineTo(pRight.x, pRight.y);
        path.moveTo(pTop.x, pTop.y);   //上-->下
        path.lineTo(pBottom.x, pBottom.y);

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointX(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pTL = new PointF(pointF.x - offset, pointF.y - offset);
        PointF pTR = new PointF(pointF.x + offset, pointF.y - offset);
        PointF pBL = new PointF(pointF.x - offset, pointF.y + offset);
        PointF pBR = new PointF(pointF.x + offset, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pTL.x, pTL.y);
        path.lineTo(pBR.x, pBR.y); //左上-->右下
        path.moveTo(pTR.x, pTR.y);
        path.lineTo(pBL.x, pBL.y); //右上-->左下

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointDiamond(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pL = new PointF(pointF.x - offset, pointF.y);
        PointF pR = new PointF(pointF.x + offset, pointF.y);
        PointF pT = new PointF(pointF.x, pointF.y - offset);
        PointF pB = new PointF(pointF.x, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pL.x, pL.y);
        path.lineTo(pB.x, pB.y); //左-->下
        path.lineTo(pR.x, pR.y); //下-->右
        path.lineTo(pT.x, pT.y); //右-->上
        path.close();

        canvas.drawPath(path, paint);
        path = null;
    }

    private static void drawPointTriangle(Canvas canvas, PointF pointF, IPointSymbol symbol, Paint paint) {
        final float offset = symbol.getSize() / 2.0f;
        PointF pTop = new PointF(pointF.x - offset, pointF.y);
        PointF pBL = new PointF(pointF.x - offset, pointF.y + offset);
        PointF pBR = new PointF(pointF.x + offset, pointF.y + offset);

        Path path = new Path();
        path.moveTo(pTop.x, pTop.y);
        path.lineTo(pBL.x, pBL.y); //上-->左下
        path.lineTo(pBR.x, pBR.y); //左下-->右下
        path.close();

        canvas.drawPath(path, paint);
        path = null;
    }


    public final void DrawPolyline(IPolyline polyline, ILineSymbol symbol) throws sRSException {
        for(int i = 0; i < polyline.PartCount(); ++i) {
            IPart part = polyline.Parts()[i];
            IPoint[] ipoints = part.Points();
            int length = ipoints.length;
            if(length == 1) {
                break;
            }

            float[] xyArray = new float[(length - 1) * 4];
            PointF pStart = this.mFromMapPointDelegate.FromMapPoint(ipoints[0]);
            xyArray[0] = pStart.x;
            xyArray[1] = pStart.y;

            for(int j = 1; j < length - 1; ++j) {
                PointF p_Current = this.mFromMapPointDelegate.FromMapPoint(ipoints[j]);
                xyArray[4 * j - 2] = p_Current.x;
                xyArray[4 * j - 1] = p_Current.y;
                xyArray[4 * j] = p_Current.x;
                xyArray[4 * j + 1] = p_Current.y;
            }

            PointF pEnd = this.mFromMapPointDelegate.FromMapPoint(ipoints[length - 1]);
            xyArray[4 * (length - 1) - 2] = pEnd.x;
            xyArray[4 * (length - 1) - 1] = pEnd.y;
            this.DrawPolyline(xyArray, symbol);
            Object var12 = null;
        }

    }

    public final void DrawPolygon(IPolygon polygon, IFillSymbol symbol) {
        if(polygon != null && polygon.PartCount() != 0) {
            Path gp = new Path();
            Integer[] indexes = polygon.ExteriorRingIndex();

            Path pF;
            IPoint[] ipoints;
            IPart part;
            int index;
            PointF pC;
            int j;
            int k;
            for(index = 0; index < indexes.length - 1; ++index) {
                part = polygon.Parts()[indexes[index].intValue()];
                ipoints = part.Points();
                pF = new Path();
                pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[0]);
                pF.moveTo(pC.x, pC.y);

                for(j = 1; j < ipoints.length; ++j) {
                    pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[j]);
                    pF.lineTo(pC.x, pC.y);
                }

                pF.close();
                if(ipoints.length >= 3) {
                    gp.addPath(pF);
                }

                for(j = indexes[index].intValue() + 1; j < indexes[index + 1].intValue() - 1; ++j) {
                    part = polygon.Parts()[j];
                    ipoints = part.Points();
                    pF = new Path();
                    pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[ipoints.length - 1]);
                    pF.moveTo(pC.x, pC.y);

                    for(k = ipoints.length - 2; k >= 0; --k) {
                        pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[k]);
                        pF.lineTo(pC.x, pC.y);
                    }

                    pF.close();
                    if(ipoints.length >= 3) {
                        gp.addPath(pF);
                    }
                }

                pC = null;
            }

            index = indexes[indexes.length - 1].intValue();
            part = polygon.Parts()[index];
            ipoints = part.Points();
            pF = new Path();
            pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[0]);
            pF.moveTo(pC.x, pC.y);

            for(j = 1; j < ipoints.length; ++j) {
                pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[j]);
                pF.lineTo(pC.x, pC.y);
            }

            pF.close();
            if(ipoints.length >= 3) {
                gp.addPath(pF);
            }

            for(j = index + 1; j < polygon.PartCount(); ++j) {
                part = polygon.Parts()[j];
                ipoints = part.Points();
                pF = new Path();
                pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[0]);
                pF.moveTo(pC.x, pC.y);

                for(k = 1; k < ipoints.length; ++k) {
                    pC = this.mFromMapPointDelegate.FromMapPoint(ipoints[k]);
                    pF.lineTo(pC.x, pC.y);
                }

                pF.close();
                if(ipoints.length >= 3) {
                    gp.addPath(pF);
                }
            }

            this.DrawPolygon(gp, symbol);
            gp = null;
        }
    }

    public final void DrawRectangle(IEnvelope rectangle, IFillSymbol symbol) {
        this.DrawPolygon(rectangle.ConvertToPolygon(), symbol);
    }

    public final void DrawText(String text, IPoint point, ITextSymbol symbol, float rate) {
        this.DrawText(text, this.mFromMapPointDelegate.FromMapPoint(point), symbol, rate);
    }

    public final void DrawImage(Bitmap image, IEnvelope extent) {
        PointF TL = this.mFromMapPointDelegate.FromMapPoint(new Point(extent.XMin(), extent.YMax()));
        PointF BR = this.mFromMapPointDelegate.FromMapPoint(new Point(extent.XMax(), extent.YMin()));
        RectF rectangle = new RectF(TL.x, TL.y, BR.x - TL.x, BR.y - TL.y);
        this.DrawImage(image, rectangle);
    }

    public final void DrawHighlightText(String text, PointF pointF, ITextSymbol symbol) {
        Paint paint = new Paint();
        paint.setColor(symbol.getColor());
        paint.setTypeface(symbol.getFont());
        this.mCanvas.drawText(text, pointF.x, pointF.y, paint);
    }

    public final void DrawPolyline(float[] pts, ILineSymbol symbol) throws sRSException {
        if(symbol instanceof ISimpleLineSymbol) {
            Paint paint = new Paint();
            paint.setColor(symbol.getColor());
            paint.setStrokeWidth(((ISimpleLineSymbol)symbol).getWidth());
            paint.setStyle(Paint.Style.STROKE);
            DashPathEffect effects = null;
            switch($SWITCH_TABLE$srs$Display$Symbol$SimpleLineStyle()[((ISimpleLineSymbol)symbol).getStyle().ordinal()]) {
                case 1:
                default:
                    break;
                case 2:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F}, 0.0F);
                    break;
                case 3:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case 4:
                    effects = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case 5:
                    effects = new DashPathEffect(new float[]{2.0F, 3.0F}, 0.0F);
            }

            if(effects != null) {
                paint.setPathEffect(effects);
            }

            this.mCanvas.drawLines(pts, paint);
            paint = null;
            effects = null;
        } else {
            throw new sRSException("1040");
        }
    }

    public final void DrawLine(PointF startPoint, PointF endPoint, ILineSymbol symbol) throws sRSException {
        this.DrawPolyline(new float[]{startPoint.x, startPoint.y, endPoint.x, endPoint.y}, symbol);
    }

    public final void DrawPolygon(PointF[] points, IFillSymbol symbol) {
        Path gp = new Path();
        Path p2D = new Path();
        PointF pC = points[0];
        p2D.moveTo(pC.x, pC.y);

        for(int part = 1; part < points.length; ++part) {
            pC = points[part];
            p2D.lineTo(pC.x, pC.y);
        }

        p2D.close();
        gp.addPath(p2D);
        Part var12 = new Part();
        PointF[] var10 = points;
        int var9 = points.length;

        for(int var8 = 0; var8 < var9; ++var8) {
            PointF point = var10[var8];
            Point p = new Point((double)point.x, (double)point.y);
            var12.AddPoint(p);
        }

        this.DrawPolygon(gp, symbol);
        gp = null;
    }

    public final void DrawRectangle(PointF TLPoint, PointF BRPoint, IFillSymbol symbol) {
        Path gp = new Path();
        gp.moveTo(TLPoint.x, TLPoint.y);
        gp.lineTo(TLPoint.x, BRPoint.y);
        gp.lineTo(BRPoint.x, BRPoint.y);
        gp.lineTo(BRPoint.x, TLPoint.y);
        gp.lineTo(TLPoint.x, TLPoint.y);
        gp.close();
        this.DrawPolygon(gp, symbol);
    }

    public final void DrawAngle(IPoint iPoint, double angle, ILineSymbol symbol) {
        Paint p = new Paint();
        p.setColor(-65536);
        RectF oval = new RectF(100.0F, 100.0F, 100.0F, 100.0F);
        this.mCanvas.drawArc(oval, 90.0F, 90.0F, false, p);
    }

    public final void DrawText(String text, PointF pointF, ITextSymbol symbol, float size) {
        this.mtextrate = size;
        Paint paint;
        Rect bounds;
        int boundWidth;
        int boundHeight;
        if(!symbol.getVertical()) {
            paint = new Paint();
            paint.setTypeface(symbol.getFont());
            paint.setColor(symbol.getColor());
            paint.setTextSize(symbol.getSize() * this.mtextrate);
            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            boundWidth = bounds.width();
            boundHeight = bounds.height();
            this.mCanvas.drawText(text, pointF.x - (float)(boundWidth * 3 / 4), pointF.y + (float)boundHeight, paint);
        } else {
            paint = new Paint();
            paint.setTypeface(symbol.getFont());
            paint.setColor(symbol.getColor());
            paint.setTextSize(symbol.getSize() * this.mtextrate);
            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            boundWidth = bounds.width();
            boundHeight = bounds.height();
            this.mCanvas.drawText(text, pointF.x - (float)(boundWidth * 3 / 4), pointF.y + (float)(boundHeight * 4 / 3), paint);
        }

    }

    public final void DrawColor(int color) {
        this.mCanvas.drawColor(color);
    }

    public final void DrawImage(Bitmap image, RectF rectangle) {
        this.mCanvas.drawBitmap(image, (Rect)null, rectangle, (Paint)null);
    }

    public final void DrawImage(Bitmap image, PointF point) {
        this.mCanvas.drawBitmap(image, point.x, point.y, (Paint)null);
    }

    private void DrawPolygon(Path gp, IFillSymbol symbol) {
        Paint paintOutLine = null;
        DashPathEffect paint;
        if(symbol.getOutLineSymbol() != null) {
            paintOutLine = new Paint();
            paintOutLine.setStrokeWidth(symbol.getOutLineSymbol().getWidth());
            paintOutLine.setColor(symbol.getOutLineSymbol().getColor());
            paintOutLine.setStyle(Paint.Style.STROKE);
            paint = null;
            switch($SWITCH_TABLE$srs$Display$Symbol$SimpleLineStyle()[((ISimpleLineSymbol)symbol.getOutLineSymbol()).getStyle().ordinal()]) {
                case 1:
                default:
                    break;
                case 2:
                    paint = new DashPathEffect(new float[]{10.0F, 3.0F}, 0.0F);
                    break;
                case 3:
                    paint = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case 4:
                    paint = new DashPathEffect(new float[]{10.0F, 3.0F, 2.0F, 3.0F, 2.0F, 3.0F}, 13.0F);
                    break;
                case 5:
                    paint = new DashPathEffect(new float[]{2.0F, 3.0F}, 0.0F);
            }

            if(paint != null) {
                paintOutLine.setPathEffect(paint);
            }
        }

        if(symbol instanceof ISimpleFillSymbol) {
            Paint paint1;
            switch($SWITCH_TABLE$srs$Display$Symbol$SimpleFillStyle()[((ISimpleFillSymbol)(symbol instanceof ISimpleFillSymbol?symbol:null)).getStyle().ordinal()]) {
                case 1:
                    paint1 = new Paint();
                    paint1.setColor(((ISimpleFillSymbol)symbol).getColor());
                    paint1.setStyle(Paint.Style.FILL);
                    this.mCanvas.drawPath(gp, paint1);
                    if(paintOutLine != null) {
                        this.mCanvas.drawPath(gp, paintOutLine);
                    }

                    paint = null;
                    break;
                case 2:
                    if(paintOutLine != null) {
                        this.mCanvas.drawPath(gp, paintOutLine);
                    }
                    break;
                default:
                    paint1 = new Paint();
                    paint1.setColor(((ISimpleFillSymbol)(symbol instanceof ISimpleFillSymbol?symbol:null)).getForeColor());
                    this.mCanvas.drawPaint(paint1);
                    if(paintOutLine != null) {
                        this.mCanvas.drawPath(gp, paintOutLine);
                    }

                    paint = null;
            }
        }

        paintOutLine = null;
    }
}
