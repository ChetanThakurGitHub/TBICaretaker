package tbi.com.fragment.caretaker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tbi.com.R;
import tbi.com.session.Session;

public class MyProfileCaretakerFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_for_profileImage, nav_image;
    private TextView tv_for_name, tv_for_email, tv_for_fullName, tv_for_emails, nav_name, nav_email;
    private Session session;

    public MyProfileCaretakerFragment() {
        // Required empty public constructor
    }

    public static MyProfileCaretakerFragment newInstance() {
        return new MyProfileCaretakerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_profile_caretaker, container, false);
        session = new Session(getContext());
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String image = session.getProfileImage();
        if (image != null && !image.equals("")) {
            Picasso.with(getContext()).load(image).into(iv_for_profileImage);
            Picasso.with(getContext()).load(image).into(nav_image);
        }
        tv_for_name.setText(session.getFullName());
        nav_name.setText(session.getFullName());
        tv_for_fullName.setText(session.getFullName());
        tv_for_email.setText(session.getEmail());
        nav_email.setText(session.getEmail());
        tv_for_emails.setText(session.getEmail());
    }

    private void initView(View view) {
        iv_for_profileImage = view.findViewById(R.id.iv_for_profileImage);
        tv_for_name = view.findViewById(R.id.tv_for_name);
        tv_for_email = view.findViewById(R.id.tv_for_email);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        tv_for_emails = view.findViewById(R.id.tv_for_emails);
        RelativeLayout mainLayout = view.findViewById(R.id.mainLayout);
        view.findViewById(R.id.layout_for_changePassword).setOnClickListener(this);
        nav_image = getActivity().findViewById(R.id.iv_for_profileImage);
        nav_name = getActivity().findViewById(R.id.tv_for_name);
        nav_email = getActivity().findViewById(R.id.tv_for_email);
        mainLayout.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
    }

}
