package tbi.org.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tbi.org.R;
import tbi.org.activity.main_activity.SuffererHomeActivity;
import tbi.org.chat.fragment.MessageSuffererFragment;
import tbi.org.fragment.sufferer.FaqsSuffererFragment;
import tbi.org.fragment.sufferer.MyCaretakerFragment;
import tbi.org.fragment.sufferer.MyProfileSufferFragment;
import tbi.org.fragment.sufferer.NotificationsSuffererFragment;
import tbi.org.fragment.sufferer.ReminderSuffererFragment;
import tbi.org.model.NavigationListModel;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class SuffererNavigationAdapter extends RecyclerView.Adapter<SuffererNavigationAdapter.ViewHolder> {
    public int lastclick = -1;
    private List<NavigationListModel> navigationList;
    private SuffererHomeActivity mContext;
    private Session session;

    public SuffererNavigationAdapter(ArrayList<NavigationListModel> navigationList, SuffererHomeActivity mContext) {
        this.navigationList = navigationList;
        this.mContext = mContext;
        session = new Session(mContext);
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

        if (position == 2) {
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

    void myCaretakerAPI(final int adapterPosition) {

        if (Utils.isNetworkAvailable(mContext)) {
            final Dialog pDialog = new Dialog(mContext);
            Constant.myDialog(mContext, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "myCaretaker", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {

                            lastclick = adapterPosition;
                            notifyDataSetChanged();
                            mContext.replaceFragment(MessageSuffererFragment.newInstance(), true, R.id.framlayout);
                            mContext.drawer.closeDrawers();
                            mContext.tv_for_tittle.setText(R.string.messages);
                            mContext.iv_for_calender.setVisibility(View.GONE);
                            mContext.iv_for_menu.setVisibility(View.VISIBLE);
                            mContext.iv_for_backIco.setVisibility(View.GONE);
                            mContext.iv_for_edit.setVisibility(View.GONE);
                            mContext.iv_for_more.setVisibility(View.GONE);
                            mContext.iv_for_block.setVisibility(View.VISIBLE);
                            mContext.iv_for_deleteChat.setVisibility(View.VISIBLE);
                            mContext.iv_for_delete.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(mContext, "You are not connect with any caretaker to have chat with, first add caretaker", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(mContext, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_for_image;
        private TextView tv_for_nameTittle, tv_for_messageCount;
        private View view_for_click, view_for_line;
        private LinearLayout layout_for_item;

        ViewHolder(View itemView) {
            super(itemView);

            iv_for_image = itemView.findViewById(R.id.iv_for_image);
            tv_for_nameTittle = itemView.findViewById(R.id.tv_for_nameTittle);
            view_for_click = itemView.findViewById(R.id.view_for_click);
            layout_for_item = itemView.findViewById(R.id.layout_for_item);
            view_for_line = itemView.findViewById(R.id.view_for_line);
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
                        mContext.replaceFragment(ReminderSuffererFragment.newInstance(), false, R.id.framlayout);
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
                        mContext.replaceFragment(MyProfileSufferFragment.newInstance(), true, R.id.framlayout);
                        mContext.drawer.closeDrawers();
                        mContext.tv_for_tittle.setText(R.string.my_profile);
                        mContext.iv_for_calender.setVisibility(View.GONE);
                        mContext.iv_for_menu.setVisibility(View.VISIBLE);
                        mContext.iv_for_backIco.setVisibility(View.GONE);
                        mContext.iv_for_edit.setVisibility(View.VISIBLE);
                        mContext.iv_for_more.setVisibility(View.GONE);
                        mContext.iv_for_delete.setVisibility(View.GONE);
                    } else if (getAdapterPosition() == 2) {

                        myCaretakerAPI(getAdapterPosition());


                    } else if (getAdapterPosition() == 3) {
                        lastclick = getAdapterPosition();
                        notifyDataSetChanged();
                        mContext.replaceFragment(MyCaretakerFragment.newInstance(), true, R.id.framlayout);
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
                        mContext.replaceFragment(NotificationsSuffererFragment.newInstance(), true, R.id.framlayout);
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
                        mContext.replaceFragment(FaqsSuffererFragment.newInstance(), true, R.id.framlayout);
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
                        Constant.logout(mContext);
                    }
                    break;
            }
        }
    }
}
