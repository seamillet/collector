/**
 * 
 */
package com.willc.collector.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.willc.collector.R;
import com.willc.collector.datamgr.GeoSplitManager;
import com.willc.collector.interoperation.acticity.ShearActivity;
import com.willc.collector.lib.tools.BaseTool;
import com.willc.collector.lib.view.MapView;

import java.io.IOException;
import java.math.BigDecimal;

import srs.Geometry.IPoint;

/**
 * @author keqian 裁剪工具
 */
public class ShearTool extends BaseTool {
	private ShearActivity mShearActivity;
	Context mContext = null;
	boolean isDraw = true;
	PointF mDownPt = null;
	boolean mIsMaginify = false;
	private TextView actionShearAreaValue, actionShear2AreaValue;
	private LinearLayout txtShearLinearLayout;

	public double shearArea;
	public double shearArea2;

	public ShearTool(Context context) {
		super.setRate();
		mContext = context;
		mDownPt = new PointF();
		mShearActivity = (ShearActivity)context;
	}

//	public void create(MapControl buddyControl) {
//		this.setBuddyControl(buddyControl);
//		this.setEnable(true);
//
//		initView();
//		setValue();
//	}

	public void create(MapView buddyControl) {
		this.setBuddyControl(buddyControl);
		this.setEnable(true);

		initView();
		setValue();
	}

	public void setValue() {
		/*int countAreas = GeoSplitManager.Instance().areas.length;
		if (countAreas>1 &&GeoSplitManager.Instance().areas!=null) {
			shearArea = GeoSplitManager.Instance().areas[countAreas-1];//倒数第一
			shearArea2 = GeoSplitManager.Instance().areas[countAreas-2];//倒数第二
		}
		if (countAreas>0 &&GeoSplitManager.Instance().areas!=null) {
			for (int i = 0;i<countAreas;i++){
				System.out.println("面积" + i + "=="+GeoSplitManager.Instance().areas[i]);
			}

		}*/
		/** 面积*//*
		if (actionShear2AreaValue != null) {

			if (shearArea != 0) {
				actionShearAreaValue.setText(mShearActivity.getResources().getString(R.string.action_shear1area)
						+ reservedDecimal(shearArea / 666.666));
			} else {
				actionShearAreaValue.setText(mShearActivity.getResources().getString(R.string.action_shear1area)
						+ mShearActivity.getResources().getString(R.string.action_invalid));
			}
		} else {
			actionShearAreaValue.setVisibility(View.GONE);
		}*/
		/** 面积2*//*
		if (actionShear2AreaValue != null) {

			if (shearArea2 != 0) {
				actionShear2AreaValue.setText(mShearActivity.getResources().getString(R.string.action_shear2area)
						+ reservedDecimal(shearArea2 / 666.666));
			} else {
				actionShear2AreaValue.setText(mShearActivity.getResources().getString(R.string.action_shear2area)
						+ mShearActivity.getResources().getString(R.string.action_invalid));
			}
		} else {
			actionShear2AreaValue.setVisibility(View.GONE);
		}*/
	}

	private void initView() {
		txtShearLinearLayout = (LinearLayout)mShearActivity.findViewById(R.id.text_shear_top);
		txtShearLinearLayout.getBackground().setAlpha(160);
		actionShearAreaValue = (TextView)mShearActivity.findViewById(R.id.action_sheararea_value);
		actionShear2AreaValue = (TextView)mShearActivity.findViewById(R.id.action_sheararea2_value);
	}

	public BigDecimal reservedDecimal(double x) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		return bd;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean flag = false;
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownPt.set(event.getX(), event.getY());
				isDraw = true;
				break;
			case MotionEvent.ACTION_MOVE:
				double xx = event.getX() - mDownPt.x;
				double yy = event.getY() - mDownPt.y;
				if (Math.sqrt(xx * xx + yy * yy) > 10) {
					isDraw = false;
				}
				break;
			case MotionEvent.ACTION_UP:
//				if (((MapControl) getBuddyControl()).MODE == ZoomPan.MAGNIFY) {
//					isDraw = true;
//					mIsMaginify = true;
//				}

				if (isDraw) {
					IPoint point = toWorldPoint(new PointF(event.getX(),
							event.getY()));
					GeoSplitManager.Instance().addPoint(point);
					GeoSplitManager.Instance().refresh();
					checkSplitable();

					flag = true;
					if (mIsMaginify) {
						flag = false;
					}
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 将屏幕坐标转换为实际地理坐标
	 * 
	 * @param pf
	 *            屏幕坐标
	 * @return 实际地理坐标
	 */
	private IPoint toWorldPoint(PointF pf) {
		return getBuddyControl().ToWorldPoint(
				new PointF(pf.x * mRate, pf.y * mRate));
	}

	/**
	 * 检查是否可以切割
	 */
	public void checkSplitable() {
		if (GeoSplitManager.Instance().canSplit()) {
			generateAlertDialog(mContext, "符合切割要求", "是否要进行切割?").show();
		}
	}

	/**
	 * 生成提示对话框
	 * 
	 * @param title
	 *            提示标题
	 * @param message
	 *            提示内容
	 * @return AlertDialog对象
	 */
	private AlertDialog generateAlertDialog(Context context,
			CharSequence title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("否", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// Clear drawline
				// GeoSplitManager.Instance().clearDrawLine();
				try {
					// 默认执行一步点撤销
					GeoSplitManager.Instance().revoke();
					GeoSplitManager.Instance().refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		builder.setPositiveButton("是", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GeoSplitManager.Instance().split();
				GeoSplitManager.Instance().clearDrawLine();
				setValue();
				try {
					GeoSplitManager.Instance().refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return builder.create();
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bitmap getBitmap() {
		// TODO Auto-generated method stub
		return null;
	}

}
