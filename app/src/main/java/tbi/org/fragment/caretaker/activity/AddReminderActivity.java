package tbi.org.fragment.caretaker.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tbi.org.R;
import tbi.org.model.AllReminderList;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class AddReminderActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = AddReminderActivity.class.getSimpleName();
    private ImageView iv_for_backIco, iv_for_calender, iv_for_menu, iv_for_edit, iv_for_more, iv_for_delete;
    private EditText ed_for_tittle, ed_for_description;
    private TextView tv_for_date, tv_for_time, tv_for_tittle, tv_for_disCount, tv_for_titleCount;
    private RelativeLayout mainLayout;
    private Button btn_for_addReminder;
    private AllReminderList editReminder;
    private int mhour = -1;
    private int mminute = -1;
    private int Update = -1;
    private SimpleDateFormat dateFormat, dateFormat2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reminder);
        initView();
        iv_for_backIco.setOnClickListener(this);
        btn_for_addReminder.setOnClickListener(this);

        dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        dateFormat2 = new SimpleDateFormat("dd MMM yyy", Locale.getDefault());

        iv_for_calender.setVisibility(View.GONE);
        iv_for_menu.setVisibility(View.GONE);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_edit.setVisibility(View.GONE);
        iv_for_more.setVisibility(View.GONE);
        iv_for_delete.setVisibility(View.GONE);

        editReminder = (AllReminderList) getIntent().getSerializableExtra("EditReminder");
        if (editReminder != null && !editReminder.title.equals("")) {
            tv_for_tittle.setText(R.string.edit_reminder);
            btn_for_addReminder.setText(R.string.edit_reminder);
            ed_for_tittle.setText(editReminder.title);
            tv_for_date.setText(editReminder.date);
            tv_for_time.setText(editReminder.time);
            ed_for_description.setText(editReminder.description);
        } else {
            tv_for_tittle.setText(R.string.add_reminder);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 100 - s.length();
                tv_for_disCount.setText(String.valueOf(text));

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        ed_for_description.addTextChangedListener(textWatcher);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 50 - s.length();
                tv_for_titleCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        ed_for_tittle.addTextChangedListener(textWatcher);
    }

    private void initView() {
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        ed_for_tittle = findViewById(R.id.ed_for_tittle);
        tv_for_date = findViewById(R.id.tv_for_date);
        tv_for_time = findViewById(R.id.tv_for_time);
        iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_calender = findViewById(R.id.iv_for_calender);
        iv_for_delete = findViewById(R.id.iv_for_delete);
        iv_for_more = findViewById(R.id.iv_for_more);
        mainLayout = findViewById(R.id.mainLayout);
        tv_for_disCount = findViewById(R.id.tv_for_disCount);
        ed_for_description = findViewById(R.id.ed_for_description);
        tv_for_titleCount = findViewById(R.id.tv_for_titleCount);
        findViewById(R.id.layout_for_date).setOnClickListener(this);
        findViewById(R.id.layout_for_time).setOnClickListener(this);
        btn_for_addReminder = findViewById(R.id.btn_for_addReminder);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.layout_for_date:
                Utils.hideKeyboard(this);
                datePicker(-1, -1, -1);
                Update = 1;
                break;
            case R.id.layout_for_time:
                Utils.hideKeyboard(this);
                timeDialogue();
                break;
            case R.id.btn_for_addReminder:
                validation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void validation() {
        String title = ed_for_tittle.getText().toString().trim();
        String date = tv_for_date.getText().toString().trim();
        String time = tv_for_time.getText().toString().trim();
        String description = ed_for_description.getText().toString().trim();

        if (title.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.title_v));
            ed_for_tittle.requestFocus();
        } else if (date.equalsIgnoreCase("date")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.date_v));
            tv_for_date.requestFocus();
        } else if (time.equalsIgnoreCase("time")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.time_v));
            tv_for_time.requestFocus();
        } else if (description.equalsIgnoreCase("")) {
            Constant.snackbar(mainLayout, getResources().getString(R.string.des_v));
            ed_for_description.requestFocus();
        } else {
            apiCallingMethod(title, date, time, description);
        }
    }

    private void apiCallingMethod(String title, String date, String time, String description) {

        Date d = new Date();
        Date strToDate = null, currentDate = null;

        CharSequence s = DateFormat.format("yyyy-M-d", d.getTime());
        String sDate = (String) s;

        CharSequence s2 = DateFormat.format("dd MMM yyyy", d.getTime());
        String sDate2 = (String) s2;

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            if (editReminder != null && !editReminder.title.equals("")) {

                if (Update == -1) {

                    strToDate = dateFormat2.parse(date);
                    currentDate = dateFormat2.parse(sDate2);

                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.US);
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                        Date editTime = parseFormat.parse(time);
                        String editTimeFormat = displayFormat.format(editTime);
                        String[] getEditTime = editTimeFormat.split(":");

                        mhour = Integer.parseInt(getEditTime[0]);
                        mminute = Integer.parseInt(getEditTime[1]);

                        c2.set(Calendar.HOUR_OF_DAY, mhour);
                        c2.set(Calendar.MINUTE, mminute);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    strToDate = dateFormat.parse(date);
                    currentDate = dateFormat.parse(sDate);

                    c2.set(Calendar.HOUR_OF_DAY, mhour);
                    c2.set(Calendar.MINUTE, mminute);
                }

            } else {
                strToDate = dateFormat.parse(date);
                currentDate = dateFormat.parse(sDate);

                c2.set(Calendar.HOUR_OF_DAY, mhour);
                c2.set(Calendar.MINUTE, mminute);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentDate != null && strToDate != null) {

            if (date.contains(sDate) | date.contains(sDate2)) {
                //current date
                if (c2.getTimeInMillis() > c1.getTimeInMillis()) {
                    if (editReminder != null && !editReminder.title.equals("")) {
                        updateReminderAPI(title, date, time, description);
                    } else {
                        addReminderAPI(title, date, time, description);
                    }
                } else {
                    Constant.snackbar(mainLayout, "Can't select past time");
                }
            } else if (strToDate.after(currentDate)) {
                //after current day simple call api
                if (editReminder != null && !editReminder.title.equals("")) {
                    updateReminderAPI(title, date, time, description);
                } else {
                    addReminderAPI(title, date, time, description);
                }
            } else {
                Toast.makeText(this, "You can't update with past date", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addReminderAPI(final String title, final String date, final String time, final String description) {

        if (Utils.isNetworkAvailable(this)) {
            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "addReminder", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Constant.snackbar(mainLayout, message);
                            Toast.makeText(AddReminderActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Constant.errorHandle(error, AddReminderActivity.this);
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("date", date);
                    params.put("time", time);
                    params.put("description", description);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(AddReminderActivity.this);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest, TAG);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    public void updateReminderAPI(final String title, final String date, final String time, final String description) {

        if (Utils.isNetworkAvailable(this)) {
            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "updateReminder", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Constant.snackbar(mainLayout, message);
                            Toast.makeText(AddReminderActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Constant.errorHandle(error, AddReminderActivity.this);
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("date", date);
                    params.put("time", time);
                    params.put("description", description);
                    params.put("reminderId", editReminder.reminderId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(AddReminderActivity.this);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest, TAG);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    private void datePicker(int i1, int i2, int i3) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        if (i1 != -1) {
            day = i1;
            month = i2 - 1;
            year = i3;
        }
        DatePickerDialog datepickerdialog = new DatePickerDialog(this, R.style.DefaultNumberPickerTheme, this, year, month, day);
        datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datepickerdialog.getWindow().setBackgroundDrawableResource(R.color.white);
        datepickerdialog.show();
    }

    private void timeDialogue() {
        Time time = new Time(System.currentTimeMillis());
        int hour = time.getHours();
        int minute = time.getMinutes();
        TimePickerDialog timePicker = new TimePickerDialog(this, R.style.DefaultNumberPickerTheme, this, hour, minute, DateFormat.is24HourFormat(this));
        timePicker.updateTime(hour, minute);
        timePicker.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // int mdate = dayOfMonth;
        int mmonth = month + 1;
        // int myear = year;
        tv_for_date.setText(year + "-" + mmonth + "-" + dayOfMonth);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        String last;
        last = " AM";
        mhour = i;
        mminute = i1;
        if (i >= 12) {
            i -= 12;
            last = " PM";
        }
        tv_for_time.setText((i < 10 ? "0" + i : i) + ":" + (i1 < 10 ? "0" + i1 : i1) + last);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(getApplicationContext()).cancelPendingRequests(TAG);
    }
}
