package cn.yjt.oa.app.task;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.widget.TimeLineAdapter;

public class TasksListAdapter extends TimeLineAdapter {
    private Context context;
    private LayoutInflater inflater;



    public TasksListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addTaskIntoAdapter(List<TaskInfo> tasks) {
        for (TaskInfo taskInfo : tasks) {
            addTaskIntoAdapter(taskInfo);
        }
    }

    public void addTaskIntoAdapter(TaskInfo taskInfo) {
        Date taskDate = retrieveTaskDate(taskInfo);
        if (taskDate == null)
            return;

        addEntry(taskDate, taskInfo);
    }

    public void removeTaskFromAdapter(TaskInfo taskInfo) {
        Date taskDate = retrieveTaskDate(taskInfo);
        if (taskDate == null)
            return;

        removeEntry(taskDate, taskInfo);
    }

    private Date retrieveTaskDate(TaskInfo taskInfo) {
        Date taskDate = null;
        if (!TextUtils.isEmpty(taskInfo.getLastReplyTime())) {
            try {
                taskDate = BusinessConstants.parseTime(taskInfo.getLastReplyTime());
            } catch (ParseException e) {
                taskDate = null;
            }
        }

        return taskDate;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.task_list_item, parent, false);

            holder = new ViewHolder();
            holder.taskColor = (ImageView) view.findViewById(R.id.task_color);
            holder.fromUserIcon = (ImageView) view.findViewById(R.id.from_user_icon);
            holder.fromUserName = (TextView) view.findViewById(R.id.from_user_name);
            holder.exactTime = (TextView) view.findViewById(R.id.task_creation_exact_time);
            holder.taskContent = (TextView) view.findViewById(R.id.task_content);
            holder.replyStatus = (TextView) view.findViewById(R.id.task_reply_state);
            holder.replyStatusContainer = (LinearLayout) view.findViewById(R.id.task_reply_state_container);
            holder.taskTopStickyContainer = (LinearLayout) view.findViewById(R.id.task_top_sticky_container);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final TaskInfo taskInfo = (TaskInfo) getItem(section, position);

        Date taskDate = null;
        if (!TextUtils.isEmpty(taskInfo.getLastReplyTime())) {
            try {
                taskDate = BusinessConstants.parseTime(taskInfo.getLastReplyTime());
            } catch (ParseException e) {
                taskDate = null;
            }
        }

        if (taskDate != null) {
            holder.exactTime.setText(BusinessConstants.getTime(taskDate));
        } else {
            holder.exactTime.setText(null);
        }

        int markColor = taskInfo.getMark();
        markColor &= 0xFFFFFF;
        if (markColor == TaskDetailActivity.COLOR_MARK_RED_TO_SERVER) {
            holder.taskColor.setBackgroundResource(R.drawable.task_reply_color_mark_alarming);
        } else if (markColor == TaskDetailActivity.COLOR_MARK_YELLOW_TO_SERVER) {
            holder.taskColor.setBackgroundResource(R.drawable.task_reply_color_mark_delay);
        } else {
            holder.taskColor.setBackgroundResource(R.drawable.task_reply_color_mark_normal);
        }

        final ImageView iconView = holder.fromUserIcon;
//        ImageURLConsulter.getInstance(context).consultImageUrl(taskInfo.getFromUser().getId(),
//                iconView);
        iconView.setImageResource(R.drawable.contactlist_contact_icon_default);
        iconView.setTag(taskInfo.getIcon());
        MainApplication.getHeadImageLoader().get(taskInfo.getIcon(), new ImageLoaderListener() {

            @Override
            public void onSuccess(ImageContainer container) {
                if (container.getUrl().equals(iconView.getTag())) {
                    iconView.setImageBitmap(container.getBitmap());
                }
            }

            @Override
            public void onError(ImageContainer container) {
                // TODO Auto-generated method stub

            }
        });

        holder.fromUserName.setText(taskInfo.getFromUser().getName());
        Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
                Html.fromHtml(taskInfo.getContent() + ""));
        Spannable processedText = URLSpanNoUnderline.removeUnderlines(spannedText);
        holder.taskContent.setText(processedText);

        holder.replyStatus.setText("回复(" + taskInfo.getReplyCount() + ")");

//        holder.taskTopStickyContainer.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "任务设为置顶", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

    public class ViewHolder {
        public ImageView taskColor;
        public ImageView fromUserIcon;
        public TextView fromUserName;
        public TextView exactTime;
        public TextView taskContent;

        public TextView replyStatus;
        public LinearLayout replyStatusContainer;
        public LinearLayout taskTopStickyContainer;
    }

    @Override
    public View getSectionView(int section, View convertView, ViewGroup parent) {
        View sectionView = convertView;
        if (sectionView == null) {
            sectionView = inflater.inflate(R.layout.task_list_section_title_layout, parent, false);
        }

        TextView sectionTitle = (TextView) sectionView.findViewById(R.id.task_list_section_title);

        Date date = getSectionDate(section);
        Calendar sectionTime = Calendar.getInstance();
        sectionTime.setTimeInMillis(date.getTime());

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == sectionTime.get(Calendar.DATE)) {
            sectionTitle.setText("今天");
        } else if (now.get(Calendar.DATE) == sectionTime.get(Calendar.DATE) + 1) {
            sectionTitle.setText("昨天");
        } else {
            sectionTitle.setText(BusinessConstants.getDate(date));
        }

        return sectionView;
    }

}
