package tbi.com.custom_calender.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.adapter.SuffererReminderAdapter;
import tbi.com.model.AllReminderList;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class CalanderSuffererActivity extends AppCompatActivity implements View.OnClickListener {

    public LinearLayout mainLayout;
    private Session session;
    private RecyclerView recycler_view;
    private SuffererReminderAdapter suffererReminderAdapter;
    private ArrayList<AllReminderList> allReminderLists;
    private TextView tv_for_noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander_sufferer);

        initView();
        session = new Session(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);

        allReminderLists = new ArrayList<>();
        suffererReminderAdapter = new SuffererReminderAdapter(allReminderLists, this);

        recycler_view.setAdapter(suffererReminderAdapter);
    }

    private void initView() {
        recycler_view = findViewById(R.id.recycler_view);
        tv_for_noData = findViewById(R.id.tv_for_noData);
        mainLayout = findViewById(R.id.mainLayout);
        findViewById(R.id.iv_for_backIco).setOnClickListener(this);
    }

    public void getAllRemindersListAPI(String date) {
        allReminderLists.clear();
        if (Utils.isNetworkAvailable(this)) {
            final Dialog pDialog = new Dialog(this);
            Constant.myDialog(this, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "getAllTypeReminderList?limit&start&date=" + date, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            allReminderLists.clear();
                            tv_for_noData.setVisibility(View.GONE);
                            JSONArray jsonArray = jsonObject.getJSONArray("suffererReminderList");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    AllReminderList allReminderList = new Gson().fromJson(object.toString(), AllReminderList.class);
                                    allReminderLists.add(allReminderList);
                                }
                                recycler_view.setAdapter(suffererReminderAdapter);
                                suffererReminderAdapter.notifyDataSetChanged();
                                //start = start + 0;
                            }
                        } else {
                            allReminderLists.clear();
                            suffererReminderAdapter.notifyDataSetChanged();
                            recycler_view.setAdapter(suffererReminderAdapter);
                            tv_for_noData.setVisibility(View.VISIBLE);
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
                    Log.i("Error", networkResponse + "");
                    Constant.errorHandle(error, CalanderSuffererActivity.this);
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
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
        }
    }
}
