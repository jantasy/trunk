package com.chinatelecom.nfc.DB.Pojo;

import android.content.Context;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.Utils.MyUtil;

public class ProtocolTitle {
	public String v;

	private MyData d;

	public ProtocolTitle(Context context)  {
		super();
		int versionCode = 100;
		try {
			versionCode = MyUtil.getVersionCode(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String agency = context.getResources().getString(R.string.nfc_agency);
		this.v = versionCode + agency;
	}
	

	public MyData getData() {
		return d;
	}
	public void setData(MyData data) {
		this.d = data;
	}
	
	
}
