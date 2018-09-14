package tbi.org.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tbi.org.R;
import tbi.org.custom_view.DailogView;
import tbi.org.fragment.caretaker.FaqsCaretakerFragment;
import tbi.org.model.FaqList;

public class CaretakerFAQAdapter extends RecyclerView.Adapter<CaretakerFAQAdapter.ViewHolder> {
    public Boolean isShow = false;
    public FaqsCaretakerFragment fragment;
    private List<FaqList> faqLists;
    private Context mContext;

    public CaretakerFAQAdapter(ArrayList<FaqList> faqLists, Context mContext, FaqsCaretakerFragment fragment) {
        this.faqLists = faqLists;
        this.mContext = mContext;
        this.fragment = fragment;
    }

    @Override
    public CaretakerFAQAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.faqs_list_layout, parent, false);
        return new CaretakerFAQAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CaretakerFAQAdapter.ViewHolder holder, final int position) {
        final FaqList faqList = faqLists.get(position);

        holder.tv_for_faqTitle.setText(faqList.title);
        holder.tv_for_descriptionFAQ.setText(faqList.description);

        holder.iv_for_check.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return faqLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_for_faqTitle, tv_for_descriptionFAQ;
        private ImageView iv_for_check;

        ViewHolder(View itemView) {
            super(itemView);

            tv_for_faqTitle = itemView.findViewById(R.id.tv_for_faqTitle);
            tv_for_descriptionFAQ = itemView.findViewById(R.id.tv_for_descriptionFAQ);
            iv_for_check = itemView.findViewById(R.id.iv_for_check);
            iv_for_check.setOnClickListener(this);
            itemView.findViewById(R.id.iv_for_editSwipe).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FaqList faqList = faqLists.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.iv_for_check:

                    if (!faqList.checkBox) {
                        iv_for_check.setBackgroundResource(R.drawable.ic_checked_faq);
                        faqList.checkBox = true;
                    } else {
                        iv_for_check.setBackgroundResource(R.drawable.ic_unchecked_faq);
                        faqList.checkBox = false;
                    }
                    break;
                case R.id.iv_for_editSwipe:
                    DailogView dailogView = new DailogView();
                    dailogView.updateFaqDailog(mContext, faqList, fragment);
                    break;
            }
        }
    }

}
