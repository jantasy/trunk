package cn.yjt.oa.app.nfctools.operationview;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.nfctools.NfcTagOperationView;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;

public class NfcTagOperationOpenAppView extends FrameLayout implements NfcTagOperationView, View.OnClickListener{
	
	private NfcTagOperation nfcTagOperation;
	private EditText editText;
	private AlertDialog alertDialog;
	
	
	public NfcTagOperationOpenAppView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public NfcTagOperationOpenAppView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public NfcTagOperationOpenAppView(Context context) {
		this(context,null);
	}

	private void init(Context context){
		LayoutInflater.from(context).inflate(R.layout.view_nfc_tag_operation_openapp, this);
		findViewById(R.id.nfc_openapp_applist_btn).setOnClickListener(this);
		editText = (EditText) findViewById(R.id.nfc_openapp_pkg_et);
	}
	
	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOperationId() {
		return nfcTagOperation.getOperationId();
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setOnCheckedChangedListener(OnCheckedChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNfcTagOperation(NfcTagOperation operation) {
		nfcTagOperation = operation;		
	}

	@Override
	public NfcTagOperation getNfcTagOperation() {
		nfcTagOperation.setExtraData(editText.getText().toString().getBytes());
		return nfcTagOperation;
	}

	@Override
	public ExtraView getExtraView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExtraView(ExtraView extraView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nfc_openapp_applist_btn:
			openAppList();
			break;

		default:
			break;
		}
	}

	private void openAppList() {
		final List<ResolveInfo> resolveInfos = AppUtils.getResolveInfos(getContext());
		ListView listView = (ListView) LayoutInflater.from(getContext()).inflate(R.layout.listview,this,false);
		listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ResolveInfo resolveInfo = resolveInfos.get(position);
				editText.setText(resolveInfo.activityInfo.packageName);
				alertDialog.dismiss();
			}
		});
		AppAdapter adapter = new AppAdapter();
		adapter.resolveInfos = resolveInfos;
		listView.setAdapter(adapter);
		alertDialog = AlertDialogBuilder.newBuilder(getContext()).setView(listView).setTitle("应用程序").show();
	}

	
	private class AppAdapter extends BaseAdapter{
		List<ResolveInfo> resolveInfos;
		@Override
		public int getCount() {
			return resolveInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return resolveInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_applist_item, parent, false);
			}
			TextView title = (TextView) convertView.findViewById(R.id.applist_title);
			ImageView icon = (ImageView) convertView.findViewById(R.id.applist_icon);
			TextView pkg = (TextView) convertView.findViewById(R.id.applist_pkg);
			ResolveInfo info = (ResolveInfo) getItem(position);
			PackageManager pm = getContext().getPackageManager();
			Drawable drawable = info.loadIcon(pm);
			CharSequence label = info.loadLabel(pm);
			String packageName = info.activityInfo.packageName;
			title.setText(label);
			icon.setImageDrawable(drawable);
			pkg.setText(packageName);
			return convertView;
		}
		
	}
}
