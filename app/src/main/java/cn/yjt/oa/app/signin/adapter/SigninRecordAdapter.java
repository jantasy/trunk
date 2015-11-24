package cn.yjt.oa.app.signin.adapter;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.signin.SigninShowPositionActivity;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.widget.TimeLineAdapter;

public class SigninRecordAdapter extends TimeLineAdapter {
	
	private static Map<String,String> SIGNIN_TYPE = new HashMap<String,String>();
	
	private LayoutInflater inflater;
	
	static{
		SIGNIN_TYPE.put("CHECK_IN","上班");
		SIGNIN_TYPE.put("CHECK_OUT","下班");
		SIGNIN_TYPE.put("VISIT","位置签到");
		SIGNIN_TYPE.put("CARD","打卡签到");
		SIGNIN_TYPE.put("NFC","NFC签到");
	}
	private Context mContext;
	public SigninRecordAdapter(Context context){
		this.mContext = context;
		inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
//	public void bindData(List<SigninInfo> list){
//		this.mList = list;
//	}
//	@Override
//	public int getCount() {
//		return mList.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return mList.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder = null;
//		if(convertView == null){
//			holder = new ViewHolder();
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.signin_record_item, parent, false);
//			holder.signin_date = (TextView) convertView.findViewById(R.id.signin_date);
//			holder.signin_type = (TextView) convertView.findViewById(R.id.signin_type);
//			holder.signin_time = (TextView) convertView.findViewById(R.id.signin_time);
//			holder.signin_iv = (ImageView) convertView.findViewById(R.id.signin_iv);
//			holder.signin_item = (RelativeLayout) convertView.findViewById(R.id.signin_item);
//			convertView.setTag(holder);
//		}else{
//			holder = (ViewHolder) convertView.getTag();
//		}
//		//2014-09-05T10:38:12:319+08:00
//		SigninInfo signinInfo = mList.get(position);
//		try {
//			holder.signin_date.setText(BusinessConstants
//					.getDate(BusinessConstants.parseTime(signinInfo
//							.getInspectInTime())));
//			holder.signin_time.setText(BusinessConstants
//					.getTime(BusinessConstants.parseTime(signinInfo
//							.getInspectInTime())));
//			String type = signinInfo.getType();
//			holder.signin_type.setText(SIGNIN_TYPE.get(type));
//			final String positionData = signinInfo.getPositionData();
//			final String positionDes = signinInfo.getPositionDescription();
//			LogUtils.log("getPositionData = " + positionData);
//			LogUtils.log("getPositionDescription = " + positionDes);
//			if ("VISIT".equals(type)) {// 如果是位置签到显示位置图片，并且有点击事件，否则不处理
//				holder.signin_iv.setVisibility(View.VISIBLE);
//				holder.signin_item.setClickable(true);
//				holder.signin_item.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						Intent intent = new Intent(mContext, SigninShowPositionActivity.class);
//						intent.putExtra(Config.POSITION_DATA, positionData);
//						intent.putExtra(Config.POSITION_DES, positionDes);
//						mContext.startActivity(intent);
//					}
//				});
//			} else {
//				holder.signin_iv.setVisibility(View.GONE);
//				holder.signin_item.setClickable(false);
//			}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		return convertView;
//	}
	public static class ViewHolder{
		RelativeLayout signin_item;
//		TextView signin_date;
		TextView signin_type;
		TextView signin_time;
		ImageView signin_iv;
	}
//	public String getSigninType(){
//		return "";
//	}
	@Override
	public View getSectionView(int section, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = inflater.inflate(R.layout.clockin_signin_date_item, parent, false);
		
		((TextView) convertView).setText(BusinessConstants.getDate(getSectionDate(section)));
		((TextView) convertView).setTextColor(mContext.getResources().getColor(R.color.context_text));
		return convertView;
	}
	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.signin_record_item, parent, false);
//			holder.signin_date = (TextView) convertView.findViewById(R.id.signin_date);
			holder.signin_type = (TextView) convertView.findViewById(R.id.signin_type);
			holder.signin_time = (TextView) convertView.findViewById(R.id.tv_check_in_time);
			holder.signin_iv = (ImageView) convertView.findViewById(R.id.signin_iv);
			holder.signin_item = (RelativeLayout) convertView.findViewById(R.id.signin_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		//2014-09-05T10:38:12:319+08:00
		SigninInfo signinInfo = (SigninInfo) getItem(section, position);
		LogUtils.i("===adapter time = " + signinInfo.getSignInTime());
		try {
//			holder.signin_date.setText(BusinessConstants
//					.getDate(BusinessConstants.parseTime(signinInfo
//							.getInspectInTime())));
			holder.signin_time.setText(BusinessConstants
					.getTimeWithSS(BusinessConstants.parseTime(signinInfo
							.getSignInTime())));
			String type = signinInfo.getType();
//			holder.signin_type.setText(SIGNIN_TYPE.get(type));
			holder.signin_type.setText(signinInfo.getPositionDescription());
			final String positionData = signinInfo.getPositionData();
			final String positionDes = signinInfo.getPositionDescription();
			LogUtils.i("getPositionData = " + positionData);
			LogUtils.i("getPositionDescription = " + positionDes);
			final String imageUrl = signinInfo.getAttachment();
			if (SigninInfo.SINGIN_TYPE_VISIT.equals(type)) {// 如果是位置签到显示位置图片，并且有点击事件，否则不处理
				holder.signin_iv.setVisibility(View.VISIBLE);
				holder.signin_iv.setImageResource(R.drawable.sign_location);
				holder.signin_item.setClickable(true);
				holder.signin_item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, SigninShowPositionActivity.class);
						intent.putExtra(Config.POSITION_DATA, positionData);
						intent.putExtra(Config.POSITION_DES, positionDes);
						intent.putExtra("imageUrl", imageUrl);
						mContext.startActivity(intent);
					}
				});
			} else if(SigninInfo.SIGNIN_TYPE_NFC.equals(type)){
				holder.signin_iv.setVisibility(View.VISIBLE);
				holder.signin_iv.setImageResource(R.drawable.sign_nfc);
			}else if(SigninInfo.SIGNIN_TYPE_QR.equals(type)){
				holder.signin_iv.setVisibility(View.VISIBLE);
				holder.signin_iv.setImageResource(R.drawable.sign_bar_code);
			}else{
				holder.signin_iv.setVisibility(View.GONE);
				holder.signin_item.setClickable(false);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

}
