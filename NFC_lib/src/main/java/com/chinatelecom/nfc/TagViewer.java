package com.chinatelecom.nfc;

import android.app.Activity;

/**
 * An {@link android.app.Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class TagViewer extends BaseNfcAdapter{


//    static final String TAG = "ViewTag";
//
//    /**
//     * This activity will finish itself in this amount of time if the user
//     * doesn't do anything.
//     */
//    static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;
//
//
////    LinearLayout mTagContent;
//    
//	private Resources res;
//	
//	
//	private String jsonData;
//	private   Gson gson ;
//	
//	
//	
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MyDebug.show(MyDebug.LIFTCYCLE, "onCreate-----> ");
//        setContentView(R.layout.tag_viewer);
////        gson = new Gson();
////        
//        this.res = getResources();
////		
////		onNewIntent(getIntent());
//    }
//
//    void readFromNFC(Intent intent) {
//        // Parse the intent
//        String action = intent.getAction();
//        
//        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            NdefMessage[] msgs;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//                
//                // Setup the views
//                setTitle(R.string.title_scanned_tag);
//                buildTagViews(msgs);
//            } else if(p != null){
//            	String card = CardManager.load(p, res);
//            	MyData d = new MyData(null, card, MyDataTable.BUS_CARD, -1, 1l, MyDataTable.TAG_READFROMNFC);
//            	getJsonData(d);
//            	MyDataDao.insert(this, d, true);
////            	onlyContentView("公交卡",card);
//            }else{
//            	
//                // Unknown tag type
//                byte[] empty = new byte[] {};
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
//                msgs = new NdefMessage[] {msg};
//                
//                // Setup the views
//                setTitle(R.string.title_scanned_tag);
//                buildTagViews(msgs);
//            }
//        }else{
//            Log.e(TAG, "Unknown intent " + intent);
//            finish();
//            return;
//        }
//    }
//
//
//	void buildTagViews(NdefMessage[] msgs) {
//        if (msgs == null || msgs.length == 0) {
//            return;
//        }
//        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
//        final int size = records.size();
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < size; i++) {
//            ParsedNdefRecord record = records.get(i);
//            sb.append(record.getContent());
//        }
//       Object datas = MyDataDao.readData(this, sb.toString(), true);
//       if(datas != null){
//    	   if (datas instanceof MyData) {
//    		   MyData myData = (MyData)datas;
//    		 
//    		   getJsonData(myData);
//    		   
//    		   switch (myData.dataType) {
//    		   case MyDataTable.MEETTING:
////    			   meettingView(myData);
//    			   break;
//
//    		   default:
//    			   break;
//			}
//    		   
//    	   }else if(datas instanceof String){
//    		   MyData d = new MyData(null, (String)datas, MyDataTable.TEXT, -1, 1l, MyDataTable.TAG_WRITETAG);
//    		   getJsonData(d);
////    		   onlyContentView("文本标签",(String)datas);
//    	   }
//       }
//    }
//	private Integer mydataId;
//	private Integer dataType;
//	private Integer tableID;
//	private MyData mMyData;
//    @Override
//    public void onNewIntent(Intent intent) {
//    	super.onNewIntent(intent);
//    	MyDebug.show(MyDebug.LIFTCYCLE, "onNewIntent-----> ");
//    	setIntent(intent);
//    	
//    	whichPage(intent);
////    	if(!bool){
////    		writeToNFC(intent);
////    	}else{
////           
////    	}
//    }
//	
//	private void writeToNFC(String str,Intent intent){
//		Tag m_tag = ((Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
//		
//		NdefRecord mNdefRecord = MyUtil.newTextRecord(str,Locale.CHINA,true);
//		  final Intent intent_to = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);
//		  intent_to.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, mNdefRecord.toByteArray());
//		try
//	    {
//	      NdefRecord[] arrayOfNdefRecord = new NdefRecord[]{mNdefRecord};
//	      NdefMessage localNdefMessage = new NdefMessage(arrayOfNdefRecord);
//	      Ndef localNdef = Ndef.get(m_tag);
//	      if (localNdef != null)
//	      {
//	        localNdef.connect();
//	        localNdef.writeNdefMessage(localNdefMessage);
//	        MyUtil.showMessage("写入成功", this);
//	      }
//	      NdefFormatable localNdefFormatable = NdefFormatable.get(m_tag);
//	      while (localNdefFormatable != null)
//	      {
//	        localNdefFormatable.connect();
//	        localNdefFormatable.format(localNdefMessage);
//	      }
//	    }
//	    catch (IOException localIOException){
//	    	localIOException.printStackTrace();
//	    }
//	    catch (FormatException localFormatException) {
//	    	localFormatException.printStackTrace();
//	    }
//	}
////	private void readFromNFC(Intent intent){
////		    String str2 = "";
////		    Parcelable localParcelable = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
////		    if (localParcelable != null){
////		    	str2 = CardManager.load(localParcelable,res);
////		    	MyData mMyData = new MyData(null, str2, MyDataTable.BUS_CARD, -1, 1l, MyDataTable.TAG_READFROMNFC);
////		    	MyDataDao.insert(this, mMyData, true);
////		    	onlyContentView("公交卡",str2);
////		      if (str2 == null){
////		        return ;
////		      }
////		      
////		    }
////	}
//    
//    
//
//    @Override
//	public void handleTag(boolean opening, String tag, Editable output,
//			XMLReader xmlReader) {
//		if (!opening && "version".equals(tag)) {
//			try {
//				output.append(getPackageManager().getPackageInfo(
//						getPackageName(), 0).versionName);
//			} catch (NameNotFoundException e) {
//			}
//		}
//	}
//
//	private Drawable spliter;
//
//	@Override
//	public Drawable getDrawable(String source) {
//		final Resources res = this.res;
//
//		final Drawable ret;
//		if (source.startsWith("spliter")) {
//			if (spliter == null) {
//				final int w = res.getDisplayMetrics().widthPixels;
//				final int h = (int) (res.getDisplayMetrics().densityDpi / 72f + 0.5f);
//
//				final int[] pix = new int[w * h];
//				Arrays.fill(pix, res.getColor(R.color.deep_blue));
//				spliter = new BitmapDrawable(Bitmap.createBitmap(pix, w, h,
//						Bitmap.Config.ARGB_8888));
//				spliter.setBounds(0, 3 * h, w, 4 * h);
//			}
//			ret = spliter;
//
//		} else if (source.startsWith("icon_main")) {
//			ret = res.getDrawable(R.drawable.list_line);
//
//			final String[] params = source.split(",");
//			final float f = res.getDisplayMetrics().densityDpi / 72f;
//			final float w = Util_.parseInt(params[1], 10, 16) * f + 0.5f;
//			final float h = Util_.parseInt(params[2], 10, 16) * f + 0.5f;
//			ret.setBounds(0, 0, (int) w, (int) h);
//
//		} else {
//			ret = null;
//		}
//
//		return ret;
//	}
//	
//	private void drawView(Integer mydataId,Integer dataType2, Integer tableID2) {
//		// TODO Auto-generated method stub
//		mMyData = MyDataDao.query(this,mydataId, dataType2);
//		if(mMyData != null){
//			getJsonData(mMyData);
//			switch (dataType2) {
//			case MyDataTable.MEETTING:
////				meettingView(mMyData);
//				
//				break;
//			case MyDataTable.TEXT:
////				onlyContentView("文本标签",mMyData.name);
//				break;
//
//			default:
//				break;
//			}
//			
//		}else{
//			MyUtil.showMessage("没有此标签", this);
//		}
//	}
//	
//	    
//	    private void whichPage(Intent intent ){
//	    	if(Intent.ACTION_DEFAULT.equals(intent.getAction())){
//	    		Intent data = getIntent();
//	    		mydataId = data.getIntExtra("mydataId", 1);
//	    		dataType = data.getIntExtra("dataType", 1);
//	    		tableID = data.getIntExtra("tableID", 1);
//	    		drawView(mydataId,dataType,tableID);
//	         }else{
//	        	 readFromNFC(intent);
//	         }
//	    }
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch (v.getId()) {
//			case R.id.btnWriteToNFC:
//				if (jsonData!= null && jsonData.length() >0) {
//					System.out.println(jsonData);
//					writeToNFC(jsonData,getIntent());
//				}
//				break;
//
//			default:
//				break;
//			}
//		}
//		
//		public void getJsonData(MyData myData) {
//			ProtocolTitle mProtocolTitle = new ProtocolTitle(null,this);
//  		   	mProtocolTitle.setData(myData);
//  		   	jsonData = gson.toJson(mProtocolTitle);
//		}
}
