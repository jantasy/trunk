package cn.yjt.oa.app.signin.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CardCheckinInfo;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.utils.ToastUtils;


/**
 * 展示考勤记录的‘下拉刷新多层级listview’的适配器
 */
@SuppressLint("SimpleDateFormat")
public class AttendanceRecordsAdapter extends BaseExpandableListAdapter {

    /** 定义四个日期显示格式 */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
            "HHmmss");
    private static final SimpleDateFormat TIME_FORMAT_VIEW_MINUTE = new SimpleDateFormat(
            "HH:mm");
    private static final SimpleDateFormat TIME_FORMAT_VIEW_SECOND = new SimpleDateFormat(
            "HH:mm:ss");


    private List<ClockInInfo> clockInInfos = Collections.emptyList();
    private List<CardCheckinInfo> originalCheckinInfos = Collections
            .emptyList();
    private List<List<CardCheckinInfo>> checkinInfos = Collections.emptyList();
    private Context context;
    private LayoutInflater inflater;
    private boolean isUpdateClockinInfo;
    private boolean isUpdateCheckinInfo;

    /** 构造方法，初始化上下文和布局填充器 */
    public AttendanceRecordsAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private boolean isEmptyList(List<?> list) {
        return "EmptyList".equals(list.getClass().getSimpleName());
    }

    public void addCardCheckinInfos(List<CardCheckinInfo> checkinInfos) {
        if (isEmptyList(this.originalCheckinInfos)) {
            this.originalCheckinInfos = new ArrayList<CardCheckinInfo>();
        }
        this.originalCheckinInfos.addAll(checkinInfos);
        isUpdateCheckinInfo = true;
    }

    public void setCardCheckinInfos(List<CardCheckinInfo> checkinInfos) {
        this.originalCheckinInfos = checkinInfos;
        isUpdateCheckinInfo = true;
    }

    public void addClockInInfos(List<ClockInInfo> clockInInfos) {
        if (isEmptyList(this.clockInInfos)) {
            this.clockInInfos = new ArrayList<ClockInInfo>();
        }
        this.clockInInfos.addAll(clockInInfos);
        isUpdateClockinInfo = true;
    }

    public void setClockInInfos(List<ClockInInfo> clockInInfos) {
        this.clockInInfos = clockInInfos;
        isUpdateClockinInfo = true;
    }

    Comparator<CardCheckinInfo> comparator = new Comparator<CardCheckinInfo>() {

        @Override
        public int compare(CardCheckinInfo lhs, CardCheckinInfo rhs) {
            try {
                if (lhs.getCardDate().equals(rhs.getCardDate())) {
                    return 0;
                } else {

                    return DATE_FORMAT.parse(rhs.getCardDate()).compareTo(
                            DATE_FORMAT.parse(lhs.getCardDate()));

                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    };

    Comparator<CardCheckinInfo> timeComparator = new Comparator<CardCheckinInfo>() {

        @Override
        public int compare(CardCheckinInfo lhs, CardCheckinInfo rhs) {

            try {
                if (lhs.getCardTime().equals(rhs.getCardTime())) {
                    return 0;
                } else {

                    int i = TIME_FORMAT.parse(rhs.getCardTime()).compareTo(
                            TIME_FORMAT.parse(lhs.getCardTime()));
                    return i;

                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    };

    private void sort() {

        Collections.sort(originalCheckinInfos, comparator);
    }

    private void grouping() {
        sort();
        List<List<CardCheckinInfo>> checkinInfos = new ArrayList<List<CardCheckinInfo>>();
        Iterator<CardCheckinInfo> originalCheckinIterator = originalCheckinInfos
                .iterator();
        List<CardCheckinInfo> tempCheckinInfos = new ArrayList<CardCheckinInfo>();
        if (!originalCheckinIterator.hasNext()) {
            return;
        }
        CardCheckinInfo cardCheckinInfo = originalCheckinIterator.next();
        String date = cardCheckinInfo.getCardDate();
        tempCheckinInfos.add(cardCheckinInfo);
        while (originalCheckinIterator.hasNext()) {
            cardCheckinInfo = originalCheckinIterator.next();
            if (date.equals(cardCheckinInfo.getCardDate())) {
                tempCheckinInfos.add(cardCheckinInfo);
            } else {
                checkinInfos.add(tempCheckinInfos);
                tempCheckinInfos = new ArrayList<CardCheckinInfo>();
                tempCheckinInfos.add(cardCheckinInfo);
                date = cardCheckinInfo.getCardDate();
            }
        }

        checkinInfos.add(tempCheckinInfos);
//        Iterator<List<CardCheckinInfo>> iterator = checkinInfos.iterator();
//        List<CardCheckinInfo> next = iterator.next();
        this.checkinInfos = new ArrayList<List<CardCheckinInfo>>();

        for (ClockInInfo clockInInfo : clockInInfos) {
            boolean success = false;
            Iterator<List<CardCheckinInfo>> iterator = checkinInfos.iterator();
            while (iterator.hasNext()) {
                List<CardCheckinInfo> next = iterator.next();
                String cardDate = next.get(0).getCardDate();


                String dutyDate = clockInInfo.getDutyDate();
                if (cardDate.equals(dutyDate)) {
                    Collections.sort(next, timeComparator);
                    this.checkinInfos.add(next);
                    success = true;
                    break;
                }


            }
            if (!success) {
                List<CardCheckinInfo> list = Collections.emptyList();
                this.checkinInfos.add(list);
            }
        }


//            String cardDate = next.get(0).getCardDate();
//            String dutyDate = clockInInfo.getDutyDate();
//            if (cardDate.equals(dutyDate)) {
//                Collections.sort(next, timeComparator);
//                this.checkinInfos.add(next);
//                if (iterator.hasNext()) {
//                    next = iterator.next();
//                }
//            } else {
//                List<CardCheckinInfo> list = Collections.emptyList();
//                this.checkinInfos.add(list);
//            }
    }

    private String formatTime(SimpleDateFormat format, String time) {
        if (TextUtils.isEmpty(time)) {
            return " - -:- -";
        }
        try {
            return format.format(TIME_FORMAT.parse(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " - -:- -";
    }

    public boolean checkDataReady() {
        if (isUpdateCheckinInfo && isUpdateClockinInfo) {
            grouping();
        }
        return isUpdateCheckinInfo && isUpdateClockinInfo;
    }

    public void notifyDataSetChangedWithCheckData() {
        if (checkDataReady()) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return clockInInfos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (checkinInfos.isEmpty() || checkinInfos.size() < groupPosition) {
            return 0;
        }
        return checkinInfos.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return clockInInfos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return checkinInfos.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("ViewTag")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.attendance_records_item,
                    parent, false);
            convertView.setTag(R.id.week, convertView.findViewById(R.id.week));
            convertView.setTag(R.id.in_time,
                    convertView.findViewById(R.id.in_time));
            convertView.setTag(R.id.out_time,
                    convertView.findViewById(R.id.out_time));
            convertView
                    .setTag(R.id.month, convertView.findViewById(R.id.month));
            convertView.setTag(R.id.day, convertView.findViewById(R.id.day));
            convertView.setTag(R.id.attendance_status,
                    convertView.findViewById(R.id.attendance_status));
            convertView.setTag(R.id.status_cicle,
                    convertView.findViewById(R.id.status_cicle));
            convertView.setTag(R.id.content_layout,
                    convertView.findViewById(R.id.content_layout));
            convertView.setTag(R.id.tv_line, convertView.findViewById(R.id.tv_line));
            convertView.setTag(R.id.filler,
                    convertView.findViewById(R.id.filler));
        }
        TextView week = (TextView) convertView.getTag(R.id.week);
        TextView inTime = (TextView) convertView.getTag(R.id.in_time);
        TextView outTime = (TextView) convertView.getTag(R.id.out_time);
        TextView month = (TextView) convertView.getTag(R.id.month);
        TextView day = (TextView) convertView.getTag(R.id.day);
        TextView attendanceStatus = (TextView) convertView
                .getTag(R.id.attendance_status);
        View statusCicle = (View) convertView.getTag(R.id.status_cicle);
        View line = (View) convertView.getTag(R.id.tv_line);
        View filler = (View) convertView.getTag(R.id.filler);

        ClockInInfo group = (ClockInInfo) getGroup(groupPosition);
        boolean normal = group.getStatusCode() == 1;
        try {
            Date date = null;
            if (!group.isToday()) {
                attendanceStatus.setText(group.getStatus());
            } else {
                attendanceStatus.setText("今日记录");
            }
            date = DATE_FORMAT.parse(group.getDutyDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            week.setText(getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            month.setText((calendar.get(Calendar.MONTH) + 1) + "月");
            day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            attendanceStatus.setTextColor(context.getResources().getColor(
                    normal ? R.color.attendance_normal
                            : R.color.attendance_abnormal));
            inTime.setTextColor(context.getResources().getColor(
                    normal ? R.color.attendance_normal
                            : R.color.attendance_abnormal));
            outTime.setTextColor(context.getResources().getColor(
                    normal ? R.color.attendance_normal
                            : R.color.attendance_abnormal));
            inTime.setText(formatTime(TIME_FORMAT_VIEW_MINUTE,
                    group.getCheckInTime()));
            outTime.setText(formatTime(TIME_FORMAT_VIEW_MINUTE,
                    group.getCheckOutTime()));

//            if (groupPosition == 0 && !normal) {
//                attendanceStatus.setText("今日记录");
//            } else {
//                attendanceStatus.setText(group.getStatus());
//            }

            statusCicle
                    .setBackgroundResource(normal ? R.drawable.ic_circle_green
                            : R.drawable.ic_circle_red);
            inTime.setCompoundDrawablesWithIntrinsicBounds(
                    normal ? R.drawable.ic_green_in : R.drawable.ic_red_in, 0,
                    0, 0);
            outTime.setCompoundDrawablesWithIntrinsicBounds(
                    normal ? R.drawable.ic_green_out : R.drawable.ic_red_out,
                    0, 0, 0);
            if (getChildrenCount(groupPosition) > 0) {
                attendanceStatus
                        .setBackgroundResource(isExpanded ? R.drawable.bg_attendance_top
                                : R.drawable.bg_attendance_whole);
                line.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                filler.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                attendanceStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        isExpanded ? R.drawable.ic_arrow_down
                                : R.drawable.ic_arrow_right, 0);
            } else {
                line.setVisibility(View.GONE);
                filler.setVisibility(View.GONE);
                attendanceStatus
                        .setBackgroundResource(R.drawable.bg_attendance_whole);
                attendanceStatus.setCompoundDrawables(null, null, null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private String getWeek(int week) {
        String weekDay = "";
        switch (week) {
            case Calendar.SUNDAY:
                weekDay = "日";
                break;
            case Calendar.MONDAY:
                weekDay = "一";
                break;
            case Calendar.TUESDAY:
                weekDay = "二";
                break;
            case Calendar.WEDNESDAY:
                weekDay = "三";
                break;
            case Calendar.THURSDAY:
                weekDay = "四";
                break;
            case Calendar.FRIDAY:
                weekDay = "五";
                break;
            case Calendar.SATURDAY:
                weekDay = "六";
                break;
            default:
                break;
        }
        return "周" + weekDay;
    }

    @SuppressLint("ViewTag")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.attendance_records_checkin_item, parent, false);
            convertView.setTag(R.id.signin_type,
                    convertView.findViewById(R.id.signin_type));
            convertView.setTag(R.id.actrual_poi,
                    convertView.findViewById(R.id.actrual_poi));
            convertView.setTag(R.id.tv_check_in_time,
                    convertView.findViewById(R.id.tv_check_in_time));
            convertView.setTag(R.id.content_layout,
                    convertView.findViewById(R.id.content_layout));
            convertView.setTag(R.id.tv_line, convertView.findViewById(R.id.tv_line));
        }
        TextView type = (TextView) convertView.getTag(R.id.signin_type);
        TextView actrual_poi = (TextView) convertView.getTag(R.id.actrual_poi);
        TextView time = (TextView) convertView.getTag(R.id.tv_check_in_time);
        View contentLayout = (View) convertView.getTag(R.id.content_layout);
        View line = (View) convertView.getTag(R.id.tv_line);

        CardCheckinInfo child = (CardCheckinInfo) getChild(groupPosition,
                childPosition);
        type.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                getTypeIcon(child.getType()), 0);
        type.setText(child.getMachineName());
        if (TextUtils.isEmpty(child.getActrualPOI())) {
            actrual_poi.setVisibility(View.GONE);
        } else {
            actrual_poi.setText(child.getActrualPOI());
            actrual_poi.setVisibility(View.VISIBLE);
        }
        time.setText(formatTime(TIME_FORMAT_VIEW_SECOND, child.getCardTime()));
        if (isLastChild) {
            line.setVisibility(View.GONE);
            contentLayout
                    .setBackgroundResource(R.drawable.bg_attendance_bottom);
        } else {
            line.setVisibility(View.VISIBLE);
            contentLayout.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    private int getTypeIcon(String type) {
        if (SigninInfo.SINGIN_TYPE_VISIT.equals(type)) {// 如果是位置签到显示位置图片，并且有点击事件，否则不处理
            return R.drawable.sign_location;
        } else if (SigninInfo.SIGNIN_TYPE_NFC.equals(type)) {
            return R.drawable.sign_nfc;
        } else if (SigninInfo.SIGNIN_TYPE_QR.equals(type)) {
            return R.drawable.sign_bar_code;
        } else if (SigninInfo.SIGNIN_TYPE_BEACON.equals(type)) {
            return R.drawable.sign_beacon;
        } else {
            return 0;
        }

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        CardCheckinInfo cardCheckinInfo = (CardCheckinInfo) getChild(
                groupPosition, childPosition);
        if (CardCheckinInfo.SINGIN_TYPE_VISIT.equals(cardCheckinInfo.getType())) {
            return true;
        }
        return false;
    }

}
