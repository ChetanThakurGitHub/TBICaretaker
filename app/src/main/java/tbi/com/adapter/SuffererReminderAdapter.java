package tbi.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tbi.com.R;

import tbi.com.custom_view.DailogView;
import tbi.com.model.AllReminderList;

public class SuffererReminderAdapter extends RecyclerView.Adapter<SuffererReminderAdapter.ViewHolder> {
    private List<AllReminderList> allReminderLists;
    private Context mContext;

    public SuffererReminderAdapter(ArrayList<AllReminderList> allReminderLists, Context mContext) {
        this.allReminderLists = allReminderLists;
        this.mContext = mContext;
    }

    @Override
    public SuffererReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_suffererlist_layout, parent, false);
        return new SuffererReminderAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuffererReminderAdapter.ViewHolder holder, final int position) {
        final AllReminderList allReminderList = allReminderLists.get(position);

        holder.tv_for_date.setText(allReminderList.date);
        holder.tv_for_month.setText(allReminderList.date.substring(3, 6));
        holder.tv_for_tittleList.setText(allReminderList.title);
        holder.tv_for_description.setText(allReminderList.description);
        holder.tv_for_time.setText(allReminderList.time);
        if (allReminderList.is_done.equals("1")) {
            holder.view_for_color.setBackgroundResource(R.drawable.circle_notificatioan_active_bg);
        }
    }

    @Override
    public int getItemCount() {
        return allReminderLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view_for_color;
        private TextView tv_for_date, tv_for_month, tv_for_tittleList, tv_for_description, tv_for_time;

        ViewHolder(View itemView) {
            super(itemView);

            view_for_color = itemView.findViewById(R.id.view_for_color);
            tv_for_date = itemView.findViewById(R.id.tv_for_date);
            tv_for_month = itemView.findViewById(R.id.tv_for_month);
            tv_for_tittleList = itemView.findViewById(R.id.tv_for_tittleList);
            tv_for_description = itemView.findViewById(R.id.tv_for_description);
            tv_for_time = itemView.findViewById(R.id.tv_for_time);
            itemView.findViewById(R.id.layout_for_list).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AllReminderList allReminderList = allReminderLists.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.layout_for_list:
                    DailogView dailogView = new DailogView();
                    dailogView.click(mContext, view_for_color, allReminderList);
                    break;
            }
        }
    }


}
