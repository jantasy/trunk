package cn.yjt.oa.app.consume;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;

public class ConsumeActivity extends TitleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
            LaunchActivity.launch(this);
            finish();
        } else {
			  /*记录操作 1101*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_CONSUME_RECORD);

            setContentView(R.layout.activity_fragment_container);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, new ConsumeFragment());
            transaction.commit();

            setTitleBackground(getResources().getDrawable(R.drawable.consume_title_bg));
            getRightButton().setVisibility(View.GONE);

            getLeftbutton().setImageResource(R.drawable.navigation_back);
        }

    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }
}
