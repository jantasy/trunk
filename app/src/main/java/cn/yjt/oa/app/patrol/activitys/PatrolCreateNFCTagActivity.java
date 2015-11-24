package cn.yjt.oa.app.patrol.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;


import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.location.BDLocationServer;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.nfctools.CreateOkActivity;
import cn.yjt.oa.app.nfctools.NfcTagOperationRecord;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperatorConfig;
import cn.yjt.oa.app.patrol.adapter.PatrolPointSpinnerAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.SharedUtils;
import io.luobo.common.Cancelable;

/**
 * 巡检路线界面
 * @author 熊岳岳
 * @since 20150908
 */
public class PatrolCreateNFCTagActivity extends TitleFragmentActivity implements OnClickListener {

    private final String TAG = "PatrolCreateNFCTagActivity";

    private static final int REQUEST_CODE_WRITE_NFC = 1;
    // tag的最大数目
    private static final int TAG_MAX_NUM = 10;
    public static final String RESULT_NFCTAGINFO = "result_nfctaginfo";

    private CheckBox readOnly;
    // AutoCompleteTextView的adapter
    private ArrayAdapter<String> adapter_auto;
    // 所有记录在sp中的tag的名称数组
    private String[] arr_tags;
    private Cancelable cancelable;
    private ProgressDialog progressDialog;
    private PatrolPointSpinnerAdapter adapter;
    private Spinner spinner;

    // 由原来的editText改为AutoCompleteTextView
    private AutoCompleteTextView tagName;
    private LatLng ll = null;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_patrol_createnfc);

		initParams();
		initView();
		fillData();
		setListener();
        request();
	}

	private void initParams() {
        adapter = new PatrolPointSpinnerAdapter(this);
	}

	private void initView() {
        tagName = (AutoCompleteTextView) findViewById(R.id.nfc_tag_name);
        readOnly = (CheckBox) findViewById(R.id.nfc_tag_readOnly);
        spinner = (Spinner) findViewById(R.id.attendance_area);
	}

	private void fillData() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
        readOnly.setChecked(false);
        spinner.setAdapter(adapter);
	}

	private void setListener() {
        findViewById(R.id.nfc_write_tag).setOnClickListener(this);
        readOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    readOnlyConfirmDialog();
                }
            }
        });
        tagName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tagName.showDropDown();
                initAutoCompleteTextView();
                if(tagName.getText().toString() != null && !"".equals(tagName.getText().toString())) {
                    tagName.setText(tagName.getText().toString());
                    tagName.setSelection(tagName.getText().toString().length());
                }
            }
        });
        tagName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(tagName.getText().toString() == null || "".equals(tagName.getText().toString())) {
                        tagName.showDropDown();
                    }
                }
            }
        });
        initLocation();
	}

	public static void lanuth(Context context) {
		Intent intent = new Intent(context, PatrolCreateNFCTagActivity.class);
		context.startActivity(intent);
	}

    private void request() {
        PatrolApiHelper.getPatrolPoint(new ProgressDialogResponseListener<List<PatrolPoint>>(this,"正在获取巡检点") {
            @Override
            public void onSuccess(List<PatrolPoint> payload) {
                adapter.setDataListsAndRefresh(payload);
            }
        },AccountManager.getCurrent(getApplicationContext()).getCustId());
    }

    private void readOnlyConfirmDialog(){
        AlertDialogBuilder.newBuilder(this).setTitle(getTitle()).setMessage("开启只读模式后NFC标签将只能被写入一次，是否确定开启？").setPositiveButton("确定", null).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                readOnly.setChecked(false);
            }
        }).show();
    }

    private void initLocation() {
//		nfcLocationListener = new NfcLocationListener();
//		mLocationClient = LocationUtil.getLocationClient(this,
//				nfcLocationListener);
//		mLocationClient.start();

        BDLocationServer.getInstance().startLocation(new NfcLocationListener());
        //        BaseLocationServer.startLocation(new NfcLocationListener());
    }

    /**
     * 初始化AutoCompleteTextView
     * 显示出最近10条常用的标签
     */
    private void initAutoCompleteTextView() {
        // 获取目前存储在sp中的标签
        String str_tag = SharedUtils.getPatrolTagName(getApplicationContext());
        // 用","分割字符串, 不同的tag用,号分开
        arr_tags = str_tag.split(",");
        List<String> tags = new ArrayList<String>();
        for (int i = 0; i < arr_tags.length; i++) {
            if(!TextUtils.isEmpty(arr_tags[i])){
                tags.add(arr_tags[i]);
            }
        }
        arr_tags = tags.toArray(new String[tags.size()]);
        // AutoCompleteTextView的adapter
        adapter_auto = new ArrayAdapter<String>(this,
                R.layout.autocompletetextview_item, arr_tags);
//		android.R.layout.simple_list_item_1, arr_tags);
        tagName.setAdapter(adapter_auto);
    }

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nfc_write_tag:
                startToWrite();
                break;

            default:
                break;
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_NFC && resultCode == RESULT_OK) {
            postNFCTagInfo(buildTagInfo(data.getStringExtra("sn"),data.getLongExtra("area_id", 0)));
        }
    }

    private void postNFCTagInfo(PatrolTag patrolTag) {
        PatrolApiHelper.addPatrolTag(new ProgressDialogResponseListener<PatrolTag>(this,"正在创建巡检标签") {
            @Override
            public void onSuccess(PatrolTag payload) {
                Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
            }
        },AccountManager.getCurrent(getApplicationContext()).getCustId(),patrolTag);
    }

    private void startToWrite() {
        if (TextUtils.isEmpty(tagName.getText())) {
            Toast.makeText(getApplicationContext(), "请添写标签名",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final PatrolPoint selectedItem = (PatrolPoint) spinner.getSelectedItem();
        if(selectedItem == null){
            Toast.makeText(getApplicationContext(), "请选择考勤区域",
                    Toast.LENGTH_SHORT).show();
            return;
        }

         /*记录操作 1721*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_CREATE_PATROLTAG);

        // 记录标签名称
        String current_tagName = tagName.getText().toString();
        // 写入sp
        writeTagOnSharedPreference(current_tagName);
//		SharedUtils.setTagName(getApplicationContext(), tagName.getText().toString());
        final NfcTagOperationRecord nfcTagOperationRecord = new NfcTagOperationRecord();
        nfcTagOperationRecord.setTagName(tagName.getText().toString());
        List<NfcTagOperation> operations = new ArrayList<NfcTagOperation>();
        operations.add(NfcTagOperatorConfig
                .getInstance(getApplicationContext())
                .findNfcTagOperationWithOperationId(
                        NfcTagOperation.NFC_TAG_OPERATION_PATROL));
        nfcTagOperationRecord.setOperations(operations);
        AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
        builder.setTitle("确定写入标签").setMessage("写入标签时会清空所有标签内原数据！")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateOkActivity.launchForResult(PatrolCreateNFCTagActivity.this, nfcTagOperationRecord, false,
                        1000, REQUEST_CODE_WRITE_NFC, readOnly.isChecked(), selectedItem.getId());
            }
        }).setNegativeButton("取消", null).show();

    }

    /**
     * 把tag写入到sp中
     */
    private void writeTagOnSharedPreference(String current_tagName) {
        // 记录current_tagName在已有的tag数组中的下标, 以便排序
        int index = -1;
        // 判断已有的tag中是否还有本次输入的tag名称
        for (int i = 0; i < arr_tags.length; i++) {
            if(current_tagName.equals(arr_tags[i])) {
                // 记录下坐标用于排序
                index = i;
                break;
            }
        }
        // 排序
        String[] new_tagArrs = SortTagArr(index, current_tagName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < new_tagArrs.length; i++) {
            sb.append(new_tagArrs[i]);
            if(i != new_tagArrs.length -1) {
                sb.append(",");
            }
        }
        // 写入到sp中
        SharedUtils.setPatrolTagName(getApplicationContext(), sb.toString());
    }

    /**
     * 对tag进行排序
     */
    private String[] SortTagArr(int index, String current_Name) {
        if(index < 0) {
            // 表示不需要排序, 将currentName放在最前端, 直接将所有的数组元素依次后移1位
            if(arr_tags.length < TAG_MAX_NUM) {
                // 不足10个
                String[] new_tagArr = new String[arr_tags.length + 1];
                new_tagArr[0] = current_Name;
                for (int i = 0; i < arr_tags.length; i++) {
                    new_tagArr[i + 1] = arr_tags[i];
                }
                return new_tagArr;
            } else {
                // 已有10个tag
                for (int i = arr_tags.length - 1; i >= 1; i--) {
                    arr_tags[i] = arr_tags[i - 1];
                }
                arr_tags[0] = current_Name;
                return arr_tags;
            }
        } else {
            // 需要重新排序, 将最新的放在第一个位置, 其余依次后移1位
            if(index != 0) {
                for (int i = index; i >= 1; i--) {
                    arr_tags[i] = arr_tags[i - 1];
                }
                arr_tags[0] = current_Name;
                return arr_tags;
            } else {
                return arr_tags;
            }
        }
    }

    private PatrolTag buildTagInfo(String sn,long areaId){
        PatrolTag patrolTag = new PatrolTag();
        UserInfo userInfo = AccountManager.getCurrent(getApplicationContext());
        patrolTag.setCreatorId(userInfo.getId());
        try {
            patrolTag.setCustId(Long.valueOf(userInfo.getCustId()));
        } catch (NumberFormatException e) {
            patrolTag.setCustId(0);
        }
        patrolTag.setSn(sn);
        patrolTag.setPointId(areaId);
        patrolTag.setName(tagName.getText().toString());
        //TODO：
//        patrolTag.setSignRange(100);
//        if (ll != null) {
//            patrolTag.setPositionData(ll.latitude + ","
//                    + ll.longitude);
//        }

//		CustSignCommonInfo selectedItem = (CustSignCommonInfo) spinner.getSelectedItem();
//		if(selectedItem!=null){
//			nfcTaginfo.setAreaId(selectedItem.getId());
//		}
        LogUtils.e(TAG, sn + "--" + tagName.getText().toString());
        return patrolTag;
    }

    private class NfcLocationListener implements BDLocationServer.YjtLocationListener {

        @Override
        public void locating() {

        }

        @Override
        public void locationSuccess(BDLocation bdLocation) {
            if(bdLocation!=null){
                ll = LocationUtil.changePointGCJ02(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
            }
        }

        @Override
        public void locationFailure(BDLocation bdLocation) {

        }
    }

}
