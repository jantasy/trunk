package cn.yjt.oa.app.notifications;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.ClipboardUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.CopyPopupWindow;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.permisson.PermissionManager;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class NotificationActivity extends TitleFragmentActivity implements
		OnItemClickListener,AdapterView.OnItemLongClickListener{
	private List<NoticeInfo> list = new ArrayList<NoticeInfo>();
	private NoticeAdapter adapter;
	private PullToRefreshListView listView;
	private boolean isRefrash = false;
	private View progressView;
	private static final int MAX = 20;
	public static final int CREAT_NOTIFICATION_REFRASH = 19;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {

			/*记录操作 0901*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_NOTIFICATION);
			setContentView(R.layout.notification_activity_layout);
			configNavigation();
			progressView = findViewById(R.id.progress);
			listView = (PullToRefreshListView) findViewById(R.id.list_view);
			listView.setOnItemClickListener(this);
			listView.setOnItemLongClickListener(this);
			listView.setOnRefreshListener(noticeRefreshListener);
			listView.setOnLoadMoreListner(new OnLoadMoreListner() {

				@Override
				public void onLoadMore() {
					getNotificatioInfos(list.size());
				}
			});
			adapter = new NoticeAdapter();
			listView.setAdapter(adapter);
			getNotificatioInfos(0);
		}
	}

	public OnRefreshListener noticeRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			isRefrash = true;
			getNotificatioInfos(0);
		}
	};
	private Cancelable cancelable;

	private void configNavigation() {
		if (PermissionManager
				.verifyCode(AccountManager.getCurrent(this).getPermission(),
						PermissionManager.PERMISSION_CODE_SEND_NOTICE)) {
			getRightButton().setImageResource(R.drawable.contact_add_group);
		}
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		if (PermissionManager
				.verifyCode(AccountManager.getCurrent(this).getPermission(),
						PermissionManager.PERMISSION_CODE_SEND_NOTICE)) {
			NotificationPublishActivity.launch(this, CREAT_NOTIFICATION_REFRASH);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CREAT_NOTIFICATION_REFRASH && resultCode == RESULT_OK) {
			noticeRefreshListener.onRefresh();
		}
	}

	private void getNotificatioInfos(int from) {
		if (!CheckNetUtils.hasNetWork(this)) {
			Toast.makeText(this, R.string.connect_network_fail,
					Toast.LENGTH_SHORT).show();
			if (isRefrash) {
				listView.onRefreshComplete();
				isRefrash = false;

			} else {
				listView.onLoadMoreComplete();
			}
			return;
		}
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_NOTICE);
		Type type = new TypeToken<Response<ListSlice<NoticeInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.addPageQueryParameter(from, MAX);
		builder.setResponseListener(new Listener<Response<ListSlice<NoticeInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				Log.d("error", arg0.getMessage(),arg0);
				cancelable = null;
				if (progressView.getVisibility() == View.VISIBLE) {
					progressView.setVisibility(View.GONE);
				}

				listView.onLoadMoreComplete();
				Toast.makeText(NotificationActivity.this, "加载消息失败", Toast.LENGTH_SHORT).show();
				if (isRefrash) {
					listView.onRefreshComplete();
					isRefrash = false;

				} else {
					listView.onLoadMoreComplete();
				}
			}

			@Override
			public void onResponse(Response<ListSlice<NoticeInfo>> arg0) {
				cancelable = null;
				if (progressView.getVisibility() == View.VISIBLE) {
					progressView.setVisibility(View.GONE);
				}
				// TODO Auto-generated method stub
				if (isRefrash) {
					list.clear();
					listView.onRefreshComplete();
					isRefrash = false;

				} else {
					listView.onLoadMoreComplete();
				}
				if (arg0.getCode() == 0) {
					ListSlice ls = arg0.getPayload();
					if (ls != null) {
						List<NoticeInfo> listTemp = ls.getContent();
						if (listTemp != null) {
							if (list.size() > 0 && listTemp.size() == 0) {
								Toast.makeText(NotificationActivity.this,
										"已经没有信息了", Toast.LENGTH_SHORT).show();
							} else {
								list.addAll(listTemp);
								adapter.notifyDataSetChanged();
							}
						}

					}
				} else {
					Toast.makeText(NotificationActivity.this, "加载消息失败", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});
		cancelable = builder.build().get();
	}
	
	@Override
	protected void onDestroy() {
		if(cancelable != null){
			cancelable.cancel();
		}
		super.onDestroy();
	}



	class NoticeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			NoticeHolder holder = null;
			if (view == null) {
				view = View.inflate(NotificationActivity.this,
						R.layout.notification_item_layout, null);
				holder = new NoticeHolder();
				holder.iconIv = (ImageView) view.findViewById(R.id.notice_icon);
				holder.titleTv = (TextView) view
						.findViewById(R.id.notice_title);
				holder.contentTv = (TextView) view
						.findViewById(R.id.notice_content);
				holder.timeTv = (TextView) view.findViewById(R.id.notice_time);
				holder.del = view.findViewById(R.id.notice_del);

				view.setTag(holder);
			}
			holder = (NoticeHolder) view.getTag();
			if (PermissionManager.verifyCode(
					AccountManager.getCurrent(getApplicationContext())
							.getPermission(),
					PermissionManager.PERMISSION_CODE_DEL_NOTICE)) {
				holder.del.setVisibility(View.VISIBLE);
			} else {
				holder.del.setVisibility(View.GONE);
			}
			final NoticeInfo info = list.get(position);
			String image = info.getImage();
			holder.iconIv.setImageResource(R.drawable.default_image);
			if (!TextUtils.isEmpty(image)) {
				AsyncRequest.getInImageView(
						image,
						holder.iconIv,
						getResources().getDimensionPixelSize(
								R.dimen.notice_icon_width),
						getResources().getDimensionPixelSize(
								R.dimen.notice_icon_height),
						R.drawable.default_image, R.drawable.default_image);
			}
			holder.titleTv.setText(info.getTitle());
			holder.contentTv.setText(info.getContent());
			String time = info.getCreateTime();
			try {
				Date date = BusinessConstants.parseTime(info.getCreateTime());
				SimpleDateFormat formart = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				time = formart.format(date);
				System.out.println(date.toString() + "   " + time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.timeTv.setText(time);
			holder.del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteConfirm(info);

					/*记录操作 0904*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_NOTIFICATION);
				}
			});
			return view;
		}

	}

	private void deleteConfirm(final NoticeInfo info) {
		AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
		builder.setTitle("删除电子公告").setMessage("确认删除此电子公告，删除后不可恢复")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete(info);
					}
				}).setNegativeButton("取消", null).show();
	}

	private void delete(final NoticeInfo info) {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_NOTICE)
				.setModuleItem(String.valueOf(info.getId()))
				.setResponseType(responseType)
				.setResponseListener(new Listener<Response<String>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						Toast.makeText(getApplicationContext(), "删除失败",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(Response<String> arg0) {
						if (arg0.getCode() == 0) {
							list.remove(info);
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(),
									arg0.getDescription(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).build().delete();
	}

	class NoticeHolder {
		public ImageView iconIv;
		public TextView titleTv;
		public TextView contentTv;
		public TextView timeTv;
		public View del;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NoticeInfo info = list.get(position - 1);
		Intent intent = new Intent(this, NotificationDetailActivity.class);
		intent.putExtra("notice_info", info);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final NoticeHolder holder = (NoticeHolder) view.getTag();
		CopyPopupWindow copyWindow = new CopyPopupWindow(NotificationActivity.this);

		copyWindow.showAsDropDown(view, dp2px(100), dp2px(-25));
		copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
			@Override
			public void onCopyClick() {
				ClipboardUtils.copyToClipboard(holder.contentTv.getText().toString());
			}
		});
		return true;
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, NotificationActivity.class);
		context.startActivity(intent);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
