package cn.yjt.oa.app.message.fragment;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.volley.GsonConverter;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.contactlist.InviteUserHandleActivity;
import cn.yjt.oa.app.enterprise.CreateCustHandleActivity;
import cn.yjt.oa.app.enterprise.CustJoinInviteHandleActivity;
import cn.yjt.oa.app.enterprise.JoinCustHandleActivity;
import cn.yjt.oa.app.enterprise.MessageDetailActivity;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.GsonHolder;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.notifications.NotificationDetailActivity;
import cn.yjt.oa.app.sharelink.ShareLinkActivity;
import cn.yjt.oa.app.task.TaskDetailActivity;
import cn.yjt.oa.app.task.URLSpanNoUnderline;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.widget.SectionAdapter;

public class MessageTopAdapter extends SectionAdapter {
	private Context context;
	private OnTopChangedUiListener listener;
	private Fragment fragment;
//	private Bitmap defaultHead;

	public MessageTopAdapter(Fragment fragment, OnTopChangedUiListener listener) {
		this.fragment = fragment;
		this.context = fragment.getActivity();
		this.listener = listener;
//		Bitmap defaultBm = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.contactlist_contact_icon_default);
//		defaultHead = BitmapUtils.getPersonalHeaderIcon(context, defaultBm);

	}

	private ArrayList<MessageInfo> infos = new ArrayList<MessageInfo>();

	public void setTop(Collection<MessageInfo> infos) {
		this.infos.clear();
		if (infos != null)
			this.infos.addAll(infos);
	}

	public void remove(MessageInfo message) {
		infos.remove(message);
	}

	public void add(MessageInfo message) {
		infos.add(message);
	}

	public MessageInfo getMessageById(long msgId) {
		for (MessageInfo info : infos) {
			if (info.getId() == msgId)
				return info;
		}

		return null;
	}

	@Override
	public View getSectionView(int section, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			view = inflater.inflate(R.layout.message_center_item_title, parent,
					false);
		}
		TextView timeTv = (TextView) view.findViewById(R.id.title_tv);
		timeTv.setText("置顶");
		return timeTv;
	}

	@Override
	public View getItemView(final int section, int position, View view,
			ViewGroup parent) {
		final MessageInfo info = infos.get(position);
		MessageHolder holder = null;
		if (view == null) {
			view = View.inflate(context, R.layout.message_center_item_layout,
					null);
			holder = new MessageHolder();
			view.setTag(holder);
		}
		holder = (MessageHolder) view.getTag();
		holder.iconIv = (ImageView) view.findViewById(R.id.item_icon);
		holder.dayTv = (TextView) view.findViewById(R.id.day);
		holder.titleTv = (TextView) view.findViewById(R.id.title);
		holder.typeTv = (ImageView) view.findViewById(R.id.mark);
		holder.contentTv = (TextView) view.findViewById(R.id.content);
		holder.topTv = (TextView) view.findViewById(R.id.top);
		holder.unreadIcon = (ImageView) view.findViewById(R.id.unread);
		Drawable leftDrawable = context.getResources().getDrawable(
				R.drawable.cancle_top);
		holder.topTv.setCompoundDrawablesWithIntrinsicBounds(leftDrawable,
				null, null, null);
		holder.topTv.setCompoundDrawablePadding(context.getResources()
				.getDimensionPixelSize(R.dimen.view_pager_spacing));
		holder.topTv.setText("取消置顶");
		if (0 == info.getIsRead()) {
			holder.unreadIcon.setVisibility(View.VISIBLE);
		} else {
			holder.unreadIcon.setVisibility(View.GONE);
		}
		final ImageView IconTemp = holder.iconIv;
		String day = info.getUpdateTime();
		try {
			Date date = BusinessConstants.parseTime(info.getUpdateTime());
			day = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.dayTv.setText(day);
		IconTemp.setImageResource(R.drawable.contactlist_contact_icon_default);
		// AsyncRequest.getBitmap(info.getIcon(), new Listener<Bitmap>() {
		//
		// @Override
		// public void onResponse(Bitmap arg0) {
		// IconTemp.setImageBitmap(BitmapUtils.getPersonalHeaderIcon(
		// context, arg0));
		// }
		//
		// @Override
		// public void onErrorResponse(InvocationError arg0) {
		// IconTemp.setImageBitmap(defaultHead);
		// }
		// });
		IconTemp.setTag(info.getIcon());
		MainApplication.getHeadImageLoader().get(info.getIcon(),
				new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						if (container.getUrl().equals(IconTemp.getTag())) {
							IconTemp.setImageBitmap(container.getBitmap());
						}
					}

					@Override
					public void onError(ImageContainer container) {
						IconTemp.setImageResource(R.drawable.contactlist_contact_icon_default);
					}
				});
		holder.titleTv.setText(info.getTitle());
		if ("task".equalsIgnoreCase(info.getType())) {
			holder.typeTv.setImageResource(R.drawable.message_center_task_icon);
			Spannable spannedText = Spannable.Factory.getInstance()
					.newSpannable(Html.fromHtml(info.getContent()));
			Spannable processedText = URLSpanNoUnderline
					.removeUnderlines(spannedText);
			holder.contentTv.setText(processedText);
		} else if ("notice".equalsIgnoreCase(info.getType())) {
			holder.typeTv.setImageResource(R.drawable.message_center_notice_icon);
			holder.contentTv.setText(info.getContent());
		} else {
			holder.typeTv.setImageResource(R.drawable.message_center_cust_icon);
			holder.contentTv.setText(info.getContent());
		}

		holder.topTv.setText("取消置顶");
		holder.topTv.setTextColor(context.getResources().getColor(
				R.color.label_red));
		holder.topTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				listener.onRemoveFromTop(info);
				notifyDataSetChanged();
				listener.onAdapterChange();

                /*记录操作 0404*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_MESSAGE_CENTER_TOP_CANCLE);
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO：
				if (info.getIsRead() == 0) {
					info.setIsRead(1);
					readListener.uploadReadStatue(info);
					readListener.needRefrash(true);
					MainActivity.getHandler().sendEmptyMessage(
							Config.REDUCTION_UNREAD_COUNT);
				}
				if ("task".equalsIgnoreCase(info.getType())) {
					Type type = new TypeToken<TaskInfo>() {
					}.getType();
					TaskInfo ti = new GsonConverter(GsonHolder.getInstance()
							.getGson()).convertToObject(info.getPayload(), type);
					ti.setIcon(info.getIcon());
					TaskDetailActivity.launchForResult(fragment, ti,
							MessageFragment.REQUEST_NEED_REFRESH);
				} else if("notice".equalsIgnoreCase(info.getType())){
					Type type = new TypeToken<NoticeInfo>() {
					}.getType();
					NoticeInfo ti = new GsonConverter(GsonHolder.getInstance()
							.getGson()).convertToObject(info.getPayload(), type);
					NotificationDetailActivity.launch(context, ti);
				}else if("JOIN_CUST_APPLY".equalsIgnoreCase(info.getType())){
					JoinCustHandleActivity.launchWithMessageInfo(fragment, info,MessageFragment.REQUEST_NEED_REFRESH);
				}else if("CREATE_CUST_APPLY".equalsIgnoreCase(info.getType())){
					CreateCustHandleActivity.launchWithMessageInfo(fragment, info,MessageFragment.REQUEST_NEED_REFRESH);
				}else if(MessageInfo.CUST_JOIN_INVITEINFO.equalsIgnoreCase(info.getType())){
					CustJoinInviteHandleActivity.launchWithMessageInfo(fragment, info,MessageFragment.REQUEST_NEED_REFRESH);
				}else if(MessageInfo.MESSAGE.equalsIgnoreCase(info.getType())){
					MessageDetailActivity.launchWithMessageInfo(fragment, info, MessageFragment.REQUEST_NEED_REFRESH);
				}else if(MessageInfo.SHARE_LINK.equalsIgnoreCase(info.getType())){
					ShareLinkActivity.launchWithMessageInfo(fragment, info, MessageFragment.REQUEST_NEED_REFRESH);
				}else if(MessageInfo.INVITE_USER.equalsIgnoreCase(info.getType())){
					InviteUserHandleActivity.launchWithMessageInfo(fragment, info, MessageFragment.REQUEST_NEED_REFRESH);
				}else{
					MessageDetailActivity.launchWithMessageInfo(fragment, info, MessageFragment.REQUEST_NEED_REFRESH);
				}
			}
		});
		return view;
	};

	@Override
	public int getSectionCount() {
		// TODO Auto-generated method stub
		if (infos != null && infos.size() > 0) {

			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int getItemCountAtSection(int section) {
		// TODO Auto-generated method stub
		return infos.size();
	}

	@Override
	public Object getSection(int section) {
		return null;
	}

	@Override
	public Object getItem(int section, int position) {
		return infos.get(position);
	}

	private OnReadMessageListener readListener;

	public void setReadStatusListener(OnReadMessageListener listener) {
		readListener = listener;
	}
}
