package com.willc.collector.settings;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * @author keqian Canvas图形绘制显示样式
 */
public final class DrawPaintStyles {
	public static Paint PointFocusedPaint = null;
	public static Paint pointNoFocusedPaint = null;
	public static Paint midPointPaint = null;
	public static Paint linePaintPaint = null;
	public static Paint polygonPaint = null;

	static {
		PointFocusedPaint = generatePaint(Color.RED, Style.FILL, 5, true, true);
		pointNoFocusedPaint = generatePaint(Color.WHITE, Style.FILL, 5, true,
				true);
		midPointPaint = generatePaint(Color.WHITE, Style.FILL, 2, true, true);
		linePaintPaint = generatePaint(Color.BLACK, Style.STROKE, 3, true, true);
		polygonPaint = generatePaint(Color.argb(120, 242, 240, 26), Style.STROKE,
				1, true, true);
	}

	private static Paint generatePaint(int color, Paint.Style style, int width,
			boolean isAntiAlias, boolean isDither) {
		Paint paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(color);
		paint.setStyle(style);
		paint.setStrokeWidth(width);
		paint.setAntiAlias(isAntiAlias);
		paint.setDither(isDither);
		return paint;
	}
}
