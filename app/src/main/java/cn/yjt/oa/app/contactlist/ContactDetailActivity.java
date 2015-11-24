package cn.yjt.oa.app.contactlist;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.BitmapUtils;

public class ContactDetailActivity extends TitleFragmentActivity implements
		OnClickListener {

	public static final String EXTRA_CONTACT = "extra_contact";
	public static final String EXTRA_FROM_STRUCT = "from_struct";

	private ImageView icon, sex;
	private TextView name, phone, company,/*department, position*/ email, tel;

	private ContactInfo info = null;

	private static final int SEX_MALE = 0;
	private static final int SEX_FEMALE = 1;
	private static final int SEX_UNKNOWN = 2;

	private View telButton;

	private List<String> departments=new ArrayList<String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setContentView(R.layout.contactlist_deatil_layout);
		getContactInfo();
		// departments = getDepartments();

		init();
		setInfo();
	}

	private void init() {
		icon = (ImageView) findViewById(R.id.contact_detail_icon);
		name = (TextView) findViewById(R.id.contact_detail_name);
		phone = (TextView) findViewById(R.id.contact_detail_phone);
		company = (TextView) findViewById(R.id.contact_detail_company);
//		department = (TextView) findViewById(R.id.contact_detail_department);
//		position = (TextView) findViewById(R.id.contact_detail_position);
		email = (TextView) findViewById(R.id.contact_detail_email);
		tel = (TextView) findViewById(R.id.contact_detail_tel);
		sex = (ImageView) findViewById(R.id.contact_sex);
		findViewById(R.id.phone).setOnClickListener(this);
		findViewById(R.id.sms).setOnClickListener(this);
		telButton = findViewById(R.id.tel);
		telButton.setOnClickListener(this);
//		View detail = findViewById(R.id.department_detail);
		boolean isFromStruct = getIntent().getBooleanExtra(EXTRA_FROM_STRUCT,
				false);
//		if (isFromStruct) {
//			detail.setVisibility(View.GONE);
//		} else if (departments == null || departments.isEmpty()) {
//			detail.setVisibility(View.GONE);
//		} else {
//			detail.setOnClickListener(this);
//		}
	}

	private void setInfo() {
		if (info == null) {
			return;
		}
		setIcon();
		name.setText(info.getName());
		String phoneNum = info.getPhone();
		if (TextUtils.isEmpty(phoneNum)) {
			phoneNum = info.getTel();
		}
		phone.setText(phoneNum);
		company.setText(info.getCustName());
		// department.setText(generateDepartment());
//		department.setText(info.getDepartment());
//		position.setText(info.getPosition());
		email.setText(info.getEmail());
		tel.setText(info.getTel());
		if (TextUtils.isEmpty(info.getTel())) {
			telButton.setVisibility(View.GONE);
		}
		setSex();
	}

//	private List<String> getDepartments() {
//		long id = info.getUserId();
//		System.out.println("getDepartments userid:" + id);
//		return ContactManager.getContactManager(getApplicationContext())
//				.getUserDepartments(id);
//	}

//	private String generateDepartment() {
//		boolean isFromStruct = getIntent().getBooleanExtra(EXTRA_FROM_STRUCT,
//				false);
//		if (isFromStruct) {
//			return info.getDepartment();
//		}
//		String deptName = "";
//		for (int i = 0; i < departments.size(); i++) {
//			String string = departments.get(i);
//			deptName += string;
//			if (i != (departments.size() - 1)) {
//				deptName += ",";
//			}
//		}
//		return deptName;
//	}

	private void setIcon() {
		String url = info.getAvatar();
		if (TextUtils.isEmpty(url)) {
			onBitmapLoadFinish(getDefaultBitmap());
			return;
		}
		AsyncRequest.getBitmap(url, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap bmp) {
				if (bmp != null) {
					onBitmapLoadFinish(bmp);
				} else {
					onBitmapLoadFinish(getDefaultBitmap());
				}
			}

			@Override
			public void onErrorResponse(InvocationError invocationerror) {
				onBitmapLoadFinish(getDefaultBitmap());
			}
		});
	}

	private void onBitmapLoadFinish(Bitmap bmp) {
		bmp = BitmapUtils
				.getPersonalHeaderIcon(ContactDetailActivity.this, bmp);
		icon.setImageBitmap(bmp);
	}

	private Bitmap getDefaultBitmap() {
		return BitmapFactory.decodeResource(getResources(),
				R.drawable.contactlist_contact_icon_default);
	}

	private void setSex() {
		int sex = info.getSex();
		this.sex.setVisibility(View.VISIBLE);
		if (sex == SEX_MALE) {
			this.sex.setImageResource(R.drawable.sex_male);
		} else if (sex == SEX_FEMALE) {
			this.sex.setImageResource(R.drawable.sex_female);
		} else {
			this.sex.setVisibility(View.GONE);
		}
	}

	private void getContactInfo() {
		info = getIntent().getParcelableExtra(EXTRA_CONTACT);
		if (!TextUtils.isEmpty(info.getDepartment())) {
			String[] split = info.getDepartment().split(",");
			for (int i = 0; i < split.length; i++) {
				departments.add(split[i]);
			}
		}
		Long userId = info.getUserId();
		System.out.println("getContactInfo userid:" + userId);
	}

	@Override
	public void onClick(View v) {
		int key = v.getId();
		switch (key) {
		case R.id.phone:
			startCall();

			 /*记录操作 0606*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_DAIL);
			break;
		case R.id.sms:
			sendMsg();

			 /*记录操作 0607*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_SEND_MESSAGE);
			break;
		case R.id.tel:
			callTel();

			 /*记录操作 0603*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_DAIL);
			break;
//		case R.id.department_detail:
//			showDepartmentsDetail(v);
//			break;
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showDepartmentsDetail(View v) {
		ListPopupWindow popupWindow = new ListPopupWindow(this);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setAdapter(new ArrayAdapter());
//		popupWindow.setAnchorView(findViewById(R.id.department_line));
		// popupWindow.setDropDownGravity(Gravity.RIGHT);
		popupWindow.show();
		popupWindow.getListView().setDivider(null);
	}

	// private int measureListViewWidth() {
	// List<String> departments2 = departments;
	// String maxDeptName = "";
	// for (String string : departments2) {
	// if(string.length() > maxDeptName.length()){
	// maxDeptName = string;
	// }
	// }
	// final Resources res = getResources();
	//
	// TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	// mTextPaint.density = res.getDisplayMetrics().density;
	// mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_15dp));
	// Rect bounds =new Rect();
	// mTextPaint.getTextBounds(maxDeptName, 0, maxDeptName.length(), bounds );
	// int textWidth = bounds.right - bounds.left;
	// return textWidth;
	// }

	private class ArrayAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public ArrayAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			if (departments == null) {
				return 0;
			}
			return departments.size();
		}

		@Override
		public Object getItem(int position) {
			return departments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_depart_detail,
						parent, false);
				// convertView.setBackgroundResource(R.drawable.dept_detail_background);
			}
			if (getCount() == 1) {
				convertView.setBackgroundResource(R.drawable.dept_detail);
				convertView.findViewById(R.id.tv_line).setVisibility(View.GONE);
			} else {
				if (position == 0) {
					convertView
							.setBackgroundResource(R.drawable.dept_detail_top);
					convertView.findViewById(R.id.tv_line).setVisibility(
							View.VISIBLE);
				} else if (position == getCount() - 1) {
					convertView
							.setBackgroundResource(R.drawable.dept_detail_bottom);
					convertView.findViewById(R.id.tv_line)
							.setVisibility(View.GONE);
				} else {
					convertView
							.setBackgroundResource(R.drawable.dept_detail_middle);
					convertView.findViewById(R.id.tv_line).setVisibility(
							View.VISIBLE);
				}

			}

			TextView deptName = (TextView) convertView
					.findViewById(R.id.dept_name);
			deptName.setText(getItem(position).toString());
			return convertView;
		}

	}

	private void callTel() {
		String num = info.getTel();
		if (!TextUtils.isEmpty(num)) {
			ContactlistUtils.startCall(this, num);
		}
	}

	private void startCall() {
		String num = info.getPhone();
		if (!TextUtils.isEmpty(num)) {
			ContactlistUtils.startCall(this, num);
		}
	}

	private void sendMsg() {
		String num = info.getPhone();
		if (!TextUtils.isEmpty(num)) {
			ContactlistUtils.sendMsg(this, num);
		}
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public static void launchWithStructContact(Context context,ContactInfo info) {
		Intent intent = new Intent();
		intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, info);
		intent.putExtra(ContactDetailActivity.EXTRA_FROM_STRUCT, true);
		intent.setClass(context, ContactDetailActivity.class);
		context.startActivity(intent);
	}
}
