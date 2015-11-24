package cn.yjt.oa.app.enterprise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.enterprise.InfoBasicInputUI.BasicInputFragment;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.picturepicker.DefaultPicturePicker;
import cn.yjt.oa.app.picturepicker.PicturePicker;

public class InfoDetailInputUI extends BaseActivity {

	@Override
	protected Fragment getFragment() {
		return new DetailInputFragment();
	}

	@Override
	protected CharSequence initTitle() {
		return "详细信息输入";
	}

	public static class DetailInputFragment extends Fragment implements
			OnClickListener {
		public static final String ENTERPRISE_CUST_REGISTER_INFO = "CustRegisterInfo";
		private String enterpriseName;
		private String enterpriseAddress;
		private CustRegisterInfo info;
		private PicturePicker picker;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			initExtraData();
			initCustRegisterInfo();
		}

		private void initExtraData() {
			Intent intent = getActivity().getIntent();
			enterpriseAddress = intent.getStringExtra(BasicInputFragment.EXTRA_ENTERPRISE_ADDRESS);
			enterpriseName = intent.getStringExtra(BasicInputFragment.EXTRA_ENTERPRISE_NAME);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.enterprise_step_detail_input,container, false);
			initView(view);
			initImageLoader();
			return view;
		}

		private Button btnNext;
		private EditText licenseNum;
		private ImageButton licenseImg;
		private EditText idcardNum;
		private ImageButton idcardImg;
		private TextView name;
		private ImageLoader imageLoader;

		private void initView(View view) {
			
			name = (TextView) view.findViewById(R.id.tv_enterprise_name);
			name.setText(enterpriseName);
			btnNext = (Button) view.findViewById(R.id.enterprise_btn_next_step);
			btnNext.setOnClickListener(this);
			licenseNum = (EditText) view.findViewById(R.id.et_license_num);
			licenseNum.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus){
						hideInput(licenseNum);
					}
					
				}
			});
			idcardNum = (EditText) view.findViewById(R.id.et_idcard_num);
			idcardNum.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus){
						hideInput(idcardNum);
					}
					
				}
			});
			licenseImg = (ImageButton) view.findViewById(R.id.ib_license_img);
			licenseImg.setOnClickListener(this);
			idcardImg = (ImageButton) view.findViewById(R.id.ib_idcard_img);
			idcardImg.setOnClickListener(this);
		}
		
		private void initImageLoader() {
			imageLoader = MainApplication.getImageLoader();
		}

		private String getIdcardNum() {
			return idcardNum.getText().toString().trim();
		}

		private String getLicenseNum() {
			return licenseNum.getText().toString().trim();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.enterprise_btn_next_step:
				nextStep();
				break;
			case R.id.ib_license_img:
				licenseNum.clearFocus();
				idcardNum.clearFocus();
				pickImg(R.id.ib_license_img);
				break;
			case R.id.ib_idcard_img:
				licenseNum.clearFocus();
				idcardNum.clearFocus();
				pickImg(R.id.ib_idcard_img);
				break;
			default:
				break;
			}
		}

		private int clicker;
		private void pickImg(int id) {
			clicker = id;
			picker = new DefaultPicturePicker();
			picker.pickPicture(this);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK && picker.isPickerResult(requestCode)) {
				Uri uri = picker.getPicture(requestCode, resultCode, data);
				setImage(uri);
			}
		}

		private void setImage(Uri uri) {
			if(clicker == R.id.ib_license_img){
				setImageWithImageLoader(licenseImg,uri.toString());
				info.setLicenseImg(uri.toString());
			}else if(clicker == R.id.ib_idcard_img){
				setImageWithImageLoader(idcardImg,uri.toString());
				info.setIdCardImg(uri.toString());
			}
		}
		
		private void setImageWithImageLoader(final ImageButton imageView,String uri){
			int width = (int) getResources().getDimension(R.dimen.enterprise_img_w_100);
			int height = (int) getResources().getDimension(R.dimen.enterprise_img_h_61_8);
			imageLoader.get(uri.toString(), width, height, new ImageLoaderListener() {
				
				@Override
				public void onSuccess(ImageContainer container) {
					System.out.println("width:"+container.getMaxWidth());
					System.out.println("bitmapwidth:"+container.getBitmap().getWidth());
					imageView.setImageBitmap(container.getBitmap());
				}
				
				@Override
				public void onError(ImageContainer container) {
					
				}
			});
		}
		
		private String getCreatorName(){
			return AccountManager.getCurrentSimpleUser(getActivity()).getName();
		}

		private void initCustRegisterInfo() {
			info = new CustRegisterInfo();
			info.setAddress(enterpriseAddress);
			info.setName(enterpriseName);
			info.setIdCardName(getCreatorName());
		}

		private void startActivity() {
			Intent intent = new Intent(getActivity(), InfoCommitUI.class);
			intent.putExtra(ENTERPRISE_CUST_REGISTER_INFO, info);
			intent.putExtra(InfoCommitUI.INFO_EDITABLE, false);
			startActivity(intent);
		}
		
		private void nextStep(){
			info.setLicenseNum(getLicenseNum());
			info.setIdCard(getIdcardNum());
			startActivity();
//			if(!hasEmpty()){
//				startActivity();
//			}else{
//				toast("信息填写不完整");
//			}
		}
		
		@SuppressWarnings("unused")
		private void toast(String msg){
			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		}
		
		@SuppressWarnings("unused")
		private boolean hasEmpty(){
			if (!TextUtils.isEmpty(info.getIdCard())
					&& !TextUtils.isEmpty(info.getIdCardImg())
					&& !TextUtils.isEmpty(info.getLicenseImg())
					&& !TextUtils.isEmpty(info.getLicenseNum())) {
				return false;
			}
			return true;
		}
		
		
		private void hideInput(EditText editText){
			InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}

	}
}
