package cn.yjt.oa.app.message.fragment;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.volley.GsonConverter;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
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
import cn.yjt.oa.app.widget.TimeLineAdapter;

import com.google.gson.Gson;

public class MessageCenterAdapter extends TimeLineAdapter {
	private Context context;
	private OnTopChangedUiListener listener;
	private Fragment fragment;

	public MessageCenterAdapter(Fragment fragment,
			OnTopChangedUiListener listener) {
		this.fragment = fragment;
		this.context = fragment.getActivity();
		this.listener = listener;
	}

	@Override
	public View getSectionView(int section, View view, ViewGroup parent) {
		SectionHolder sectionHolder = null;
		if (view == null) {
			view = View.inflate(context, R.layout.message_center_item_title,
					null);
			sectionHolder = new SectionHolder();
			view.setTag(sectionHolder);
		}
		sectionHolder = (SectionHolder) view.getTag();
		if (sectionHolder == null) {
			sectionHolder = new SectionHolder();
			view.setTag(sectionHolder);
		}
		sectionHolder.timeTv = (TextView) view.findViewById(R.id.title_tv);
		Date date = getSectionDate(section);
		String s = BusinessConstants.getDate(date);
		String today = BusinessConstants.getDate(new Date(System
				.currentTimeMillis()));
		if (today.equals(s)) {
			sectionHolder.timeTv.setText("今天");
		} else {
			sectionHolder.timeTv.setText(s);
		}
		return view;
	}

	@Override
	public View getItemView(final int section, int position, View view,
			ViewGroup parent) {
		final MessageInfo info = (MessageInfo) getItem(section, position);
		MessageHolder holder = null;
		if (view == null) {
			view = View.inflate(context, R.layout.message_center_item_layout,
					null);
			holder = new MessageHolder();
			holder.iconIv = (ImageView) view.findViewById(R.id.item_icon);
			holder.dayTv = (TextView) view.findViewById(R.id.day);
			holder.titleTv = (TextView) view.findViewById(R.id.title);
			holder.typeTv = (ImageView) view.findViewById(R.id.mark);
			holder.contentTv = (TextView) view.findViewById(R.id.content);
			holder.topTv = (TextView) view.findViewById(R.id.top);
			holder.unreadIcon = (ImageView) view.findViewById(R.id.unread);
			view.setTag(holder);
		}
		holder = (MessageHolder) view.getTag();
		setupReadStatus(info, holder);
		setupDate(info, holder);
		loadHeadIcon(info, holder.iconIv);
		setupTitle(info, holder);
		setupContent(info, holder);
		setupTop(info, holder);
		setupClickListener(view, info);
		return view;
	}

	private void setupClickListener(View view, final MessageInfo info) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (info.getIsRead() == 0) {
					info.setIsRead(1);
					readListener.uploadReadStatue(info);
					readListener.needRefrash(true);
					MainActivity.getHandler().sendEmptyMessage(
							Config.REDUCTION_UNREAD_COUNT);
				}
				if ("task".equalsIgnoreCase(info.getType())) {
					TaskInfo taskInfo = new Gson().fromJson(info.getPayload(),
							TaskInfo.class);
					taskInfo.setIcon(info.getIcon());
					TaskDetailActivity.launchForResult(fragment, taskInfo,
							MessageFragment.REQUEST_NEED_REFRESH);
				} else if ("notice".equalsIgnoreCase(info.getType())) {
					Type type = new TypeToken<NoticeInfo>() {
					}.getType();
					NoticeInfo noticeInfo = new GsonConverter(GsonHolder
							.getInstance().getGson()).convertToObject(
							info.getPayload(), type);
					Intent noticeIntent = new Intent(context,
							NotificationDetailActivity.class);
					noticeIntent.putExtra("notice_info", noticeInfo);
					noticeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(noticeIntent);
				} else if ("JOIN_CUST_APPLY".equalsIgnoreCase(info.getType())) {
					JoinCustHandleActivity.launchWithMessageInfo(fragment,
							info, MessageFragment.REQUEST_NEED_REFRESH);
				} else if ("CREATE_CUST_APPLY".equalsIgnoreCase(info.getType())) {
					CreateCustHandleActivity.launchWithMessageInfo(fragment,
							info, MessageFragment.REQUEST_NEED_REFRESH);
				} else if (MessageInfo.CUST_JOIN_INVITEINFO
						.equalsIgnoreCase(info.getType())) {
					CustJoinInviteHandleActivity.launchWithMessageInfo(
							fragment, info,
							MessageFragment.REQUEST_NEED_REFRESH);
				} else if (MessageInfo.MESSAGE.equalsIgnoreCase(info.getType())) {
					MessageDetailActivity.launchWithMessageInfo(fragment, info,
							MessageFragment.REQUEST_NEED_REFRESH);
				} else if (MessageInfo.SHARE_LINK.equalsIgnoreCase(info
						.getType())) {
					ShareLinkActivity.launchWithMessageInfo(fragment, info,
							MessageFragment.REQUEST_NEED_REFRESH);
				} else if (MessageInfo.INVITE_USER.equalsIgnoreCase(info
						.getType())) {
					InviteUserHandleActivity.launchWithMessageInfo(fragment,
							info, MessageFragment.REQUEST_NEED_REFRESH);
				} else {
					MessageDetailActivity.launchWithMessageInfo(fragment, info,
							MessageFragment.REQUEST_NEED_REFRESH);
				}

				/*记录操作 1301*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_NEWS_DETAIL);
			}
		});
	}

	private void setupTop(final MessageInfo info, MessageHolder holder) {
		Resources resources = MainApplication.getAppContext().getResources();
		Drawable leftDrawable = resources.getDrawable(R.drawable.set_top);
		holder.topTv.setCompoundDrawablePadding(resources
				.getDimensionPixelSize(R.dimen.view_pager_spacing));
		holder.topTv.setCompoundDrawablesWithIntrinsicBounds(leftDrawable,
				null, null, null);
		holder.topTv.setText("置顶");
		holder.topTv.setTextColor(resources.getColor(R.color.normal_black));
		holder.topTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.onAddToTop(info);
				notifyDataSetChanged();
				listener.onAdapterChange();

                 /*记录操作 0403*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_MESSAGE_CENTER_TOP);
			}
		});
	}

	private void setupContent(final MessageInfo info, MessageHolder holder) {
		if ("task".equalsIgnoreCase(info.getType())) {
			setupTaskContent(info, holder);
		} else if ("notice".equalsIgnoreCase(info.getType())) {
			setupNoticeContent(info, holder);
		} else {
			setupOtherContent(info, holder);
		}
	}

	private void setupOtherContent(final MessageInfo info, MessageHolder holder) {
		holder.typeTv.setImageResource(R.drawable.message_center_cust_icon);
		holder.contentTv.setText(info.getContent());
	}

	private void setupNoticeContent(final MessageInfo info, MessageHolder holder) {
		holder.typeTv.setImageResource(R.drawable.message_center_notice_icon);
		holder.contentTv.setText(info.getContent());
	}

	private void setupTaskContent(final MessageInfo info, MessageHolder holder) {
		holder.typeTv.setImageResource(R.drawable.message_center_task_icon);
		String content = info.getContent();
		Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
				Html.fromHtml(content));
		Spannable processedText = URLSpanNoUnderline
				.removeUnderlines(spannedText);
		holder.contentTv.setText(processedText);
	}

	private void setupTitle(final MessageInfo info, MessageHolder holder) {
		holder.titleTv.setText(info.getTitle());
	}

	private void setupDate(final MessageInfo info, MessageHolder holder) {
		String day = info.getUpdateTime();
		try {
			final Date date = BusinessConstants.parseTime(info.getUpdateTime());
			day = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		holder.dayTv.setText(day);
	}

	private void setupReadStatus(final MessageInfo info, MessageHolder holder) {
		if (0 == info.getIsRead()) {
			holder.unreadIcon.setVisibility(View.VISIBLE);
		} else {
			holder.unreadIcon.setVisibility(View.GONE);
		}
	}

	private void loadHeadIcon(final MessageInfo info, final ImageView IconTemp) {
		IconTemp.setImageResource(R.drawable.contactlist_contact_icon_default);
		String icon = info.getIcon();
		if (TextUtils.isEmpty(icon)) {
			return;
		}
		IconTemp.setTag(icon);
		MainApplication.getHeadImageLoader().get(icon,
				new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						if (container.getUrl().equals(IconTemp.getTag())) {
							IconTemp.setImageBitmap(container.getBitmap());
						}
					}

					@Override
					public void onError(ImageContainer container) {
						if (container.getUrl().equals(IconTemp.getTag())) {
							IconTemp.setImageResource(R.drawable.contactlist_contact_icon_default);
						}
					}
				});
	};

	@Override
	public int getSectionCount() {
		return super.getSectionCount();
	}

	private OnReadMessageListener readListener;

	// private Bitmap defaultHead;

	public void setReadStatusListener(OnReadMessageListener listener) {
		readListener = listener;
	}

	public class SectionHolder {
		public TextView timeTv;
	}

}
