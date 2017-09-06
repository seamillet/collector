/**
 * 
 */
package com.willc.collector.settings;

import android.graphics.Color;

import srs.Display.Symbol.ISimpleFillSymbol;
import srs.Display.Symbol.ISimpleLineSymbol;
import srs.Display.Symbol.ISimplePointSymbol;
import srs.Display.Symbol.SimpleFillStyle;
import srs.Display.Symbol.SimpleFillSymbol;
import srs.Display.Symbol.SimpleLineStyle;
import srs.Display.Symbol.SimpleLineSymbol;
import srs.Display.Symbol.SimplePointStyle;
import srs.Display.Symbol.SimplePointSymbol;

/**
 * @author keqian Elements默认显示样式
 */
public final class ElementStyles {
	public final static ISimplePointSymbol NoFocusedPointStyle = new SimplePointSymbol(
			Color.WHITE, 14, SimplePointStyle.Square);
	public final static ISimplePointSymbol FocusedPointStyle = new SimplePointSymbol(
			Color.RED, 14, SimplePointStyle.Square);
	public final static ISimplePointSymbol NoFocusedMidPointStyle = new SimplePointSymbol(
			Color.rgb(64, 200, 255), 9, SimplePointStyle.Circle);
	public final static ISimplePointSymbol FocusedMidPointStyle = new SimplePointSymbol(
			Color.RED, 9, SimplePointStyle.Square);
	public final static ISimpleLineSymbol LineStyle = new SimpleLineSymbol(
			Color.BLACK, 4, SimpleLineStyle.Solid);
	public final static ISimpleFillSymbol PolygonStyle = new SimpleFillSymbol(
			Color.argb(120, 242, 240, 26), LineStyle, SimpleFillStyle.Soild);
	public final static ISimpleLineSymbol LineStyleHighlight = new SimpleLineSymbol(
			Color.argb(255, 0, 255, 255), 3, SimpleLineStyle.Solid);
	public final static ISimpleFillSymbol PolygonStyleHighlight = new SimpleFillSymbol(
			Color.argb(64, 0, 64, 240), LineStyleHighlight,
			SimpleFillStyle.Soild);
}
