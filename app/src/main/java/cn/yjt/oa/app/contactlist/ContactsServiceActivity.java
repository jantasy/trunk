package cn.yjt.oa.app.contactlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;

public class ContactsServiceActivity extends BackTitleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_contacts_service);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactsServiceFragment()).commit();
	}
	
	
	public static void launch(Context context){
		Intent intent = new Intent(context, ContactsServiceActivity.class);
		context.startActivity(intent );
	}
	
}

