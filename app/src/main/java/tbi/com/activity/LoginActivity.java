package tbi.com.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.activity.main_activity.CaretakerHomeActivity;
import tbi.com.activity.main_activity.SuffererHomeActivity;
import tbi.com.chat.model.FirebaseData;
import tbi.com.model.UserFullDetail;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.StatusBarUtil;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Session session;
    private String userType = "";
    private boolean checkBox = false;
    private ImageView iv_uncheck;
    private EditText et_for_email, et_for_password;
    private RelativeLayout mainLayout;
    private View view_for_mobile;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this);
        setContentView(R.layout.activity_login);
        initView();

        session = new Session(this);

        if (session.isSoftKey()) {
            view_for_mobile.setVisibility(View.VISIBLE);
        } else {
            view_for_mobile.setVisibility(View.GONE);
        }

        userType = getIntent().getStringExtra("userType");
        if (session.getUserTypeR().equalsIgnoreCase(userType)) {
            if (!session.getEmailR().equals("") && session.getEmailR() != null) {
                checkBox = true;
                et_for_email.setText(session.getEmailR());
                et_for_password.setText(session.getPasswordR());
                iv_uncheck.setBackgroundResource(R.drawable.ic_checked2);
            }
        }
    }

    private void initView() {
        findViewById(R.id.layout_for_signup).setOnClickListener(this);
        findViewById(R.id.layout_for_remember).setOnClickListener(this);
        findViewById(R.id.btn_for_login).setOnClickListener(this);
        findViewById(R.id.tv_for_forgotPassword).setOnClickListener(this);
        iv_uncheck = findViewById(R.id.iv_uncheck);
        et_for_email = findViewById(R.id.et_for_email);
        et_for_password = findViewById(R.id.et_for_password);
        mainLayout = findViewById(R.id.mainLayout);
        view_for_mobile = findViewById(R.id.view_for_mobile);
    }

    private void validation() {
        String email = et_for_email.getText().toString().trim();
        String password = et_for_password.getText().toString().trim();
        if (email.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.email_v));
            et_for_email.requestFocus();
        } else if (!Utils.Validationemail(et_for_email.getText().toString(), mainLayout)) {
        } else if (password.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.password_v));
            et_for_password.requestFocus();
        } else if (password.length() < 6) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.password_required));
            et_for_password.requestFocus();
        } else {
            doLogin(email, password);
        }
    }

    public void doLogin(final String email, final String password) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITHOUT_LOGIN + "userLogin", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");

                            UserFullDetail userFullDetail = new Gson().fromJson(userDetail.toString(), UserFullDetail.class);
                            userFullDetail.password = password;
                            if (userFullDetail.status.equals("1")) {

                                firebaseLogin(userFullDetail);

                            } else {
                                Constant.snackbar(mainLayout, "Your account has been inactivated by admin, please contact to activate");
                            }

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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    if (FirebaseInstanceId.getInstance().getToken() != null) {
                        params.put("email", email);
                        params.put("password", password);
                        params.put("userType", userType);
                        params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                    } else {
                        Constant.snackbar(mainLayout, "Something is wrong");
                    }

                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, UserSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_remember:
                if (!checkBox) {
                    iv_uncheck.setBackgroundResource(R.drawable.ic_checked2);
                    checkBox = true;
                } else {
                    iv_uncheck.setBackgroundResource(R.drawable.ic_uncheck);
                    checkBox = false;
                }
                break;
            case R.id.layout_for_signup:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("userType", userType);
                startActivity(intent);
                break;
            case R.id.btn_for_login:
                validation();
                break;
            case R.id.tv_for_forgotPassword:
                intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void writeToDBProfiles(FirebaseData firebaseData) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users/" + firebaseData.uid);
        myRef.setValue(firebaseData);
    }

    private void firebaseLogin(final UserFullDetail userDetails) {

        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@tbi.com";
        final String password = "123456";

        //added
        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.name;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.userId;
        //

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            firebaseRagistration(userDetails);

                        } else {

                            //added
                            writeToDBProfiles(firebaseData);
                            //
                            session.createSession(userDetails);
                            if (!checkBox) {
                                session.logoutMyPre();
                            }
                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, SuffererHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, CaretakerHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    private void firebaseRagistration(final UserFullDetail userDetails) {
        auth = FirebaseAuth.getInstance();

        String id = userDetails.userId;
        final String email = id + "@tbi.com";
        final String password = "123456";

        final FirebaseData firebaseData = new FirebaseData();
        firebaseData.name = userDetails.name;
        firebaseData.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        firebaseData.userType = userDetails.userType;
        firebaseData.uid = userDetails.userId;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails);

                        } else {

                            writeToDBProfiles(firebaseData);

                            session.createSession(userDetails);
                            if (!checkBox) {
                                session.logoutMyPre();
                            }
                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(LoginActivity.this, SuffererHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, CaretakerHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
}
