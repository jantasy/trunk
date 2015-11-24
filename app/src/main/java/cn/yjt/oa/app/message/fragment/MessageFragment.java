package cn.yjt.oa.app.message.fragment;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.MessageInfoCache;
import cn.yjt.oa.app.beans.MessageInfoContentCache;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TimeClockFragment;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.message.Message;
import cn.yjt.oa.app.message.MessageManager;
import cn.yjt.oa.app.message.MessageObserver;
import cn.yjt.oa.app.message.MessageScrollToPos;
import cn.yjt.oa.app.message.fragment.TopMessageIdManager.OnTopChangedDataListener;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.GooView.onGooViewDispperListener;
import cn.yjt.oa.app.widget.WarapSectionAdapter;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * 消息中心展示消息的Fragment
 * 
 */
public class MessageFragment extends TimeClockFragment implements
		OnTopChangedDataListener, OnReadMessageListener,
		onGooViewDispperListener, MessageScrollToPos {
	static final String TAG = MessageFragment.class.getSimpleName();

	public static final int REQUEST_NEED_REFRESH = 100;
	private View root;

	// 自定义的下拉刷新listview
	private PullToRefreshListView listView;

	// 置顶消息适配器
	public MessageTopAdapter allTopAdapter;
	// 非置顶消息适配器
	public MessageCenterAdapter allCenterAdapter;
	public WarapSectionAdapter mAdapter;

	private Cancelable topMessageTask;
	private Cancelable otherMessageTask;

	// 是否正在加载
	private boolean isRefreshing;
	// 是否是第一次显示
	private boolean isFirstShow = true;

	// 过滤字符串（默认为all）
	private String filter = "all";
	private int from;
	private static final int MAX = 20;

	// 存放置顶消息的集合
	private List<MessageInfo> topMessageList = new ArrayList<MessageInfo>();
	// 存放其他消息的集合
	private List<MessageInfo> centerMessageList = new ArrayList<MessageInfo>();
	// 定义一个缓存线程池
	private static ExecutorService executorService = Executors
			.newCachedThreadPool();
	// 定义一个Handler
	public static Handler myHandler = new Handler();

	//存放当前消息中心未读消息的下标的集合。
	private List<Integer> unReadIndex = new ArrayList<Integer>();
	//定义一个标识符，判断adapter是否发生变化。
	private boolean isAdapterChange = false;

	private boolean needRefrash;
	// ----------构造方法------------
	public MessageFragment() {
	}

	public MessageFragment(String filter) {
		this();
		this.filter = filter;
	}

	// ----------------------------

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			filter = savedInstanceState.getString("filter");
		}
		if (MainActivity.sGvRedPoint != null) {
			if ("all".equalsIgnoreCase(filter)) {
				MainActivity.sGvRedPoint.setOnGooViewDispperListener(this);
				Log.e("aaa", "注册成功");
			}
		}
		// 置顶消息管理注册监听，监听置顶消息的改变
		TopMessageIdManager.registTopChangeListener(this);
		// TODO：
		MessageManager.getInstance().registerManagerObserver(messageObserver);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (getActivity() != null) {
				((MainActivity) getActivity()).setMessageScrollListener(this);
				LogUtils.e(TAG, "setUserVisibleHint");
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消所有当前任务
		cancelCurrentTasks();
		// 解注册
		TopMessageIdManager.unregistTopChangeListener(this);
		MessageManager.getInstance().unregisterManagerObserver(messageObserver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 将filter保存起来
		outState.putString("filter", filter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (needRefrash) {
			mAdapter.notifyDataSetChanged();
			adapterIsChanging();
			needRefrash = false;
		}

	}

	// 置顶消息ui监听器
	// listener registed to view adapters
	OnTopChangedUiListener listener = new OnTopChangedUiListener() {

		@Override
		public void onAddToTop(MessageInfo message) {
			TopMessageIdManager.addTopMessage(message);
		}

		@Override
		public void onRemoveFromTop(MessageInfo message) {
			TopMessageIdManager.removeTopMessage(message);
		}

		@Override
		public void onAdapterChange() {
			adapterIsChanging();
		}

	};

	// --------------OnTopChangedDataListener接口的复写方法---------------
	@Override
	public void onAddTopMessage(MessageInfo msg) {
		onAddToTop(msg);
	}

	@Override
	public void onRemoveTopMessage(MessageInfo msg) {
		onRemoveFromTop(msg);
	}

	@Override
	public void onRemoveTopMsgId(long msgId) {
		onRemoveFromTop(allTopAdapter.getMessageById(msgId));
	}

	// ----------------------------------------------------------------

	/**
	 * 添加置顶消息
	 * 
	 * @param message 所要添加置顶的消息
	 */
	// notified by parent activity
	public void onAddToTop(MessageInfo message) {
		if ("task".equalsIgnoreCase(filter)) {
			if ("task".equalsIgnoreCase(message.getType())) {
				// if (adapter != null){
				allTopAdapter.add(message);
				allCenterAdapter.removeEntry(message);
				mAdapter.notifyDataSetChanged();
				adapterIsChanging();
				// }
			}
		} else if ("notice".equalsIgnoreCase(filter)) {
			if ("notice".equalsIgnoreCase(message.getType())) {
				// if (adapter != null){
				allTopAdapter.add(message);
				allCenterAdapter.removeEntry(message);
				mAdapter.notifyDataSetChanged();
				adapterIsChanging();
				// }
			}
		} else if ("all".equalsIgnoreCase(filter)) {
			// if (adapter != null){
			allTopAdapter.add(message);
			allCenterAdapter.removeEntry(message);
			mAdapter.notifyDataSetChanged();
			adapterIsChanging();
			// }
		}

	}

	/**
	 * 移除置顶消息
	 * 
	 * @param message 所要移除的置顶消息
	 */
	// notified by parent activity
	public void onRemoveFromTop(MessageInfo message) {
		if (message != null) {
			if ("task".equalsIgnoreCase(filter)) {
				if ("task".equalsIgnoreCase(message.getType())) {
					if (mAdapter != null) {
						topMessageList.remove(message);
						allTopAdapter.remove(message);
						allCenterAdapter.addEntry(message);
						mAdapter.notifyDataSetChanged();
						adapterIsChanging();
					}
				}
			} else if ("notice".equalsIgnoreCase(filter)) {
				if ("notice".equalsIgnoreCase(message.getType())) {
					if (mAdapter != null) {
						topMessageList.remove(message);
						allTopAdapter.remove(message);
						allCenterAdapter.addEntry(message);
						mAdapter.notifyDataSetChanged();
						adapterIsChanging();
					}
				}
			} else if ("all".equalsIgnoreCase(filter)) {
				if (mAdapter != null) {
					topMessageList.remove(message);
					allTopAdapter.remove(message);
					allCenterAdapter.addEntry(message);
					mAdapter.notifyDataSetChanged();
					adapterIsChanging();
				}
			}
		}
	}

	/**
	 * 加载数据
	 */
	public void loadData() {
		//获取企业是否更换
		boolean isDeptChange = StorageUtils.getSystemSettings(getActivity()).getBoolean(Config.IS_DEPT_CHANGED, true);
		// 取消所有任务
		cancelCurrentTasks();
		// 先获取的是置顶消息，如果是第一次获取，先获取本地缓存的
		if (isFirstShow) {
			//如果更换了企业，就不读取缓存数据
			if(!isDeptChange){
				getTopMessageFromLocal();
				getOtherMessagesFromLocal();
			}else{
				StorageUtils.getSystemSettings(getActivity()).edit()
				.putBoolean(Config.IS_DEPT_CHANGED, false)
				.commit();
			}
		}
		getTopMessages();
		getOtherMessages();

		// 将该fragment是否是第一次展示设为false
		isFirstShow = false;
		// 将是否正在刷新设为true
		isRefreshing = true;
	}

	/**
	 * 只从服务器获取数据
	 */
	public void loadDataForService() {
		cancelCurrentTasks();
		getTopMessages();
		getOtherMessages();
		isRefreshing = true;
	}

	/**
	 * 刷新数据
	 */
	public void refreshData() {
		if (listView != null) {
			listView.setRefreshingState();
			from = 0;
			isRefreshing = true;
			loadData();
		}
	}

	// 取消当前所有的任务
	private void cancelCurrentTasks() {
		if (topMessageTask != null) {
			topMessageTask.cancel();
			topMessageTask = null;
		}

		if (otherMessageTask != null) {
			otherMessageTask.cancel();
			otherMessageTask = null;
		}
	}

	// 缓存置顶消息
	private void cacheTopMessage(List<MessageInfo> topMessageListTemp) {
		// 创建缓存消息的集合
		final List<MessageInfoCache> cacheTemp = new ArrayList<MessageInfoCache>();
		// 将置顶的消息添加到缓存集合中
		for (MessageInfo mi : topMessageListTemp) {
			cacheTemp.add(new MessageInfoCache(mi));
		}
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				// 删除之前缓存的置顶消息，并重新缓存
				new Delete().from(MessageInfoCache.class).execute();
				for (MessageInfoCache mic : cacheTemp) {
					mic.save();
				}
				// }
			}
		});
	}

	// 缓存其他消息（非置顶）
	private void cacheOtherMessage() {

		final List<MessageInfoContentCache> cacheTemp = new ArrayList<MessageInfoContentCache>();
		for (MessageInfo mi : centerMessageList) {
			cacheTemp.add(new MessageInfoContentCache(mi));
		}
		if ("all".equalsIgnoreCase(filter)) {
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					new Delete().from(MessageInfoContentCache.class).execute();
					for (MessageInfoContentCache micc : cacheTemp) {
						micc.save();
					}
				}
			});
		}
	}

	// 过滤置顶的消息，根据消息的类型过滤掉不属于当前fragment的filter的消息
	private void filterTopMessage(MessageInfo mi) {
		if (!TopMessageIdManager.containsTopMsgId(mi.getId())) {
			return;
		}
		if ("task".equalsIgnoreCase(filter)) {
			if ("task".equalsIgnoreCase(mi.getType())) {
				topMessageList.add(mi);
			}
		} else if ("notice".equalsIgnoreCase(filter)) {
			if ("notice".equalsIgnoreCase(mi.getType())) {
				topMessageList.add(mi);
			}

		} else if ("all".equalsIgnoreCase(filter)) {
			topMessageList.add(mi);
		}
	}

	// 过滤其他的消息（非置顶），过滤掉其他消息中非置顶，其消息类型不属于当前fragment的fitler
	private void filterOtherMessage(Iterator<MessageInfo> it, MessageInfo mf) {
		boolean isTopMessage = false;
		for (MessageInfo info : topMessageList) {
			if (info.getId() == mf.getId()) {
				isTopMessage = true;
				break;
			}
		}
		if (!isTopMessage) {
			if (TopMessageIdManager.containsTopMsgId(mf.getId())) {
				it.remove();
			}
			if ("task".equalsIgnoreCase(filter)) {
				if ("task".equalsIgnoreCase(mf.getType())) {
					centerMessageList.add(mf);
				}
			} else if ("notice".equalsIgnoreCase(filter)) {
				if ("notice".equalsIgnoreCase(mf.getType())) {
					centerMessageList.add(mf);
				}

			} else if ("all".equalsIgnoreCase(filter)) {
				centerMessageList.add(mf);
			}
		}
	}

	// 获取置顶的消息
	private void getTopMessages() {

		// 从TopMessageIdManager获取置顶小消息的Id
		// （TopMessageIdManager中将置顶消息的id，拼接在一起保存在sp中）
		String topMsgIdsString = TopMessageIdManager.getTopMsgIdsString();
		Log.e("ccc", "置顶消息的id" + topMsgIdsString);
		// 如果没有置顶消息，就不继续往下执行了
		if (TextUtils.isEmpty(topMsgIdsString)) {
			return;
		}
		// TODO：请求网络
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MESSAGE_CENTER);
		builder.addQueryParameter("messageIds", topMsgIdsString);
		Type type = new TypeToken<Response<List<MessageInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<List<MessageInfo>>>() {

			@Override
			public void onResponse(Response<List<MessageInfo>> response) {
				if (response.getCode() == 0) {
					// 判断存放置顶消息的集合是否为空，如果为空，创建一个新的，如果不为空就清除集合中的数据
					if (topMessageList != null) {
						topMessageList.clear();
					} else {
						topMessageList = new ArrayList<MessageInfo>();
					}
					// 响应成功后，首先将得到的消息内容赋值给listTemp
					List<MessageInfo> topMessageListTemp = response
							.getPayload();
					// 如果listTemp
					if (topMessageListTemp != null) {
						for (MessageInfo mi : topMessageListTemp) {
							// Log.e("ccc", "TOP:"+mi.toString());
							// 过滤置顶消息
							filterTopMessage(mi);
						}
						// 缓存置顶消息
						cacheTopMessage(topMessageListTemp);
					}
					// 更新ui的显示
					allTopAdapter.setTop(topMessageList);
					mAdapter.notifyDataSetChanged();
					adapterIsChanging();
				} else {
					Toast.makeText(getActivity(), response.getDescription(),
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onErrorResponse(InvocationError arg0) {
				try {
					System.out.println(arg0.getMessage());
					System.out.println(arg0.getCause());
					arg0.fillInStackTrace().printStackTrace();
				} catch (Throwable t) {
				}
			}
		});
		topMessageTask = builder.build().get();
	}

	// 从本地读取置顶缓存
	private void getTopMessageFromLocal() {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				// 从本地缓存中查询出所有缓存的置顶消息
				List<MessageInfoCache> cacheList = new Select().from(
						MessageInfoCache.class).execute();
				// 将缓存的置顶消息转化成MessageInfo对象，存放在集合listTemp中
				List<MessageInfo> tempList = new ArrayList<MessageInfo>();
				for (MessageInfoCache mic : cacheList) {
					tempList.add(mic.change2MessageInfo());
				}
				// 判断存放置顶消息的集合是否为空，如果为空，创建一个新的，如果不为空就清除集合中的数据
				if (topMessageList != null) {
					topMessageList.clear();
				} else {
					topMessageList = new ArrayList<MessageInfo>();
				}
				// 过滤置顶消息
				for (MessageInfo mi : tempList) {
					filterTopMessage(mi);
				}
				// 更新ui的显示
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						allTopAdapter.setTop(topMessageList);
						mAdapter.notifyDataSetChanged();
						adapterIsChanging();
					}
				});
			}
		});
	}

	// 获取其他消息(非置顶消息)
	private void getOtherMessages() {

		// 请求网络
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MESSAGE_CENTER);
		builder.addQueryParameter("filter", filter);
		builder.addPageQueryParameter(from, MAX);
		Type type = new TypeToken<Response<ListSlice<MessageInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<ListSlice<MessageInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				listView.onLoadMoreComplete();
				if (isRefreshing) {
					listView.onRefreshComplete();
					isRefreshing = false;
				} else {
					listView.onLoadMoreComplete();
				}
			}

			@Override
			public void onResponse(Response<ListSlice<MessageInfo>> response) {
				if (getActivity() == null) {
					return;
				}
				if (response.getCode() == 0) {
					// 如果正处于刷新状态
					if (isRefreshing) {
						((MainActivity) getActivity()).setFromTime((BusinessConstants
								.formatTime(System.currentTimeMillis())));
						// ((MainActivity)(getActivity())).hintCount();
						((MainActivity) (getActivity())).updataCount();
					}

					ListSlice<MessageInfo> slice = response.getPayload();
					if (slice != null) {
						List<MessageInfo> tempList = slice.getContent();
						Log.e("ccc", "返回的数据条目" + tempList.size());
						// ----------

						MainApplication application = (MainApplication) getActivity()
								.getApplication();
						application.setMessageCenterMessages(tempList);
						from += tempList.size();
						Iterator<MessageInfo> it = tempList.iterator();
						centerMessageList = new ArrayList<MessageInfo>();
						while (it.hasNext()) {
							// Log.e("ccc", "OTHER:"+it.toString());
							MessageInfo mf = it.next();
							Log.e("abc", mf.toString());
							filterOtherMessage(it, mf);
						}
						cacheOtherMessage();

						if (isRefreshing)
							allCenterAdapter.clear();
						allCenterAdapter.addEntries(centerMessageList);
						mAdapter.notifyDataSetChanged();
						adapterIsChanging();

					} else {
						if (getActivity() != null)
							Toast.makeText(getActivity(), "没有更多数据了...", Toast.LENGTH_SHORT)
									.show();
					}
				} else {
					if (getActivity() != null)
						Toast.makeText(getActivity(),
								response.getDescription(), Toast.LENGTH_SHORT).show();
				}
				if (isRefreshing) {
					listView.onRefreshComplete();
					isRefreshing = false;
				} else {
					listView.onLoadMoreComplete();
				}
			}
		});
		otherMessageTask = builder.build().get();
	}

	// 从本地缓存读取其他消息
	private void getOtherMessagesFromLocal() {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<MessageInfoContentCache> execute = new Select().from(
						MessageInfoContentCache.class).execute();
				final List<MessageInfoContentCache> miccs = execute;

				myHandler.post(new Runnable() {
					@Override
					public void run() {
						List<MessageInfo> tempList = new ArrayList<MessageInfo>();
						for (MessageInfoContentCache micc : miccs) {
							tempList.add(micc.change2MessageInfo());
						}
						MainApplication application = (MainApplication) getActivity()
								.getApplication();
						application.setMessageCenterMessages(tempList);
						from += tempList.size();
						Iterator<MessageInfo> it = tempList.iterator();
						centerMessageList = new ArrayList<MessageInfo>();
						while (it.hasNext()) {
							MessageInfo mf = it.next();
							Log.e("abc", mf.toString());
							filterOtherMessage(it, mf);
						}
						if (isRefreshing)
							allCenterAdapter.clear();
						allCenterAdapter.addEntries(centerMessageList);
						mAdapter.notifyDataSetChanged();
						adapterIsChanging();
					}
				});

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView:" + filter);

		if (getActivity() instanceof MainActivity) {

			MainActivity activity = (MainActivity) getActivity();
			int index;
			if ("all".equals(filter)) {
				index = 0;
			} else if ("task".equals(filter)) {
				index = 1;

			} else if ("notice".equals(filter)) {
				index = 2;

			} else {
				index = -1;
			}
			activity.getCenterScrollFragment().fragments[index] = this;
			Log.d(TAG, "getCenterScrollFragment:" + filter);
		}
		if (root == null) {
			root = View.inflate(getActivity(),
					R.layout.message_center_fragment, null);
			listView = (PullToRefreshListView) root.findViewById(R.id.listview);
			allTopAdapter = new MessageTopAdapter(this, listener);
			allCenterAdapter = new MessageCenterAdapter(this, listener);
			allTopAdapter.setReadStatusListener(this);
			allCenterAdapter.setReadStatusListener(this);
			mAdapter = new WarapSectionAdapter();
			mAdapter.setWarapedAdapters(allTopAdapter, allCenterAdapter);
			setListViewAdapter(mAdapter);

			listView.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					isRefreshing = true;
					from = 0;
					loadData();

                    /*记录操作 0402*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_REFRESH_MESSAGE_CENTER);
				}
			});
			listView.setOnLoadMoreListner(new OnLoadMoreListner() {

				@Override
				public void onLoadMore() {
					getOtherMessages();
				}
			});

			// loadData();
			Log.d(TAG, "loadData:" + filter);
			// listView.setRefreshingState();
			Log.d(TAG, "setRefreshingState:" + filter);
			// if(getUserVisibleHint()){
			// loadData();
			// listView.setRefreshingState();
			// }
		}

		return root;
	}

	//
	// 一开始就选中该页面
	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// if(getUserVisibleHint()){
	// loadData();
	// listView.setRefreshingState();
	// }
	// super.onActivityCreated(savedInstanceState);
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_NEED_REFRESH) {
			refreshData();
		}
	}

	public void uploadReadStatue(MessageInfo info) {
		Builder builder = new Builder();
		String uploadUrl = "/messagecenter/" + info.getId() + "/isread";
		builder.addQueryParameter("messageId", String.valueOf(info.getId()));
		builder.setModule(uploadUrl);
		builder.setResponseType(new TypeToken<Response<String>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {

			}

			@Override
			public void onResponse(Response<String> arg0) {

			}
		});
		builder.build().put();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}

	@Override
	public boolean needRefrash(boolean needRefrash) {
		this.needRefrash = needRefrash;
		return needRefrash;
	}

	/**
	 * 消息观察者对象
	 */
	private MessageObserver messageObserver = new MessageObserver() {

		@Override
		public void onReadStateChanged(String type, long id, int read) {
			if (mAdapter == null) {
				return;
			}
			//遍历适配器，然后根据传进来的id，修改其是否已读
			int count = mAdapter.getCount();
			for (int i = 0; i < count; i++) {
				Object item = mAdapter.getItem(i);
				if (item != null) {
					if (item instanceof Message) {
						Message message = (Message) item;
						if (message.getTypeId() == id) {
							message.setRead(read);
							mAdapter.notifyDataSetChanged();
							adapterIsChanging();
						}
					}
				}
			}
		}

		@Override
		public long getId() {
			return -1;
		}

		@Override
		public void setIsRead(int read) {
		}

	};


	// 将所有未读消息变为已读
	private void clearAllUnReadStatus() {
		Builder builder = new Builder();
		String uploadUrl = AsyncRequest.MODULE_CLEAR_ALLUNREAD;
		builder.setModule(uploadUrl);
		builder.setResponseType(new TypeToken<Response<String>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {
				Log.e("clear", arg0.getMessage()+"  删除失败");
			}

			@Override
			public void onResponse(Response<String> response) {
				Log.e("clear", response.getCode()+"  删除成功");
				
			}
		});
		builder.build().put();
	}

	/**
	 * onGooViewDispperListener接口中的重写方法，
	 * 在黏性控件消失的时候调用
	 * 这里是将所有未读消息变为已读
	 */
	@Override
	public void updataData() {
//		Log.e("aaa", "开始clear");
		//遍历当前适配器，将适配器中的未读消息置为已读消息。
		for (int i = 0; i < mAdapter.getCount(); i++) {
			Object temp = mAdapter.getItem(i);
			if(temp instanceof MessageInfo){
				if (((MessageInfo)temp).getIsRead() == 0) {
					((MessageInfo)temp).setIsRead(1);
				}
			}
		}
		clearAllUnReadStatus();
		needRefrash(true);
		mAdapter.notifyDataSetChanged();
		//listView.setRefreshingState();
		MainActivity.getHandler().sendEmptyMessage(
				Config.REDUCTION_UNREAD_COUNT);
	}

	/**
	 * listview跳到下一条未读信息
	 */
	@Override
	public void scrollToNextUnRead() {
		//定义一个整形用来记录sectionType的数量。
		int sectionTypeCount = 0;
		//判断如果当前未读消息下标的集合为空或者适配器adapter发生了改变
		if (unReadIndex.size() == 0||isAdapterChange) {
			unReadIndex.clear();
			//遍历适配器获取其中的未读消息，将他们的下标放入unReadIndex集合中。
			for (int i = 0; i < mAdapter.getCount(); i++) {
				Object temp = mAdapter.getItem(i);
				if(temp instanceof MessageInfo){
					if (((MessageInfo)temp).getIsRead() == 0) {
						unReadIndex.add(i);
					}
				}
			}
			//listview选中第一个
			listView.setSelection(1);
			//将适配器改变改为false
			isAdapterChange = false;
		}else{
			//否则listview选中未读消息下标集合中的第一个未读消息
			listView.setSelection(unReadIndex.remove(0) + 1);
		}
	}

	@Override
	public void scrollToFirst() {
		listView.smoothScrollToPosition(0);
	}

	//适配器
	private void adapterIsChanging() {
		isAdapterChange = true;
	}
}
