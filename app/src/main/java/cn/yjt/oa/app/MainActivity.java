package cn.yjt.oa.app;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ToastUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.AppRequest;
import cn.yjt.oa.app.app.activity.AppEntranceActivity;
import cn.yjt.oa.app.app.utils.UpdateHelper;
import cn.yjt.oa.app.app.whitelist.WhiteList;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.MessageInfoCache;
import cn.yjt.oa.app.beans.MessageInfoContentCache;
import cn.yjt.oa.app.beans.NewMessageInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.ContactsFragment;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.dashboard.DashBoardFragment;
import cn.yjt.oa.app.enterprise.CreateEnterpriseActivity;
import cn.yjt.oa.app.enterprise.EnterpriseListActivity;
import cn.yjt.oa.app.find.FinderListFragment;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.message.MessageOnResume;
import cn.yjt.oa.app.message.MessageScrollToPos;
import cn.yjt.oa.app.message.fragment.MessageCenterScrollFragment;
import cn.yjt.oa.app.message.fragment.TopMessageIdManager;
import cn.yjt.oa.app.personalcenter.AboutActivity;
import cn.yjt.oa.app.personalcenter.FeedBackActivity;
import cn.yjt.oa.app.personalcenter.LoginActivity;
import cn.yjt.oa.app.personalcenter.PersonalHomeActivity;
import cn.yjt.oa.app.personalcenter.SettingActivity;
import cn.yjt.oa.app.push.PushMessageManager;
import cn.yjt.oa.app.utils.BitmapUtils;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.GooView;
import cn.yjt.oa.app.widget.TableMsgWidget;

import com.activeandroid.query.Delete;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingInterruptor;

/** 主界面 */
public class MainActivity extends TitleFragmentActivity implements View.OnClickListener, MessageOnResume {

    private static final String TAG = MainActivity.class.getSimpleName();

    /** 用户名 */
    public static String userName;
    /** 当前登录的用户 */
    public UserInfo mCurrentUserInfo;
    /** SharedPreferences对象 */
    private SharedPreferences mSharedPrefs;

    /** 消息中心 */
    private TextView mTvMessageCenter;
    /** 工作台 */
    private TextView mTvDashBoard;
    /** 通讯录 */
    private TextView mTvContact;
    /** 生活圈 */
    private TextView mTvFinder;

    /** 标题栏左侧的图片按钮，这里用来显示当前用户的头像 */
    private ImageView mIvLoginHead;
    /** 标题栏右侧的图片按钮,暂时并不知道作何用 */
    private ImageView mIvRightMore;
    /** 放置消息中心，工作台，联系人，生活圈Fragment的ViewPager */
    private ViewPager mLlPager;

    /** 消息中心的Fragment */
    private MessageCenterScrollFragment mMessageCenterFragement;
    /** 工作台的Fragment */
    private DashBoardFragment mDashBoardFragment;
    /** 联系人的Fragment */
    private ContactsFragment mContactsFragment;
    /** 生活圈的Fragment */
    private FinderListFragment mFinderListFragment;
    /** 封装四个Fragment集合 */
    private BaseFragment[] mFragments = null;

    /** Fragment适配器 */
    private FragmentAdapter mFragmentAdapter;
    /** viewpager的页面改变监听 */
    private OnPageChangeListener mPageChangeListener;

    /** 主界面最外层的FrameLayout */
    private FrameLayout mFlMainLayout;
    /** 消息滚动到指定位置的接口 */
    private MessageScrollToPos messageScroll;
    /** 未读消息提示的小红点，(已废弃) */
    @SuppressWarnings("unused")
    private static TextView sTvRedPoint;
    /** 粘性控件（可拖拽的未读消息小圆点，拖拽一定距离后释放直接清除所有未读消息） */
    public static GooView sGvRedPoint = new GooView(MainApplication.getAppContext());

    /** 左侧侧滑菜单控件 */
    private SlidingMenu mSlidingMenu;
    /** 左侧侧拉菜单的布局 */
    private View mSlidingRoot;

    /** 推送新消息的广播接收者 */
    private PushNewMessageReciever mPushNewMessageReceiver;
    /** 修改企业成功的广播接收者 */
    private ChangedCustShortNameReceiver mChangedCustShortNameReceiver;

    private ImageView mIvIcon;
    private TextView nameTv;
    private TextView custNameTv;
    private TextView enterpriseTV;

    private String fromTime;
    private ImageView leftBtn;
    private LinearLayout createEnterprise;
    private ImageView enterpriseLine;
    private boolean isContactLoadDialogShow;
    private TextView help;

    private static long unReadCount = 0;
    List<MessageInfo> topAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_center_activity_layout);
//        ToastUtils.shortToastShow(AccountManager.getCurrent(this).getId()+":::"+AccountManager.getCurrent(this).getCustId());
        initParams();
        initSlidingMenu();
        initView();
        fillData();
        setListener();
        setAreadeCouplingStatus();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        // 界面销毁时，将小圆点移出去
        if (mFlMainLayout != null && sGvRedPoint != null) {
            mFlMainLayout.removeView(sGvRedPoint);
        }
        PushMessageManager.unregistPushMessageHandler(new NewMessageInfo());
        unregisterReceiver(mPushNewMessageReceiver);
        unregisterReceiver(mChangedCustShortNameReceiver);
        super.onDestroy();
    }

    /** 初始化参数 */
    private void initParams() {
        //TODO:
        new WhiteList().checkWhiteList(this);

        PushMessageManager.registPushMessageHandler(new NewMessageInfo());
        // 获取当前登录用户信息
        UserInfo infos = getIntent().getParcelableExtra("info");
        mCurrentUserInfo = infos != null ? infos : AccountManager.getCurrent(this);
        //初始化share-prefs
        mSharedPrefs = getSharedPreferences("areadeCoupling", MODE_PRIVATE);
        //初始化Fragment适配器
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        //viewpager页面改变的监听
        mPageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitle(mFragmentAdapter.getPageTitle(position));
                showCurrentItem(position == 0, position == 1, position == 2, position == 3);
                mLlPager.setCurrentItem(position, false);
                mFragments[position].configRightButton(mIvRightMore);
                mFragments[position].onFragmentSelected();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        };

        //注册广播监听
        initReceiver();
    }

    /** 初始化控件 */
    private void initView() {
        mIvLoginHead = getLeftbutton();
        mIvRightMore = getRightButton();
        mFlMainLayout = (FrameLayout) findViewById(R.id.main_layout);
        mTvMessageCenter = (TextView) findViewById(R.id.message_center);
        mTvDashBoard = (TextView) findViewById(R.id.control_center);
        mTvContact = (TextView) findViewById(R.id.contact_center);
        mTvFinder = (TextView) findViewById(R.id.finder_center);
        sTvRedPoint = (TextView) findViewById(R.id.new_count);
        mLlPager = (ViewPager) findViewById(R.id.container);
        mMessageCenterFragement = new MessageCenterScrollFragment();
        mDashBoardFragment = new DashBoardFragment();
        mContactsFragment = new ContactsFragment();
        mFinderListFragment = new FinderListFragment();
        mFragments = new BaseFragment[]{mMessageCenterFragement, mDashBoardFragment, mContactsFragment,
                mFinderListFragment};
    }

    /** 填充数据 */
    private void fillData() {
        //将标题左边的图片按钮设置为当前用户的头像。
        if (mCurrentUserInfo != null) {
            BitmapUtils.setLoginHeadIcon(this, mCurrentUserInfo.getAvatar(), mIvLoginHead, getResources()
                    .getDimensionPixelSize(R.dimen.message_center_header_size), R.drawable.defalut_icon);
        }
        //设置标题右边图片按钮
        mIvRightMore.setImageResource(R.drawable.navigation_menu);
        //将粘性控件(未读消息小红点)添加到主界面最外层的布局中
        fillGooView();
        //消息中心设置被选中
        mTvMessageCenter.setSelected(true);
        mMessageCenterFragement.setMessageOnResume(this);
        //viewpager设置为不空用？同时给viewpager设置适配器
        mLlPager.setEnabled(false);
        mLlPager.setAdapter(mFragmentAdapter);
        //viewpager选择第一个页面
        mPageChangeListener.onPageSelected(1);
    }

    private void fillGooView() {
        sGvRedPoint.setVisibility(View.GONE);
        ViewGroup tempvViewGroup = (ViewGroup) sGvRedPoint.getParent();
        if (tempvViewGroup != null) {
            tempvViewGroup.removeView(sGvRedPoint);
        }
        mFlMainLayout.addView(sGvRedPoint);
    }

    /** 设置监听 */
    private void setListener() {
        //消息中心设置点击监听（小心中心比较特殊，包含textview和未读消息小红点）
        findViewById(R.id.message_center_layout).setOnClickListener(this);
        mTvDashBoard.setOnClickListener(this);
        mTvContact.setOnClickListener(this);
        mTvFinder.setOnClickListener(this);
        //viewpager设置page改变的监听
        mLlPager.setOnPageChangeListener(mPageChangeListener);
    }

    /** 初始化左侧侧拉菜单 */
    private void initSlidingMenu() {
        initMenuView();
        fillMenuData();
        setMenuListener();

    }

    /** 初始化菜单中的控件 */
    private void initMenuView() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMenu(R.layout.slide_menu);// 设置menu的layout
        mSlidingRoot = mSlidingMenu.getMenu();// 获取menu的layout
        mIvIcon = (ImageView) mSlidingRoot.findViewById(R.id.icon);
        nameTv = (TextView) mSlidingRoot.findViewById(R.id.name);
        custNameTv = (TextView) mSlidingRoot.findViewById(R.id.cust_name);
        createEnterprise = (LinearLayout) mSlidingRoot.findViewById(R.id.create_enterprise);
        enterpriseLine = (ImageView) mSlidingRoot.findViewById(R.id.enterprise_line);
        enterpriseTV = (TextView) mSlidingRoot.findViewById(R.id.enterprise);
        help = (TextView) mSlidingRoot.findViewById(R.id.help);
    }

    /** 给菜单中的控件填充数据和参数 */
    private void fillMenuData() {
        mSlidingMenu.setBackgroundColor(Color.BLACK);
        mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置滑动方向
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 设置监听开始滑动的触碰范围
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);// 设置边缘阴影的宽度，通过dimens资源文件中的ID设置
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置边缘阴影的颜色/图片，通过资源文件ID设置
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置menu全部打开后，主界面剩余部分与屏幕边界的距离，通过dimens资源文件ID设置
        mSlidingMenu.setFadeEnabled(true);// 设置是否淡入淡出
        mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的值，只在setFadeEnabled设置为true时有效
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);// 将menu绑定到Activity，同时设置绑定类型


        if (mCurrentUserInfo != null) {
            BitmapUtils.setLoginHeadIcon(this, mCurrentUserInfo.getAvatar(), mIvIcon, 0,
                    R.drawable.contactlist_contact_icon_default);
            if (0 == mCurrentUserInfo.getIsYjtUser()) {
                mSlidingRoot.findViewById(R.id.car_line).setVisibility(View.GONE);
                mSlidingRoot.findViewById(R.id.car_ll).setVisibility(View.GONE);
            }
        }
    }

    /** 给菜单中的控件设置监听 */
    private void setMenuListener() {
        mIvIcon.setOnClickListener(this);
        mSlidingRoot.findViewById(R.id.download_card_app).setOnClickListener(this);
        mSlidingRoot.findViewById(R.id.feedback).setOnClickListener(this);
        mSlidingRoot.findViewById(R.id.setting).setOnClickListener(this);
        enterpriseTV.setOnClickListener(MainActivity.this);
        mSlidingRoot.findViewById(R.id.exit).setOnClickListener(this);
        mSlidingRoot.findViewById(R.id.about).setOnClickListener(this);
        help.setOnClickListener(this);
        mSlidingMenu.setOnCloseListener(new OnCloseListener() {
            @Override
            public void onClose() {
                mIvLoginHead.setVisibility(View.VISIBLE);
            }
        });
        mSlidingMenu.setOnOpenListener(new OnOpenListener() {
            @Override
            public void onOpen() {
                mCurrentUserInfo = AccountManager.getCurrent(MainActivity.this);
                mIvLoginHead.setVisibility(View.VISIBLE);
            }
        });
    }

    /** 初始化广播接收者 */
    private void initReceiver() {
        //注册新消息推送广播
        mPushNewMessageReceiver = new PushNewMessageReciever();
        IntentFilter filter = new IntentFilter();
        filter.addAction("yijitong.action.new_message");
        registerReceiver(mPushNewMessageReceiver, filter);
        //注册企业修改成功广播
        mChangedCustShortNameReceiver = new ChangedCustShortNameReceiver();
        IntentFilter changedCustShortNameFilter = new IntentFilter();
        changedCustShortNameFilter.addAction(MainApplication.ACTION_CHANGED_CUST_SHORT_NAME);
        changedCustShortNameFilter.addAction(MainApplication.ACTION_CURRENT_LOGINUSER_CHANGED);
        registerReceiver(mChangedCustShortNameReceiver, changedCustShortNameFilter);
    }

    public MessageCenterScrollFragment getCenterScrollFragment() {
        return (MessageCenterScrollFragment) mFragments[0];
    }

    public void addSlidingMenuInterruptor(SlidingInterruptor interruptor) {
        if (mSlidingMenu != null) {
            mSlidingMenu.addInterruptor(interruptor);
        }
    }

    public void addToFragments(int index, BaseFragment baseFragment) {
        if (index > 3 || index < 0) {
            return;
        }
        mFragments[index] = baseFragment;
    }

    // 暂时使用这个方法初始化
    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return mFragments[arg0];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            System.out.println("instantiateItem：" + object);
            mFragments[position] = (BaseFragment) object;
            switch (position) {
                case 0:
                    mMessageCenterFragement = (MessageCenterScrollFragment) object;
                    break;
                case 1:
                    mDashBoardFragment = (DashBoardFragment) object;
                    break;
                case 2:
                    mContactsFragment = (ContactsFragment) object;
                    break;

                default:
                    break;
            }
            return object;
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            UserInfo userInfo = AccountManager.getCurrent(getApplicationContext());
            String custName = userInfo.getCustShortName();
            if (TextUtils.isEmpty(custName)) {
                custName = userInfo.getCustName();
            }
            // 20150331 zhengran
            if (TextUtils.isEmpty(custName)) {
                // return fragments[position].getPageTitle(MainActivity.this);
                // 没有企业信息
                // 20150331 zhengran
                setTitleOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        CreateEnterpriseActivity.launch(MainActivity.this);
                    }

                });
                return "点击创建或加入企业";
            }
            setTitleOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    /*记录操作 0204*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_POPUP_DEPTLISTS);

                    EnterpriseListActivity.launch(MainActivity.this);
                }

            });
            return custName;
        }
    }

    @Override
    protected void onResume() {
        mCurrentUserInfo = AccountManager.getCurrent(this);
        if (mCurrentUserInfo == null) {
            nameTv.setText("姓名");
            custNameTv.setText("企业名称");
        } else {
            nameTv.setText(mCurrentUserInfo.getName());
            // 20150331 zhengran
            if (!TextUtils.isEmpty(mCurrentUserInfo.getCustShortName())) {
                custNameTv.setText(mCurrentUserInfo.getCustShortName());
            } else if (!TextUtils.isEmpty(mCurrentUserInfo.getCustName())) {
                custNameTv.setText(mCurrentUserInfo.getCustName());
            } else {
                custNameTv.setText("");
            }

            if (mCurrentUserInfo.getCustVCode() == 1) {
                custNameTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_v, 0, 0, 0);
                custNameTv.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
            } else {
                custNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
        BitmapUtils.setLoginHeadIcon(this, mCurrentUserInfo == null ? "" : mCurrentUserInfo.getAvatar(), mIvIcon, 0,
                R.drawable.contactlist_contact_icon_default);

        BitmapUtils.setLoginHeadIcon(this, mCurrentUserInfo == null ? "" : mCurrentUserInfo.getAvatar(), mIvLoginHead,
                getResources().getDimensionPixelSize(R.dimen.message_center_header_size), R.drawable.defalut_icon);
        super.onResume();
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击头像
            case R.id.icon:
                Intent personalIntent = new Intent(this, PersonalHomeActivity.class);
                personalIntent.putExtra("info", mCurrentUserInfo);
                startActivity(personalIntent);
                break;
            case R.id.download_card_app:
                Intent downloadCardAppIntent = new Intent(this, AppEntranceActivity.class);
                downloadCardAppIntent.setAction(AppRequest.ACTION_CARD_APP);
                startActivity(downloadCardAppIntent);
                break;
            case R.id.feedback:
                Intent feedBackIntent = new Intent(this, FeedBackActivity.class);
                startActivity(feedBackIntent);
                 /*记录操作 0301*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_SYSTEM_SETTING);
                break;
            case R.id.setting:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                 /*记录操作 0311*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_FEEDBACK);
                break;
            case R.id.enterprise:

                /*记录操作 0203*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_DEPTLISTS);
                EnterpriseListActivity.launch(this);
                break;
            case R.id.exit:
                exitLogin();

                /*记录操作 0108*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_LOGOUT);

                break;

            // 消息中心
            case R.id.message_center_layout:
                if (messageScroll != null) {
                    messageScroll.scrollToNextUnRead();
                }
                mLlPager.setCurrentItem(0, false);
                showCurrentItem(true, false, false, false);

                /*记录操作 0401*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_MESSAGE_CENTER);
                break;
            // 工作台
            case R.id.control_center:
                mLlPager.setCurrentItem(1, false);
                showCurrentItem(false, true, false, false);

                /*记录操作 0501*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_DASHBOARD);
                break;
            // 联系人
            case R.id.contact_center:
                mLlPager.setCurrentItem(2, false);
                showCurrentItem(false, false, true, false);

                /*记录操作 0601*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_CONTACTLIST);
                break;

            case R.id.finder_center:
                mLlPager.setCurrentItem(3, false);
                showCurrentItem(false, false, false, true);

                /*记录操作 0701*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_LIFECIRCLE);
                break;
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);

                 /*记录操作 0313*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_ABOUT);
                break;
            case R.id.help:
                // Intent helpIntent = new Intent(this, AboutActivity.class);
                // startActivity(helpIntent);

                Intent guideIntent = new Intent(this, GuideActivity.class);
                startActivity(guideIntent);

                  /*记录操作 0314*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_HELP);
                break;
            default:
                break;
        }
    }

    public void onLeftButtonClick() {
        super.hideSoftInput();
        mSlidingMenu.showMenu();
    }

    @Override
    public void onRightButtonClick() {
        int current = mLlPager.getCurrentItem();
        if (mFragments[current].onRightButtonClick())
            return;
        super.onRightButtonClick();
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    private void logOut() {
        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(AsyncRequest.MODULE_LOGOUT);
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        builder.setResponseType(type);
        builder.setResponseListener(new Listener<Response<Object>>() {

            @Override
            public void onErrorResponse(InvocationError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(Response<Object> arg0) {
                // TODO Auto-generated method stub

            }
        });
        builder.build().get();
    }

    private void deleteWidget() {
        Intent intent = new Intent(this, TableMsgWidget.class);
        intent.putExtra(TableMsgWidget.EXTRA_DELETE_WIDGET, true);
        startService(intent);
    }

    private void exitLogin() {
        Builder builder = AlertDialogBuilder.newBuilder(this);
        builder.setTitle(R.string.exit_login).setMessage(R.string.exit_login_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWidget();
                        AccountManager.clearAccountCookie();
                        ContactManager.destoryContactManager();
                        logOut();
                        // 删除之前缓存的消息中心的消息
                        new Delete().from(MessageInfoCache.class).execute();
                        new Delete().from(MessageInfoContentCache.class).execute();
                        TopMessageIdManager.deleteTopIds();
                        AccountManager.exitLogin(MainActivity.this);
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果变动了则持久化菜单信息
        // ((MentroView)findViewById(R.id.mv_my)).serialMenu();
    }

    // public void addFragment(Fragment... fragment){
    // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // for (Fragment fragment2 : fragment) {
    // ft.add(R.id.container, fragment2);
    // }
    // ft.addToBackStack("controlcenter");
    // ft.commit();
    // }
    //
    // public void replace(Fragment f){
    // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // ft.replace(R.id.container, f);
    // ft.addToBackStack("main");
    // ft.commit();
    // }
    //
    //
    // public void replaceFragment(Fragment... fragments ){
    // FragmentManager manager = getSupportFragmentManager();
    // List<Fragment> list = manager.getFragments();
    // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // if (list != null){
    // for (Fragment fragment : list) {
    // if (fragment != null){
    // ft.remove(fragment);
    // }
    // }
    // }
    // for (Fragment fragment:fragments) {
    // ft.add(R.id.container, fragment);
    // }
    // ft.addToBackStack("main");
    // ft.commit();
    // }

    @Override
    protected Dialog onCreateDialog(int id) {
        // switch (id) {
        // case contactsFragment.CONTACTS_LOAD_DG_ID:
        // ProgressDialog dialog = new ProgressDialog(this);
        // String msg = getString(R.string.contacts_loading);
        // dialog.setMessage(msg);
        // dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // dialog.setCanceledOnTouchOutside(false);
        // dialog.setOnDismissListener(new OnDismissListener() {
        //
        // @Override
        // public void onDismiss(DialogInterface dialog) {
        // isContactLoadDialogShow = false;
        // }
        // });
        // return dialog;
        // }
        return super.onCreateDialog(id);
    }


    /** 推送新消息的广播接收者 */
    class PushNewMessageReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            updataCount();
        }
    }

    class ChangedCustShortNameReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            UserInfo userInfo = AccountManager.getCurrent(getApplicationContext());
            String custName = userInfo.getCustShortName();
            if (TextUtils.isEmpty(custName)) {
                custName = userInfo.getCustName();
            }
            setTitle(custName);
            mCurrentUserInfo = userInfo;
            BitmapUtils.setLoginHeadIcon(MainActivity.this,
                    mCurrentUserInfo == null ? "" : mCurrentUserInfo.getAvatar(), mIvIcon, 0,
                    R.drawable.contactlist_contact_icon_default);
            BitmapUtils.setLoginHeadIcon(MainActivity.this, userInfo.getAvatar(), mIvLoginHead, getResources()
                    .getDimensionPixelSize(R.dimen.message_center_header_size), R.drawable.defalut_icon);
        }

    }

    public void updataCount() {
        // if (TextUtils.isEmpty(fromTime)) {
        // return;
        // }
        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(AsyncRequest.MESSAGE_CENTER_UNREAD);
        Type type = new TypeToken<Response<Long>>() {
        }.getType();
        builder.setResponseType(type);
        builder.setResponseListener(new Listener<Response<Long>>() {

            @Override
            public void onErrorResponse(InvocationError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(Response<Long> arg0) {
                if (arg0 != null) {
                    if (arg0.getCode() == 0) {

                        long count = arg0.getPayload();
                        unReadCount = count;
                        if (count != 0) {
                            // 粘性控件初始化参数
                            sGvRedPoint.init();
                            // 将粘性控件添加进来
                            // mainLayout.addView(gooView);
                            // 黏性控件disapper属性设置为false
                            sGvRedPoint.setDisapper(false);
                            // 黏性控件设置为可见
                            sGvRedPoint.setVisibility(View.VISIBLE);
                            // 设置粘性控件中显示的数字
                            sGvRedPoint.setmCount((int) count);
                            // 刷新gooView
                            sGvRedPoint.invalidate();

                            // countTv.setVisibility(View.VISIBLE);
                            // countTv.setText(count + "");

                        } else {
                            // 粘性控件设置不可见
                            sGvRedPoint.setVisibility(View.GONE);
                            sGvRedPoint.setmCount(0);
                            // mainLayout.removeView(gooView);

                            // countTv.setVisibility(View.GONE);
                            // countTv.setText("");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), arg0.getDescription(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        builder.build().get();
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Config.REDUCTION_UNREAD_COUNT:
                    if (unReadCount <= 0 || unReadCount == 1) {
                        sGvRedPoint.setVisibility(View.GONE);
                        sGvRedPoint.setmCount(0);
                    } else {

                        unReadCount--;
                        sGvRedPoint.setmCount((int) unReadCount);
                        sGvRedPoint.setVisibility(View.VISIBLE);
                        sGvRedPoint.invalidate();
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public static Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onBackPressed() {
        int currentItem = mLlPager.getCurrentItem();
        if (mFragments != null && mFragments.length > currentItem) {
            BaseFragment baseFragment = mFragments[currentItem];
            if (baseFragment instanceof OnBackPressedInterface) {
                OnBackPressedInterface pressedInterface = (OnBackPressedInterface) baseFragment;
                if (pressedInterface.onBackPressed()) {
                    return;
                }
            }

        }
        if (isPrepareBack) {
            super.onBackPressed();
        } else {
            prepareBack();
        }
    }

    private boolean isPrepareBack;

    private void prepareBack() {
        getWindow().getDecorView().postDelayed(new Runnable() {

            @Override
            public void run() {
                isPrepareBack = false;
            }
        }, 2000);
        isPrepareBack = true;
        Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();

    }

    public void setTopAll(List<MessageInfo> topAll) {
        this.topAll = topAll;
    }

    public List<MessageInfo> getTopAll() {
        return topAll;
    }

    public int getCurrentItem() {
        return mLlPager.getCurrentItem();
    }

    private void showCurrentItem(boolean showMessage, boolean showDashboard, boolean showContact, boolean showFinder) {
        mTvMessageCenter.setSelected(showMessage);
        mTvDashBoard.setSelected(showDashboard);
        mTvContact.setSelected(showContact);
        mTvFinder.setSelected(showFinder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("tabIndex")) {
            int tabIndex = intent.getIntExtra("tabIndex", 0);
            if (tabIndex < 4) {
                mLlPager.setCurrentItem(tabIndex);
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }

        return true;
    }

    public static void launchAndSelectTab(Context context, int tabIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tabIndex", tabIndex);
        context.startActivity(intent);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void launchClearTask(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            ((Activity) context).finish();
        }
        context.startActivity(intent);
    }

    /** 小圆点设置成可触摸 */
    @Override
    public void messageOnResume() {
        sGvRedPoint.setCanTouch(true);
    }

    /** 小圆点设置成不可触摸 */
    @Override
    public void messageOnPause() {
        sGvRedPoint.setCanTouch(false);
    }

    /** 设置消息滚动到指定位置的接口 */
    public void setMessageScrollListener(MessageScrollToPos messageScrollToPos) {
        this.messageScroll = messageScrollToPos;
    }

    /**
     * 设置当前企业的考勤位置的解耦状态
     * <pre>
     * 这个状态标识由于修改之后第二天才能生效，所以为了提高效率，每天只在第一次登陆时获取一次即可
     * 因此这个标识是和今天的日期外加用户的id用','链接合并成一个字符串保存在sp中，例如  20150713-false
     * 每次从sp中取出和今天的日期做比较，如果日期不匹配的话，就从服务器中获取
     * </pre>
     */
    public void setAreadeCouplingStatus() {
        String areadeCoupling = mSharedPrefs.getString("areade_coupling", "0,false,-1");
        String[] temp = areadeCoupling.split(",");
        String date = temp[0];
        String nowDate = AsyncRequest.DATE_FORMAT.format(new Date());
        String deptId = "-1";
        LogUtils.d(TAG, "字符串的长度" + temp.length);
        if (temp.length > 2) {
            deptId = temp[2];
        }
        //查看从sp中获取的标识符的值的信息
        //LogUtils.i(TAG, "日期：" + date + " 状态：" + temp[1] + "用户id" + temp[2]);

        //判断标识符的日期是否和今天的日期相同，如果相同，直接终止该方法
        if (date != 0 + "" && date.equals(nowDate) && deptId.equals(mCurrentUserInfo.getId() + "")) {
            //TODO:
            return;
        }
        //从服务器从服务器获取当前企业的考勤位置的解耦的状态
        getAreadeCouplingStatusFromService();
    }

    /** 从服务器获取当前企业的考勤位置的解耦的状态 */
    private void getAreadeCouplingStatusFromService() {
        cn.yjt.oa.app.http.AsyncRequest.Builder builder = new cn.yjt.oa.app.http.AsyncRequest.Builder();
        String url = AsyncRequest.MODULE_AREADE_COUPLING_STATUS;
        String uploadUrl = String.format(url, mCurrentUserInfo.getCustId());
        LogUtils.i(TAG, String.format(url, mCurrentUserInfo.getCustId()));
        builder.setModule(uploadUrl);
        builder.setResponseType(new TypeToken<Response<Boolean>>() {
        }.getType());
        builder.setResponseListener(new Listener<Response<Boolean>>() {
            @Override
            public void onErrorResponse(InvocationError arg0) {
                LogUtils.e(TAG, arg0.getMessage() + "  获取失败");
                writeAreadeCouplingStatusToSp(0 + "", false, mCurrentUserInfo.getId() + "");
            }

            @Override
            public void onResponse(Response<Boolean> response) {
                LogUtils.d(TAG, response.getCode() + "  获取成功");
                if (response.getCode() == 0) {
                    boolean status = response.getPayload();
                    LogUtils.i(TAG, "解耦状态：" + status);
                    writeAreadeCouplingStatusToSp(AsyncRequest.DATE_FORMAT.format(new Date()), status,
                            mCurrentUserInfo.getId() + "");
                }
            }

        });
        builder.build().get();
    }

    /**
     * 将从服务器获取到的当前企业的考勤位置是否解耦的状态写入sp中
     * <pre>
     *  由于该状态是和修改后第二天凌晨生效，于是和当天日期绑定写入sp中
     * </pre>
     *
     * @param date   日期
     * @param status 解耦状态
     */
    private void writeAreadeCouplingStatusToSp(String date, boolean status, String userId) {
        if (mSharedPrefs == null) {
            mSharedPrefs = getSharedPreferences("areadeCoupling", MODE_PRIVATE);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(",");
        sb.append(status);
        sb.append(",");
        sb.append(userId);
        mSharedPrefs.edit().putString("areade_coupling", sb.toString()).commit();
    }
}
