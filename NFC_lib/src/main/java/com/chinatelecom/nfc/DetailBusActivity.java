package com.chinatelecom.nfc;

import java.util.Arrays;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.chinatelecom.nfc.Card.Util_;
import com.chinatelecom.nfc.Utils.MyUtil;

public class DetailBusActivity extends BaseTag implements View.OnClickListener,Html.ImageGetter, Html.TagHandler{
	
	private TextView etTitle;
	private TextView tvTimeDetail;
	private TextView etContent;
	
	private Context context;
	private Resources res;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_detail_bus);
		init();
		mMyData = getMyData();
		if(mMyData != null){
			showView();
		}
	}
	
	@Override
	public void init() {
		super.init();
		context = this;
		res = getResources();
		etTitle = (TextView) findViewById(R.id.title);
		tvTimeDetail = (TextView) findViewById(R.id.tvTimeDetail);
		etContent = (TextView) findViewById(R.id.tvContent);
		
	}
	
	@Override
	public void showView() {
		super.showView();
		//手机模式
		etTitle.setText(R.string.nfc_bus_title);
		tvTimeDetail.setText(MyUtil.dateFormat(mMyData.time));
		etContent.setText(Html.fromHtml(mMyData.n,this,null));
		
	}

	
	@Override
	public void edit(boolean isEdit) {
		super.edit(isEdit);
//		etTitle.setEnabled(isEdit);
//		tvTimeDetail.setEnabled(isEdit);
//		etContent.setEnabled(isEdit);
//		
//		etTitle.setSelected(isEdit);
//		tvTimeDetail.setSelected(isEdit);
//		etContent.setSelected(isEdit);
		
		
	}
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (!opening && "version".equals(tag)) {
			try {
				output.append(getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName);
			} catch (NameNotFoundException e) {
			}
		}
	}

	private Drawable spliter;

	@Override
	public Drawable getDrawable(String source) {
		final Resources res = this.res;

		final Drawable ret;
		if (source.startsWith("spliter")) {
			if (spliter == null) {
				final int w = res.getDisplayMetrics().widthPixels;
				final int h = (int) (res.getDisplayMetrics().densityDpi / 72f + 0.5f);

				final int[] pix = new int[w * h];
				Arrays.fill(pix, res.getColor(R.color.nfc_black));
				spliter = new BitmapDrawable(Bitmap.createBitmap(pix, w, h,
						Bitmap.Config.ARGB_8888));
				spliter.setBounds(0, 3 * h, w, 4 * h);
			}
			ret = spliter;

		} else if (source.startsWith("icon_main")) {
			ret = res.getDrawable(R.drawable.nfc_list_line);

			final String[] params = source.split(",");
			final float f = res.getDisplayMetrics().densityDpi / 72f;
			final float w = Util_.parseInt(params[1], 10, 16) * f + 0.5f;
			final float h = Util_.parseInt(params[2], 10, 16) * f + 0.5f;
			ret.setBounds(0, 0, (int) w, (int) h);

		} else {
			ret = null;
		}

		return ret;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
