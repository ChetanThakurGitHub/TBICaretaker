package tbi.com.fragment.sufferer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tbi.com.R;
import tbi.com.adapter.SuffererReminderAdapter;
import tbi.com.model.AllReminderList;
import tbi.com.pagination.EndlessRecyclerViewScrollListener;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

public class ReminderSuffererFragment extends Fragment {
    private Session session;
    private RecyclerView recycler_view;
    private SuffererReminderAdapter suffererReminderAdapter;
    private ArrayList<AllReminderList> allReminderLists;
    private TextView tv_for_noData, tv_for_tittle;
    private RelativeLayout mainLayout;
    private ImageView iv_for_menu, iv_for_backIco, iv_for_edit, iv_for_more, iv_for_delete, iv_for_calender, iv_for_deleteChat, iv_for_block;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int start = 0;

    public ReminderSuffererFragment() {
        // Required empty public constructor
    }

    public static ReminderSuffererFragment newInstance() {
        return new ReminderSuffererFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_reminder_sufferer2, container, false);
        initView(view);
        session = new Session(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);

        allReminderLists = new ArrayList<>();
        suffererReminderAdapter = new SuffererReminderAdapter(allReminderLists, getContext());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0;
                allReminderLists.clear();
                getAllRemindersListAPI();
            }
        });
        recycler_view.setAdapter(suffererReminderAdapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAllRemindersListAPI();
            }
        };
        recycler_view.addOnScrollListener(scrollListener);

        return view;
    }

    private void initView(View view) {
        iv_for_menu = getActivity().findViewById(R.id.iv_for_menu);
        iv_for_backIco = getActivity().findViewById(R.id.iv_for_backIco);
        tv_for_tittle = getActivity().findViewById(R.id.tv_for_tittle);
        iv_for_edit = getActivity().findViewById(R.id.iv_for_edit);
        iv_for_more = getActivity().findViewById(R.id.iv_for_more);
        iv_for_delete = getActivity().findViewById(R.id.iv_for_delete);
        iv_for_calender = getActivity().findViewById(R.id.iv_for_calender);
        iv_for_deleteChat = getActivity().findViewById(R.id.iv_for_deleteChat);
        iv_for_block = getActivity().findViewById(R.id.iv_for_block);

        recycler_view = view.findViewById(R.id.recycler_view);
        tv_for_noData = view.findViewById(R.id.tv_for_noData);
        mainLayout = view.findViewById(R.id.mainLayout);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        tv_for_tittle.setText(R.string.reminders);
        iv_for_backIco.setVisibility(View.GONE);
        iv_for_edit.setVisibility(View.GONE);
        iv_for_more.setVisibility(View.GONE);
        iv_for_delete.setVisibility(View.GONE);
        iv_for_block.setVisibility(View.GONE);
        iv_for_deleteChat.setVisibility(View.GONE);
        iv_for_menu.setVisibility(View.VISIBLE);
        iv_for_calender.setVisibility(View.VISIBLE);

        allReminderLists.clear();
        if (Utils.isNetworkAvailable(getContext())) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    allReminderLists.clear();
                    start = 0;
                    getAllRemindersListAPI();
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
        suffererReminderAdapter.notifyDataSetChanged();
    }

    public void getAllRemindersListAPI() {

        if (Utils.isNetworkAvailable(getContext())) {

            int limit = 20;
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "getAllTypeReminderList?" + "limit=" + limit + "&start=" + start, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            tv_for_noData.setVisibility(View.GONE);
                            JSONArray jsonArray = jsonObject.getJSONArray("suffererReminderList");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    AllReminderList allReminderList = new Gson().fromJson(object.toString(), AllReminderList.class);
                                    allReminderLists.add(allReminderList);
                                }
                                if (start == 0) {
                                    recycler_view.setAdapter(suffererReminderAdapter);
                                }
                                start = start + 20;
                            }
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Constant.errorHandle(error, getActivity());
                    Constant.snackbar(mainLayout, networkResponse + "");
                    mSwipeRefreshLayout.setRefreshing(false);
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, getResources().getString(R.string.check_net_connection));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
