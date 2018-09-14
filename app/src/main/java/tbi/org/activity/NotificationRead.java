package tbi.org.activity;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class NotificationRead extends AppCompatActivity {

    private String notificationId_nr = "";

    @Override
    protected void onStart() {
        super.onStart();

        if (notificationId_nr != null && !notificationId_nr.equalsIgnoreCase("")) {
            notificationStatus(notificationId_nr);
        }
    }

    public void setNotificationId(String notificationId) {
        this.notificationId_nr = notificationId;
    }

    public void notificationStatus(final String notificationId) {
        if (Utils.isNetworkAvailable(this)) {

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "notificationReadStauts", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("notificationId", notificationId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(NotificationRead.this);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
        }
    }
}
