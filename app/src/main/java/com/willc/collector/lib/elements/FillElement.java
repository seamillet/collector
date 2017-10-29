package com.willc.collector.lib.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.willc.collector.lib.Drawing;

import java.math.BigDecimal;

import srs.Display.FromMapPointDelegate;
import srs.Display.Symbol.IFillSymbol;
import srs.Display.Symbol.ISymbol;
import srs.Display.Symbol.SimpleFillSymbol;
import srs.Display.Symbol.TextSymbol;
import srs.Element.IElement;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.Geometry.IPolygon;
import srs.Utility.sRSException;

/**
 * Created by stg on 17/10/29.
 */
public class FillElement extends Element implements IFillElement, IElement {
    private IFillSymbol _Symbol;
    private boolean mIsDraw = false;

    public FillElement() {
        this._Symbol = new SimpleFillSymbol();
    }

    public FillElement(boolean isDraw) {
        this.mIsDraw = isDraw;
        this._Symbol = new SimpleFillSymbol();
    }

    public final IFillSymbol getSymbol() {
        return this._Symbol;
    }

    public final void setSymbol(IFillSymbol value) {
        if(this._Symbol != value) {
            this._Symbol = value;
        }

    }

    public void Draw(Bitmap canvas, FromMapPointDelegate Delegate) {
        try {
            if(this.getGeometry() == null) {
                throw new sRSException("1020");
            }

            if(this._Symbol == null) {
                throw new sRSException("1021");
            }

            if(!(this.getGeometry() instanceof IEnvelope) && !(this.getGeometry() instanceof IPolygon)) {
                throw new sRSException("1022");
            }

            Drawing e = new Drawing(new Canvas(canvas), Delegate);
            if(this.getGeometry() instanceof IEnvelope) {
                e.DrawRectangle((IEnvelope)this.getGeometry(), this._Symbol);
            } else {
                double areaValue = ((IPolygon)this.getGeometry()).Area();
                IPoint iPoint = this.getGeometry().CenterPoint();
                if(this.mIsDraw) {
                    BigDecimal bd = (new BigDecimal(areaValue / 666.666D)).setScale(4, 4);
                    e.DrawText(bd + "(亩)", iPoint, new TextSymbol(), 2.0F);
                }
                e.DrawPolygon((IPolygon)this.getGeometry(), this._Symbol);
            }
        } catch (sRSException var8) {
            var8.printStackTrace();
        }
    }

    public IElement Clone() {
        FillElement element = new FillElement();
        element.setName(this.getName());
        if(this.getGeometry() != null) {
            element.setGeometry(this.getGeometry().Clone());
        }

        if(element.getSymbol() instanceof IFillSymbol) {
            ISymbol tempVar = this._Symbol.Clone();
            element.setSymbol((IFillSymbol)(tempVar instanceof IFillSymbol?tempVar:null));
        }

        return element;
    }
}
