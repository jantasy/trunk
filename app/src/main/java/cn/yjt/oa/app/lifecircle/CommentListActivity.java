package cn.yjt.oa.app.lifecircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.lifecircle.adapter.CommentListAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.view.FooterListView;
import cn.yjt.oa.app.lifecircle.view.FooterListView.OnAdapterListener;
import cn.yjt.oa.app.lifecircle.view.FooterListView.OnPagingListener;

public class CommentListActivity extends TitleFragmentActivity {
    private CommentListAdapter adapter;
    private ProgressBar center_progress;
    private NetConnection conn;
    private int headContentHeight;
    private LinearLayout hometype_ll;
    private String id = "";
    private boolean isDone = false;
    private boolean isPaging_price = true;
    private boolean isStore;
    private boolean isT = false;
    private View loadMoreView_price;
    private Handler mHandler = new Handler();
    private ArrayList<Netable> netableList;
    private int pag_price = 1;
    private ProgressDialog pd;
    private Netable sNetable = new Netable();
    private FooterListView su_list;
    private ArrayList<Netable> tempList;


    private void init() {
        su_list = (FooterListView) findViewById(R.id.save_list);
        center_progress = (ProgressBar) findViewById(R.id.center_progress);
        netableList = new ArrayList();
        tempList = new ArrayList();
        conn = new NetConnection(this);
        pd = new ProgressDialog(this);
        pd.setTitle("提示:");
        pd.setMessage("请稍后,正在操作中");
        loadMoreView_price = getLayoutInflater().inflate(R.layout.loadmoreview, null);
        measureView(loadMoreView_price);
        headContentHeight = loadMoreView_price.getMeasuredHeight();
        adapter = new CommentListAdapter(this, su_list, netableList);
        id = getIntent().getExtras().getString("id");
        isStore = getIntent().getExtras().getBoolean("isStore");
        su_list.addFooterView(loadMoreView_price, null, false);
        su_list.setAdapter(adapter);
    }

    private void measureView(View view) {
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(-1, -2);
        }
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
        int i = layoutParams.height;
        view.measure(childMeasureSpec, i > 0 ? MeasureSpec.makeMeasureSpec(i, 1073741824) : MeasureSpec.makeMeasureSpec(0, 0));
    }

    private void sendRequest() {
        new Thread() {
            public void run() {
                super.run();
                sNetable = conn.list_comments(id, new StringBuilder(String.valueOf(pag_price)).toString());
                tempList = sNetable.getComments();
                for (int i = 0; i < tempList.size(); i++) {
                    adapter.addNewsItem((Netable) tempList.get(i));
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        center_progress.setVisibility(View.GONE);
                        su_list.setVisibility(View.GONE);
                        if (tempList.size() != 10) {
                            loadMoreView_price.setPadding(0, 0, 0, headContentHeight * -1);
                            isPaging_price = false;
                            if (pag_price == 1 && tempList.size() == 0) {
                                Toast.makeText(CommentListActivity.this, "提示:暂无数据!", Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        center_progress.setVisibility(View.GONE);
                        su_list.setVisibility(View.VISIBLE);
                        isDone = true;
                        if (pag_price == 1) {
                            su_list.setSelectionFromTop(0, 0);
                        }
                    }
                });
            }
        }.start();
    }

    private void setListener() {
        su_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
//                Intent intent = new Intent();
//                intent.setClass(CommentListActivity.this, CommentDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("netable", (Serializable) netableList.get(i));
//                bundle.putBoolean("isStore", isStore);
//                bundle.putString("id", id);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
        su_list.setOnAdapterListener(new OnAdapterListener() {
            public BaseAdapter onAdapter() {
                return adapter;
            }
        });
        su_list.setonPagingListener(new OnPagingListener() {
            public void onPaging() {
                if (isPaging_price && isDone) {
                    pag_price++;
                    sendRequest();
                }
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_commentpl);
		setTitle("商户评论");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
        init();
        setListener();
        sendRequest();
    }
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
