package cn.yjt.oa.app.nfctools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.nfctools.NfcReader.NfcReaderInterface;
import cn.yjt.oa.app.nfctools.fragment.NFCBaseFragment;
import cn.yjt.oa.app.nfctools.fragment.NFCOtherFragment;
import cn.yjt.oa.app.nfctools.fragment.NFCReadFragment;
import cn.yjt.oa.app.nfctools.fragment.NFCWriteFragment;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 关于NFC互动标签的界面
 *
 */
public class NFCActivity extends NFCBaseActivity implements OnClickListener {
	
	private final static String TAG = "NFCActivity";
	
	private NfcReader nfcReader;
//	public NfcReader nfcOtherReader;
//	private NfcTagWriter nfcOtherTagWriter;
	private ViewPager mViewPager;
	private NFCPagerAdapter mAdapter;
	private List<NFCBaseFragment> fragments;
	private static final int READER_POSITION = 0;
	
	private TextView tvRead;
	private TextView tvWrite;
//	private TextView tvOther;
	
	private NFCOtherFragment nfcOtherFragment;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		LogUtils.d(TAG, "onCreate");
		setContentView(R.layout.nfc_fragment_pager);
		 /*记录操作 1403*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_INTERACTTAG);

		initFragments();
		initView();
		initNfc();
//		initOtherNfc();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(nfcReader!=null){
			nfcReader.resume();
			if(!nfcReader.isDetecting()){
				nfcReader.startDetecting();
			}
		}
//		if(nfcOtherReader!=null){
//			nfcOtherReader.resume();
//		}
//		if(nfcOtherTagWriter!=null){
//			nfcOtherTagWriter.resume();
//		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(nfcReader != null){
			nfcReader.pause();
			if(nfcReader.isDetecting()){
				nfcReader.stopDetecting();
			}
		}
//		if(nfcOtherReader != null){
//			nfcOtherReader.pause();
//			if(nfcOtherReader.isDetecting()){
//				nfcOtherReader.stopDetecting();
//			}
//		}
//		if(nfcOtherTagWriter != null){
//			nfcOtherTagWriter.pause();
//			if(nfcOtherTagWriter.isDetecting()){
//				nfcOtherTagWriter.stopDetecting();
//			}
//		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initOtherNfc() {
//		if(mAdapter.getItem(2) instanceof NfcReaderInterface){
//			nfcOtherReader=new NfcReader(this, (NfcReaderInterface)mAdapter.getItem(2));
//			nfcOtherFragment.setNfcReader(nfcOtherReader);
//		}
		
//		if(mAdapter.getItem(2) instanceof NfcTagWriterInterface){
//			nfcOtherTagWriter=new NfcTagWriter(this, (NfcTagWriterInterface)mAdapter.getItem(2));
//			nfcOtherFragment.setNfcTagWriter(nfcOtherTagWriter);
//		}
	}

	private void initNfc(){
		NfcReaderInterface readerInterface = getNfcReader();
		if(readerInterface != null){
			nfcReader = new NfcReader(this, readerInterface);
			nfcReader.create();
		}
	}
	
	private NfcReaderInterface getNfcReader(){
		if(mAdapter.getItem(0) instanceof NfcReaderInterface){
			return (NfcReaderInterface)mAdapter.getItem(READER_POSITION);
		}
		return null;
	}
	
	private void initView(){
		
		mViewPager = (ViewPager)findViewById(R.id.nfc_pager);
		mAdapter = new NFCPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new NFCPageChangeListener());
		
		tvRead = (TextView) findViewById(R.id.pager_read);
		tvWrite = (TextView)findViewById(R.id.pager_write);
//		tvOther = (TextView)findViewById(R.id.pager_other);
		tvRead.setOnClickListener(this);
		tvWrite.setOnClickListener(this);
//		tvOther.setOnClickListener(this);
		setTitleTabSelect(true, false, false);
		
	}
	

	
	/**初始化Fragment*/
	private void initFragments(){
		fragments = new ArrayList<NFCBaseFragment>();
		fragments.add(new NFCReadFragment());
		fragments.add(new NFCWriteFragment());
//		nfcOtherFragment=new NFCOtherFragment();
//		fragments.add(nfcOtherFragment);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println("onNewIntent");
		super.onNewIntent(intent);
		if(nfcReader != null){
			nfcReader.newIntent(intent);
		}
//		if(nfcOtherReader != null){
//			nfcOtherReader.newIntent(intent);
//		}
//		if(nfcOtherReader != null){
//			nfcOtherTagWriter.newIntent(intent);
//		}
		
		if(intent.hasExtra("refresh")&&intent.getBooleanExtra("refresh", false)){
			NFCWriteFragment nfcBaseFragment = (NFCWriteFragment) fragments.get(1);
			nfcBaseFragment.refreshData();
			
		}
	}
	
	
	class NFCPagerAdapter extends FragmentPagerAdapter{
		public NFCPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
	
	private void setTitleTabSelect(boolean nfcReade,boolean nfcWrite,boolean nfcOther){
		tvRead.setSelected(nfcReade);
		tvWrite.setSelected(nfcWrite);
//		tvOther.setSelected(nfcOther);
	}
	
	class NFCPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				setTitleTabSelect(true, false, false);
				break;
			case 1:
				setTitleTabSelect(false, true, false);
				break;
//			case 2:
//				setTitleTabSelect(false, false, true);
//				break;

			default:
				break;
			}
			
		}
		
	}
	
	public static void launch(Context context){
		Intent intent = new Intent(context, NFCActivity.class);
		context.startActivity(intent );
	}
	public static void launchForRefresh(Context context){
		Intent intent = new Intent(context, NFCActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("refresh", true);
		context.startActivity(intent );
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pager_read:
			setTitleTabSelect(true, false, false);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.pager_write:
			setTitleTabSelect(false, true, false);
			mViewPager.setCurrentItem(1);
			break;
//		case R.id.pager_other:
//			setTitleTabSelect(false, false, true);
//			mViewPager.setCurrentItem(2);
//			break;
		default:
			break;
		}
		
	}

}
