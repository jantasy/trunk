package cn.yjt.oa.app.task;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;

public class TaskMainActivity extends TitleFragmentActivity implements OnClickListener {
    private static final String FRAGMENT_TAG_INITED_BY_ME = "TAG_" + InitedByMeFragment.class.getSimpleName();
    private static final String FRAGMENT_TAG_TASK_ALL = "TAG_" + TaskAllFragment.class.getSimpleName();
    private static final String FRAGMENT_TAG_SENT_TO_ME = "TAG_" + SentToMeFragment.class.getSimpleName();
    private static final String[] TASK_FRAGMENT_TAGS = new String[3];
    static {
        TASK_FRAGMENT_TAGS[0] = FRAGMENT_TAG_INITED_BY_ME;
        TASK_FRAGMENT_TAGS[1] = FRAGMENT_TAG_TASK_ALL;
        TASK_FRAGMENT_TAGS[2] = FRAGMENT_TAG_SENT_TO_ME;
    }
    
    private final int MENU_ITEM_ID_ALL_TASKS = 3;
    private final int MENU_ITEM_ID_INITED_BY_ME = 4;
    private final int MENU_ITEM_ID_SENT_TO_ME = 5;
    
    private MenuItem mCheckedItem;
    
    private String mSelectedTaskTag;
    
    private Fragment mInitedByMeTask;
    private Fragment mTaskAll;
    private Fragment mSentToMeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {

            /*记录操作 1001*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_TASK);
            
			setContentView(R.layout.task_main_layout);

			initTitleBar();

			Button newTaskButton = (Button) findViewById(R.id.start_new_task);
			newTaskButton.setOnClickListener(this);

			setSelectedFragment(FRAGMENT_TAG_TASK_ALL);
		}
    }
    
    private void initTitleBar() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        getRightButton().setImageResource(R.drawable.navigation_menu);
    }
    
    @Override
    public void onLeftButtonClick() {
    	super.onBackPressed();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (RESULT_OK == resultCode) {
            if (TaskBaseFragment.REQUEST_CODE_OPEN_TASK_DETAIL == requestCode) {
                boolean taskDecreased = intent.getBooleanExtra(TaskDetailActivity.TASK_LIST_DECREASED_KEY, false);
                if (taskDecreased) {
                    // 删除目标任务
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    for (int i = 0; i < TASK_FRAGMENT_TAGS.length; i++) {
                        String tag = TASK_FRAGMENT_TAGS[i];
                        TaskBaseFragment fragment = (TaskBaseFragment)fragmentManager.findFragmentByTag(tag);
                        if (fragment != null) {
                            fragment.onTaskListDecreased(intent);
                        }
                    }
                }
                
                boolean newReplyAdded = intent.getBooleanExtra(TaskDetailActivity.TASK_NEW_REPLY_SENT, false);
                if (newReplyAdded) {
                    refreshTaskList();
                }
            }
            
            if (TaskBaseFragment.REQUEST_CODE_PUBLISHING_NEW_TASK == requestCode) {
                boolean taskAdded = intent.getBooleanExtra(TaskPublishingActivity.TASK_NEW_ADDED_KEY, false);
                if (taskAdded) {
                    refreshTaskList();
                }
            }
        }
        
    }

    private void refreshTaskList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < TASK_FRAGMENT_TAGS.length; i++) {
            String tag = TASK_FRAGMENT_TAGS[i];
            TaskBaseFragment fragment = (TaskBaseFragment)fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                fragment.onTaskListRefreshing();
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Resources res = getResources();
        menu.add(0, MENU_ITEM_ID_ALL_TASKS, 1, res.getString(R.string.task_fiter_item_all));
        menu.add(0, MENU_ITEM_ID_INITED_BY_ME, 2, res.getString(R.string.task_filter_item_inited_by_me));
        menu.add(0, MENU_ITEM_ID_SENT_TO_ME, 3, res.getString(R.string.task_filter_item_sent_to_me));
        
        mCheckedItem = menu.getItem(0);
        mCheckedItem.setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        mCheckedItem.setChecked(false);
        item.setChecked(true);
        mCheckedItem = item;
        
        switch (item.getItemId()) {
        case MENU_ITEM_ID_ALL_TASKS:
            setSelectedFragment(FRAGMENT_TAG_TASK_ALL);
            break;
        case MENU_ITEM_ID_INITED_BY_ME:
            setSelectedFragment(FRAGMENT_TAG_INITED_BY_ME);
            break;
        case MENU_ITEM_ID_SENT_TO_ME:
            setSelectedFragment(FRAGMENT_TAG_SENT_TO_ME);
            break;
        default:
            break;
        }
        
        return true;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.start_new_task:
            Intent intent = new Intent(this, TaskPublishingActivity.class);
            startActivityForResult(intent, TaskBaseFragment.REQUEST_CODE_PUBLISHING_NEW_TASK);
            break;
        default:
            break;
        }        
    }
    
    private void setSelectedFragment(String newItemTag) {
        if (newItemTag.equals(mSelectedTaskTag))
            return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        for (int i = 0; i < TASK_FRAGMENT_TAGS.length; i++) {
            String tag = TASK_FRAGMENT_TAGS[i];
            fragment = fragmentManager.findFragmentByTag(tag);
            
            if (tag.equals(mSelectedTaskTag) && fragment != null) {
                transaction.hide(fragment);
            }
            
            if (newItemTag.equals(tag)) {
                if (fragment == null) {
                    fragment = getTargetFragment(tag);
                    transaction.add(R.id.task_fragment_container, fragment, tag);
                }
                transaction.show(fragment);
            }
        }
        transaction.commit();
        
        mSelectedTaskTag = newItemTag;
    }
    
    private Fragment getTargetFragment(String tag) {
        Fragment fragment;
        if (tag.equals(FRAGMENT_TAG_INITED_BY_ME)) {
            if (mInitedByMeTask == null)
                mInitedByMeTask = new InitedByMeFragment();
            fragment = mInitedByMeTask;
        } else if (tag.equals(FRAGMENT_TAG_SENT_TO_ME)) {
            if (mSentToMeTask == null)
                mSentToMeTask = new SentToMeFragment();
            fragment = mSentToMeTask;
        } else {
            if (mTaskAll == null)
                mTaskAll = new TaskAllFragment();
            fragment = mTaskAll;
        }
        return fragment;
    }

}
