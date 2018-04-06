package tbi.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tbi.com.R;
import tbi.com.activity.main_activity.CaretakerHomeActivity;
import tbi.com.activity.main_activity.SuffererHomeActivity;
import tbi.com.fragment.caretaker.AddSuffererFragment;
import tbi.com.fragment.caretaker.ReminderCaretakerFragment;
import tbi.com.fragment.sufferer.FaqsSuffererFragment;
import tbi.com.fragment.sufferer.MyCaretakerFragment;
import tbi.com.fragment.sufferer.ReminderSuffererFragment;
import tbi.com.model.NotificationList;
import tbi.com.util.Constant;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationList> notificationLists;
    private Context mContext;
    private Boolean aBoolean = false;

    public NotificationAdapter(ArrayList<NotificationList> notificationLists, Context mContext, Boolean aBoolean) {
        this.notificationLists = notificationLists;
        this.mContext = mContext;
        this.aBoolean = aBoolean;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_layout, parent, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, final int position) {
        final NotificationList notificationList = notificationLists.get(position);

        //2018-03-27
        String date = notificationList.reminder_date.trim();
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Date date1;
        String formatDate;
        String[] dayMonth = new String[2];

        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            formatDate = outputFormat.format(date1);
            dayMonth = formatDate.split("\\s+");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dayMonth[0] != null && dayMonth[0].length() != 0) {
            holder.tv_for_date.setText(dayMonth[0]);
        } else {
            holder.tv_for_date.setText(R.string.na);
        }
        if (dayMonth[1] != null && dayMonth[1].length() != 0) {
            holder.tv_for_month.setText(dayMonth[1]);
        } else {
            holder.tv_for_month.setText(R.string.na);
        }
        if (notificationList.reminder_time.isEmpty()) {
            holder.tv_for_time.setText(R.string.na);
        } else {
            holder.tv_for_time.setText(notificationList.reminder_time);
        }

        holder.tv_for_tittleList.setText(notificationList.reminder_title);
        holder.tv_for_description.setText(notificationList.reminder_description);
        holder.tv_for_timeNoti.setText(notificationList.crd);

    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_for_date, tv_for_month, tv_for_time, tv_for_tittleList, tv_for_description, tv_for_timeNoti;

        ViewHolder(View itemView) {
            super(itemView);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            tv_for_month = itemView.findViewById(R.id.tv_for_month);
            tv_for_time = itemView.findViewById(R.id.tv_for_time);
            tv_for_tittleList = itemView.findViewById(R.id.tv_for_tittleList);
            tv_for_description = itemView.findViewById(R.id.tv_for_description);
            tv_for_timeNoti = itemView.findViewById(R.id.tv_for_timeNoti);

            if (aBoolean) {
                tv_for_tittleList.setTextColor(Color.parseColor("#333333"));
                tv_for_date.setTextColor(Color.parseColor("#358132"));
                tv_for_month.setTextColor(Color.parseColor("#358132"));
                tv_for_time.setTextColor(Color.parseColor("#358132"));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationList notificationList = notificationLists.get(getAdapterPosition());
                    Constant.notificationStatus(mContext, notificationList.notificationId);
                    if (notificationList.type.equals("Reminder_add") | notificationList.type.equals("Reminder_delete") | notificationList.equals("Reminder_update") | notificationList.type.equals("Sufferer_removed")) {
                        ((SuffererHomeActivity) mContext).replaceFragment(ReminderSuffererFragment.newInstance(), true, R.id.framlayout);
                    } else if (notificationList.type.equals("Sufferer_add")) {
                        ((SuffererHomeActivity) mContext).replaceFragment(MyCaretakerFragment.newInstance(), true, R.id.framlayout);
                    } else if (notificationList.type.equals("Reminder_done")) {
                        ((CaretakerHomeActivity) mContext).replaceFragment(ReminderCaretakerFragment.newInstance(), true, R.id.framlayout);
                    } else if (notificationList.type.equals("Caretaker_removed")) {
                        ((CaretakerHomeActivity) mContext).replaceFragment(AddSuffererFragment.newInstance(), true, R.id.framlayout);
                    } else if (notificationList.type.equals("FAQS_Add") | notificationList.type.equals("FAQS_Update") | notificationList.type.equals("FAQS_Delete")) {
                        ((SuffererHomeActivity) mContext).replaceFragment(FaqsSuffererFragment.newInstance(), true, R.id.framlayout);
                    }
                }
            });
        }
    }

}
