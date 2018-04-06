package tbi.com.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.io.IOException;
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
import tbi.com.vollyemultipart.AppHelper;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_for_fullname, et_for_email, et_for_password, et_for_cpassword;
    private Session session;
    private ImageView iv_profile_image;
    private Bitmap profileImageBitmap;
    private String userType = "";
    private RelativeLayout mainLayout;
    private View view_for_mobile;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this);
        setContentView(R.layout.activity_registration);
        initView();
        session = new Session(this);

        if (session.isSoftKey()) {
            view_for_mobile.setVisibility(View.VISIBLE);
        } else {
            view_for_mobile.setVisibility(View.GONE);
        }

        userType = getIntent().getStringExtra("userType");
    }

    private void initView() {
        findViewById(R.id.layout_for_signup).setOnClickListener(this);
        et_for_fullname = findViewById(R.id.et_for_fullname);
        et_for_email = findViewById(R.id.et_for_email);
        mainLayout = findViewById(R.id.mainLayout);
        et_for_password = findViewById(R.id.et_for_password);
        et_for_cpassword = findViewById(R.id.et_for_cpassword);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        view_for_mobile = findViewById(R.id.view_for_mobile);
        findViewById(R.id.btn_for_signup).setOnClickListener(this);
        findViewById(R.id.layout_for_userImg).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_signup:
                onBackPressed();
                break;
            case R.id.btn_for_signup:
                validation();
                break;
            case R.id.layout_for_userImg:
                selectImage();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationActivity.this);
        alert.setTitle(getString(R.string.text_add_photo));
        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.text_take_photo))) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, Constant.CAMERA);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, Constant.CAMERA);
                    }
                } else if (items[item].equals(getString(R.string.text_chose_gellery))) {

                    if (Build.VERSION.SDK_INT >= 23) {

                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, Constant.GALLERY);
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, Constant.GALLERY);
                    }
                } else if (items[item].equals(getString(R.string.text_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (profileImageBitmap != null) {
                    iv_profile_image.setPadding(0, 0, 0, 0);
                    iv_profile_image.setImageBitmap(profileImageBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                profileImageBitmap = (Bitmap) data.getExtras().get("data");
                if (profileImageBitmap != null) {
                    iv_profile_image.setPadding(0, 0, 0, 0);
                    iv_profile_image.setImageBitmap(profileImageBitmap);
                }
            }
        }
    }

    private void validation() {
        String fullName = et_for_fullname.getText().toString().trim();
        String email = et_for_email.getText().toString().trim();
        String password = et_for_password.getText().toString().trim();
        String cpassword = et_for_cpassword.getText().toString().trim();
        if (fullName.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.fullname_v));
            et_for_fullname.requestFocus();
        } else if (fullName.length() < 3) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.fullname_required));
            et_for_fullname.requestFocus();
        } else if (email.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.email_v));
            et_for_email.requestFocus();
        } else if (!Utils.Validationemail(et_for_email.getText().toString(), mainLayout)) {
        } else if (password.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.password_v));
            et_for_password.requestFocus();
        } else if (password.length() < 6) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.password_required));
            et_for_password.requestFocus();
        } else if (cpassword.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.cpassword_v));
            et_for_cpassword.requestFocus();
        } else if (cpassword.length() < 6) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.cpassword_required));
            et_for_cpassword.requestFocus();
        } else if (!password.equals(cpassword)) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.password_match));
        } else {
            et_for_cpassword.requestFocus();
            doRegistration(fullName, email, password, cpassword);
        }
    }

    private void doRegistration(final String fullName, final String email, final String password, final String cpassword) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITHOUT_LOGIN + "userRegistration", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            String userDetail = jsonObject.getString("userDetail");

                            UserFullDetail userFullDetail = new Gson().fromJson(userDetail, UserFullDetail.class);
                            userFullDetail.password = password;

                            firebaseLogin(userFullDetail);

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
                    params.put("name", fullName);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("confirm_password", cpassword);
                    params.put("userType", userType);
                    params.put("deviceToken", FirebaseInstanceId.getInstance().getToken());

                    if (profileImageBitmap == null) {
                        params.put("profileImage", "");
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (profileImageBitmap != null) {
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                    }
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RegistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
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
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
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

                            session.logoutMyPre();

                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(RegistrationActivity.this, SuffererHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(RegistrationActivity.this, CaretakerHomeActivity.class);
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
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            firebaseLogin(userDetails);

                        } else {

                            writeToDBProfiles(firebaseData);

                            session.createSession(userDetails);

                            session.logoutMyPre();

                            if (userDetails.userType.equals("1")) {
                                Intent intent = new Intent(RegistrationActivity.this, SuffererHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(RegistrationActivity.this, CaretakerHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
}
