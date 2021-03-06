package tbi.org.fragment.sufferer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tbi.org.R;
import tbi.org.adapter.SuffererFAQAdapter;
import tbi.org.model.FaqList;
import tbi.org.pagination.EndlessRecyclerViewScrollListener;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class FaqsSuffererFragment extends Fragment {
    public SuffererFAQAdapter suffererFAQAdapter;
    public ArrayList<FaqList> faqLists;
    private RecyclerView recycler_view;
    private Session session;
    private RelativeLayout mainLayout;
    private TextView tv_for_noData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int start = 0;

    public FaqsSuffererFragment() {
        // Required empty public constructor
    }

    public static FaqsSuffererFragment newInstance() {
        return new FaqsSuffererFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_faqs_sufferer, container, false);

        initView(view);
        session = new Session(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(false);

        faqLists = new ArrayList<>();
        suffererFAQAdapter = new SuffererFAQAdapter(faqLists);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                faqLists.clear();
                start = 0;
                getAllFAQListAPI();
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                faqLists.clear();
                start = 0;
                getAllFAQListAPI();
            }
        });
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAllFAQListAPI();
            }
        };
        recycler_view.addOnScrollListener(scrollListener);
        return view;
    }

    private void initView(View view) {
        recycler_view = view.findViewById(R.id.recycler_view);
        mainLayout = view.findViewById(R.id.mainLayout);
        tv_for_noData = view.findViewById(R.id.tv_for_noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getAllFAQListAPI() {

        if (Utils.isNetworkAvailable(getContext())) {

            int limit = 20;
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "faqList?" + "limit=" + limit + "&start=" + start, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            tv_for_noData.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            faqLists.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("faqList");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    FaqList faqList = new Gson().fromJson(object.toString(), FaqList.class);
                                    faqLists.add(faqList);
                                }
                                if (start == 0) {
                                    recycler_view.setAdapter(suffererFAQAdapter);
                                }
                                start = start + 20;
                                suffererFAQAdapter.notifyDataSetChanged();
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

}
