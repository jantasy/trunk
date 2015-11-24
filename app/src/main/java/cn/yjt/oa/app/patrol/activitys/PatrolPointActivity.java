package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.POIPicker;
import cn.yjt.oa.app.utils.POIPicker.POIPickerListener;
import cn.yjt.oa.app.widget.AlwaysMarqueeTextView;
import cn.yjt.oa.app.widget.ViewContainerStub;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

/**
 * 创建更新巡检点界面
 *
 * @author 熊岳岳
 * @since 20150907
 */
public class PatrolPointActivity extends TitleFragmentActivity implements OnClickListener, POIPickerListener {

    /*和巡检点位置区域相关的的常量(巡检点半径)*/
    private static final int SEEKBAR_CHANGE_UNIT = 1;
    private static final int SEEKBAR_MAX = 100;
    private static final int SEEKBAR_DEFAULT = 100;
    private static final int RANGE_UNIT = 10;
    private static final int RANGE_MAX = SEEKBAR_MAX * RANGE_UNIT;
    private static final int RANGE_DEFAULT = SEEKBAR_DEFAULT * RANGE_UNIT;

    /*从intent中获取数据的key*/
    private static final String PATROLPOING_STRING = "patrol_point";

    /*页面控件*/
    private ViewContainerStub mVcsBaiduMap;
    private EditText mEtPatrolName;
    private EditText mEtPatrolDescription;
    private CheckBox mCbLoacation;
    private CheckBox mCbTag;
    private TextView mTvPatrolLocation;
    private AlwaysMarqueeTextView mTvCurrentPosition;
    private ImageView mIvRangeSubtract;
    private ImageView mIvRangeAdd;
    private SeekBar mSbPatrolRange;
    private EditText mEtPatrolRange;
    /*百度地图的控件*/
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /** 选择当前位置的对话框 */
    private POIPicker poiPicker;
    /** 百度地图覆盖物对象 */
    private Overlay mOverlay;
    /** 随intent携带过来的PointInfo对象 */
    private PatrolPoint mPointInfo;

    private boolean isProgressChange = false;

    private String latlng;
    private String poiName;

    /*-----生命周期方法START-----*/
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_patrol_point);
        initParams();
        initView();
        fillData();
        setListener();
    }

	/*-----生命周期方法END-----*/

    /*-----oncreate中调用的方法START-----*/
    private void initParams() {
        poiPicker = new POIPicker(this, this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPointInfo = (PatrolPoint) bundle.get(PATROLPOING_STRING);
        }
    }

    private void initView() {
        mVcsBaiduMap = (ViewContainerStub) findViewById(R.id.vcs_baidu_map);
        mEtPatrolName = (EditText) findViewById(R.id.et_patrol_name);
        mEtPatrolDescription = (EditText) findViewById(R.id.et_patrol_description);
        mCbLoacation = (CheckBox) findViewById(R.id.cb_check_loacation);
        mCbTag = (CheckBox) findViewById(R.id.cb_check_tag);
        mTvPatrolLocation = (TextView) findViewById(R.id.tv_patrol_location);
        mTvCurrentPosition = (AlwaysMarqueeTextView) findViewById(R.id.tv_current_position);
        mIvRangeSubtract = (ImageView) findViewById(R.id.iv_range_subtract);
        mIvRangeAdd = (ImageView) findViewById(R.id.iv_range_add);
        mSbPatrolRange = (SeekBar) findViewById(R.id.sb_patrol_range);
        mEtPatrolRange = (EditText) findViewById(R.id.et_patrol_range);
        initMap();
    }

    private void fillData() {
        mSbPatrolRange.setMax(100);
        if (mPointInfo != null) {
            mEtPatrolName.setText(mPointInfo.getName());
            mEtPatrolDescription.setText(mPointInfo.getDescription());
            mCbLoacation.setChecked(mPointInfo.getCheckPosition() == 1 ? true : false);
            mCbTag.setChecked(mPointInfo.getCheckTag() == 1 ? true : false);
            mTvCurrentPosition.setText(mPointInfo.getPositionDescription());
            mSbPatrolRange.setProgress(mPointInfo.getSignRange()
                    / RANGE_UNIT);
            mEtPatrolRange.setText(mPointInfo.getSignRange() + "");

            try {
                latlng = mPointInfo.getPositionData();
                String[] split = latlng.split(",");
                animateWithFece(new LatLng(Double.parseDouble(split[0]),
                        Double.parseDouble(split[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mSbPatrolRange.setProgress(mSbPatrolRange.getMax());
            mEtPatrolRange.setText(1000 + "");
        }


        getLeftbutton().setImageResource(R.drawable.navigation_back);
        getRightButton().setImageResource(R.drawable.contact_list_save);
    }

    private void setListener() {
        watchRangeView();
        mTvPatrolLocation.setOnClickListener(this);
        mIvRangeAdd.setOnClickListener(this);
        mIvRangeSubtract.setOnClickListener(this);
    }

    private void initMap() {
        BaiduMapOptions options = new BaiduMapOptions();
        options.zoomGesturesEnabled(true);
        options.zoomControlsEnabled(false);
        mMapView = new MapView(this, options);
        mVcsBaiduMap.setView(mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15f));
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                LocationMode.FOLLOWING, false, BitmapDescriptorFactory
                .fromResource(R.drawable.attendance_icon_location)));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void watchRangeView() {
        mSbPatrolRange.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isProgressChange = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isProgressChange = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (isProgressChange) {
                    mEtPatrolRange.setText(getRange());
                }

            }
        });
        mEtPatrolRange.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    if (Integer.valueOf(editText.getText().toString()) > RANGE_MAX) {
                        mEtPatrolRange.setText(String.valueOf(RANGE_MAX));
                    }
                }

            }
        });
        mEtPatrolRange.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String rang = s.toString().trim();
                if (!TextUtils.isEmpty(rang)) {
                    int range = Integer.valueOf(rang);
                    if (Integer.valueOf(rang) > RANGE_MAX) {
                        range = RANGE_MAX;
                    }
                    mSbPatrolRange.setProgress(range / RANGE_UNIT);
                    addBaiduMapOverlayRange(range, null);
                }
            }
        });
    }

	/*-----oncreate中调用的方法END-----*/

    /** 获取巡检点的半径 */
    private String getRange() {
        return String.valueOf(mSbPatrolRange.getProgress() * RANGE_UNIT);
    }

    /** 根据巡检半径，在百度地图上添加覆盖物 */
    private void addBaiduMapOverlayRange(int rang, LatLng circleCenterOptions) {
        if (mOverlay != null) {
            mOverlay.remove();
        }
        if (circleCenterOptions == null) {
            MyLocationData locationData = mBaiduMap.getLocationData();
            if (locationData == null) {
                System.out.println("locationData:" + locationData);
                return;
            }
            circleCenterOptions = new LatLng(locationData.latitude,
                    locationData.longitude);
        }
        Stroke stroke = new Stroke(1, 0xAA5096eb);
        CircleOptions circleOptions = new CircleOptions()
                .center(circleCenterOptions).radius(rang).stroke(stroke)
                .fillColor(Color.parseColor("#115096eb"));
        mOverlay = mBaiduMap.addOverlay(circleOptions);
    }

    /***/
    private void animateWithFece(LatLng latLng) {
        MyLocationData locData = new MyLocationData.Builder()
                .latitude(latLng.latitude).longitude(latLng.longitude).build();
        mBaiduMap.setMyLocationData(locData);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(u);
        int range = mSbPatrolRange.getProgress() * RANGE_UNIT;
        if (mPointInfo != null) {
            range = mPointInfo.getSignRange();
        }
        addBaiduMapOverlayRange(range, latLng);
    }

    /** 启动当前界面 */
    public static void launth(Context context) {
        launthWithPatrolPoint(context, null);
    }

    /** 启动该界面并传递一个Patrol对象 */
    public static void launthWithPatrolPoint(Context context, PatrolPoint info) {

        Intent intent = new Intent(context, PatrolPointActivity.class);
        if (info != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PATROLPOING_STRING, info);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /** 判断表格是否填写完整 */
    private boolean isFillComplete() {
        if (TextUtils.isEmpty(mEtPatrolName.getText())) {
            Toast.makeText(this, "请填写巡更点名称", Toast.LENGTH_SHORT).show();
            return false;
        }

        //未选择标签
        if (!(mCbTag.isChecked() || mCbLoacation.isChecked())) {
            Toast.makeText(this, "请至少选择一种校验方式", Toast.LENGTH_SHORT).show();
            return false;
        }


        //未选择位置
        if (mCbLoacation.isChecked()) {
            if (TextUtils.isEmpty(mTvCurrentPosition.getText().toString())
                    || TextUtils.isEmpty(latlng)) {
                Toast.makeText(this, "请巡检点选择位置", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void commitPatrolPoint(PatrolPoint info) {
        String message = null;
        if (info.getId() == 0) {
            message = "正在创建巡检点";

            /*记录操作 1713*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_CREATE_PATROLPOINT);
        } else {
            message = "正在更新巡检点";

            /*记录操作 1714*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_PATROLPOINT);
        }
        String custId = AccountManager.getCurrent(this).getCustId();
        PatrolApiHelper.addOrUpdatePatrolPoint(new ProgressDialogResponseListener<PatrolPoint>(this, message) {
            @Override
            public void onSuccess(PatrolPoint payload) {
                finish();
            }
        }, custId, info);
    }

    @Override
    public void onPick(PoiInfo info) {
        animateWithFece(info.location);
        if ("当前位置".equals(info.name)) {
            poiName = info.address;
        } else {
            poiName = info.name;
        }
        latlng = info.location.latitude + "," + info.location.longitude;
        mTvCurrentPosition.setText(poiName);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {
        if (!isFillComplete()) {
            return;
        }
        PatrolPoint info = new PatrolPoint();
        if (mPointInfo != null) {
            info.setId(mPointInfo.getId());
        }
        info.setName(mEtPatrolName.getText().toString());
        info.setDescription(mEtPatrolDescription.getText().toString());
        info.setPositionDescription(mTvCurrentPosition.getText().toString());
        info.setPositionData(latlng);
        info.setSignRange(Integer.valueOf(mEtPatrolRange.getText().toString()));

        info.setCustId(Long.valueOf(AccountManager.getCurrent(this).getCustId()));
        info.setCheckTag(mCbTag.isChecked() ? PatrolPoint.CHECK_TRUE : PatrolPoint.CHECK_FALSE);
        info.setCheckPosition(mCbLoacation.isChecked() ? PatrolPoint.CHECK_TRUE : PatrolPoint.CHECK_FALSE);
        commitPatrolPoint(info);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_patrol_location:
                poiPicker.show();
                break;
            case R.id.iv_range_add:
                if (mSbPatrolRange.getProgress() < SEEKBAR_MAX - SEEKBAR_CHANGE_UNIT) {
                    mSbPatrolRange.setProgress(mSbPatrolRange.getProgress()
                            + SEEKBAR_CHANGE_UNIT);
                } else {
                    mSbPatrolRange.setProgress(SEEKBAR_MAX);
                }
                mEtPatrolRange.setText(getRange());
                break;
            case R.id.iv_range_subtract:
                if (mSbPatrolRange.getProgress() > SEEKBAR_CHANGE_UNIT) {
                    mSbPatrolRange.setProgress(mSbPatrolRange.getProgress()
                            - SEEKBAR_CHANGE_UNIT);
                } else {
                    mSbPatrolRange.setProgress(0);
                }
                mEtPatrolRange.setText(getRange());
                break;

            default:
                break;
        }
    }


}
