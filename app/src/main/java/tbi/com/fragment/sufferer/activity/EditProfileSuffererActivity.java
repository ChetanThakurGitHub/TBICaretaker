package tbi.com.fragment.sufferer.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
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

public class EditProfileSuffererActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_for_calender, iv_for_menu, iv_for_backIco, iv_for_edit, iv_for_more, iv_for_delete, iv_profile_image;
    private TextView tv_for_tittle, tv_for_name, tv_for_email, et_for_age, et_for_gender, et_for_bloodGroup;
    private RelativeLayout mainLayout;
    private Bitmap profileImageBitmap;
    private Session session;
    private EditText et_for_fullName, et_for_emails, et_for_contactNo, et_for_weight, et_for_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_sufferer);
        session = new Session(this);
        initView();
        iv_for_calender.setVisibility(View.GONE);
        iv_for_menu.setVisibility(View.GONE);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_edit.setVisibility(View.GONE);
        iv_for_more.setVisibility(View.GONE);
        iv_for_delete.setVisibility(View.GONE);
        tv_for_tittle.setText(R.string.edit_profile);
        setData();
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
        et_for_contactNo.setText(session.getContact());
        if (!session.getAge().equals("")) et_for_age.setText(session.getAge());
        else et_for_age.setText(R.string.na);
        if (!session.getBloadGroup().equals("")) et_for_bloodGroup.setText(session.getBloadGroup());
        else et_for_bloodGroup.setText(R.string.na);
        et_for_weight.setText(session.getWeight());
        et_for_height.setText(session.getHeight());
        if (!session.getGender().equals("")) et_for_gender.setText(session.getGender());
        else et_for_gender.setText(R.string.na);
    }

    private void initView() {
        iv_for_calender = findViewById(R.id.iv_for_calender);
        iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_more = findViewById(R.id.iv_for_more);
        iv_for_delete = findViewById(R.id.iv_for_delete);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        mainLayout = findViewById(R.id.mainLayout);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        et_for_fullName = findViewById(R.id.et_for_fullName);
        et_for_emails = findViewById(R.id.et_for_emails);
        et_for_age = findViewById(R.id.et_for_age);
        et_for_bloodGroup = findViewById(R.id.et_for_bloodGroup);
        et_for_contactNo = findViewById(R.id.et_for_contactNo);
        et_for_weight = findViewById(R.id.et_for_weight);
        et_for_height = findViewById(R.id.et_for_height);
        et_for_gender = findViewById(R.id.et_for_gender);
        tv_for_name = findViewById(R.id.tv_for_name);
        tv_for_email = findViewById(R.id.tv_for_email);
        findViewById(R.id.layout_for_changePassword).setOnClickListener(this);
        findViewById(R.id.iv_profile_image).setOnClickListener(this);
        findViewById(R.id.btn_for_update).setOnClickListener(this);
        iv_for_backIco.setOnClickListener(this);
        et_for_age.setOnClickListener(this);
        et_for_gender.setOnClickListener(this);
        et_for_bloodGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.layout_for_changePassword:
                DailogView dailogView = new DailogView();
                dailogView.changePasswordDailog(this);
                break;
            case R.id.iv_profile_image:
                selectImage();
                break;
            case R.id.btn_for_update:
                validation();
                break;
            case R.id.et_for_age:
                ageDailog();
                break;
            case R.id.et_for_gender:
                genderDailog();
                break;
            case R.id.et_for_bloodGroup:
                DailogView dailogView1 = new DailogView();
                dailogView1.bloodGroupDailog(this, et_for_bloodGroup);
                break;
        }
    }

    private void selectImage() {

        final CharSequence[] items = {getString(R.string.text_take_photo), getString(R.string.text_chose_gellery), getString(R.string.text_cancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileSuffererActivity.this);
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
            editProfileAPI(fullName, email);
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
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            String updatedRecords = jsonObject.getString("updatedRecords");
                            UserFullDetail userFullDetail = new Gson().fromJson(updatedRecords, UserFullDetail.class);
                            userFullDetail.password = session.getPassword();
                            session.setEmailR(userFullDetail.email);
                            session.createSession(userFullDetail);
                            setData();
                            Toast.makeText(EditProfileSuffererActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish();

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
                    Constant.errorHandle(error, EditProfileSuffererActivity.this);
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
                    params.put("userType", "1");
                    params.put("age", et_for_age.getText().toString().trim());
                    params.put("blood_group", et_for_bloodGroup.getText().toString().trim());
                    params.put("weight", et_for_weight.getText().toString().trim());
                    params.put("height", et_for_height.getText().toString().trim());
                    params.put("gender", et_for_gender.getText().toString().trim());
                    params.put("contact_number", et_for_contactNo.getText().toString().trim());
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
            VolleySingleton.getInstance(EditProfileSuffererActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    public void ageDailog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_age_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);

        numberPicker.setMaxValue(0);
        numberPicker.setMaxValue(100);

        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                et_for_age.setText(newVal + " Yr");
            }
        });

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!session.getAge().equals("")) {
            String age = session.getAge();
            String best = age.replace(" Yr", "");
            numberPicker.setValue(Integer.parseInt(best));
        }
        btn_for_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void genderDailog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_age_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        et_for_gender.setText(R.string.male);

        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        TextView tv_for_yr = dialog.findViewById(R.id.tv_for_yr);
        TextView txt = dialog.findViewById(R.id.txt);
        tv_for_yr.setVisibility(View.GONE);

        numberPicker.setMaxValue(0);
        numberPicker.setMaxValue(2);
        numberPicker.setDisplayedValues(new String[]{"Male", "Female", "Other"});
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0) {
                    et_for_gender.setText("Male");
                } else if (newVal == 1) {
                    et_for_gender.setText("Female");
                } else if (newVal == 2) {
                    et_for_gender.setText("Other");
                }

            }
        });

        txt.setText("Gender");

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_for_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
