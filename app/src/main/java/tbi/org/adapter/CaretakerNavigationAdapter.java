package tbi.org.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tbi.org.R;
import tbi.org.activity.main_activity.CaretakerHomeActivity;
import tbi.org.fragment.caretaker.AddSuffererFragment;
import tbi.org.fragment.caretaker.FaqsCaretakerFragment;
import tbi.org.chat.fragment.MessageCaretakerFragment;
import tbi.org.fragment.caretaker.MyProfileCaretakerFragment;
import tbi.org.fragment.caretaker.MySuffererFragment;
import tbi.org.fragment.caretaker.NotificationsCaretakerFragment;
import tbi.org.fragment.caretaker.ReminderCaretakerFragment;
import tbi.org.model.NavigationListModel;
import tbi.org.session.Session;

public class CaretakerNavigationAdapter extends RecyclerView.Adapter<CaretakerNavigationAdapter.ViewHolder> {
    public int lastclick = -1;
    private List<NavigationListModel> navigationList;
    private CaretakerHomeActivity mContext;
    private Session session;

    public CaretakerNavigationAdapter(ArrayList<NavigationListModel> navigationList, CaretakerHomeActivity mContext) {
        this.navigationList = navigationList;
        this.mContext = mContext;
        session = new Session(mContext);
    }

    @NonNull
    @Override
    public CaretakerNavigationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_layout, parent, false);
        return new CaretakerNavigationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CaretakerNavigationAdapter.ViewHolder holder, final int position) {
        NavigationListModel navigationListModel = navigationList.get(position);

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

        if (position == 3) {
            FirebaseDatabase.getInstance().getReference().child("massage_count").child(session.getUserID()).child("count").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String count = dataSnapshot.getValue().toString();
                        if (count != null && !count.equals("0")) {
                            holder.tv_for_messageCount.setVisibility(View.VISIBLE);
                            holder.tv_for_messageCount.setText(count);
                        } else {
                            holder.tv_for_messageCount.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            holder.tv_for_messageCount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return navigationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_for_image;
        private TextView tv_for_nameTittle, tv_for_messageCount;
        private View view_for_click;
        private LinearLayout layout_for_item;

        ViewHolder(View itemView) {
            super(itemView);

            iv_for_image = itemView.findViewById(R.id.iv_for_image);
            tv_for_nameTittle = itemView.findViewById(R.id.tv_for_nameTittle);
            view_for_click = itemView.findViewById(R.id.view_for_click);
            layout_for_item = itemView.findViewById(R.id.layout_for_item);
            tv_for_messageCount = itemView.findViewById(R.id.tv_for_messageCount);
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
                        Session session = new Session(mContext);
                        if (session.getLogin().equals("0")) {
                            mContext.replaceFragment(AddSuffererFragment.newInstance(), true, R.id.framlayout);
                            mContext.tv_for_tittle.setText(R.string.add_sufferer);
                            mContext.iv_for_calender.setVisibility(View.GONE);
                            mContext.iv_for_menu.setVisibility(View.VISIBLE);
                            mContext.iv_for_backIco.setVisibility(View.GONE);
                            mContext.iv_for_edit.setVisibility(View.GONE);
                            mContext.iv_for_more.setVisibility(View.GONE);
                            mContext.iv_for_delete.setVisibility(View.GONE);
                        } else {
                            mContext.replaceFragment(MySuffererFragment.newInstance(), true, R.id.framlayout);
                            mContext.tv_for_tittle.setText(R.string.my_sufferer);
                            mContext.iv_for_calender.setVisibility(View.GONE);
                            mContext.iv_for_menu.setVisibility(View.VISIBLE);
                            mContext.iv_for_backIco.setVisibility(View.GONE);
                            mContext.iv_for_edit.setVisibility(View.GONE);
                            mContext.iv_for_more.setVisibility(View.GONE);
                            mContext.iv_for_delete.setVisibility(View.GONE);
                        }
                        mContext.drawer.closeDrawers();
                    } else if (getAdapterPosition() == 1) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        mContext.replaceFragment(ReminderCaretakerFragment.newInstance(), false, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.reminders);
                        mContext.iv_for_calender.setVisibility(View.VISIBLE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 2) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        mContext.replaceFragment(MyProfileCaretakerFragment.newInstance(), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.my_profile);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.VISIBLE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 3) {
                        if ((session.getLogin().equals("0"))) {
                            Toast.makeText(mContext, "You are not connect with any TBI client to have chat with, first add TBI client", Toast.LENGTH_SHORT).show();
                        } else {
                            lastclick = getAdapterPosition();
                            notifyDataSetChanged();
                            mContext.replaceFragment(MessageCaretakerFragment.newInstance(), true, R.id.framlayout);
                            mContext.drawer.closeDrawers();
                            mContext.tv_for_tittle.setText(R.string.messages);
                            mContext.iv_for_calender.setVisibility(View.GONE);
                            mContext.iv_for_menu.setVisibility(View.VISIBLE);
                            mContext.iv_for_backIco.setVisibility(View.GONE);
                            mContext.iv_for_edit.setVisibility(View.GONE);
                            mContext.iv_for_more.setVisibility(View.GONE);
                            mContext.iv_for_delete.setVisibility(View.GONE);
                            mContext.iv_for_block.setVisibility(View.VISIBLE);
                            mContext.iv_for_deleteChat.setVisibility(View.VISIBLE);
                        }
                    } else if (getAdapterPosition() == 4) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        mContext.replaceFragment(NotificationsCaretakerFragment.newInstance(), true, R.id.framlayout);
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
                        mContext.replaceFragment(FaqsCaretakerFragment.newInstance(), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.faq_s);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.GONE);
                        mContext.iv_for_more.setVisibility(View.VISIBLE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

}
