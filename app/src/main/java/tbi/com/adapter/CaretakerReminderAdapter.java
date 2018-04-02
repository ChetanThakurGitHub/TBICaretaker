package tbi.com.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tbi.com.R;
import tbi.com.custom_view.DailogView;
import tbi.com.fragment.caretaker.activity.AddReminderActivity;
import tbi.com.model.AllReminderList;

public class CaretakerReminderAdapter extends RecyclerView.Adapter<CaretakerReminderAdapter.ViewHolder> {
    public List<AllReminderList> allReminderLists;
    private Context mContext;

    public CaretakerReminderAdapter(ArrayList<AllReminderList> allReminderLists, Context mContext) {
        this.allReminderLists = allReminderLists;
        this.mContext = mContext;
    }

    @Override
    public CaretakerReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list_layout, parent, false);
        return new CaretakerReminderAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CaretakerReminderAdapter.ViewHolder holder, final int position) {
        final AllReminderList allReminderList = allReminderLists.get(position);

        holder.tv_for_date.setText(allReminderList.date);
        holder.tv_for_month.setText(allReminderList.date.substring(3, 6));
        holder.tv_for_tittleList.setText(allReminderList.title);
        holder.tv_for_description.setText(allReminderList.description);
        holder.tv_for_time.setText(allReminderList.time);
        if (allReminderList.is_done.equals("1")) {
            holder.view_for_color.setBackgroundResource(R.drawable.circle_notificatioan_active_bg);
            holder.line.setVisibility(View.GONE);
            holder.iv_for_editSwipe.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return allReminderLists.size();
    }

    private void click(Context context, String date, String time, String status, String title, String description) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_reminder_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tv_for_dateDailog = dialog.findViewById(R.id.tv_for_dateDailog);
        TextView tv_for_timeDailog = dialog.findViewById(R.id.tv_for_timeDailog);
        TextView tv_for_status = dialog.findViewById(R.id.tv_for_status);
        TextView tv_for_titleDailog = dialog.findViewById(R.id.tv_for_titleDailog);
        TextView tv_for_descriptionDailog = dialog.findViewById(R.id.tv_for_descriptionDailog);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        RelativeLayout layout_for_rememberHeader = dialog.findViewById(R.id.layout_for_rememberHeader);

        tv_for_dateDailog.setText(date);
        tv_for_timeDailog.setText(time);
        if (status.equals("1")) {
            tv_for_status.setText(R.string.completed_);
            tv_for_status.setTextColor(Color.parseColor("#358132"));
            layout_for_rememberHeader.setBackgroundColor(Color.parseColor("#358132"));
            layout_for_rememberHeader.setBackgroundResource(R.drawable.dailog_bg_top_active);
        }
        tv_for_titleDailog.setText(title);
        tv_for_descriptionDailog.setText(description);
        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view_for_color, line;
        private TextView tv_for_date, tv_for_month, tv_for_tittleList, tv_for_description, tv_for_time;
        private RelativeLayout layout_for_list;
        private ImageView iv_for_editSwipe;

        ViewHolder(View itemView) {
            super(itemView);

            view_for_color = itemView.findViewById(R.id.view_for_color);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            tv_for_month = itemView.findViewById(R.id.tv_for_month);
            tv_for_tittleList = itemView.findViewById(R.id.tv_for_tittleList);
            tv_for_description = itemView.findViewById(R.id.tv_for_description);
            tv_for_time = itemView.findViewById(R.id.tv_for_time);
            line = itemView.findViewById(R.id.line);
            layout_for_list = itemView.findViewById(R.id.layout_for_list);
            iv_for_editSwipe = itemView.findViewById(R.id.iv_for_editSwipe);
            iv_for_editSwipe.setOnClickListener(this);
            itemView.findViewById(R.id.view_for_deleteSwipe).setOnClickListener(this);
            layout_for_list.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AllReminderList allReminderList = allReminderLists.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.layout_for_list:
                    click(mContext, allReminderList.date, allReminderList.time, allReminderList.is_done, allReminderList.title, allReminderList.description);
                    break;
                case R.id.view_for_deleteSwipe:
                    DailogView dailogView = new DailogView();
                    dailogView.deletelDailog(mContext, allReminderList.reminderId, layout_for_list, getAdapterPosition(), CaretakerReminderAdapter.this);
                    break;
                case R.id.iv_for_editSwipe:
                    Intent intent = new Intent(mContext, AddReminderActivity.class);
                    intent.putExtra("EditReminder", allReminderList);
                    mContext.startActivity(intent);
                    break;
            }
        }
    }

}
