package tbi.com.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tbi.com.R;
import tbi.com.activity.main_activity.SuffererHomeActivity;
import tbi.com.chat.fragment.MessageSuffererFragment;
import tbi.com.fragment.sufferer.FaqsSuffererFragment;
import tbi.com.fragment.sufferer.MyCaretakerFragment;
import tbi.com.fragment.sufferer.MyProfileSufferFragment;
import tbi.com.fragment.sufferer.NotificationsSuffererFragment;
import tbi.com.fragment.sufferer.ReminderSuffererFragment;
import tbi.com.model.NavigationListModel;
import tbi.com.util.Constant;


public class SuffererNavigationAdapter extends RecyclerView.Adapter<SuffererNavigationAdapter.ViewHolder> {
    public int lastclick = -1;
    private List<NavigationListModel> navigationList;
    private SuffererHomeActivity mContext;

    public SuffererNavigationAdapter(ArrayList<NavigationListModel> navigationList, SuffererHomeActivity mContext) {
        this.navigationList = navigationList;
        this.mContext = mContext;
    }

    @Override
    public SuffererNavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_layout, parent, false);
        return new SuffererNavigationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuffererNavigationAdapter.ViewHolder holder, final int position) {
        NavigationListModel navigationListModel = navigationList.get(position);

        if (position == 6) {
            holder.view_for_line.setVisibility(View.GONE);
        } else {
            holder.view_for_line.setVisibility(View.VISIBLE);
        }

        holder.iv_for_image.setImageResource(navigationListModel.image);
        holder.tv_for_nameTittle.setText(navigationListModel.name);

        if (lastclick != -1) {
            if (lastclick == position) {
                holder.view_for_click.setVisibility(View.VISIBLE);
                holder.iv_for_image.setImageResource(navigationListModel.selectedImage);
                holder.tv_for_nameTittle.setTextColor(Color.parseColor("#5e8d93"));
            } else {
                holder.view_for_click.setVisibility(View.GONE);
                holder.iv_for_image.setImageResource(navigationListModel.image);
                holder.tv_for_nameTittle.setTextColor(Color.parseColor("#333333"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return navigationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_for_image;
        private TextView tv_for_nameTittle;
        private View view_for_click, view_for_line;
        private LinearLayout layout_for_item;

        ViewHolder(View itemView) {
            super(itemView);

            iv_for_image = itemView.findViewById(R.id.iv_for_image);
            tv_for_nameTittle = itemView.findViewById(R.id.tv_for_nameTittle);
            view_for_click = itemView.findViewById(R.id.view_for_click);
            layout_for_item = itemView.findViewById(R.id.layout_for_item);
            view_for_line = itemView.findViewById(R.id.view_for_line);
            layout_for_item.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_for_item:
                    view_for_click.setVisibility(View.VISIBLE);
                    if (getAdapterPosition() == 0) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(ReminderSuffererFragment.newInstance(""), false, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.reminders);
                        mContext.iv_for_calender.setVisibility(View.VISIBLE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 1) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(MyProfileSufferFragment.newInstance(""), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.my_profile);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.VISIBLE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 2) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(MessageSuffererFragment.newInstance(""), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.messages);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 3) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(MyCaretakerFragment.newInstance(""), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.my_caretaker);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 4) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(NotificationsSuffererFragment.newInstance(""), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.notifications);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 5) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        ((SuffererHomeActivity) mContext).replaceFragment(FaqsSuffererFragment.newInstance(""), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.faq_s);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 6) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                       /* session.logout(mContext);
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("userType","1");
                        mContext.startActivity(intent);*/
                        Constant.logout(mContext);
                    }
                    break;
            }
        }
    }
}
