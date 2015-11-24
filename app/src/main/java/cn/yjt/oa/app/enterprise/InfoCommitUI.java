package cn.yjt.oa.app.enterprise;

import io.luobo.common.Cancelable;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.enterprise.InfoDetailInputUI.DetailInputFragment;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.picturepicker.DefaultPicturePicker;
import cn.yjt.oa.app.picturepicker.PicturePicker;

public class InfoCommitUI extends BaseActivity {
	public static final String INFO_EDITABLE = "editable";

	@Override
	protected Fragment getFragment() {
		return new CommitFragment();
	}

	@Override
	protected CharSequence initTitle() {
		return "提交申请";
	}

	public static class CommitFragment extends Fragment implements OnClickListener,
			Listener<Response<CustRegisterInfo>> {
		public static final String EXTRA_ENTERPRISE_NAME = "name";
		public static final String EXTRA_REPONSE_CODE = "code";
		private boolean isEditable;
		private CustRegisterInfo info;
		private PicturePicker picker;
		private List<Cancelable> tasks = new ArrayList<Cancelable>();

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			initExtraData();
			initImageLoader();
		}

		private void initExtraData() {
			Intent intent = getActivity().getIntent();
			info = intent.getParcelableExtra(DetailInputFragment.ENTERPRISE_CUST_REGISTER_INFO);
			isEditable = intent.getBooleanExtra(INFO_EDITABLE, false);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.enterprise_info_commit,container, false);
			if (info != null) {
				initView(view);
			}
			return view;
		}

		public CustRegisterInfo getInfo() {
			info.setIdCard(idcardNum.getText().toString().trim());
			info.setLicenseNum(licenseNum.getText().toString().trim());
			info.setName(name.getText().toString().trim());
			return info;
		}

		private Button btnCommit;
		private EditText licenseNum;
		private ImageButton licenseImg;
		private EditText idcardNum;
		private ImageButton idcardImg;
		private TextView address;
		private TextView status;
		private TextView name;
		private ProgressDialog progress;
		private ImageLoader imageLoader;

		private ImageView licenseImgDelete;
		private ImageView idcardImgDelete;
		
		private void initView(View view) {
			name = (TextView) view.findViewById(R.id.tv_enterprise_name);
			name.setText(info.getName());

			btnCommit = (Button) view.findViewById(R.id.enterprise_btn_next_step);
			btnCommit.setOnClickListener(this);

			licenseNum = (EditText) view.findViewById(R.id.et_license_num);
			licenseNum.setFocusable(isEditable);
			licenseNum.setText(info.getLicenseNum());

			licenseImg = (ImageButton) view.findViewById(R.id.ib_license_img);
			if(!TextUtils.isEmpty(info.getLicenseImg())){
				setImageWithImageLoader(licenseImg, info.getLicenseImg());
			}
			
			licenseImg.setFocusable(isEditable);

			idcardNum = (EditText) view.findViewById(R.id.et_idcard_num);
			idcardNum.setText(info.getIdCard());
			idcardNum.setFocusable(isEditable);

			idcardImg = (ImageButton) view.findViewById(R.id.ib_idcard_img);
			if(!TextUtils.isEmpty(info.getIdCardImg())){
				setImageWithImageLoader(idcardImg, info.getIdCardImg());
			}

			idcardImg.setFocusable(isEditable);

			address = (TextView) view.findViewById(R.id.et_address);
			address.setText(info.getAddress());
			address.setFocusable(isEditable);

			status = (TextView) view.findViewById(R.id.tv_status);

			initStatus(info.getCheckStatus());

			if (!isEditable) {
				licenseNum.setBackgroundColor(Color.TRANSPARENT);
				idcardNum.setBackgroundColor(Color.TRANSPARENT);
				address.setBackgroundColor(Color.TRANSPARENT);
			}
			
			licenseImgDelete = (ImageView) view.findViewById(R.id.iv_license_img_delete);
			if(isEditable){
				if(!TextUtils.isEmpty(info.getLicenseImg())){
					licenseImgDelete.setVisibility(View.VISIBLE);
				}
//				licenseImg.setImageResource(R.drawable.enterprise_add_image);
				licenseImgDelete.setOnClickListener(this);
				licenseImg.setOnClickListener(this);
			}
			
			idcardImgDelete = (ImageView) view.findViewById(R.id.iv_idcard_img_delete);
			if(isEditable){
				if(!TextUtils.isEmpty(info.getIdCardImg())){
					idcardImgDelete.setVisibility(View.VISIBLE);
				}
//				idcardImg.setImageResource(R.drawable.enterprise_add_image);
				idcardImgDelete.setOnClickListener(this);
				idcardImg.setOnClickListener(this);
			}
		}

		private void initStatus(int code) {
			switch (code) {
			case 0:
				status.setText("未提交");
				break;
			case -1:
				status.setText("审核未通过");
				break;

			default:
				break;
			}
		}

		private synchronized void showProgressDialog(String msg) {
			if(getActivity() == null){
				return;
			}
			if(progress == null){
				progress = new ProgressDialog(getActivity());
				progress.setCanceledOnTouchOutside(false);
				progress.setCancelable(true);
				progress.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						cancelTask();
					}
				});
				progress.setMessage(msg);
				progress.show();
				System.out.println("progress.show();");
				
			}else{
				progress.setMessage(msg);
				if(!progress.isShowing()){
					progress.show();
				}
			}
		}
		
		private void dismissProgressDialog(){
			if(progress != null && progress.isShowing()){
				progress.dismiss();
			}
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.enterprise_btn_next_step:
				commitFinish();
				break;
			case R.id.ib_license_img:
				pickImg(R.id.ib_license_img);
				break;
			case R.id.ib_idcard_img:
				pickImg(R.id.ib_idcard_img);
				break;
			case R.id.iv_license_img_delete:
				deleteImg(R.id.iv_license_img_delete);
				break;
			case R.id.iv_idcard_img_delete:
				deleteImg(R.id.iv_idcard_img_delete);
				break;
			default:
				break;
			}
		}
		
		private void deleteImg(int id){
			if(id == R.id.iv_license_img_delete){
				licenseImg.setImageResource(R.drawable.enterprise_add_image);
				licenseImgDelete.setVisibility(View.GONE);
			}else if(id == R.id.iv_idcard_img_delete){
				idcardImg.setImageResource(R.drawable.enterprise_add_image);
				idcardImgDelete.setVisibility(View.GONE);
			}
		}

		private void pickImg(int id) {
			clicker = id;
			picker = new DefaultPicturePicker();
			picker.pickPicture(this);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK && picker.isPickerResult(requestCode)) {
				Uri uri = picker.getPicture(requestCode, resultCode, data);
				setImage(uri);
			}
		}

		private int clicker;
		private FileClient createSingleThreadFileClient;

		private void setImage(Uri uri) {
			if (clicker == R.id.ib_license_img) {
				setImageWithImageLoader(licenseImg, uri.toString());
				licenseImgDelete.setVisibility(View.VISIBLE);
				info.setLicenseImg(uri.toString());
			} else if (clicker == R.id.ib_idcard_img) {
				setImageWithImageLoader(idcardImg, uri.toString());
				idcardImgDelete.setVisibility(View.VISIBLE);
				info.setIdCardImg(uri.toString());
			}
		}

		private void commitFinish() {
			createSingleThreadFileClient = FileClientFactory.createSingleThreadFileClient(getActivity());
			if (info.getIdCardImg() != null || info.getLicenseImg() != null) {
				boolean isUploaded = upload();
				if(isUploaded){
					commit();
				}
			}else{
				commit();
			}
		}

		private void startActivity(int code) {
			Intent intent = new Intent(getActivity(), InfoCommitFinishUI.class);
			intent.putExtra(EXTRA_ENTERPRISE_NAME, info.getName());
			intent.putExtra(EXTRA_REPONSE_CODE, code);
			startActivity(intent);
			getActivity().finish();
		}

		public void commit() {
			 if(info.getCheckStatus() == -1){
				update();
			}else{
				submit();
			}
		}
		
		private void submit(){
			System.out.println("submit");
			showProgressDialog("提交企业注册数据");
			tasks.add(getAsyncRequest(AsyncRequest.MODULE_CUSTS).post());
		}
		
		public void update() {
			System.out.println("update");
			showProgressDialog("提交企业注册数据");
			tasks.add(getAsyncRequest(String.format(AsyncRequest.MODULE_CUSTS_REGISTERINFO_ID,info.getId())).put());
		}
		
		
		private AsyncRequest getAsyncRequest(String module){
			Type type_enterprise = new TypeToken<Response<CustRegisterInfo>>(){}.getType();
			AsyncRequest.Builder builder = new AsyncRequest.Builder();
			builder.setModule(module)
			.setResponseType(type_enterprise).setRequestBody(getInfo())
			.setResponseListener(this);
			return builder.build();
		}

		private boolean upload() {
			System.out.println("upload");
			boolean idCardImageHandled = false;
			boolean idLicenseImageHandled = false;
			if(info.getIdCardImg() != null){
				if(!info.getIdCardImg().startsWith("http")){
					tasks.add(uploadTask(info.getIdCardImg()));
				}else{
					//committed.
					idCardImageHandled = true;
				}
			}else{
				idCardImageHandled = true;
			}
			
			if(info.getLicenseImg() != null){
				if(!info.getLicenseImg().startsWith("http")){
					tasks.add(uploadTask(info.getLicenseImg()));
				}else{
					//committed.
					idLicenseImageHandled = true;
				}
			}else{
				idLicenseImageHandled = true;
			}
			
			return idCardImageHandled && idLicenseImageHandled;
			
		}
		
		private boolean isAllImageFinished() {
			boolean idCardImageHandled = false;
			boolean idLicenseImageHandled = false;
			if(info.getIdCardImg() != null){
				if(info.getIdCardImg().startsWith("http")){
					idCardImageHandled = true;
				}
			}else{
				idCardImageHandled = true;
			}
			
			if(info.getLicenseImg() != null){
				if(info.getLicenseImg().startsWith("http")){
					idLicenseImageHandled = true;
				}
			}else{
				idLicenseImageHandled = true;
			}
			
			return idCardImageHandled && idLicenseImageHandled;
			
		}

		private String getImageLoadUrl() {
			return BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE);
		}

		private Cancelable uploadTask(final String uri) {
			System.out.println("uploadTask:"+uri);
			return AsyncRequest.upload(getActivity(), Uri.parse(uri),
					getImageLoadUrl(), "image", 720, 1280, false,
					new ProgressListener<Response<String>>() {

						@Override
						public void onStarted() {
							System.out.println("onStarted");
							if(getActivity() != null&&!getActivity().isFinishing()){
								getActivity().runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										showProgressDialog("正在上传图片...");
									}
								});
							}
						}

						@Override
						public void onProgress(long progress, long total) {

						}

						@Override
						public void onFinished(Response<String> response) {
							System.out.println("onFinished:"+response);
							if(response.getPayload()!=null){
								if (uri.equals(info.getIdCardImg())) {
									info.setIdCardImg(response.getPayload());
								} else if (uri.equals(info.getLicenseImg())) {
									info.setLicenseImg(response.getPayload());
								}
							}
							
							if (getActivity() != null && !getActivity().isFinishing()) {

								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (isAllImageFinished()) {
											commit();
										}
									}
								});
							}
						}

						@Override
						public void onError(InvocationError arg0) {
							toast("上传失败");
						}
					}, createSingleThreadFileClient);
		}

		@Override
		public void onErrorResponse(InvocationError error) {
			dismissProgressDialog();
			toast("提交失败，请重新提交");
		}

		@Override
		public void onResponse(final Response<CustRegisterInfo> response) {
			if(getActivity() != null&&!getActivity().isFinishing()){
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						dismissProgressDialog();
						if (response.getCode() == 0) {
							startActivity(response.getCode());
						} else if(response.getCode() == 2){
							toast(response.getDescription());
						}else if(response.getCode() == 3001){
							toast(response.getDescription());
						}else{
							toast("提交失败，请重新提交");
						}
					}
				});
			}
		}

		private void toast(String msg) {
			if(getActivity()!=null){
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		}

		private void initImageLoader() {
			imageLoader = MainApplication.getImageLoader();
		}
		
		public void cancelTask(){
			for(Cancelable task:tasks){
				try{
					task.cancel();
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
			tasks.clear();
		}

		private void setImageWithImageLoader(final ImageButton image,String uri) {
			AsyncRequest.getInImageView(uri, image, getResources().getDimensionPixelSize(R.dimen.enterprise_img_w_50), getResources().getDimensionPixelSize(R.dimen.enterprise_img_h_40),R.drawable.default_image, R.drawable.default_image);
//			imageLoader.get(uri.toString(),width,height,new ImageLoaderListener() {
//
//						@Override
//						public void onSuccess(ImageContainer container) {
//							image.setImageBitmap(container.getBitmap());
//						}
//
//						@Override
//						public void onError(ImageContainer container) {
//
//						}
//					});
		}

	}

	public static void launchForEdit(Context context, boolean edit,CustRegisterInfo info) {
		Intent intent = new Intent(context, InfoCommitUI.class);
		intent.putExtra(DetailInputFragment.ENTERPRISE_CUST_REGISTER_INFO, info);
		intent.putExtra(INFO_EDITABLE, edit);
		context.startActivity(intent);
	}

}
