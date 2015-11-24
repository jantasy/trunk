package cn.yjt.oa.app.dashboard;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.OnBackPressedInterface;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.app.download.AppDownloadManager;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.download.AppDownloadManager.Status;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.DashBoardItem;
import cn.yjt.oa.app.beans.DashBoardItem.IconCallback;
import cn.yjt.oa.app.beans.DashBoardItem.IconResCallback;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.dashboard.OperationMenu.OperationListener;
import cn.yjt.oa.app.dashboard.config.DashBoardDefaultFilter;
import cn.yjt.oa.app.dashboard.config.DashBoardHelper;
import cn.yjt.oa.app.dashboard.config.DashBoardHideDefaultFilter;
import cn.yjt.oa.app.dashboard.config.DashBoardLoader;
import cn.yjt.oa.app.dashboard.config.DashBoardServerConfigFilter;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.office.ConnectedAppUtil;
import cn.yjt.oa.app.task.TaskPublishingActivity;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.GridViewDropSpace;
import cn.yjt.oa.app.widget.GridViewDropSpace.DragOverListener;
import cn.yjt.oa.app.widget.dragdrop.DragContainer;

public class DashBoardFragment extends BaseFragment implements
		AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,
		GridViewDropSpace.ReorderListener, View.OnClickListener,
		OnBackPressedInterface, DragOverListener {

	private List<DashBoardItem> dashBoardItems;
	private AsyncTask<?, ?, ?> loadTask;
	private AsyncTask<?, ?, ?> saveTask;
	private GridViewDropSpace gridViewDropSpace;

	private static final long ITEMID_ADD = 1;

	private static final int REQUEST_ADDICON = 1;
	private DashBoardServerConfigFilter dashBoardServerConfigFilter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity activity = (MainActivity) getActivity();
		activity.addToFragments(1, this);

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.dashboard, container, false);
			promt(rootView);

			rootView.findViewById(R.id.addtask).setOnClickListener(this);
			GridViewDropSpace gridView = (GridViewDropSpace) rootView
					.findViewById(R.id.gridview);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gridView.setDragContainer((DragContainer) rootView
					.findViewById(R.id.dragcontainer));
			gridView.setAdapter(adapter);
			gridView.setOnItemLongClickListener(this);
			gridView.setOnItemClickListener(this);
			gridView.setReorderListener(this);
			gridView.setDragOverListener(this);

			gridViewDropSpace = gridView;
			gridViewDropSpace.setIgnorePosition(adapter.getCount() - 1);
		}
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
	}

	private View promt;

	private void promt(View rootView) {
		promt = rootView.findViewById(R.id.dashboard_promt);
		if (StorageUtils.isFirstForPromt(getActivity())) {
			promt.setVisibility(View.VISIBLE);
		} else {
			promt.setVisibility(View.GONE);
		}
		promt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StorageUtils.saveDashboardPromtState(getActivity());
				promt.setVisibility(View.GONE);
			}
		});
	}

	private BroadcastReceiver appInstallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				final String packageName = intent.getData()
						.getSchemeSpecificPart();
				onPackageChanged(packageName, true);
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				final String packageName = intent.getData()
						.getSchemeSpecificPart();
				onPackageChanged(packageName, false);
			}
		}
	};

	private BroadcastReceiver loginReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			requestDashConfig();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashBoardServerConfigFilter = new DashBoardServerConfigFilter(DashBoardHelper.getServerDashBoard());
		requestDashConfig();
		loadDashboard();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		getActivity().registerReceiver(appInstallReceiver, filter);
		IntentFilter loginFilter = new IntentFilter(
				MainApplication.ACTION_LOGIN);
		getActivity().registerReceiver(loginReceiver, loginFilter);
	}

	private void checkDashBoardItemInstallState() {
		Iterator<DashBoardItem> iterator = dashBoardItems.iterator();
		boolean isChanged = false;
		while (iterator.hasNext()) {
			DashBoardItem item = iterator.next();
			if (item.getPackageName() != null) {
				boolean isAppInstalled = AppUtils.isAppInstalled(MainApplication.getAppContext(),
						item.getPackageName());
				boolean isComponentExist = AppUtils.isComponentExist(MainApplication.getAppContext(),
						item.getPackageName(), item.getClassName());
				if (!isAppInstalled
						&& !isComponentExist) {
					iterator.remove();
					isChanged = true;
				}
			}
		}
		if(isChanged){
			save();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadTask != null) {
			loadTask.cancel(true);
			loadTask = null;
		}
		try {
			getActivity().unregisterReceiver(appInstallReceiver);
		} catch (Throwable e) {
			// ignore
		}
		try {
			getActivity().unregisterReceiver(loginReceiver);
		} catch (Throwable e) {

		}
	}

	private void loadDashboard() {
		dashBoardItems = new DashBoardLoader(new DashBoardDefaultFilter(),dashBoardServerConfigFilter).load();
		checkDashBoardItemInstallState();
		refreshUi();
	}

	
	private void requestDashConfig() {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CONFIG);
		Type type = new TypeToken<Response<ListSlice<DashBoardItem>>>() {}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new ResponseListener<ListSlice<DashBoardItem>>() {

			@Override
			public void onSuccess(ListSlice<DashBoardItem> payload) {
				if(payload!=null){
					dashBoardServerConfigFilter = new DashBoardServerConfigFilter(payload.getContent());
					DashBoardHelper.saveServerConfig(payload.getContent());	
				}
				loadDashboard();
			}
		});
		builder.build().get();
	}

	@SuppressWarnings("unchecked")
	private void save() {
		List<DashBoardItem> items = getAllDashboardItems();
		if (saveTask != null) {
			saveTask.cancel(true);
		}
		saveTask = new AsyncTask<List<DashBoardItem>, Void, Void>() {

			@Override
			protected void onPostExecute(final Void result) {
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(
					final List<DashBoardItem>... params) {
				if (isCancelled())
					return null;
				UserInfo userInfo = AccountManager
						.getCurrent(getActivity());
				if(userInfo != null){
					List<DashBoardItem> defaultDashBoard = DashBoardHelper.getDefaultDashBoard();
					defaultDashBoard.removeAll(params[0]);
					
					DashBoardHideDefaultFilter boardHideDefaultFilter = new DashBoardHideDefaultFilter();
					boardHideDefaultFilter.filt(defaultDashBoard);
					if(dashBoardServerConfigFilter != null){
						dashBoardServerConfigFilter.filt(defaultDashBoard);
					}
					
					DashBoardHelper.save(params[0], defaultDashBoard);
				}
				return null;
			}

		}.execute(items);
	}

	private void addDashBoardItem(DashBoardItem item) {
		dashBoardItems.add(item);
		refreshUi();
		save();
	}

	private void removeDashBoradItem(DashBoardItem item) {
		dashBoardItems.remove(item);
		refreshUi();
		save();
	}

	@SuppressWarnings("unused")
	private DashBoardItem getDashBoardItem(String pkg) {
		for (DashBoardItem item : dashBoardItems) {
			if (pkg.equals(item.getPackageName())) {
				return item;
			}
		}
		return null;
	}

	private List<DashBoardItem> getAllDashboardItems() {
		ArrayList<DashBoardItem> items = new ArrayList<DashBoardItem>(
				dashBoardItems);
		return items;
	}

	private void refreshUi() {
		if (gridViewDropSpace != null) {
			gridViewDropSpace.setIgnorePosition(adapter.getCount() - 1);
			adapter.notifyDataSetChanged();
		}
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.dashboard_item, parent, false);
			}

			DashBoardItem item = (DashBoardItem) getItem(position);
			if (item != null) {
				String title = item.getTitle(getActivity());
				((TextView) convertView)
						.setText(title == null ? "未知应用" : title);
				final TextView textView = (TextView) convertView;
				textView.setCompoundDrawablesWithIntrinsicBounds(0,
						R.drawable.dashboard_add, 0, 0);
				item.getIcon(getActivity(), new IconCallback() {

					@Override
					public void onSuccess(Drawable drawable) {
						textView.setCompoundDrawables(null, drawable, null,
								null);
					}

					@Override
					public void onError() {
						textView.setCompoundDrawablesWithIntrinsicBounds(0,
								R.drawable.app_default_icon, 0, 0);
					}
				});
			} else {
				long id = getItemId(position);
				if (id == ITEMID_ADD) {
					((TextView) convertView).setText("添加");
					((TextView) convertView)
							.setCompoundDrawablesWithIntrinsicBounds(0,
									R.drawable.dashboard_add, 0, 0);
					if (gridViewDropSpace.isInDragMode()) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
					}
				}
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			int size = getDashboardSize();
			if (position == size)
				return ITEMID_ADD;
			return 0;
		}

		@Override
		public Object getItem(int position) {
			int size = getDashboardSize();
			if (position < size)
				return dashBoardItems.get(position);
			else
				return null;
		}

		@Override
		public int getCount() {
			return getDashboardSize() + 1;
		}

		private int getDashboardSize() {
			return dashBoardItems != null ? dashBoardItems.size() : 0;
		}
	};
	private OperationMenu operationMenu;
	private View rootView;

	@Override
	public boolean onItemLongClick(final AdapterView<?> parent, View view,
			final int position, long id) {
		if (ITEMID_ADD != id) {
			gridViewDropSpace.startDragMode(position, view);
			int lastVisible = gridViewDropSpace.getLastVisiblePosition();
			if (lastVisible == adapter.getCount() - 1) {
				gridViewDropSpace.getChildAt(
						gridViewDropSpace.getChildCount() - 1).setVisibility(
						View.INVISIBLE);
			}
			final DashBoardItem item = (DashBoardItem) adapter
					.getItem(position);
			operationMenu = new OperationMenu(getActivity());

			operationMenu.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					operationMenu = null;
				}
			});
			operationMenu.setOperationListener(new OperationListener() {

				@Override
				public void onUninstallClicked() {

				}

				@Override
				public void onHideClicked() {
					removeDashBoradItem(item);
				}

				@Override
				public void onDesktopClicked() {
					item.getIconRes(getActivity(), new IconResCallback() {

						@Override
						public void onSuccess(Object obj) {

							ViewUtil.sendToDesktop(getActivity(),
									item.getClassName(), item.getPackageName(),
									item.getTitle(getActivity()),
									item.getIconResUri(), obj);
							Toast.makeText(getActivity(), "已发送到桌面快捷方式",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onError() {
							ViewUtil.sendToDesktop(getActivity(),
									item.getClassName(), item.getPackageName(),
									item.getTitle(getActivity()),
									item.getIconResUri(),
									R.drawable.app_default_icon);
							Toast.makeText(getActivity(), "已发送到桌面快捷方式",
									Toast.LENGTH_SHORT).show();

						}
					});

				}
			});
			if (item.getClassName() != null
					&& item.getClassName().startsWith(DashBoardItem.DEVELOPING)) {
				operationMenu.isVisible(false, false);
				operationMenu.showAsDropDown(view, 0, -40);
			} else {

				operationMenu.isVisible(false, true);
				operationMenu.showAsDropDown(
						view,
						-getResources().getDimensionPixelSize(
								R.dimen.activity_horizontal_margin), -40);
			}

		}
		return true;
	}

	@Override
	public boolean onReorder(int fromPos, int toPos) {
		DashBoardItem item = dashBoardItems.remove(fromPos);
		dashBoardItems.add(toPos, item);
		save();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (ITEMID_ADD == id) {
			AdditionalIconUtils.startAppActiviyForResult(this, getActivity(),
					(ArrayList<? extends AdditionalIcon>) DashBoardHelper.getUserHideDashBoard(), getAddedPackages(),
					REQUEST_ADDICON);
		} else {

			DashBoardItem item = (DashBoardItem) adapter.getItem(position);
			if (item.getClassName() != null
					&& item.getClassName().startsWith(DashBoardItem.DEVELOPING)) {
				Toast.makeText(getActivity(), "即将推出，敬请期待", Toast.LENGTH_SHORT)
						.show();
			} else {
				launcheDashBoardItem(item);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ADDICON: {
				int index = data.getIntExtra(
						AdditionalIconUtils.EXTRA_RESULT_INDEX, -1);
				String pkg = data
						.getStringExtra(AdditionalIconUtils.EXTRA_RESULT_PKG);
				if (index >= 0) {
					// add hided items
					DashBoardItem item = DashBoardHelper.getUserHideDashBoard().get(index);
					addDashBoardItem(item);
				} else if (!TextUtils.isEmpty(pkg)) {
					// add pkg
					final PackageManager packageManager = getActivity()
							.getPackageManager();
					DashBoardItem item = new DashBoardItem();
					item.setPackageName(pkg);
					item.setIconResUri(data
							.getStringExtra(AdditionalIconUtils.EXTRA_RESULT_ICON_URL));
					item.setTitle(data
							.getStringExtra(AdditionalIconUtils.EXTRA_RESULT_APPNAME));
					try {
						packageManager.getPackageInfo(pkg, 0);
						item.setInstalled(true);
					} catch (PackageManager.NameNotFoundException e) {
						// app not installed
						item.setInstalled(false);
					}
					addDashBoardItem(item);
				}
			}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (R.id.addtask == v.getId()) {
			TaskPublishingActivity.launch(this);
		}
	}

	private void launcheDashBoardItem(DashBoardItem item) {

		if(item!=null){
			if("com.gsww.lecare.hute".equals(item.getPackageName())){
				//乐健康
				/*记录操作 1501*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_HEALTHY);
			}
			if("com.gsww.androidnma.activity".equals(item.getPackageName())){
				//综合办公
				/*记录操作 1502*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_OFFICE);
			}
			if("com.corp21cn.mail189".equals(item.getPackageName())){
				//189邮箱
				/*记录操作 1503*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_189EMAIL);
			}
			if("com.ctsi.android.mts.client".equals(item.getPackageName())){
				//外勤助手
				/*记录操作 1504*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_OUTWORK);
			}
		}

		if (item.isInstalled()) {
			PackageManager pm = getActivity().getPackageManager();
			if (TextUtils.isEmpty(item.getClassName())) {
				if (ConnectedAppUtil.isConnectorApp(item.getPackageName())) {
					ConnectedAppUtil.launchPackage(getActivity(),
							item.getPackageName());
					return;
				}
				Intent intent = pm.getLaunchIntentForPackage(item
						.getPackageName());
				startActivity(intent);
			} else {
				Intent intent = new Intent();
				intent.setClassName(item.getPackageName(), item.getClassName());
				startActivity(intent);

				if("com.zbar.lib.CaptureActivity".equals(item.getClassName())){
					/*记录操作 0803*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_QRCODE);
				}
			}

		} else {
			AppDownloadTask task = AppDownloadManager
					.getAppDownloadTaskForPackage(item.getPackageName());
			if (task == null) {
				Toast.makeText(getActivity(), "开始下载", Toast.LENGTH_LONG).show();
				AppDownloadManager.downloadWithNotification(getActivity(),
						item.getPackageName());
			} else {
				if (task.getStatus() == Status.FINISHED) {
					if (task.getAppDownloadedFile() != null) {
						AppUtils.appInstall(getActivity(),
								task.getAppDownloadedFile());
					}
				} else {
					// task running
					Toast.makeText(getActivity(), "应用正在下载中请稍候",
							Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	@Override
	public CharSequence getPageTitle(Context context) {
		return "工作台";
	}

	@Override
	public boolean onRightButtonClick() {
		return false;
	}

	@Override
	public void configRightButton(ImageView imgView) {
		imgView.setImageBitmap(null);
	}

	private void onPackageChanged(String packageName, boolean isAddOrRemove) {
		if (dashBoardItems == null || packageName == null)
			return;

		Iterator<DashBoardItem> iterator = dashBoardItems.iterator();
		while (iterator.hasNext()) {
			DashBoardItem item = iterator.next();
			if (packageName.equals(item.getPackageName())) {
				item.setInstalled(isAddOrRemove);
				if (!isAddOrRemove) {
					iterator.remove();
					refreshUi();
					save();
				}
			}
		}
	}

	private ArrayList<String> getAddedPackages() {
		System.out.println("");
		
		//TODO
		if (dashBoardItems == null)
			return new ArrayList<String>();
		ArrayList<String> pkgs = new ArrayList<String>();

		for (DashBoardItem item : dashBoardItems) {
			pkgs.add(item.getPackageName());
		}

		return pkgs;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onDragOver(float disX, float disY) {
		int scaledTouchSlop = ViewConfiguration.get(getActivity())
				.getScaledTouchSlop();
		if (Math.abs(disX) > scaledTouchSlop
				|| Math.abs(disY) > scaledTouchSlop) {
			if (operationMenu != null) {
				operationMenu.dismiss();
			}
		}
	}
}
