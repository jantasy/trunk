package cn.yjt.oa.app.contactlist.adpter;

import io.luobo.widget.OnNodeClickListener;
import io.luobo.widget.XAdapter;
import io.luobo.widget.XNode;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.task.TaskPublishingActivity;

public class ContactDeptAdapter extends XAdapter {

	public ContactDeptAdapter(Context context, List<XNode> nodes,
			OnNodeClickListener onNodeClickListener) {
		super(context, nodes, onNodeClickListener);
	}

	@Override
	public View getNodeView(XNode xNode, boolean isExpanded, View convertView,
			ViewGroup parent) {
		DeptDetailInfo detailInfo = (DeptDetailInfo) xNode;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.dept_item, null);
		}
		TextView deptTitle = (TextView) convertView.findViewById(R.id.dept_title);
		ImageView flagIcon = (ImageView) convertView.findViewById(R.id.flag_icon);
		deptTitle.setText(detailInfo.getName());
		if (detailInfo.getXChildren() != null&& detailInfo.getXChildren().size() > 0) {
			if (isExpanded) {
				flagIcon.setImageResource(R.drawable.expanded);
			} else {
				flagIcon.setImageResource(R.drawable.unexpanded);
			}
		}else{
			flagIcon.setImageResource(R.drawable.expanded_null);
		}

		return convertView;
	}

	@Override
	public View getNodeView2(XNode xNode, boolean isExpanded, View convertView,
			ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			if (xNode instanceof DeptDetailInfo) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.dept_item, null);
				holder.deptTitle = (TextView) convertView
						.findViewById(R.id.dept_title);
				holder.flagIcon = (ImageView) convertView
						.findViewById(R.id.flag_icon);
				
				convertView.setTag(holder);
			} else if (xNode instanceof DeptDetailUserInfo) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.dept_contact_item, null);
				setupUserViewHolder(convertView, holder);
			}
		}
		
		holder = (Holder) convertView.getTag();
		if (xNode instanceof DeptDetailInfo) {
			DeptDetailInfo detailInfo = (DeptDetailInfo) xNode;
			holder.deptTitle.setText(detailInfo.getName());
			
			if(detailInfo.getXChildren()!=null && detailInfo.getXChildren().size()>0){
				if(isExpanded){
					holder.flagIcon.setImageResource(R.drawable.expanded);
				}else{
					holder.flagIcon.setImageResource(R.drawable.unexpanded);
				}
			}else {
				holder.flagIcon.setImageResource(R.drawable.expanded_null);
			}
			
			convertView.setPadding(getContext().getResources().getDimensionPixelSize(R.dimen.dept_padding_left)*TYPE_2, 0, 0, 0);
		} else if (xNode instanceof DeptDetailUserInfo) {
			DeptDetailUserInfo userInfo = (DeptDetailUserInfo) xNode;
			setupUserItemView(convertView, holder, userInfo,TYPE_2);
		}
		
		
		return convertView;
	}

	private void setupUserItemView(View convertView, Holder holder,
			DeptDetailUserInfo userInfo ,int type) {
		holder.listener.userInfo = userInfo;
		holder.contactName.setText(userInfo.getXTitle());
		holder.itemPhone.setText(userInfo.getPhone());
		holder.itemPosition.setText(userInfo.getPosition());
		holder.detailName.setText(userInfo.getXTitle());
		loadImage(userInfo.getAvatar(), holder.icon, R.drawable.contactlist_contact_icon_default);
		int sexDrawable = -1;
		if(userInfo.getSex() == UserInfo.SEX_MALE){
			sexDrawable = R.drawable.male_uncheck;
		}else if(userInfo.getSex() == UserInfo.SEX_FEMALE){
			sexDrawable = R.drawable.female_uncheck;
		}
		holder.detailSex.setImageResource(sexDrawable);
		holder.detailPhone.setText(userInfo.getPhone());
		holder.detailEmail.setText(userInfo.getEmail());
		holder.detailTel.setText(userInfo.getTel());
		final View layoutContactDetail = convertView.findViewById(R.id.layout_contact_detail);
		layoutContactDetail.setVisibility(View.GONE);
		convertView.findViewById(R.id.layout_contact_item).setPadding(getContext().getResources().getDimensionPixelSize(R.dimen.dept_padding_left_type)*type, 0, 0, 0);
		convertView.findViewById(R.id.layout_contact_item).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layoutContactDetail.setVisibility(layoutContactDetail.getVisibility() == View.GONE?View.VISIBLE:View.GONE);
			}
		});
		
	}

	@Override
	public View getNodeView3(XNode xNode, boolean isLastNode, View convertView,
			ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			if (xNode instanceof DeptDetailUserInfo) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.dept_contact_item, null);
				setupUserViewHolder(convertView, holder);
			} else {
				return null;
			}
		}
		holder = (Holder) convertView.getTag();
		if (xNode instanceof DeptDetailUserInfo) {
			DeptDetailUserInfo userInfo = (DeptDetailUserInfo) xNode;
			setupUserItemView(convertView, holder, userInfo,TYPE_3);
		}
		return convertView;
	}

	private void setupUserViewHolder(View convertView, Holder holder) {
		holder.contactName = (TextView) convertView
				.findViewById(R.id.contact_item_name);
		holder.divider = convertView.findViewById(R.id.divider);
		holder.itemPhone = (TextView) convertView.findViewById(R.id.contact_item_phone);
		holder.itemPosition = (TextView) convertView.findViewById(R.id.contact_item_position);
		holder.btnPhone = (ImageView) convertView.findViewById(R.id.btn_contact_item_phone);
		holder.btnAt = (ImageView) convertView.findViewById(R.id.btn_contact_item_at);
		holder.detailName = (TextView) convertView.findViewById(R.id.detail_username);
		holder.detailSex = (ImageView) convertView.findViewById(R.id.detail_sex);
		holder.detailPhone = (TextView) convertView.findViewById(R.id.detail_phone);
		holder.detailEmail = (TextView) convertView.findViewById(R.id.detail_email);
		holder.detailTel = (TextView) convertView.findViewById(R.id.detail_tel);
		holder.icon  = (ImageView) convertView.findViewById(R.id.detail_user_icon);
		holder.listener = new ItemClickListener();
		holder.btnPhone.setOnClickListener(holder.listener);
		holder.btnAt.setOnClickListener(holder.listener);
		holder.detailPhone.setOnClickListener(holder.listener);
		holder.detailTel.setOnClickListener(holder.listener);
		convertView.setTag(holder);
	}

	class Holder {
		TextView contactName;
		TextView deptTitle;
		TextView itemPhone;
		TextView itemPosition;
		ImageView icon;
		ImageView btnPhone;
		ImageView btnAt;
		ImageView flagIcon;
		TextView detailName;
		ImageView detailSex;
		TextView detailPhone;
		TextView detailEmail;
		TextView detailTel;
		View divider;
		ItemClickListener listener;
	}

	private void loadImage(String url, final ImageView icon,
			final int dfResId) {
		icon.setImageResource(dfResId);
		if (TextUtils.isEmpty(url)) {
			return;
		}
		icon.setTag(url);
		MainApplication.getHeadImageLoader().get(url, new ImageLoaderListener() {
			
			@Override
			public void onSuccess(ImageContainer container) {
				if(container.getUrl().equals(icon.getTag())){
					icon.setImageBitmap(container.getBitmap());
				}
			}
			
			@Override
			public void onError(ImageContainer container) {
				if(container.getUrl()!= null && container.getUrl().equals(icon.getTag())){
					icon.setImageResource(dfResId);
				}
				
			}
		});
	}

	@Override
	public int getNodeViewType2(XNode xNode) {
		if (xNode instanceof DeptDetailInfo) {
			return super.getNodeViewType2(xNode);
		} else if (xNode instanceof DeptDetailUserInfo) {
			return TYPE_3;
		}
		return super.getNodeViewType2(xNode);
	}

	private class ItemClickListener implements OnClickListener{

		private DeptDetailUserInfo userInfo;
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_contact_item_phone:
				startCall();
				break;
			case R.id.detail_phone:
				sendMsg();
				break;
			case R.id.btn_contact_item_at:
				sendTask();
				break;
			case R.id.detail_tel:
				callTel();
				break;

			default:
				break;
			}
		}
		
		private void sendTask() {
			TaskPublishingActivity.launchWithUserInfo(getContext(), userInfo);
		}

		private void callTel() {
			String num = userInfo.getTel();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.startCall(getContext(), num);
			}
		}

		private void startCall() {
			String num = userInfo.getPhone();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.startCall(getContext(), num);
			}
		}

		private void sendMsg() {
			String num = userInfo.getPhone();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.sendMsg(getContext(), num);
			}
		}
		
	}
}
