package cn.yjt.oa.app.contactlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.adpter.ContactsStructAdapter;

public class ContactsStructActivity extends TitleFragmentActivity implements OnItemClickListener {

	private ContactsStructAdapter adapter;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_contacts_struct);
		ListView structListView = (ListView) findViewById(R.id.struct_listview);
		structListView.setOnItemClickListener(this);
		DeptDetailInfo depts = getIntent().getParcelableExtra("structs");
		adapter = new ContactsStructAdapter(this);
		adapter.setData(depts.getXChildren());
		structListView.setAdapter(adapter);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setTitle(depts.getName());
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public static void launchWithDepts(Context context,DeptDetailInfo deptDetailInfo){
		Intent intent = new Intent(context, ContactsStructActivity.class);
		intent.putExtra("structs", deptDetailInfo);
		context.startActivity(intent );
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object item = parent.getItemAtPosition(position);
		if(item instanceof DeptDetailInfo){
			ContactsStructActivity.launchWithDepts(this, (DeptDetailInfo) item);
		}else{
				DeptDetailUserInfo info = (DeptDetailUserInfo) item;
		}		
	}
	
	
}
