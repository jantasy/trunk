package cn.yjt.oa.app.sharelink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.RedPacketShareUrlInfo;
import cn.yjt.oa.app.beans.ShareLinkInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class ShareLinkActivity extends TitleFragmentActivity implements OnClickListener,ImageLoaderListener {
	private ImageView iconView;

	private RedPacketShareUrlInfo packetShareUrlInfo;
	private int imageWidth;

	private Bitmap readEnvelopesBitmap;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_sharelink);
		imageWidth=getResources().getDimensionPixelSize(R.dimen.share_link_image_width);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		
		ShareLinkInfo info = getIntent().getParcelableExtra("shareLinkInfo");
		TextView title = (TextView) findViewById(R.id.sharelink_title);
		TextView contentText = (TextView) findViewById(R.id.sharelink_content);
	    iconView = (ImageView) findViewById(R.id.sharelink_icon);
	    title.setText(info.getTitle());
		contentText.setText(info.getContent());
		findViewById(R.id.sharelink_sharebtn).setOnClickListener(this);
		String payload = info.getPayload();
		packetShareUrlInfo = RedPacketShareUrlInfo.parseJson(payload);
		switch (packetShareUrlInfo.getImageType()) {
		case 0:
			readEnvelopesBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_envelopes);
			iconView.setImageBitmap(readEnvelopesBitmap);
			break;

		default:
			if(!TextUtils.isEmpty(packetShareUrlInfo.getImageUrl())){
				MainApplication.getImageLoader().get(packetShareUrlInfo.getImageUrl(),imageWidth,1, this,false);
			}
			break;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(readEnvelopesBitmap != null){
			readEnvelopesBitmap.recycle();
			readEnvelopesBitmap = null;
		}
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sharelink_sharebtn:
			share();
			break;
		default:
			break;
		}
	}
	
	private void share(){
		Intent shareIntent = new Intent();  
		shareIntent.setAction(Intent.ACTION_SEND);  
		shareIntent.setType("text/plain");  
		shareIntent.putExtra(Intent.EXTRA_TEXT, packetShareUrlInfo.getDescription());
		startActivity(Intent.createChooser(shareIntent, "分享到"));

		/*记录操作 1304*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_SHARE);
	}
	
	public static void launchWithMessageInfo(Fragment fragment,MessageInfo info,int requestCode){
		ShareLinkInfo shareLinkInfo=new ShareLinkInfo(info);
		Intent intent = new Intent(fragment.getActivity(), ShareLinkActivity.class);
		intent.putExtra("shareLinkInfo", shareLinkInfo);
		fragment.startActivityForResult(intent , requestCode);
	}
	public static void launch(Context context,MessageInfo info){
		ShareLinkInfo shareLinkInfo=new ShareLinkInfo(info);
		Intent intent = new Intent(context, ShareLinkActivity.class);
		intent.putExtra("shareLinkInfo", shareLinkInfo);
		context.startActivity(intent);
	}

	@Override
	public void onSuccess(final ImageContainer container) {
		iconView.post(new Runnable() {
			
			@Override
			public void run() {
				Bitmap bitmap = container.getBitmap();
				int oldWidth = bitmap.getWidth();
				int oldHeight = bitmap.getHeight();
				float f = ((float) imageWidth) / oldWidth;
				int dheight = (int) (f * oldHeight);
				Bitmap bm = null;
				try {
					bm = Bitmap.createScaledBitmap(bitmap, imageWidth, dheight, false);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					System.gc();
					MainApplication.getImageLoader().getImageCache().trim();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					bm = Bitmap.createScaledBitmap(bitmap, imageWidth, dheight, false);
				}
			
				iconView.setImageBitmap(bm);
			}
		});
		
	}

	@Override
	public void onError(ImageContainer container) {
	}



}
