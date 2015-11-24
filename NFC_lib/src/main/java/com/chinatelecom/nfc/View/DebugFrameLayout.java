package com.chinatelecom.nfc.View;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.chinatelecom.nfc.Utils.ViewConstant;
import com.chinatelecom.nfc.View.Pen.RedPen;

public class DebugFrameLayout extends BaseFrameLayout{
	private int x = 20;
	private int y = 30;
	private int currentPage;
	private int test = ViewConstant.VIEW_CARDPACKAGE;
	private String showMesage = "";
	
	public void setShowMesage(String showMesage) {
		this.showMesage = showMesage;
		invalidate();
	}

	public void setTest(int test) {
		this.test = test;
		invalidate();
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		invalidate();
	}
	
	public DebugFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public DebugFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public DebugFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawText("Page:" + currentPage, x, y, RedPen.getPaint());
		String viewPage = "VIEW_CARDPACKAGE";
		switch (test) {
		case ViewConstant.VIEW_CARDPACKAGE:
			viewPage = "VIEW_CARDPACKAGE";
			break;
		case ViewConstant.VIEW_HISTORY:
			viewPage = "VIEW_HISTORY";
			break;
		case ViewConstant.VIEW_SETTING:
			viewPage = "VIEW_SETTING";
			break;
		case ViewConstant.VIEW_CARD:
			viewPage = "VIEW_CARD";
			break;
		case ViewConstant.VIEW_COUPONS:
			viewPage = "VIEW_COUPONS";
			break;
		case ViewConstant.VIEW_OTHERFEATURES:
			viewPage = "VIEW_OTHERFEATURES";
			break;
		case ViewConstant.VIEW_BOTTOM:
			viewPage = "VIEW_BOTTOM";
		case ViewConstant.VIEW_BUSINESSCARD:
			viewPage = "VIEW_BUSINESSCARD";
			break;

		default:
			break;
		}
		canvas.drawText("当前View:"+viewPage, x, y+30, RedPen.getPaint());
		canvas.drawText(showMesage, x, y+60, RedPen.getPaint());
		
		super.onDraw(canvas);
	}


}
