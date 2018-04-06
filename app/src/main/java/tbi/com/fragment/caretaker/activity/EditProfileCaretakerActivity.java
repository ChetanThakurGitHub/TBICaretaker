package tbi.com.fragment.caretaker.activity;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.custom_view.DailogView;
import tbi.com.model.UserFullDetail;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.AppHelper;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class EditProfileCaretakerActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap profileImageBitmap;
    private ImageView iv_profile_image;
    private RelativeLayout mainLayout;
    private TextView tv_for_name, tv_for_email, tv_for_tittle;
    private EditText et_for_fullName, et_for_emails;
    private Session session;
    private ImageView iv_for_calender, iv_for_menu, iv_for_backIco, iv_for_edit, iv_for_more, iv_for_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_caretaker);
        initView();
        session = new Session(this);
        setData();
        iv_for_calender.setVisibility(View.GONE);
        iv_for_menu.setVisibility(View.GONE);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_edit.setVisibility(View.GONE);
        iv_for_more.setVisibility(View.GONE);
        iv_for_delete.setVisibility(View.GONE);
        tv_for_tittle.setText(R.string.edit_profile);
        iv_for_backIco.setOnClickListener(this);
    }

    private void initView() {
        findViewById(R.id.layout_for_userImg).setOnClickListener(this);
        findViewById(R.id.btn_for_update).setOnClickListener(this);
        findViewById(R.id.layout_for_changePassword).setOnClickListener(this);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        mainLayout = findViewById(R.id.mainLayout);
        et_for_fullName = findViewById(R.id.et_for_fullName);
        et_for_emails = findViewById(R.id.et_for_emails);
        tv_for_name = findViewById(R.id.tv_for_name);
        tv_for_email = findViewById(R.id.tv_for_email);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        iv_for_calender = findViewById(R.id.iv_for_calender);
        iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_more = findViewById(R.id.iv_for_more);
        iv_for_delete = findViewById(R.id.iv_for_delete);

    }

    private void setData() {
        String image = session.getProfileImage();
        if (image != null && !image.equals("")) {
            Picasso.with(this).load(image).into(iv_profile_image);
        }
        tv_for_name.setText(session.getFullName());
        et_for_fullName.setText(session.getFullName());
        tv_for_email.setText(session.getEmail());
        et_for_emails.setText(session.getEmail());
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.layout_for_userImg:
                selectImage();
                break;

            case R.id.btn_for_update:
                validation();
                break;

            case R.id.layout_for_changePassword:
                DailogView dailogView = new DailogView();
                dailogView.changePasswordDailog(this);
                break;

            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }

    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileCaretakerActivity.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void validation() {
        String fullName = et_for_fullName.getText().toString().trim();
        String email = et_for_emails.getText().toString().trim();

        if (fullName.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.fullname_v));
            et_for_fullName.requestFocus();
        } else if (fullName.length() < 3) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.fullname_required));
            et_for_fullName.requestFocus();
        } else if (email.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.email_v));
            et_for_emails.requestFocus();
        } else if (!Utils.Validationemail(et_for_emails.getText().toString(), mainLayout)) {
        } else {
            if (profileImageBitmap != null | !fullName.equals(session.getFullName()) | !email.equals(session.getEmail())) {
                editProfileAPI(fullName, email);
            }
        }
    }

    private void editProfileAPI(final String fullName, final String email) {

        if (Utils.isNetworkAvailable(this)) {

            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "profileUpdate", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            String updatedRecords = jsonObject.getString("updatedRecords");
                            UserFullDetail userFullDetail = new Gson().fromJson(updatedRecords, UserFullDetail.class);
                            userFullDetail.password = session.getPasswordR();
                            session.setEmailR(userFullDetail.email);
                            session.createSession(userFullDetail);
                            setData();

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
                    Constant.errorHandle(error, EditProfileCaretakerActivity.this);
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("full_name", fullName);
                    params.put("email", email);
                    params.put("userType", "2");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("authToken", session.getAuthToken());
                    return headers;
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
            VolleySingleton.getInstance(EditProfileCaretakerActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }
}
