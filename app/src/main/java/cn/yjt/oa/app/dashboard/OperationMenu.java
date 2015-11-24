package cn.yjt.oa.app.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;

@SuppressLint("ViewConstructor")
public class OperationMenu extends PopupWindow implements OnClickListener {
	private View menu;
	private View uninstallLine;
	private View btnUninstall;
	private View btnDesktop;
	private View desktopLine;
	
	
	private OperationListener operationListener;
	
	public OperationMenu() {
		super();
	}

	public OperationMenu(Context context) {
		super(context);
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.operation_menu, null);
		
		menu = contentView.findViewById(R.id.ll_menu);
		contentView.findViewById(R.id.ll_hide).setOnClickListener(this);
		uninstallLine=contentView.findViewById(R.id.tv_line);
		btnUninstall = contentView.findViewById(R.id.ll_uninstall);
		desktopLine=contentView.findViewById(R.id.desktop_line);
		btnDesktop = contentView.findViewById(R.id.ll_desktop);
		btnUninstall.setOnClickListener(this);
		btnDesktop.setOnClickListener(this);
		setWidth( WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight( WindowManager.LayoutParams.WRAP_CONTENT);
		setOutsideTouchable(true);
		setFocusable(true);
		setContentView(contentView);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}
	
	public void isVisible(boolean uninstall,boolean desktop){
		if(uninstall ||desktop){
			menu.setBackgroundResource(R.drawable.operation_menu_more_bg);
		}else{
			menu.setBackgroundResource(R.drawable.operation_menu_bg);
		}
		if(!uninstall){
			uninstallLine.setVisibility(View.GONE);
			btnUninstall.setVisibility(View.GONE);
		}else{
			uninstallLine.setVisibility(View.VISIBLE);
			btnUninstall.setOnClickListener(this);
			btnUninstall.setVisibility(View.VISIBLE);
		}
		if(desktop){
			btnDesktop.setVisibility(View.VISIBLE);
			desktopLine.setVisibility(View.VISIBLE);
			btnDesktop.setOnClickListener(this);
		}else{
			btnDesktop.setVisibility(View.GONE);
			desktopLine.setVisibility(View.GONE);
		}
	}
	
	public void setOperationListener(OperationListener operationListener) {
		this.operationListener = operationListener;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.ll_hide:
			if(operationListener != null){
				operationListener.onHideClicked();
                /*记录操作 0502*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_DASHBOARD_HIDE_ICON);
			}
			break;
		case R.id.ll_uninstall:
			if(operationListener != null){
				operationListener.onUninstallClicked();
			}
			break;
		case R.id.ll_desktop:
			if(operationListener != null){
				operationListener.onDesktopClicked();
                /*记录操作 0503*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_DASHBOARD_SEND_ICON_DESKTOP);
			}
			break;
		default:
			break;
		}
	}
	
	
	
	public static interface OperationListener{
		
		public void onHideClicked();
		
		public void onUninstallClicked();
		
		public void onDesktopClicked();
		
		
		
	}

}
