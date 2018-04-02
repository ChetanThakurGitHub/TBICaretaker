package tbi.com.fragment.caretaker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.activity.main_activity.CaretakerHomeActivity;
import tbi.com.fragment.caretaker.activity.AddReminderActivity;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;


public class AddSuffererFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AddReminderActivity.class.getSimpleName();
    private EditText et_for_email;
    private RelativeLayout mainLayout;
    private Session session;

    public AddSuffererFragment() {
    }

    public static AddSuffererFragment newInstance(String param1) {
        AddSuffererFragment fragment = new AddSuffererFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_sufferer, container, false);
        initView(view);
        session = new Session(getContext());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initView(View view) {
        et_for_email = view.findViewById(R.id.et_for_email);
        mainLayout = view.findViewById(R.id.mainLayout);
        view.findViewById(R.id.btn_for_addSufferer).setOnClickListener(this);
        mainLayout.setOnClickListener(this);
    }

    private void validation() {
        String email = et_for_email.getText().toString().trim();
        if (email.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.email_v));
            et_for_email.requestFocus();
        } else if (Utils.Validationemail(email, mainLayout)) {
            //write here your code
            addCaretakerAPI(email);
        }
    }

    public void addCaretakerAPI(final String email) {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "addSufferer", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Constant.snackbar(mainLayout, message);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            session.setLogin("1");
                            ((CaretakerHomeActivity) getActivity()).replaceFragment(ReminderCaretakerFragment.newInstance(""), false, R.id.framlayout);
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest, TAG);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_for_addSufferer:
                validation();
                break;
        }
    }


}
