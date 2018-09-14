package tbi.org.fragment.sufferer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tbi.org.R;
import tbi.org.adapter.NotificationAdapter;
import tbi.org.model.NotificationList;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class NotificationsSuffererFragment extends Fragment {
    private static final String TAG = NotificationsSuffererFragment.class.getSimpleName();
    public NotificationAdapter notificationAdapter;
    public ArrayList<NotificationList> notificationLists;
    private RecyclerView recycler_view;
    private Session session;
    private RelativeLayout mainLayout;
    private TextView tv_for_noData;

    public NotificationsSuffererFragment() {
        // Required empty public constructor
    }

    public static NotificationsSuffererFragment newInstance() {
        return new NotificationsSuffererFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notifications_sufferer, container, false);

        initView(view);
        session = new Session(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);

        notificationLists = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationLists, getContext(), false);
        getAllNotificationListAPI();

        return view;
    }

    private void initView(View view) {
        recycler_view = view.findViewById(R.id.recycler_view);
        mainLayout = view.findViewById(R.id.mainLayout);
        tv_for_noData = view.findViewById(R.id.tv_for_noData);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getAllNotificationListAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "notificationList", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            tv_for_noData.setVisibility(View.GONE);
                            notificationLists.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("notificationList");
                            if (jsonArray != null) {
                                NotificationList notificationList = new NotificationList();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    notificationList.notificationId = object.getString("notificationId");
                                    notificationList.notification_type = object.getString("notification_type");
                                    notificationList.crd = object.getString("crd");
                                    JSONObject jsonObjectLast = new JSONObject(object.getString("notification_message"));
                                    notificationList.title = jsonObjectLast.getString("title");
                                    notificationList.body = jsonObjectLast.getString("body");
                                    notificationList.type = jsonObjectLast.getString("type");
                                    notificationList.reminder_title = jsonObjectLast.getString("reminder_title");
                                    notificationList.reminder_date = jsonObjectLast.getString("reminder_date");
                                    notificationList.reminder_time = jsonObjectLast.getString("reminder_time");
                                    notificationList.reminder_description = jsonObjectLast.getString("reminder_description");
                                    notificationList.reference_id = jsonObjectLast.getString("reference_id");
                                    notificationList.click_action = jsonObjectLast.getString("click_action");

                                    notificationLists.add(notificationList);
                                }
                                recycler_view.setAdapter(notificationAdapter);
                                notificationAdapter.notifyDataSetChanged();
                            }
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest, TAG);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG);
    }

}
