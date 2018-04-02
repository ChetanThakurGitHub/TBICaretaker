package tbi.com.fragment.sufferer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import tbi.com.R;
import tbi.com.model.MySuffererList;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class MyCaretakerFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_for_profileImage;
    private TextView tv_for_name, tv_for_email, tv_for_fullName, tv_for_emailId, tv_for_noCaretaker;
    private Session session;
    private RelativeLayout mainLayout;

    public MyCaretakerFragment() {
        // Required empty public constructor
    }

    public static MyCaretakerFragment newInstance(String param1) {
        MyCaretakerFragment fragment = new MyCaretakerFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_caretaker, container, false);
        initView(view);
        session = new Session(getContext());
        myCaretakerAPI();
        return view;
    }

    private void initView(View view) {
        iv_for_profileImage = view.findViewById(R.id.iv_for_profileImage);
        tv_for_name = view.findViewById(R.id.tv_for_name);
        tv_for_email = view.findViewById(R.id.tv_for_email);
        tv_for_fullName = view.findViewById(R.id.tv_for_fullName);
        tv_for_emailId = view.findViewById(R.id.tv_for_emailId);
        mainLayout = view.findViewById(R.id.mainLayout);
        tv_for_noCaretaker = view.findViewById(R.id.tv_for_noCaretaker);
        view.findViewById(R.id.btn_for_remove).setOnClickListener(this);
        mainLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_for_remove:
                removeCaretakerAPI();
                break;
        }
    }

    public void myCaretakerAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "myCaretaker", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            String myCaretaker = jsonObject.getString("myCaretaker");
                            MySuffererList mySuffererList = new Gson().fromJson(myCaretaker, MySuffererList.class);
                            String image = mySuffererList.profileImage;
                            if (image != null && !image.equals("")) {
                                Picasso.with(getContext()).load(mySuffererList.profileImage).into(iv_for_profileImage);
                            }
                            tv_for_name.setText(mySuffererList.name);
                            tv_for_fullName.setText(mySuffererList.name);
                            tv_for_email.setText(mySuffererList.email);
                            tv_for_emailId.setText(mySuffererList.email);
                        } else {
                            Constant.snackbar(mainLayout, message);
                            tv_for_noCaretaker.setVisibility(View.VISIBLE);

                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
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

    public void removeCaretakerAPI() {

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
                            Constant.snackbar(mainLayout, message);
                            tv_for_noCaretaker.setVisibility(View.VISIBLE);

                        } else {
                            Constant.snackbar(mainLayout, message);
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
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
                    Map<String, String> headers = new HashMap<String, String>();
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
