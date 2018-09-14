package tbi.org.chat.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tbi.org.R;
import tbi.org.chat.model.Chatting;
import tbi.org.session.Session;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chatting> chattings;
    private Context mContext;
    private Session session;
    private Chatting chatting;

    public ChatAdapter(ArrayList<Chatting> chattings, Context mContext) {
        this.chattings = chattings;
        this.mContext = mContext;
        session = new Session(mContext);
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        chatting = chattings.get(viewType);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
        return new ChatAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
        chatting = chattings.get(position);
        String time = null;

        try {
            long timeStamp = (long) chatting.timeStamp;

            DateFormat f = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss.mmm'Z'", Locale.US);
            System.out.println(f.format(timeStamp));

            String CurrentString = f.format(timeStamp);
            int hourOfDay = Integer.parseInt(CurrentString.substring(11, 13));
            int minute = Integer.parseInt(CurrentString.substring(14, 16));

            String status, minutes;

            if (hourOfDay > 12) {
                hourOfDay -= 12;
                status = "PM";
            } else if (hourOfDay == 0) {
                hourOfDay += 12;
                status = "AM";
            } else if (hourOfDay == 12) {
                status = "PM";
            } else {
                status = "AM";
            }

            minutes = (minute < 10) ? "0" + minute : String.valueOf(minute);
            time = hourOfDay + ":" + minutes + " " + status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chatting.uid.equals(session.getUserID())) {
            holder.layout_for_reciver.setVisibility(View.GONE);
            holder.layout_for_sender.setVisibility(View.VISIBLE);
            if (chatting.message.contains("tbicaretaker-e76f6.appspot.com")) {
                holder.tv_for_senderTxt.setVisibility(View.GONE);
                holder.iv_for_sender.setVisibility(View.VISIBLE);

                if (chatting.message != null && !chatting.message.equals("")) {
                    Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.ic_background).into(holder.iv_for_sender);
                } else {
                    Picasso.with(mContext).load(R.drawable.ic_background).fit().into(holder.iv_for_sender);
                }
            } else {
                holder.iv_for_sender.setVisibility(View.GONE);
                holder.tv_for_senderTxt.setVisibility(View.VISIBLE);
                holder.tv_for_senderTxt.setText(chatting.message);
            }
            holder.tv_for_senderTime.setText(time);

        } else {
            holder.layout_for_reciver.setVisibility(View.VISIBLE);
            holder.layout_for_sender.setVisibility(View.GONE);

            if (chatting.message.contains("tbicaretaker-e76f6.appspot.com")) {
                holder.tv_for_reciverTxt.setVisibility(View.GONE);
                holder.iv_for_reciver.setVisibility(View.VISIBLE);

                if (chatting.message != null && !chatting.message.equals("")) {
                    Picasso.with(mContext).load(chatting.message).placeholder(R.drawable.ic_background).into(holder.iv_for_reciver);
                } else {
                    Picasso.with(mContext).load(R.drawable.ic_background).fit().into(holder.iv_for_reciver);
                }
            } else {
                holder.iv_for_reciver.setVisibility(View.GONE);
                holder.tv_for_reciverTxt.setVisibility(View.VISIBLE);
                holder.tv_for_reciverTxt.setText(chatting.message);
            }
            holder.tv_for_reciverTime.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return chattings.size();
    }

    private void zoomImageDialog(Context mContext, int position) {
        final Dialog dialog = new Dialog(this.mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_zoomimage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        PhotoView iv_for_image = dialog.findViewById(R.id.iv_for_image);

        Chatting chat = chattings.get(position);

        if (chat.message != null && !chat.message.equals("")) {
            Picasso.with(mContext).load(chat.message).placeholder(R.drawable.ic_background).into(iv_for_image);
        } else {
            Picasso.with(mContext).load(R.drawable.ic_background).fit().into(iv_for_image);
        }

        dialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layout_for_sender, layout_for_reciver;
        private TextView tv_for_senderTxt, tv_for_senderTime, tv_for_reciverTxt, tv_for_reciverTime;
        private ImageView iv_for_sender, iv_for_reciver;

        ViewHolder(View itemView) {
            super(itemView);

            layout_for_sender = itemView.findViewById(R.id.layout_for_sender);
            layout_for_reciver = itemView.findViewById(R.id.layout_for_reciver);
            tv_for_reciverTime = itemView.findViewById(R.id.tv_for_reciverTime);
            tv_for_senderTime = itemView.findViewById(R.id.tv_for_senderTime);
            tv_for_senderTxt = itemView.findViewById(R.id.tv_for_senderTxt);
            tv_for_reciverTxt = itemView.findViewById(R.id.tv_for_reciverTxt);
            iv_for_sender = itemView.findViewById(R.id.iv_for_sender);
            iv_for_reciver = itemView.findViewById(R.id.iv_for_reciver);

            iv_for_sender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageDialog(mContext, getAdapterPosition());
                }
            });
            iv_for_reciver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageDialog(mContext, getAdapterPosition());
                }
            });
        }
    }
}
