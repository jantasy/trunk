package cn.yjt.oa.app.task;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.ContactlistActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.utils.ViewUtil;

/** 发起任务的界面 */
public class TaskPublishingActivity extends TitleFragmentActivity implements
        OnClickListener, NewInterface {

    static final String TAG = "TaskPublishingActivity";

    /** 定义常量 */
    private static final String EXTRA_USER_SIMPLE_INFO = "UserSimpleInfo";
    public static final String TASK_NEW_ADDED_KEY = "task_new_added_key";

    private TaskEditText mEditText;

    private CheckBox mDailyReportCheck;

    private ProgressDialog mProgressDialog;
    private PictureBoradFragment pictureBoradFragment;
    private SpeechBoradFragment speechBoradFragment;
    private File recordFile;

    private List<String> imageUrls = new ArrayList<String>();
    private String voiceUrl;

    private FileClient client;

    private static final int VOICE_INDEX = 4;
    private Map<Integer, Cancelable> cancelables = new HashMap<Integer, Cancelable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_publishing_layout);
        initTitleBar();

        mEditText = (TaskEditText) findViewById(R.id.test);
        mEditText.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (view instanceof EditText
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_DEL) {
                    Editable buffer = ((EditText) view).getText();
                    // If the cursor is at the end of a RecipientSpan then
                    // remove the whole span
                    int start = Selection.getSelectionStart(buffer);
                    int end = Selection.getSelectionEnd(buffer);
                    if (start == end) {
                        UserSimpleInfo.RecipientSpan[] link = buffer
                                .getSpans(start, end,
                                        UserSimpleInfo.RecipientSpan.class);
                        if (link != null && link.length > 0
                                && start != buffer.getSpanStart(link[0])) {
                            buffer.replace(buffer.getSpanStart(link[0]),
                                    buffer.getSpanEnd(link[0]), "");
                            buffer.removeSpan(link[0]);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (before == 0 && count == 1) {
                    if (s.charAt(start) == '@') {
                        closeSoftInput();

                        ContactlistActivity.startActivityForChoiceContact(
                                TaskPublishingActivity.this,
                                ContactlistActivity.REQUEST_CODE_INPUT);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText.setOnClickListener(this);

        ImageView addButton = (ImageView) findViewById(R.id.add_new_recipient);
        addButton.setOnClickListener(this);
        mDailyReportCheck = (CheckBox) findViewById(R.id.daily_report_checked);
        Button sendButton = (Button) findViewById(R.id.send_new_task);
        sendButton.setOnClickListener(this);

        findViewById(R.id.speech_input).setOnClickListener(this);
        findViewById(R.id.camera_input).setOnClickListener(this);

        pictureBoradFragment = new PictureBoradFragment();
        speechBoradFragment = new SpeechBoradFragment();

        recordFile = createGppFile();
        speechBoradFragment.setRecorderFile(recordFile);
        client = FileClientFactory.createSingleThreadFileClient(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("任务创建中, 请稍等");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_USER_SIMPLE_INFO)) {
            UserSimpleInfo info = intent.getParcelableExtra(EXTRA_USER_SIMPLE_INFO);
            Editable edit_text = mEditText.getEditableText();
            edit_text.insert(mEditText.getSelectionStart(),
                    info.toCharSequence());
            edit_text.insert(mEditText.getSelectionStart(), " ");
        }
    }

    @Override
    protected void onDestroy() {
        if (recordFile != null && recordFile.exists()) {
            recordFile.delete();
        }
        super.onDestroy();
    }

    private File createGppFile() {
        String fileName = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/yijitong/"
                + MainActivity.userName
                + "/"
                + UUID.randomUUID() + ".amr";
        File file = new File(fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        return file;
    }

    private void initTitleBar() {
        getLeftbutton().setImageResource(
                R.drawable.activity_back_indicator_selector);
        getRightButton().setVisibility(View.GONE);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        View container = findViewById(R.id.borad_container);
        switch (id) {
            case R.id.send_new_task:
                closeSoftInput();
                if (existUploadFiles()) {
                    uploadFiles();
                } else {
                    sendNewTask();
                }
                break;
            case R.id.add_new_recipient:
                closeSoftInput();
                closeBorad();
                ContactlistActivity.startActivityForChoiceContact(this,
                        ContactlistActivity.REQUEST_CODE_CLICK);
                break;
            case R.id.speech_input:
                hideSoftInput();
                refreshNew();
                if (speechBoradFragment.isAdded()) {
                    container.setVisibility(container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                } else {
                    container.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.borad_container, speechBoradFragment)
                            .commit();
                }
                break;
            case R.id.camera_input:
                hideSoftInput();
                refreshNew();
                if (pictureBoradFragment.isAdded()) {
                    container.setVisibility(container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                } else {
                    container.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.borad_container, pictureBoradFragment)
                            .commit();
                }
                break;
            case R.id.test:
                closeBorad();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        View container = findViewById(R.id.borad_container);
        if (container.getVisibility() == View.VISIBLE
                && (pictureBoradFragment.isAdded() || speechBoradFragment
                .isAdded())) {
            closeBorad();
        } else {
            if (existUploadFiles()) {
                AlertDialogBuilder.newBuilder(this)
                        .setMessage("是否放弃发起任务?")
                        .setPositiveButton("放弃",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        superOnBackPressed();
                                    }
                                }).setNegativeButton("取消", null).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    private boolean existUploadFiles() {
        return recordFile.exists() || pictureBoradFragment.hasPicture();
    }

    private void superOnBackPressed() {
        super.onBackPressed();
    }

    private void closeBorad() {
        View container = findViewById(R.id.borad_container);
        container.setVisibility(View.GONE);
        refreshNew();
    }

    public void refreshNew() {
        findViewById(R.id.task_speech_new).setVisibility(
                recordFile.exists() ? View.VISIBLE : View.GONE);
        findViewById(R.id.task_camera_new).setVisibility(
                pictureBoradFragment.hasPicture() ? View.VISIBLE : View.GONE);
    }

    private void deleteTempFiles() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (recordFile != null && recordFile.exists()) {
                    recordFile.delete();
                }

                List<Uri> pictures = pictureBoradFragment.getPictures();
                if (pictures != null) {
                    for (Uri uri : pictures) {
                        if ("file".equals(uri.getScheme())) {
                            new File(uri.getPath()).delete();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private void sendNewTask() {
        TaskInfo info;
        try {
            info = getCurrentTask();
        } catch (NoExistingToUser e) {
            Toast.makeText(this, R.string.task_no_existing_to_user_alert,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "return");
            return;
        }

        showProgressDialog("正在提交任务...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(null);
        Log.d(TAG, "show");

        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.setRequestBody(info);
        requestBuilder.setResponseType(new TypeToken<Response<TaskInfo>>() {
        }.getType());
        requestBuilder.setResponseListener(new Listener<Response<TaskInfo>>() {

            @Override
            public void onResponse(Response<TaskInfo> response) {
                dismissProgressDialog();

                if (response.getCode() == 0) {

                    Toast.makeText(TaskPublishingActivity.this, "任务创建成功",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra(TASK_NEW_ADDED_KEY, true);
                    setResult(RESULT_OK, intent);
                    finish();

                    deleteTempFiles();
                } else {
                    Toast.makeText(getApplicationContext(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onErrorResponse(InvocationError error) {
                dismissProgressDialog();
                Toast.makeText(TaskPublishingActivity.this, "任务创建失败",
                        Toast.LENGTH_LONG).show();
            }
        });
        requestBuilder.build().post();
        Log.d(TAG, "post");
    }

    private void uploadFiles() {

        if (!hasReceiver()) {
            Toast.makeText(this, R.string.task_no_existing_to_user_alert,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                for (Cancelable cancelable : cancelables.values()) {
                    cancelable.cancel();
                }
                cancelables.clear();
                imageUrls.clear();
            }
        });
        List<Uri> pictures = pictureBoradFragment.getPictures();
        for (int i = 0; i < pictures.size(); i++) {
            Uri uri = pictures.get(i);
//			if ("file".equals(uri.getScheme())) {
//				FileUtils.compressImageFile(uri.getPath(), 720, 1280);
//				uploadFile(
//						BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE),
//						"image", new File(uri.getPath()), i);
//				
            Cancelable cancelable = uploadFile(i, uri, "image", BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE));
            cancelables.put(i, cancelable);
//			}

        }
        if (recordFile.exists()) {
//			uploadFile(
//					BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_FILE),
//					"file", recordFile, VOICE_INDEX);
            Cancelable cancelable = uploadFile(VOICE_INDEX, Uri.fromFile(recordFile), "file", BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_FILE));
            cancelables.put(VOICE_INDEX, cancelable);
        }

    }

    private Cancelable uploadFile(final int index, Uri uri, String fileType, String url) {

        final Cancelable cancelable = AsyncRequest.upload(this, uri, url, fileType, 720, 1280, true, new ProgressListener<Response<String>>() {

            @Override
            public void onError(InvocationError arg0) {
                cancelables.remove(index);
                Toast.makeText(getApplicationContext(), "文件上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished(Response<String> arg0) {
                cancelables.remove(index);
                String url = arg0.getPayload();
                Log.d(TAG, "url=" + url);
                if (index == VOICE_INDEX) {
                    voiceUrl = url;
                } else {
                    imageUrls.add(url);
                }

                checkAndUpload();
            }

            @Override
            public void onProgress(long progress, long max) {
                Log.d(TAG, String.format(
                        "onProgress(max=%d,progress=%d)", max,
                        progress));
                // updateProgress(index, max, progress);
            }

            @Override
            public void onStarted() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (index == VOICE_INDEX) {
                            showProgressDialog("正在上传录音...");
                        } else {
                            showProgressDialog("正在上传图片...");
                        }
                    }
                });
            }
        }, client);
        return cancelable;
    }

    private void showProgressDialog(String message) {
        if (isFinishing()) {
            return;
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mProgressDialog.setMessage(message);

    }

    private void dismissProgressDialog() {
        if (isFinishing()) {
            return;
        }
        mProgressDialog.dismiss();
    }

    private void checkAndUpload() {
        if (checkImageUpload() && checkVoiceUpload()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    sendNewTask();
                }
            });
        }
    }

    private boolean checkImageUpload() {
        boolean b = pictureBoradFragment.getPictures().size() == imageUrls
                .size();
        Log.d(TAG, "checkImageUpload:" + b);
        return b;
    }

    private boolean checkVoiceUpload() {
        boolean b = false;
        if (!recordFile.exists()) {
            b = true;
        } else if (voiceUrl != null) {
            b = true;
        }
        Log.d(TAG, "checkVoiceUpload:" + b);
        return b;
    }


    // private void updateProgress(final int index, long max, long progress) {
    // ProgressBar progressBar;
    // switch (index) {
    // case 0:
    // progressBar = progressBar1;
    // break;
    // case 1:
    // progressBar = progressBar2;
    // break;
    // case 2:
    // progressBar = progressBar3;
    // break;
    // case 3:
    // progressBar = progressBar4;
    // break;
    // default:
    // progressBar = null;
    // break;
    // }
    // if (progressBar != null) {
    // progressBar.setMax((int) max);
    // progressBar.setProgress((int) progress);
    // }
    // }

    private boolean hasReceiver() {
        Editable editable = mEditText.getText();
        UserSimpleInfo.RecipientSpan[] spans = editable.getSpans(0,
                editable.length(), UserSimpleInfo.RecipientSpan.class);
        GroupInfo.RecipientSpan[] groupSpans = editable.getSpans(0,
                editable.length(), GroupInfo.RecipientSpan.class);

        if ((spans != null && spans.length > 0)
                || (groupSpans != null && groupSpans.length > 0)) {
            return true;
        } else {
            return false;
        }
    }

    private TaskInfo getCurrentTask() throws NoExistingToUser {
        boolean isAtGroup = false;

        Log.d(TAG, "getCurrentTask");
        TaskInfo taskInfo = new TaskInfo();

        Editable editable = mEditText.getText();
        UserSimpleInfo.RecipientSpan[] spans = editable.getSpans(0,
                editable.length(), UserSimpleInfo.RecipientSpan.class);
        GroupInfo.RecipientSpan[] groupSpans = editable.getSpans(0,
                editable.length(), GroupInfo.RecipientSpan.class);

        if ((spans != null && spans.length > 0)
                || (groupSpans != null && groupSpans.length > 0)) {
            List<UserSimpleInfo> toUsers = new ArrayList<UserSimpleInfo>(
                    spans.length);
            for (UserSimpleInfo.RecipientSpan span : spans) {
                UserSimpleInfo tempUser = span.getRecipient();

                // 过滤掉可能重复的to user
                boolean tempUserAdded = false;
                for (UserSimpleInfo userInfo : toUsers) {
                    if (tempUser.getId() == userInfo.getId()) {
                        tempUserAdded = true;
                        break;
                    }
                }

                if (!tempUserAdded) {
                    toUsers.add(tempUser);
                }
            }

            taskInfo.setToUsers(toUsers);

            List<GroupInfo> toGroups = new ArrayList<GroupInfo>(spans.length);
            for (GroupInfo.RecipientSpan span : groupSpans) {
                GroupInfo tempGroup = span.getRecipient();

                // 过滤掉可能重复的to user
                boolean tempGroupAdded = false;
                for (GroupInfo groupInfo : toGroups) {
                    if (tempGroup.getId() == groupInfo.getId()) {
                        tempGroupAdded = true;
                        break;
                    }
                }

                if (!tempGroupAdded) {
                    toGroups.add(tempGroup);
                }


            }
            if (toGroups.size() > 0) {
                isAtGroup = true;
            }
            taskInfo.setToGroups(toGroups);
        } else {
            throw new NoExistingToUser("there is no toUser");
        }

        taskInfo.setFromUser(AccountManager.getCurrentSimpleUser(this));

        StringBuilder taskString = new StringBuilder("<html>");
        int startIndex = 0;
        for (UserSimpleInfo.RecipientSpan span : spans) {
            int spanStartIndex = editable.getSpanStart(span);
            taskString.append(editable.subSequence(startIndex, spanStartIndex));

            UserSimpleInfo tempUser = span.getRecipient();
            taskString.append("<a href=\"\">" + tempUser.toCharSequence()
                    + "</a>");

            startIndex = editable.getSpanEnd(span);
        }
        for (GroupInfo.RecipientSpan groupSpan : groupSpans) {
            int spanStartIndex = editable.getSpanStart(groupSpan);
            taskString.append(editable.subSequence(startIndex, spanStartIndex));

            GroupInfo tempGroup = groupSpan.getRecipient();
            taskString.append("<a href=\"\">" + tempGroup.toCharSequence()
                    + "</a>");

            startIndex = editable.getSpanEnd(groupSpan);
        }
        if (startIndex < editable.length()) {
            taskString.append(editable.subSequence(startIndex,
                    editable.length()));
        }

        if (mDailyReportCheck.isChecked()) {
            taskString
                    .append(" " + "<a href=\"\">" + "#" + "日报" + "#" + "</a>");
        }
        taskString.append("</html>");
        taskInfo.setContent(taskString.toString());

        taskInfo.setDailyReport(mDailyReportCheck.isChecked());
        if (!imageUrls.isEmpty()) {
            taskInfo.setImageUrl(buildImageUrl());
        }
        if (voiceUrl != null) {
            taskInfo.setVoiceUrl(voiceUrl);
        }
        Log.d(TAG, "return task:" + taskInfo);

        if (isAtGroup) {
            if (!imageUrls.isEmpty() && voiceUrl != null) {
                  /*记录操作 1009*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_S_I_AT_GROUP);
            } else if (!imageUrls.isEmpty() && voiceUrl == null) {
                 /*记录操作 1008*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_I_AT_GROUP);
            } else if (imageUrls.isEmpty() && voiceUrl != null) {
				 /*记录操作 1007*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_S_AT_GROUP);
            } else if(imageUrls.isEmpty() && voiceUrl == null) {
                /*记录操作 1006*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_AT_GROUP);
            }
        } else {
            if (!imageUrls.isEmpty() && voiceUrl != null) {
                /*记录操作 1005*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_S_I);
            } else if (!imageUrls.isEmpty() && voiceUrl == null) {
               /*记录操作 1004*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_I);
            } else if (imageUrls.isEmpty() && voiceUrl != null) {
                /*记录操作 1003*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_W_S);
            }else if(imageUrls.isEmpty() && voiceUrl == null) {
                /*记录操作 1002*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_PUBLIC_TASK_ONLYWRITING);
            }
        }

        return taskInfo;
    }

    private String buildImageUrl() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imageUrls.size(); i++) {
            builder.append(imageUrls.get(i));
            if (i != imageUrls.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ContactlistActivity.REQUEST_CODE_INPUT:
                if (resultCode == RESULT_OK) {
                    List<UserSimpleInfo> infoList = data
                            .getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT);
                    List<GroupInfo> groupInfos = data
                            .getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_GROUP_RESULT);

                    if ((infoList == null || infoList.size() == 0)
                            && (groupInfos == null || groupInfos.isEmpty()))
                        break;

                    // 删除'@'字符
                    Editable editable = mEditText.getText();
                    editable.replace(Selection.getSelectionStart(editable) - 1,
                            Selection.getSelectionStart(editable), "");

                    for (UserSimpleInfo info : infoList) {
                        Editable edit_text = mEditText.getEditableText();
                        edit_text.insert(mEditText.getSelectionStart(),
                                info.toCharSequence());
                        edit_text.insert(mEditText.getSelectionStart(), " ");
                    }

                    for (GroupInfo groupInfo : groupInfos) {
                        Editable edit_text = mEditText.getEditableText();
                        edit_text.insert(mEditText.getSelectionStart(),
                                groupInfo.toCharSequence());
                        edit_text.insert(mEditText.getSelectionStart(), " ");
                    }
                }
                break;
            case ContactlistActivity.REQUEST_CODE_CLICK:
                if (resultCode == RESULT_OK) {
                    List<UserSimpleInfo> infoList = data
                            .getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT);
                    List<GroupInfo> groupInfos = data
                            .getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_GROUP_RESULT);

                    if ((infoList == null || infoList.size() == 0)
                            && (groupInfos == null || groupInfos.isEmpty()))
                        break;

                    for (UserSimpleInfo info : infoList) {
                        Editable edit_text = mEditText.getEditableText();
                        edit_text.insert(mEditText.getSelectionStart(),
                                info.toCharSequence());
                        edit_text.insert(mEditText.getSelectionStart(), " ");
                    }

                    for (GroupInfo groupInfo : groupInfos) {
                        Editable edit_text = mEditText.getEditableText();
                        edit_text.insert(mEditText.getSelectionStart(),
                                groupInfo.toCharSequence());
                        edit_text.insert(mEditText.getSelectionStart(), " ");
                    }
                }
                break;
            default:
                break;
        }
    }

    private static class NoExistingToUser extends Exception {
        public NoExistingToUser(String message) {
            super(message);
        }
    }

    private void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void launch(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), TaskPublishingActivity.class);
        fragment.startActivityForResult(intent, TaskBaseFragment.REQUEST_CODE_PUBLISHING_NEW_TASK);
    }

    public static void launchWithUserInfo(Context context, DeptDetailUserInfo userInfo) {
        UserSimpleInfo simpleInfo = new UserSimpleInfo();
        simpleInfo.setIcon(userInfo.getAvatar());
        simpleInfo.setId(userInfo.getUserId());
        simpleInfo.setName(userInfo.getName());
        Intent intent = new Intent(context, TaskPublishingActivity.class);
        intent.putExtra(EXTRA_USER_SIMPLE_INFO, simpleInfo);
        context.startActivity(intent);

    }
}
