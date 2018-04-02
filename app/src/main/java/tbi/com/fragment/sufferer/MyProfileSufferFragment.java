package tbi.com.fragment.sufferer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tbi.com.R;
import tbi.com.session.Session;


public class MyProfileSufferFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_for_profileImage, nav_image;
    private TextView tv_for_name, tv_for_email, tv_for_fullName, tv_for_emails, tv_for_contactNo, tv_for_age,
            tv_for_bloodGroup, tv_for_weight, tv_for_height, tv_for_gender, nav_name, nav_email;
    private Session session;

    public MyProfileSufferFragment() {
        // Required empty public constructor
    }

    public static MyProfileSufferFragment newInstance(String param1) {
        MyProfileSufferFragment fragment = new MyProfileSufferFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_profile_sufferer, container, false);
        initView(view);
        session = new Session(getContext());

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
        if (!session.getContact().equals("")) tv_for_contactNo.setText(session.getContact());
        if (!session.getAge().equals("")) tv_for_age.setText(session.getAge());
        if (!session.getBloadGroup().equals("")) tv_for_bloodGroup.setText(session.getBloadGroup());
        if (!session.getWeight().equals("")) tv_for_weight.setText(session.getWeight());
        if (!session.getHeight().equals("")) tv_for_height.setText(session.getHeight());
        if (!session.getGender().equals("")) tv_for_gender.setText(session.getGender());
    }

    private void initView(View view) {
        iv_for_profileImage = view.findViewById(R.id.iv_for_profileImage);
        tv_for_name = view.findViewById(R.id.tv_for_name);
        tv_for_email = view.findViewById(R.id.tv_for_email);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        tv_for_emails = view.findViewById(R.id.tv_for_emails);
        tv_for_contactNo = view.findViewById(R.id.tv_for_contactNo);
        tv_for_age = view.findViewById(R.id.tv_for_age);
        tv_for_bloodGroup = view.findViewById(R.id.tv_for_bloodGroup);
        tv_for_weight = view.findViewById(R.id.tv_for_weight);
        tv_for_height = view.findViewById(R.id.tv_for_height);
        tv_for_gender = view.findViewById(R.id.tv_for_gender);
        view.findViewById(R.id.mainLayout).setOnClickListener(this);
        view.findViewById(R.id.layout_for_changePassword).setOnClickListener(this);
        nav_image = getActivity().findViewById(R.id.iv_for_profileImage);
        nav_name = getActivity().findViewById(R.id.tv_for_name);
        nav_email = getActivity().findViewById(R.id.tv_for_email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_changePassword:
                break;
        }
    }
}
