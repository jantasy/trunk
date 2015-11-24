package cn.yjt.oa.app.lifecircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.lifecircle.adapter.AreaLeftAdapter;
import cn.yjt.oa.app.lifecircle.adapter.AreaRightAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;
import cn.yjt.oa.app.lifecircle.view.MyListView;

public class TypeActivity extends Fragment implements OnItemClickListener {
	private static final String TAG = "TypeActivity";
    private NetConnection conn;
    ArrayList<String> firstType;
    private LinearLayout left_ll;
    private MyListView listView;
    private Handler mHandelr = new Handler();;
    private AreaLeftAdapter myAdapter;
    private LinearLayout name_ll;
    private TextView name_txt;
    ArrayList<ArrayList<String>> secondType;
    private AreaRightAdapter subAdapter;
    private MyListView subListView;
    private int tag;
    Netable type = new Netable();;
    private SurroundActivity mContext;

    private void selectDefult() {
        myAdapter.setSelectedPosition(0);
        myAdapter.notifyDataSetInvalidated();
        subAdapter = new AreaRightAdapter(mContext, secondType, 0);
        subListView.setAdapter(subAdapter);
        subListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (tag == 1) {
                	mContext.setTypeTxtAndSend((String) firstType.get(0), (String) ((ArrayList) secondType.get(0)).get(i), false, i == 0);
                	mContext.changeArrowState(View.GONE, false, false, false);
                } else if (tag == 2) {
//                	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                    MapSearchActivity.setTypeText((String) TypeActivity.this.firstType.get(0), (String) ((ArrayList) TypeActivity.this.secondType.get(0)).get(i));
//                    if (i == 0) {
//                        MapSearchActivity.second = RContactStorage.PRIMARY_KEY;
//                    }
//                    MapSearchActivity.isRefrsh = true;
                } else if (tag == 3) {
//                	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("first", (String) TypeActivity.this.firstType.get(0));
//                    bundle.putString("second", (String) ((ArrayList) TypeActivity.this.secondType.get(0)).get(i));
//                    bundle.putInt("action", 0);
//                    bundle.putSerializable("list", r0.sixList);
//                    Intent intent = new Intent();
//                    intent.setClass(r0, SurroundActivity.class);
//                    intent.putExtras(bundle);
//                    r0.startActivity(intent);
//                    r0.finish();
                }
            }
        });
    }

    private void sendRequest() {
        firstType = new ArrayList();
        secondType = new ArrayList();
        type = new Netable();
        new Thread() {
            public void run() {
                super.run();
                type = Constants.getFirstTypes();
                for (int i = 0; i < type.getFirstTypes().size(); i++) {
                    ArrayList arrayList = new ArrayList();
                    Netable netable = (Netable) type.getFirstTypes().get(i);
                    arrayList.add(netable.getName() + "-全部");
                    for (int i2 = 0; i2 < netable.getSecondTypes().size(); i2++) {
                        arrayList.add(((Netable) netable.getSecondTypes().get(i2)).getName());
                    }
                    firstType.add(netable.getName());
                    secondType.add(arrayList);
                }
                mHandelr.post(new Runnable() {
                    public void run() {
                        if (type.getFirstTypes().size() == 0) {
                            Toast.makeText(mContext, "提示:没有该市数据!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        myAdapter = new AreaLeftAdapter(mContext, firstType);
                        listView.setAdapter(myAdapter);
                        selectDefult();
                    }
                });
            }
        }.start();
    }

    private void setListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            class subListViewListener implements OnItemClickListener {
                private final int position;

                subListViewListener(int i) {
                    position = i;
                }

                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (tag == 1) {
                    	mContext.setTypeTxtAndSend((String) firstType.get(position), (String) ((ArrayList) secondType.get(position)).get(i), false, i == 0);
                    	mContext.changeArrowState(View.GONE, false, false, false);
                    } else if (tag == 2) {
//                    	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                        MapSearchActivity.setTypeText((String) TypeActivity.this.firstType.get(this.val$location), (String) ((ArrayList) TypeActivity.this.secondType.get(this.val$location)).get(i));
//                        if (i == 0) {
//                            MapSearchActivity.second = RContactStorage.PRIMARY_KEY;
//                        }
//                        MapSearchActivity.isRefrsh = true;
                    } else if (tag == 3) {
//                    	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("first", (String) TypeActivity.this.firstType.get(this.val$location));
//                        bundle.putString("second", (String) ((ArrayList) TypeActivity.this.secondType.get(this.val$location)).get(i));
//                        bundle.putInt("action", 0);
//                        bundle.putSerializable("list", r0.sixList);
//                        Intent intent = new Intent();
//                        intent.setClass(r0, SurroundActivity.class);
//                        intent.putExtras(bundle);
//                        r0.startActivity(intent);
//                        r0.finish();
                    }
                }
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                myAdapter.setSelectedPosition(i);
                myAdapter.notifyDataSetInvalidated();
                subAdapter = new AreaRightAdapter(mContext, secondType, i);
                subListView.setAdapter(subAdapter);
                subListView.setOnItemClickListener(new subListViewListener(i));
            }
        });
        name_ll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (tag == 1) {
                	mContext.setTypeTxtAndSend("", "", true, false);
                	mContext.changeArrowState(View.GONE, false, false, false);
                } else if (tag == 2) {
//                	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                    MapSearchActivity.first = RContactStorage.PRIMARY_KEY;
//                    MapSearchActivity.second = RContactStorage.PRIMARY_KEY;
//                    MapSearchActivity.isRefrsh = true;
                } else if (tag == 3) {
//                	TypeAllActivity r0 = (TypeAllActivity) TypeActivity.this.getActivity();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("first", RContactStorage.PRIMARY_KEY);
//                    bundle.putString("second", RContactStorage.PRIMARY_KEY);
//                    bundle.putInt("action", 0);
//                    bundle.putSerializable("list", r0.sixList);
//                    Intent intent = new Intent();
//                    intent.setClass(r0, SurroundActivity.class);
//                    intent.putExtras(bundle);
//                    r0.startActivity(intent);
//                    r0.finish();
                }
            }
        });
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.activity_area, viewGroup, false);
        listView = (MyListView) view.findViewById(R.id.listView);
        subListView = (MyListView) view.findViewById(R.id.subListView);
        left_ll = (LinearLayout) view.findViewById(R.id.left_ll);
        name_ll = (LinearLayout) view.findViewById(R.id.name_ll);
        name_txt = (TextView) view.findViewById(R.id.name_txt);
        name_txt.setText("全行业");
        mContext = (SurroundActivity) getActivity();
        conn = new NetConnection(mContext);
        setListener();
        return view;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
    }

    public void onResume() {
        super.onResume();
        tag = Constants.typeFrom;
        Log.d(TAG, tag+"");
        if (type.getName() == null || type.getName().equals(PreferfenceUtils.getCityPreferences(mContext))) {
            sendRequest();
        }
    }
}
