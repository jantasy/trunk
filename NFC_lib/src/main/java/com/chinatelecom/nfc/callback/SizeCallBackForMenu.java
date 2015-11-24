package com.chinatelecom.nfc.callback;

import android.widget.Button;
import android.widget.ImageView;

public class SizeCallBackForMenu implements SizeCallBack {

	private Button menu;
	private int menuWidth;
	
	
	public SizeCallBackForMenu(Button menu){
		super();
		this.menu = menu;
	}
	
	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		// 璁＄��button���瀹藉害锛�������涓?0
		this.menuWidth = this.menu.getMeasuredWidth() + 80;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		// TODO Auto-generated method stub
		dims[0] = width;
		dims[1] = height;
		
		/*瑙���句�����涓���磋�����*/
		if(idx != 1){
			dims[0] = width - this.menuWidth;
		}
	}

}
