package cn.yjt.oa.app.lifecircle;

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

public class AreaActivity extends Fragment implements OnItemClickListener {
	private static final String tag = "AreaActivity/";
    private Netable area = new Netable();;
    private ArrayList<ArrayList<String>> areas;
    private ArrayList<String> citys;
    private NetConnection conn;
    private LinearLayout left_ll;
    private MyListView listView;
    private SurroundActivity mContext;
    private Handler mHandelr = new Handler();;
    private AreaLeftAdapter myAdapter;
    private LinearLayout name_ll;
    private TextView name_txt;
    private AreaRightAdapter subAdapter;
    private MyListView subListView;

    private void selectDefult() {
        myAdapter.setSelectedPosition(0);
        myAdapter.notifyDataSetInvalidated();
        subAdapter = new AreaRightAdapter(mContext, areas, 0);
        subListView.setAdapter(subAdapter);
        subListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                mContext.setAreaText((String) citys.get(0), (String) ((ArrayList) areas.get(0)).get(i));
                if (i == 0) {
                    mContext.zonePress = "";
                }
                mContext.changeArrowState(View.GONE, false, false, false);
                mContext.send();
            }
        });
    }

    private void sendRequest() {
        citys = new ArrayList();
        areas = new ArrayList();
        area = new Netable();
        new Thread() {
            public void run() {
                super.run();
                area = Constants.getArea();
                for (int i = 0; i < area.getAreas().size(); i++) {
                    ArrayList arrayList = new ArrayList();
                    Netable netable = (Netable) area.getAreas().get(i);
                    arrayList.add(netable.getName());
                    for (int i2 = 0; i2 < netable.getZones().size(); i2++) {
                        arrayList.add(((Netable) netable.getZones().get(i2)).getName());
                    }
                    citys.add(netable.getName());
                    areas.add(arrayList);
                }
                mHandelr.post(new Runnable() {
                    public void run() {
                        if (area.getAreas().size() == 0) {
                            Toast.makeText(getActivity(), "提示:没有该市数据!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        myAdapter = new AreaLeftAdapter(mContext, citys);
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
                    mContext.setAreaText((String) citys.get(position), (String) ((ArrayList) areas.get(position)).get(i));
                    if (i == 0) {
                        mContext.zonePress = "";
                    }
                    mContext.changeArrowState(View.GONE, false, false, false);
                    mContext.send();
                }
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                myAdapter.setSelectedPosition(i);
                myAdapter.notifyDataSetInvalidated();
                subAdapter = new AreaRightAdapter(getActivity().getApplicationContext(), areas, i);
                subListView.setAdapter(subAdapter);
                subListView.setOnItemClickListener(new subListViewListener(i));
            }
        });
        name_ll.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                mContext.mTVArea.setText("全城");
                mContext.areaPress = "";
                mContext.zonePress = "";
                mContext.changeArrowState(View.GONE, false, false, false);
                mContext.send();
            }
        });
    }
    
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.activity_area, viewGroup, false);
        listView = (MyListView) inflate.findViewById(R.id.listView);
        subListView = (MyListView) inflate.findViewById(R.id.subListView);
        left_ll = (LinearLayout) inflate.findViewById(R.id.left_ll);
        name_ll = (LinearLayout) inflate.findViewById(R.id.name_ll);
        name_txt = (TextView) inflate.findViewById(R.id.name_txt);
        name_txt.setText("全城");
        mContext = (SurroundActivity) getActivity();
        conn = new NetConnection(mContext);
        setListener();
        
        return inflate;
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, PreferfenceUtils.getCityPreferences(getActivity()));
        if (area.getName() != null) {
        	Log.d(tag, area.getName());
        }
        if (area.getName() == null || !area.getName().equals(PreferfenceUtils.getCityPreferences(getActivity()))) {
            sendRequest();
        }
    }
}
