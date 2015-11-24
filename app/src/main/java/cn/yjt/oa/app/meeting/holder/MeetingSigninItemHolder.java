package cn.yjt.oa.app.meeting.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.LogUtils;

/**
 * 会议出席人员的适配holder
 * @author 熊岳岳
 * @since 20150828
 * 
 */
public class MeetingSigninItemHolder extends YjtBaseHolder<MeetingSignInInfo> {

	private final String TAG = "MeetingSigninItemHolder";

	public TextView mTvSignName;
	public TextView mTvSigninTime;
	public LinearLayout mLlSigninDetail;
	public TextView mTvDetailName;
	public TextView mTvDetailPhone;
	public TextView mTvDetailDept;
	public ImageView mIvDetailIcon;

	public MeetingSigninItemHolder(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		mConvertView = View.inflate(mContext, R.layout.meeting_sign_list_item, null);
		mTvSignName = (TextView) mConvertView.findViewById(R.id.tv_signin_name);
		mTvSigninTime = (TextView) mConvertView.findViewById(R.id.tv_signin_time);
		mTvDetailName = (TextView) mConvertView.findViewById(R.id.tv_detail_name);
		mTvDetailPhone = (TextView) mConvertView.findViewById(R.id.tv_detail_phone);
		mTvDetailDept = (TextView) mConvertView.findViewById(R.id.tv_detail_dept);
		mIvDetailIcon = (ImageView) mConvertView.findViewById(R.id.iv_detail_icon);
		mLlSigninDetail = (LinearLayout) mConvertView.findViewById(R.id.ll_signin_detail);
		return mConvertView;
	}

	@Override
	public void refreshView(int position, MeetingSignInInfo info) {
		if (position == 0) {
			mTvSignName.setText("姓名");
			mTvSigninTime.setTextColor(Color.BLACK);
			mTvSigninTime.setText("签到时间");
			mLlSigninDetail.setVisibility(View.GONE);
		} else {
			if (info != null) {
				if (!TextUtils.isEmpty(info.getName())) {
					mTvSignName.setText(info.getName());
				} else {
					mTvSignName.setText(info.getPhone());
				}
				mTvSigninTime.setText(DateUtils.requestToDateTime(info.getSignInTime()));
				mTvDetailName.setText(info.getName());
				mTvDetailPhone.setText(info.getPhone());
				mTvDetailDept.setText(info.getCustName());
				initIcon(info);
			}
		}
	}

	/** 加载头像 */
	private void initIcon(MeetingSignInInfo info) {
		MainApplication.getHeadImageLoader().get(info.getAvatar(), new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				mIvDetailIcon.setImageBitmap(container.getBitmap());
			}

			@Override
			public void onError(ImageContainer container) {
				LogUtils.e(TAG, "加载头像出错");
			}
		});
	}
}
