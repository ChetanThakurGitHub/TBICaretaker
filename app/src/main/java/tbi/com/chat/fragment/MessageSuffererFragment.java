package tbi.com.chat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tbi.com.R;


public class MessageSuffererFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    public MessageSuffererFragment() {
        // Required empty public constructor
    }

    public static MessageSuffererFragment newInstance(String param1) {
        MessageSuffererFragment fragment = new MessageSuffererFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_sufferer, container, false);
    }


}
