package cn.yjt.oa.app.enterprise.operation;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.UserManagerInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.utils.OperaEventUtils;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class OperationMembersMenu extends Dialog implements OnTouchListener,
		OnClickListener, AnimatorListener, OnKeyListener {
	private Context context;
	private TextView memberName;
	private ImageView adminIcon;
	private TextView memberPhone;
	private TextView memberPosition;
	private TextView memberDepartment;

	private TextView settingAdmin;
	private TextView modifyMember;
	private TextView checkAttendance;
	private TextView memberDelete;

	private EditText memberNameEt;
	private EditText memberPositionEt;
	private EditText memberDepartmentEt;
	private TextView memberNameTv;
	private TextView memberPhoneTv;

	private View memberSave;

	private View frontView;
	private View backView;

	private View operationMenu;
	private View.OnClickListener mListener;
	private UserManagerInfo mUserManagerInfo;
	private ViewRotate viewRotate;
	private ObjectAnimator dismissAnimator;
	private ObjectAnimator showAnimator;

	public OperationMembersMenu(Context context) {
		super(context, -1);
		this.context = context;
		initView(context);
		dismissAnimator = ObjectAnimator.ofFloat(
				operationMenu,
				"Y",
				0,
				context.getResources().getDimensionPixelSize(
						R.dimen.enterprise_member_detail_height));
		dismissAnimator.setDuration(250);
		dismissAnimator.setInterpolator(new AccelerateInterpolator());
		dismissAnimator.addListener(this);

		showAnimator = ObjectAnimator.ofFloat(
				operationMenu,
				"Y",
				context.getResources().getDimensionPixelSize(
						R.dimen.enterprise_member_detail_height), 0);
		showAnimator.setDuration(250);
		showAnimator.setInterpolator(new DecelerateInterpolator());

	}

	private void initView(Context context) {
		operationMenu = LayoutInflater.from(context).inflate(
				R.layout.operation_member_menu, null);
		frontView = operationMenu.findViewById(R.id.front_layout);
		backView = operationMenu.findViewById(R.id.back_layout);
		backView.setOnClickListener(this);

		memberName = (TextView) operationMenu.findViewById(R.id.member_name);
		adminIcon = (ImageView) operationMenu.findViewById(R.id.admin_icon);

		memberPhone = (TextView) operationMenu.findViewById(R.id.member_phone);
		memberPosition = (TextView) operationMenu
				.findViewById(R.id.member_position);
		memberDepartment = (TextView) operationMenu
				.findViewById(R.id.member_department);

		settingAdmin = (TextView) operationMenu
				.findViewById(R.id.setting_admin);
		modifyMember = (TextView) operationMenu
				.findViewById(R.id.modify_member);
		checkAttendance = (TextView) operationMenu
				.findViewById(R.id.check_attendance);
		memberDelete = (TextView) operationMenu
				.findViewById(R.id.member_delete);
		operationMenu.findViewById(R.id.back).setOnClickListener(this);
//		settingAdmin.setOnClickListener(this);
		checkAttendance.setOnClickListener(this);
		

		memberNameEt = (EditText) operationMenu
				.findViewById(R.id.et_member_name);
		memberPositionEt = (EditText) operationMenu
				.findViewById(R.id.et_member_position);
		memberDepartmentEt = (EditText) operationMenu
				.findViewById(R.id.et_member_department);
		memberNameTv = (TextView) operationMenu
				.findViewById(R.id.member_name_tv);
		memberPhoneTv = (TextView) operationMenu
				.findViewById(R.id.member_phone_tv);

		memberSave = operationMenu.findViewById(R.id.member_save);
		memberSave.setOnClickListener(this);

		operationMenu.setOnTouchListener(this);
		operationMenu.setOnKeyListener(this);

		setContentView(operationMenu);
		viewRotate = new ViewRotate(operationMenu, frontView, backView, 100);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

	}

	public void show() {
		super.show();
		showAnimator.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int height = operationMenu.findViewById(R.id.container).getTop();
		int y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (y < height) {
				hideSoftInput();
				dismissMenu();
			}
		}
		return true;
	}

	@Override
	public void dismiss() {
		dismissAnimator.start();

	}

	public UserManagerInfo getmUserManagerInfo() {
		return mUserManagerInfo;
	}

	public void setmUserManagerInfo(UserManagerInfo mUserManagerInfo) {
		this.mUserManagerInfo = mUserManagerInfo;
		bindData();
		switchAdminIcon();
	}

	private void bindData() {
		memberName.setText(mUserManagerInfo.getName());
		memberPhone.setText(mUserManagerInfo.getPhone());
		memberDepartment.setText(mUserManagerInfo.getDepartment());
		memberPosition.setText(mUserManagerInfo.getPosition());
		Drawable drawable = null;
		Drawable drawablemodify = null;
		Drawable drawableDelete = null;
		if (AccountManager.getCurrent(context).getIsYjtUser() == 1) {
			drawablemodify = context.getResources().getDrawable(
					R.drawable.no_modify_member);
			drawableDelete = context.getResources().getDrawable(
					R.drawable.no_member_delete);
			modifyMember.setClickable(false);
			memberDelete.setClickable(false);

		} else {
			drawablemodify = context.getResources().getDrawable(
					R.drawable.modify_member);
			modifyMember.setClickable(true);
			modifyMember.setOnClickListener(this);
			

			if (mUserManagerInfo.getId() == AccountManager.getCurrent(context)
					.getId()) {
				memberDelete.setClickable(false);
				drawableDelete = context.getResources().getDrawable(
						R.drawable.no_member_delete);
			} else {
				drawableDelete = context.getResources().getDrawable(
						R.drawable.member_delete);
				memberDelete.setClickable(true);
				memberDelete.setOnClickListener(this);
			}

		}
		if (mUserManagerInfo.getIsCustAdmin() == 0) {
			settingAdmin.setClickable(true);
			settingAdmin.setOnClickListener(this);
			settingAdmin.setText("设置管理员");
			drawable = context.getResources().getDrawable(
					R.drawable.setting_admin);
		} else {
			settingAdmin.setText("取消管理员");
			if (mUserManagerInfo.getId() == AccountManager.getCurrent(
					context).getId()) {
				settingAdmin.setClickable(false);
				drawable = context.getResources().getDrawable(
						R.drawable.no_cancel_admin);
			} else {
				settingAdmin.setClickable(true);
				settingAdmin.setOnClickListener(this);
				drawable = context.getResources().getDrawable(
						R.drawable.cancel_admin);
			}
		}
		int pixelSize = context.getResources().getDimensionPixelSize(
				R.dimen.setting_admin);
		drawable.setBounds(0, 0, pixelSize, pixelSize);
		drawablemodify.setBounds(0, 0, pixelSize, pixelSize);
		drawableDelete.setBounds(0, 0, pixelSize, pixelSize);
		settingAdmin.setCompoundDrawables(null, drawable, null, null);
		modifyMember.setCompoundDrawables(null, drawablemodify, null, null);
		memberDelete.setCompoundDrawables(null, drawableDelete, null, null);
		
	}

	private void switchAdminIcon() {
		if (mUserManagerInfo.getIsCustAdmin() == 0) {
			adminIcon.setVisibility(View.INVISIBLE);
		} else {
			adminIcon.setVisibility(View.VISIBLE);
		}
	}

	private void changeAdminIcon() {
		if (mUserManagerInfo.getIsCustAdmin() == 0) {
			dismissAdminIcon();
		} else {
			showAdminIcon();
		}
	}

	private void modifyMember() {
		memberNameEt.setText(mUserManagerInfo.getName());
		memberDepartmentEt.setText(mUserManagerInfo.getDepartment());
		memberPositionEt.setText(mUserManagerInfo.getPosition());
		memberNameTv.setText(mUserManagerInfo.getName());
		memberPhoneTv.setText(mUserManagerInfo.getPhone());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.member_save:
			if (TextUtils.isEmpty(memberNameEt.getText())) {
				toast("用户名不能为空");
			} else {
				mUserManagerInfo.setName(memberNameEt.getText().toString());
				mUserManagerInfo.setPosition(memberPositionEt.getText()
						.toString());
				mUserManagerInfo.setDepartment(memberDepartmentEt.getText()
						.toString());
			}
			viewRotate.applyRotation(-1, 360, 270);
			bindData();
			bindListener(v);
			hideSoftInput();

			break;
		case R.id.modify_member:
			viewRotate.applyRotation(0, 0, 90);
			modifyMember();

             /*记录操作 0210*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_EDIT_MEMBER);

			break;
		case R.id.setting_admin:
			if (mUserManagerInfo.getIsCustAdmin() == 0) {
				mUserManagerInfo.setIsCustAdmin(1);
			} else {
				mUserManagerInfo.setIsCustAdmin(0);
			}
			bindData();
			changeAdminIcon();
			bindListener(v);
			break;
		case R.id.check_attendance:
			bindListener(v);
			break;
		case R.id.member_delete:
			bindListener(v);

             /*记录操作 0211*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_MEMBER);
			break;
		case R.id.back:
			viewRotate.applyRotation(-1, 360, 270);
			hideSoftInput();

			break;
		case R.id.back_layout:
			hideSoftInput();
			break;
		default:
			break;
		}

	}

	private void bindListener(View v) {
		// dismiss();
		mListener.onClick(v);
	}

	public void setOnItemClickLListener(View.OnClickListener mListener) {
		this.mListener = mListener;
	}

	private void toast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAnimationCancel(Animator arg0) {
	}

	@Override
	public void onAnimationEnd(Animator arg0) {
		if (arg0 == dismissAnimator) {
			super.dismiss();
		}
	}

	@Override
	public void onAnimationRepeat(Animator arg0) {
	}

	@Override
	public void onAnimationStart(Animator arg0) {
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			dismissMenu();
		}
		return false;
	}

	public void dismissMenu() {
		int visibility = findViewById(R.id.front_layout).getVisibility();
		if (visibility == View.GONE) {
			viewRotate.applyRotation(-1, 360, 270);
		} else {
			dismiss();
		}
	}

	@SuppressLint("NewApi") 
	private void showAdminIcon() {
		final ImageView iconAnim = this.adminIcon;
		iconAnim.setVisibility(View.VISIBLE);
		
		TextView settingAdmin = this.settingAdmin;
		AnimatorSet animatorSet = new AnimatorSet();
		
		int iconWidth = settingAdmin.getCompoundDrawables()[1].getIntrinsicWidth();
		View parent = (View) settingAdmin.getParent();
		float iconLeft = (settingAdmin.getWidth() - iconWidth)/2f;
		float offset = (iconWidth-iconAnim.getWidth())/2f;
		
		float xStart = parent.getX() + iconLeft + offset;
		float yStart = parent.getY() + settingAdmin.getPaddingTop() + offset;
		float xEnd = iconAnim.getX();
		float yEnd = iconAnim.getY();
		float scale = (float) iconWidth / iconAnim.getWidth();
		
		Animator xAnim = ObjectAnimator.ofFloat(iconAnim, "X", xStart, xEnd);
		Animator yAnim = ObjectAnimator.ofFloat(iconAnim, "Y", yStart, yEnd);
		Animator scaleXAnim = ObjectAnimator.ofFloat(iconAnim, "scaleX", scale, 1);
		Animator scaleYAnim = ObjectAnimator.ofFloat(iconAnim, "scaleY", scale, 1);
		
		animatorSet.setInterpolator(new DecelerateInterpolator());
		animatorSet.playTogether(xAnim, yAnim, scaleXAnim, scaleYAnim);
		animatorSet.setDuration(200);
		animatorSet.start();
	}

	private void dismissAdminIcon() {
		final ImageView adminIcon = this.adminIcon;
		adminIcon.setVisibility(View.INVISIBLE);
		int[] location = new int[2];
		adminIcon.getLocationInWindow(location);
		int[] parentLocation = new int[2];
		operationMenu.findViewById(R.id.container).getLocationInWindow(
				parentLocation);

		TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
				parentLocation[1] - adminIcon.getHeight() - location[1]);
		animation.setDuration(200);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				adminIcon.setVisibility(View.INVISIBLE);
			}
		});
		adminIcon.startAnimation(animation);

	}

	protected void hideSoftInput() {
		InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(memberNameEt.getWindowToken(), 0);
		}
	}
	
	private List<String> getDepartments(){
		long id = mUserManagerInfo.getId();
		System.out.println("getDepartments userid:"+id);
		return ContactManager.getContactManager(context).getUserDepartments(id);
	}
	
	private String generateDepartment(){
		List<String> departments = getDepartments();
		String deptName = "";
		for (int i = 0; i < departments.size(); i++) {
			String string = departments.get(i);
			deptName += string;
			if(i != (departments.size() -1)){
				deptName += ",";
			}
		}
		return deptName;
	}

}
