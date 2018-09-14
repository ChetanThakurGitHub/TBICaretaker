package tbi.org.fragment.caretaker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tbi.org.R;
import tbi.org.activity.main_activity.CaretakerHomeActivity;
import tbi.org.model.MySuffererList;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class MySuffererFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mainLayout;
    private TextView tv_for_age, tv_for_bloodGroup, tv_for_weight, tv_for_height, tv_for_gender, tv_for_name, tv_for_email;
    private ImageView iv_for_profileImage;
    private Session session;

    public MySuffererFragment() {
        // Required empty public constructor
    }

    public static MySuffererFragment newInstance() {
        return new MySuffererFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_sufferer, container, false);
        session = new Session(getContext());
        initView(view);
        mySuffererAPI();
        return view;
    }

    private void initView(View view) {
        mainLayout = view.findViewById(R.id.mainLayout);
        tv_for_age = view.findViewById(R.id.tv_for_age);
        tv_for_bloodGroup = view.findViewById(R.id.tv_for_bloodGroup);
        tv_for_weight = view.findViewById(R.id.tv_for_weight);
        tv_for_height = view.findViewById(R.id.tv_for_height);
        tv_for_gender = view.findViewById(R.id.tv_for_gender);
        iv_for_profileImage = view.findViewById(R.id.iv_for_profileImage);
        tv_for_name = view.findViewById(R.id.tv_for_name);
        tv_for_email = view.findViewById(R.id.tv_for_email);
        view.findViewById(R.id.btn_for_remove).setOnClickListener(this);
        mainLayout.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void mySuffererAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "mySufferer", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            String mySufferer = jsonObject.getString("mySufferer");
                            MySuffererList mySuffererList = new Gson().fromJson(mySufferer, MySuffererList.class);
                            String image = mySuffererList.profileImage;
                            if (image != null && !image.equals("")) {
                                Picasso.with(getContext()).load(mySuffererList.profileImage).into(iv_for_profileImage);
                            }
                            tv_for_name.setText(mySuffererList.name);
                            tv_for_email.setText(mySuffererList.email);

                            tv_for_age.setText(mySuffererList.age.equals("") ? getResources().getString(R.string.na) : mySuffererList.age);
                            tv_for_bloodGroup.setText(mySuffererList.blood_group.equals("") ? getResources().getString(R.string.na) : mySuffererList.blood_group);

                            if (mySuffererList.weight.equals("")) {

                                tv_for_weight.setText(R.string.na);
                            } else {
                                tv_for_weight.setText(mySuffererList.weight);
                            }
                            if (mySuffererList.height.equals("")) {

                                tv_for_height.setText(R.string.na);
                            } else {
                                tv_for_height.setText(mySuffererList.height);
                            }
                            if (mySuffererList.gender.equals("")) {
                                tv_for_gender.setText(R.string.na);
                            } else {
                                tv_for_gender.setText(mySuffererList.gender);
                            }

                        } else {
                            ((CaretakerHomeActivity) getContext()).replaceFragment(AddSuffererFragment.newInstance(), true, R.id.framlayout);
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Constant.errorHandle(error, getActivity());
                    Constant.snackbar(mainLayout, networkResponse + "");
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_for_remove:
                removeSuffererAPI();
                break;
        }
    }

    public void removeSuffererAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "removeUser", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            ((CaretakerHomeActivity) getActivity()).replaceFragment(ReminderCaretakerFragment.newInstance(), false, R.id.framlayout);
                            session.setLogin("0");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            Constant.snackbar(mainLayout, message);
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Constant.snackbar(mainLayout, networkResponse + "");
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

}
