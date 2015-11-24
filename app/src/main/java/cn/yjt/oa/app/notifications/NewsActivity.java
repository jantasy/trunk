package cn.yjt.oa.app.notifications;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.NewsInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class NewsActivity extends TitleFragmentActivity {
	private View progressView;
	private PullToRefreshListView listView;
	private List<NewsInfo> list = new ArrayList<NewsInfo>();
	private NewsAdapter adapter;
	private boolean isRefrash;
	private ImageView backIv;
	private ImageView rightIv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_activity_layout);
		progressView = findViewById(R.id.progress);
		listView = (PullToRefreshListView) findViewById(R.id.list_view);
		adapter = new NewsAdapter();
		listView.setAdapter(adapter);
		backIv = getLeftbutton();
		backIv.setImageResource(R.drawable.navigation_back);
		rightIv = getRightButton();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(NewsActivity.this, NewsDetaileActivity.class);	
				intent.putExtra("content_url", list.get(position-1).getContent());
				startActivity(intent);
			}
		});
		
		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefrash = true;
				list.clear();
				getNews();
			}
		});
		listView.setOnLoadMoreListner(new OnLoadMoreListner() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				getNews();
			}
		});
		getNews();
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	
	
	private void getNews(){
		if (!CheckNetUtils.hasNetWork(this)){
			Toast.makeText(this, R.string.connect_network_fail, 0).show();
			if (isRefrash){
				listView.onRefreshComplete();
				isRefrash = false;
				
			}else{
				listView.onLoadMoreComplete();
			}
			return;
		}
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_NEWS);
		Type type = new TypeToken<Response<ListSlice<NewsInfo>>>(){}.getType();
		builder.setResponseType(type);
		builder.addPageQueryParameter(list.size(), 20);
		builder.setResponseListener(new Listener<Response<ListSlice<NewsInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				if (progressView.getVisibility() == View.VISIBLE){
					progressView.setVisibility(View.GONE);
				}
				
				// TODO Auto-generated method stub
				listView.onLoadMoreComplete();
				Toast.makeText(NewsActivity.this, "加载消息失败", 0).show();
				if (isRefrash){
					listView.onRefreshComplete();
					isRefrash = false;
					
				}else{
					listView.onLoadMoreComplete();
				}
				
			}

			@Override
			public void onResponse(Response<ListSlice<NewsInfo>> arg0) {
				if (progressView.getVisibility() == View.VISIBLE){
					progressView.setVisibility(View.GONE);
				}
				// TODO Auto-generated method stub
				if (isRefrash){
					listView.onRefreshComplete();
					isRefrash = false;
					
				}else{
					listView.onLoadMoreComplete();
				}
				if (arg0.getCode() == 0){
					ListSlice ls = arg0.getPayload();
					if (ls != null){
						List<NewsInfo> listTemp = ls.getContent();
						if (listTemp.size() == 0){
							Toast.makeText(NewsActivity.this, "已经没有信息了", 0).show();
						}else{
							list.addAll(listTemp);
							adapter.notifyDataSetChanged();
						}
					}
				}else{
					Toast.makeText(NewsActivity.this, "加载消息失败", 0).show();
				}
			}


		});
		builder.build().get();
	}
	
	
	
	
	class NewsAdapter extends BaseAdapter{

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
			if (view == null){
				view = View.inflate(NewsActivity.this, R.layout.news_item_layout, null);
				holder = new NoticeHolder();
				view.setTag(holder);
			}
			holder = (NoticeHolder) view.getTag();
			holder.iconIv = (ImageView) view.findViewById(R.id.news_icon);
			holder.titleTv = (TextView) view.findViewById(R.id.news_title);
			holder.contentTv = (TextView) view.findViewById(R.id.news_content);
			holder.timeTv = (TextView) view.findViewById(R.id.news_time);
			NewsInfo info = list.get(position);
			AsyncRequest.getInImageView(info.getImage(), holder.iconIv, R.drawable.message_b, R.drawable.message_b);
			holder.titleTv.setText(info.getTitle());
			holder.contentTv.setText(info.getAbstracts());
			String time = info.getCreateTime();
			try {
				Date date = BusinessConstants.parseTime(info.getCreateTime());
				SimpleDateFormat formart = new SimpleDateFormat("yy-MM-dd HH:mm");
				time = formart.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();System.out.println("e: "+e.getMessage());
			}
			holder.timeTv.setText(time);
			return view;
		}
		
		
	}
	
	class NoticeHolder{
		public ImageView iconIv;
		public TextView titleTv;
		public TextView contentTv;
		public TextView timeTv;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "市场资讯");
		menu.add(0, 2, 2, "公司资讯");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			break;
		case 2:
			break;
		default:
			break;
		}

		return true;
	}
}
