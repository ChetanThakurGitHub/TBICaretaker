package tbi.com.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.StatusBarUtil;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_for_email;
    private RelativeLayout mainLayout;
    private View view_for_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this);
        setContentView(R.layout.activity_forgot_pass);
        Session session = new Session(this);
        initView();

        if (session.isSoftKey()) {
            view_for_mobile.setVisibility(View.VISIBLE);
        } else {
            view_for_mobile.setVisibility(View.GONE);
        }
    }

    private void initView() {
        findViewById(R.id.btn_for_resetPass).setOnClickListener(this);
        et_for_email = findViewById(R.id.et_for_email);
        mainLayout = findViewById(R.id.mainLayout);
        view_for_mobile = findViewById(R.id.view_for_mobile);
    }

    private void validation() {
        String email = et_for_email.getText().toString().trim();
        if (email.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.email_v));
            et_for_email.requestFocus();
        } else if (Utils.Validationemail(email, et_for_email)) {
            //write here your code
            forgotPasswordAPI(email);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_for_resetPass:
                validation();
                break;
        }
    }

    public void forgotPasswordAPI(final String email) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITHOUT_LOGIN + "forgotPassword", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(ForgotPassActivity.this, message, Toast.LENGTH_SHORT).show();

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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ForgotPassActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }
}
