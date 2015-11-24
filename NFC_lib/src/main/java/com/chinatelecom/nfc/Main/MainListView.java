package com.chinatelecom.nfc.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailAdActivity;
import com.chinatelecom.nfc.DetailBusActivity;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailCouponActivity;
//import com.chinatelecom.nfc.DetailLotteryActivity;
import com.chinatelecom.nfc.DetailMeetingActivity;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailMovieActivity;
//import com.chinatelecom.nfc.DetailParkActivity;
import com.chinatelecom.nfc.DetailTextActivity;
import com.chinatelecom.nfc.DetailWebActivity;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.LotteryManageActivity02;
import com.chinatelecom.nfc.NameCardManageActivity;
import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.TagViewer;
import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Main.MainListView.MainAdapter.MainViewHolder;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.chinatelecom.nfc.View.RefreshListView;
import com.chinatelecom.nfc.View.RefreshListView.OnRefreshListener;

public class MainListView implements Html.ImageGetter, Html.TagHandler{
	private ListView listView;
	//private RefreshListView listView;
	private MainAdapter adpter;
	private List<Map<String, Object>> datas;
	private Button btnAddEnvironments;
	private Context context;

	public ListView getListViewView(){
		return listView;
	}
	public void delete(){
		new DeleteTask().execute();
	}
	public List<Map<String, Object>> getListDatas(){
		return datas;
	}
	private String mDataType;
	private String mReadOrWrite;
	
	public MainListView(final Context context,String dataType,String readOrWrite) {
		this.context = context;
		this.mDataType = dataType;
		this.mReadOrWrite = readOrWrite;
		listView = (ListView) ((Activity)context).findViewById(R.id.mainListView);
//		listView = (RefreshListView) ((Activity)context).findViewById(R.id.mainListView);
//		listView.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				 new GetDataTask().execute();
//			}
//			
//		});
		datas = getDatas(dataType,readOrWrite);
		adpter = new MainAdapter(context);
		final Context _context = context;
		listView.setAdapter(adpter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MainViewHolder holder = (MainViewHolder) view.getTag();
				jumpToAdapterPage(_context, holder);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						MainViewHolder holder = (MainViewHolder) view.getTag();
						//如果是名片夹列表点击我的名片Item则去掉删除选项。
						if(!holder.hideReadOrWrite.getText().toString().equals(String.valueOf(MyDataTable.TAG_MY_NAMECARD))){
						String[] Items = new String[]{
								context.getResources().getString(R.string.nfc_view),//查看
								context.getResources().getString(R.string.nfc_loveorhite),//收藏
								context.getResources().getString(R.string.nfc_delete_),//删除
								context.getResources().getString(R.string.nfc_cancel),//取消
								};
							createLongClickDialog_Nomarl(holder, Items).show();
						}else{
							String[] Items = new String[]{
									context.getResources().getString(R.string.nfc_view),//查看
									context.getResources().getString(R.string.nfc_cancel),//取消
									};
								createLongClickDialog_MyNameCard(holder, Items).show();
						}
						return true;
					}
				});
	}
	/**
	 * 删除当前应用的桌面快捷方式
	 * 
	 * @param cx
	 */
	public static void delShortcut(Context cx,String name ) {
	    Intent shortcut = new Intent(
	            "com.android.launcher.action.UNINSTALL_SHORTCUT");

	    // 获取当前应用名称
	    String title = name;
//	    try {
//	        final PackageManager pm = cx.getPackageManager();
//	        title = pm.getApplicationLabel(pm.getApplicationInfo(cx.getPackageName(),PackageManager.GET_META_DATA)).toString();
//	    } catch (Exception e) {
//	    }
	    // 快捷方式名称
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
	    Intent shortcutIntent = cx.getPackageManager()
	            .getLaunchIntentForPackage(cx.getPackageName());
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	    cx.sendBroadcast(shortcut);
	}
	/**
	* 删除快捷方式 
	* */ 
	public void deleteShortCut(Activity activity,String shortcutName) 
	{ 
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT"); 
		//快捷方式的名称 
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,shortcutName); 
		/**改成以下方式能够成功删除，估计是删除和创建需要对应才能找到快捷方式并成功删除**/ 
		Intent intent = new Intent(); 
		intent.setClass(activity, activity.getClass()); 
		intent.setAction("android.intent.action.MAIN"); 
		intent.addCategory("android.intent.category.LAUNCHER"); 
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent); 
		activity.sendBroadcast(shortcut); 
	} 
	/**
	 * 创建长按list响应事件
	 */
	private Dialog createLongClickDialog_Nomarl(final MainViewHolder holder, final String[] items){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(holder.title.getText().toString());
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					jumpToAdapterPage(context,holder);
					break;
				case 1:
					//收藏
					String hideReadOrWrite = holder.hideReadOrWrite.getText().toString();
					MyData my = null;
					if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READFROMNFC))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
							null, null, null, null, MyDataTable.TAG_READ_FAVORITE);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_nc_love);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_WRITETAG))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_WRITE_FAVORITE);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_nc_love);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READ_FAVORITE))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_READFROMNFC);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_checkbox_null);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_WRITE_FAVORITE))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_WRITETAG);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_checkbox_null);
					}
					int flag = MyDataDao.update(context, my);
					update(mDataType, mReadOrWrite);
					break;
				case 2:
					 List<Map<String,Object>> myids = getIds(MyDataDao.getMyDatasFromDB(context, holder.id.getText().toString(), null, null));
					
					 if(myids != null ){
						 MyDataDao.delete(context, myids);
						 update(mDataType, mReadOrWrite);
					 }
					 
//					 if(mDataType !=null && mDataType.equals(String.valueOf(MyDataTable.NAMECARD)))
//						 deleteShortCut(MyUtil.getActivity(),holder.title.getText().toString());
					break;

				default:
					break;
				}
			}
		});
		return builder.create();
	}
	/**
	 * 创建长按list响应事件
	 */
	private Dialog createLongClickDialog_MyNameCard(final MainViewHolder holder, final String[] items){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(holder.title.getText().toString());
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					jumpToAdapterPage(context,holder);
					break;
				case 1:
					//收藏
					String hideReadOrWrite = holder.hideReadOrWrite.getText().toString();
					MyData my = null;
					if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READFROMNFC))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
							null, null, null, null, MyDataTable.TAG_READ_FAVORITE);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_nc_love);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_WRITETAG))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_WRITE_FAVORITE);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_nc_love);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READ_FAVORITE))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_READFROMNFC);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_checkbox_null);
					}else if(hideReadOrWrite.equals(String.valueOf(MyDataTable.TAG_WRITE_FAVORITE))){
						my = new MyData(Integer.parseInt(holder.id.getText().toString()), 
								null, null, null, null, MyDataTable.TAG_WRITETAG);
						holder.ivLove.setBackgroundResource(R.drawable.nfc_checkbox_null);
					}
					int flag = MyDataDao.update(context, my);
					update(mDataType, mReadOrWrite);
					break;
				default:
					break;
				}
			}
		});
		return builder.create();
	}
	public void update(String dataType,String readOrWrite) {
		// TODO Auto-generated method stub
		datas = getDatas(dataType,readOrWrite);
		notifyDataSetChanged();
	}
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		adpter.notifyDataSetChanged();
	}
	
	
	
	
	private List<Map<String, Object>> getDatas(String dataType,String readOrWrite){
		return MyDataDao.getMyDatasFromDB(context,dataType,readOrWrite);
	}
	
	private class DeleteTask extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			MyDataDao.delete(context, getIds());
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			update(mDataType, mReadOrWrite);
			super.onPostExecute(result);
		}
		
	}
	
    //执行异步的操作
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
        	datas = getDatas(mDataType, mReadOrWrite);
        	
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
        	//添加数据
//        	Map<String, Object> map = new HashMap<String, Object>();
//        	datas.add(0, map);

            // Call onRefreshComplete when the list has been refreshed.
        	notifyDataSetChanged();
//            listView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
	
	
	
	//adapter
	class MainAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context context;
//		private List<Map<String, Object>> envirDatas;

		public MainAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return datas.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MainViewHolder holder = null;
			if (convertView == null) {

				holder = new MainViewHolder();

				convertView = mInflater.inflate(R.layout.nfc_main_item, null);
				holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);
				holder.id = (TextView) convertView.findViewById(R.id.tvId);
				holder.hideId = (TextView) convertView.findViewById(R.id.hideId);
				holder.hideType = (TextView) convertView.findViewById(R.id.hideType);
				holder.hideReadOrWrite = (TextView) convertView.findViewById(R.id.hideReadOrWrite);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.ivLove = (ImageView) convertView.findViewById(R.id.ivArrow);
				convertView.setTag(holder);

			} else {

				holder = (MainViewHolder) convertView.getTag();
			}
//			holder.llItem.setBackgroundResource((Integer) datas.get(position).get("bg"));
			holder.id.setText(String.valueOf(datas.get(position).get("id")));
			holder.hideId.setText(String.valueOf(datas.get(position).get("hideId")));
			String dataType = String.valueOf(datas.get(position).get("tagType"));
			holder.hideType.setText(dataType);
			holder.hideReadOrWrite.setText(String.valueOf(datas.get(position).get("readorwrite")));
			holder.icon.setBackgroundResource((Integer) datas.get(position).get("img"));
			holder.title.setText((String) datas.get(position).get("title"));
			String info = (String) datas.get(position).get("info");
			if(dataType.equals(String.valueOf(MyDataTable.BUS_CARD))){
				info = info.replace("<img src=\"spliter\"/>", " ");
				holder.content.setText(Html.fromHtml(info));
			}else{
				if(info.equals("null"))
					holder.content.setText("");
				else
					holder.content.setText(info);
			}
			holder.time.setText((String) datas.get(position).get("createTime"));
			Integer readOrWrite = (Integer)datas.get(position).get("readorwrite");
			if(readOrWrite == MyDataTable.TAG_READ_FAVORITE || readOrWrite == MyDataTable.TAG_WRITE_FAVORITE){
				holder.ivLove.setBackgroundResource(R.drawable.nfc_nc_love);
			}else{
				holder.ivLove.setBackgroundResource(R.drawable.nfc_checkbox_null);
			}
			return convertView;
		}


		public final class MainViewHolder {
			public TextView id;
			public TextView hideId;
			public TextView hideType;
			public TextView hideReadOrWrite;
			public ImageView icon;
			public TextView title;
			public TextView content;
			public TextView time;
			public Button viewBtn;
			public LinearLayout llItem;
			public ImageView ivLove;
		}

	}
	
	
	public void jumpToAdapterPage(final Context _context,MainViewHolder holder) {
		String dataType = holder.hideType.getText().toString();
		String tmp = holder.title.getText().toString();
		String mydataId = holder.id.getText().toString();
		String tableID = holder.hideId.getText().toString();
		if (dataType != null && dataType.length() > 0) {
			int iDataType = -1;
			int iTableID = 1;
			int iMydataId = 1;
			try {
				iDataType = Integer.parseInt(dataType);
				iTableID = Integer.parseInt(tableID);
				iMydataId = Integer.parseInt(mydataId);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				Intent intent = new Intent();
//				intent.setAction(Intent.ACTION_DEFAULT);
				intent.setClass(_context, TagViewer.class);
				intent.putExtra(Constant.MYDATA_ID, iMydataId);
				intent.putExtra(Constant.MYDATA_DATATYPE, iDataType);
				intent.putExtra(Constant.MYDATA_TABLEID, iTableID);
				String strReadOrWrite = holder.hideReadOrWrite.getText().toString();
				if(strReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READFROMNFC))
						|| strReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READ_FAVORITE))){
					intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_READ_TAG);
				}else{
					intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_MAKE_TAG);
				}
				
				switch (iDataType) {
				case -1:
					MyUtil.showMessage(R.string.nfc_m_parse_exception, _context);
					break;
				case MyDataTable.MYMODE:
					
					break;
				case MyDataTable.MEETTING:
					intent.setClass(_context, DetailMeetingActivity.class);
					break;
				//ashley 0923 删除无用资源
//				case MyDataTable.COUPON:
//					intent.setClass(_context, DetailCouponActivity.class);
//					break;
//				case MyDataTable.LOTTERY:
//					if(strReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READFROMNFC))
//							|| strReadOrWrite.equals(String.valueOf(MyDataTable.TAG_READ_FAVORITE))){
//						intent.setClass(_context, DetailLotteryActivity.class);
//						intent.putExtra("mTitle", "我的抽奖器");
//					}else{
//						intent.setClass(_context, LotteryManageActivity02.class);
//						intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_MAKE_TAG);
//					}
//					
//					break;
//				case MyDataTable.AD:
//					intent.setClass(_context, DetailAdActivity.class);
//					break;
				case MyDataTable.MYMODECUSTOM:
					break;
					
				case MyDataTable.NAMECARD:
					
					intent.setClass(_context, NameCardManageActivity.class);
					String a = holder.hideReadOrWrite.getText().toString();
					String b = holder.content.getText().toString();
					if(holder.hideReadOrWrite.getText().toString().equals(String.valueOf(MyDataTable.TAG_MY_NAMECARD))&&(holder.content.getText().toString().equals(""))){
						intent.setAction(Intent.ACTION_EDIT);
						intent.putExtra(Constant.INTENT_MY_NAMECARD, strReadOrWrite);
					}else
						intent.setAction(Intent.ACTION_DEFAULT);
					break;
				//ashley 0923 删除无用资源
//				case MyDataTable.MOVIETICKET:
//					intent.setClass(_context, DetailMovieActivity.class);
//					break;
//				case MyDataTable.PARKTICKET:
//					intent.setClass(_context, DetailParkActivity.class);
////					intent.putExtra("MYPARKTICKET", "MYPARKTICKET");
//					break;
				case MyDataTable.TEXT:
					intent.setClass(_context, DetailTextActivity.class);
					break;
				case MyDataTable.WEB:
					intent.setClass(_context, DetailWebActivity.class);
					break;
				case MyDataTable.BUS_CARD:
					intent.setClass(_context, DetailBusActivity.class);
					break;
					
				default:
					break;
				}
				if(iDataType != -1){
					_context.startActivity(intent);
				}
				
			}
		}
	}
	
	
	
	public List<Map<String,Object>> getIds(){
		List<Map<String,Object>> ids = new ArrayList<Map<String,Object>>();
		
		List<Integer> idMymode = new ArrayList<Integer>();
		List<Integer> idMeetting = new ArrayList<Integer>();
		List<Integer> idCoupon = new ArrayList<Integer>();
		List<Integer> idLottery = new ArrayList<Integer>();
		List<Integer> idAd = new ArrayList<Integer>();
		List<Integer> idMymodecustom = new ArrayList<Integer>();
		List<Integer> idNamecard = new ArrayList<Integer>();
		List<Integer> idMovieticket = new ArrayList<Integer>();
		List<Integer> idParkticket = new ArrayList<Integer>();
		List<Integer> idOther = new ArrayList<Integer>();
		StringBuilder readOrWrite = new StringBuilder();
//		readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",").append(MyDataTable.TAG_WRITETAG);
		readOrWrite.append(MyDataTable.TAG_READFROMNFC);
		List<Map<String,Object>> mydatas = MyDataDao.getMyDatasFromDB(context, null, null, readOrWrite.toString());
		if(mydatas == null || mydatas.size() == 0) 
			return null;
		for (Map<String, Object> map : mydatas) {
			Integer dataType = -1;
			try {
				dataType = (Integer)map.get("tagType");
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
				return null;
			}
			switch (dataType) {
			case MyDataTable.MYMODE:
				idMymode.add((Integer)map.get("hideId"));
				break;
			case MyDataTable.MEETTING:
				idMeetting.add((Integer)map.get("hideId"));
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.COUPON:
//				idCoupon.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.LOTTERY:
//				idLottery.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.AD:
//				idAd.add((Integer)map.get("hideId"));
//				break;
			case MyDataTable.MYMODECUSTOM:
				idMymodecustom.add((Integer)map.get("hideId"));
				break;
			case MyDataTable.NAMECARD:
				idNamecard.add((Integer)map.get("hideId"));
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.MOVIETICKET:
//				idMovieticket.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.PARKTICKET:
//				idParkticket.add((Integer)map.get("hideId"));
//				break;
			default:
				break;
			}
			//所有ID
			idOther.add((Integer)map.get("id"));
		}
		Map<String,Object> m = null;
		if(idMymode != null && idMymode.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[0]);
			m.put("ids", idMymode);
			ids.add(m);
		}
		if(idMeetting != null && idMeetting.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[1]);
			m.put("ids", idMeetting);
			ids.add(m);
		}
		//ashley 0923 删除无用资源
//		if(idCoupon != null && idCoupon.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[2]);
//			m.put("ids", idCoupon);
//			ids.add(m);
//		}
//		if(idLottery != null && idLottery.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[3]);
//			m.put("ids", idLottery);
//			ids.add(m);
//		}
//		if(idAd != null && idAd.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[4]);
//			m.put("ids", idAd);
//			ids.add(m);
//		}
		if(idMymodecustom != null && idMymodecustom.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[2]);
			m.put("ids", idMymodecustom);
			ids.add(m);
		}
		if(idNamecard != null && idNamecard.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[3]);
			m.put("ids", idNamecard);
			ids.add(m);
		}
		//ashley 0923 删除无用资源
//		if(idMovieticket != null && idMovieticket.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[7]);
//			m.put("ids", idMovieticket);
//			ids.add(m);
//		}
//		if(idParkticket != null && idParkticket.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[8]);
//			m.put("ids", idParkticket);
//			ids.add(m);
//		}
		if(idOther != null && idOther.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[4]);
			m.put("ids", idOther);
			ids.add(m);
		}
		
		return ids;
	}
	public List<Map<String,Object>> getIds(List<Map<String, Object>> listMaps){
		List<Map<String,Object>> ids = new ArrayList<Map<String,Object>>();
		
		List<Integer> idMymode = new ArrayList<Integer>();
		List<Integer> idMeetting = new ArrayList<Integer>();
		List<Integer> idCoupon = new ArrayList<Integer>();
		List<Integer> idLottery = new ArrayList<Integer>();
		List<Integer> idAd = new ArrayList<Integer>();
		List<Integer> idMymodecustom = new ArrayList<Integer>();
		List<Integer> idNamecard = new ArrayList<Integer>();
		List<Integer> idMovieticket = new ArrayList<Integer>();
		List<Integer> idParkticket = new ArrayList<Integer>();
		List<Integer> idOther = new ArrayList<Integer>();
		
		if(datas == null) 
			return null;
		for (Map<String, Object> map : listMaps) {
			Integer dataType = -1;
			try {
				dataType = (Integer)map.get("tagType");
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
				return null;
			}
			switch (dataType) {
			case MyDataTable.MYMODE:
				idMymode.add((Integer)map.get("hideId"));
				break;
			case MyDataTable.MEETTING:
				idMeetting.add((Integer)map.get("hideId"));
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.COUPON:
//				idCoupon.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.LOTTERY:
//				idLottery.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.AD:
//				idAd.add((Integer)map.get("hideId"));
//				break;
			case MyDataTable.MYMODECUSTOM:
				idMymodecustom.add((Integer)map.get("hideId"));
				break;
			case MyDataTable.NAMECARD:
				idNamecard.add((Integer)map.get("hideId"));
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.MOVIETICKET:
//				idMovieticket.add((Integer)map.get("hideId"));
//				break;
//			case MyDataTable.PARKTICKET:
//				idParkticket.add((Integer)map.get("hideId"));
//				break;
			default:
				break;
			}
			//所有ID
			idOther.add((Integer)map.get("id"));
		}
		Map<String,Object> m = null;
		//ashley 0923 删除无用资源
		if(idMymode != null && idMymode.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[0]);
			m.put("ids", idMymode);
			ids.add(m);
		}
		if(idMeetting != null && idMeetting.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[1]);
			m.put("ids", idMeetting);
			ids.add(m);
		}
//		if(idCoupon != null && idCoupon.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[2]);
//			m.put("ids", idCoupon);
//			ids.add(m);
//		}
//		if(idLottery != null && idLottery.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[3]);
//			m.put("ids", idLottery);
//			ids.add(m);
//		}
//		if(idAd != null && idAd.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[4]);
//			m.put("ids", idAd);
//			ids.add(m);
//		}
		if(idMymodecustom != null && idMymodecustom.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[2]);
			m.put("ids", idMymodecustom);
			ids.add(m);
		}
		if(idNamecard != null && idNamecard.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[3]);
			m.put("ids", idNamecard);
			ids.add(m);
		}
//		if(idMovieticket != null && idMovieticket.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[7]);
//			m.put("ids", idMovieticket);
//			ids.add(m);
//		}
//		if(idParkticket != null && idParkticket.size() > 0){
//			m = new HashMap<String, Object>();
//			m.put("table", MyDataDao.tables[8]);
//			m.put("ids", idParkticket);
//			ids.add(m);
//		}
		if(idOther != null && idOther.size() > 0){
			m = new HashMap<String, Object>();
			m.put("table", MyDataDao.tables[4]);
			m.put("ids", idOther);
			ids.add(m);
		}
		
		return ids;
	}
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (!opening && "version".equals(tag)) {
			try {
				output.append(context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0).versionName);
			} catch (NameNotFoundException e) {
			}
		}
	}


	@Override
	public Drawable getDrawable(String source) {

		return null;
	}
}
