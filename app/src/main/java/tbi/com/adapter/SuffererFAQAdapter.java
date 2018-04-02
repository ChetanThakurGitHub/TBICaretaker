package tbi.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tbi.com.R;
import tbi.com.model.FaqList;

public class SuffererFAQAdapter extends RecyclerView.Adapter<SuffererFAQAdapter.ViewHolder> {
    public List<FaqList> faqLists;

    public SuffererFAQAdapter(ArrayList<FaqList> faqLists, Context mContext) {
        this.faqLists = faqLists;
        Context mContext1 = mContext;
    }

    @Override
    public SuffererFAQAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.faqs_list_sufferer_layout, parent, false);
        return new SuffererFAQAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuffererFAQAdapter.ViewHolder holder, final int position) {
        final FaqList faqList = faqLists.get(position);
        holder.tv_for_faqTitle.setText(faqList.title);
        holder.tv_for_descriptionFAQ.setText(faqList.description);
    }

    @Override
    public int getItemCount() {
        return faqLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_for_faqTitle, tv_for_descriptionFAQ;

        ViewHolder(View itemView) {
            super(itemView);
            tv_for_faqTitle = itemView.findViewById(R.id.tv_for_faqTitle);
            tv_for_descriptionFAQ = itemView.findViewById(R.id.tv_for_descriptionFAQ);
        }
    }

}