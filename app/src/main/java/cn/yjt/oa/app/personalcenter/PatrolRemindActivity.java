package cn.yjt.oa.app.personalcenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.Config;

/**
 * 巡更提醒设置activity
 * <pre>
 * 设置巡更提醒是否开启，同时可以设置巡更提醒的类型，点击确定之后设置才生效
 * <pre>
 * @author 熊岳岳
 * @since 20150730
 */
public class PatrolRemindActivity extends TitleFragmentActivity implements OnClickListener {

	/**是否开启巡更提醒的checkbox*/
	private CheckBox mCbIsPatrolRemind;
	/**设置巡更提醒类型的radiogroup*/
	private RadioGroup mRgPatrolRemindType;

	/**shareprefs对象*/
	private SharedPreferences mShraePrefs;

	/**保存radiobutton的id的数组*/
	private int[] mRadioButtonIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patrol_remind);

		initParams();
		initView();
		fillData();
		setListener();
		businessLogic();

	}

	/*-----onCreate中执行的方法START-----*/
	/**初始化一些成员参数*/
	private void initParams() {
		mShraePrefs = StorageUtils.getSystemSettings(this);
		mRadioButtonIds = new int[] { R.id.rb_client, R.id.rb_short_message, R.id.rb_both };
	}

	/**初始化控件*/
	private void initView() {
		mCbIsPatrolRemind = (CheckBox) findViewById(R.id.cb_is_patrol_remind);
		mRgPatrolRemindType = (RadioGroup) findViewById(R.id.rg_patrol_remind_type);
	}

	/**向控件中填充数据*/
	private void fillData() {
		//设置标题左边按钮
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		//初始化checkbox和radiogroup中的数据
		mCbIsPatrolRemind.setChecked(mShraePrefs.getBoolean(Config.IS_OPEN_PATROL_REMIND, true));
		mRgPatrolRemindType.check(mRadioButtonIds[mShraePrefs.getInt(Config.TYPE_PATROL_REMIND,
				Config.TYPE_PATROL_CLIENT)]);
	}

	/**设置监听*/
	private void setListener() {
		//注册‘确认，取消’按钮点击事件
		findViewById(R.id.btn_patrol_remind_sure).setOnClickListener(this);
		findViewById(R.id.btn_patrol_remind_cancle).setOnClickListener(this);
	}

	/**业务逻辑*/
	private void businessLogic() {

	}

	/*-----onCreate中执行的方法END-----*/

	/**根据选中的radiobutton,返回对应的整形标识符*/
	public int getPatrolType() {
		switch (mRgPatrolRemindType.getCheckedRadioButtonId()) {

		//选择了客户端提醒
		case R.id.rb_client:
			return Config.TYPE_PATROL_CLIENT;

			//选择了短信提醒
		case R.id.rb_short_message:
			return Config.TYPE_PATROL_MESSAGE;

			//选择了客户端和短信提醒
		case R.id.rb_both:
			return Config.TYPE_PATROL_BOTH;

		default:
			return Config.TYPE_PATROL_CLIENT;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		//点击确定按钮
		case R.id.btn_patrol_remind_sure:
			//将是否开启巡更的标识符写入sp中
			mShraePrefs.edit().putBoolean(Config.IS_OPEN_PATROL_REMIND, mCbIsPatrolRemind.isChecked()).commit();
			mShraePrefs.edit().putInt(Config.TYPE_PATROL_REMIND, getPatrolType()).commit();
			this.finish();
			break;

		//点击取消按钮
		case R.id.btn_patrol_remind_cancle:
			this.finish();
			break;

		default:
			break;
		}
	}
}
