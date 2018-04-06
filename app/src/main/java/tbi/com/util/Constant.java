package tbi.com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import tbi.com.R;
import tbi.com.activity.UserSelectionActivity;
import tbi.com.session.Session;
import tbi.com.snackBarPackage.TSnackbar;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PROFILE_IMAGE = "profileImage";
    public static final String CONTACT = "emergency_contact_number";
    public static final String USER_TYPE = "userType";
    public static final String DEVICETYPE = "deviceType";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String SOCIAL_ID = "socialId";
    public static final String SOCILA_TYPE = "socialType";
    public static final String AUTHTOKEN = "authToken";
    public static final String STATUS = "status";
    public static final String CRD = "crd";
    public static final String UPD = "upd";
    public static final String AGE = "age";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String GENDER = "gender";
    public static final String LOGIN = "login";
    public static final String THUMB_IMAGE = "thumbImage";

    public static final String URL_WITH_LOGIN = "http://tbicaretakercoach.com/service/user/";
    public static final String URL_WITHOUT_LOGIN = "http://tbicaretakercoach.com/service/";

    public static final int BackPressed_Exit = 2000;
    public static final int CAMERA = 7;
    public static final int GALLERY = 3;
    public static final int SPLESH_TIME = 3000;
    public static final int RequestPermissionCode = 1;
    public static final int RESULT_OK = -1;
    public static final int CALLING = 15;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 8;
    public static final String NETWORK_SWITCH_FILTER = "com.devglan.broadcastreceiver.NETWORK_SWITCH_FILTER";
    public static int NETWORK_CHECK = 0;

    public static void errorHandle(VolleyError error, Activity activity) {
        NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }
        } else {
            String result = new String(networkResponse.data);
            try {
                JSONObject response = new JSONObject(result);

                String status = response.getString("responseCode");
                String message = response.getString("message");

                if (status.equals("300")) {
                    if (activity != null) {
                        showAlertDialog(activity, "Please Login Again", "Session Expired", "LogOut");
                    }
                }

                if (networkResponse.statusCode == 404) {
                    errorMessage = "Resource not found";
                } else if (networkResponse.statusCode == 401) {
                    errorMessage = message + " Please login again";
                } else if (networkResponse.statusCode == 400) {
                    errorMessage = message + " Check your inputs";
                } else if (networkResponse.statusCode == 500) {
                    errorMessage = message + " Something is getting wrong";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (activity != null) {

                }
            }
        }
    }

    public static void showAlertDialog(final Activity con, String msg, String title, String ok) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        signOut(con);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void logout(final Activity con) {

        if (Utils.isNetworkAvailable(con)) {

            final Dialog pDialog = new Dialog(con);
            Constant.myDialog(con, pDialog);
            pDialog.show();

            final Session session = new Session(con);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET,
                    Constant.URL_WITH_LOGIN + "logout?deviceToken=" + session.getDeviceToken(), new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String message = jsonObject.getString("message");

                        if (message.equals("Invalid Auth Token")) {
                            signOut(con);
                        }
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            signOut(con);

                        } else {
                            Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(con, networkResponse + "", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();

                    String auth = session.getAuthToken();
                    headers.put("authToken", auth);

                    return headers;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(con).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(con, R.string.check_net_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public static void signOut(final Activity con) {

        Session session = new Session(con);

        FirebaseDatabase.getInstance().getReference().child("users").child(session.getUserID()).child("firebaseToken").setValue("");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        NotificationManager notificationManager = (NotificationManager) con.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(con, UserSelectionActivity.class);
        session.logout(con);
        con.finish();
        con.startActivity(intent);
    }

    public static void myDialog(Context context, Dialog pDialog) {
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.progress_bar_layout);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //pDialog.show();
    }

    public static void snackbar(View coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#5e8d93"));
        textView.setGravity(Gravity.CENTER);
        snackbar.setActionTextColor(Color.parseColor("#5e8d93"));
        sbView.setBackgroundColor(Color.WHITE);
        snackbar.show();

    }

    public static void snackbarTop(View coordinatorLayout, String message) {
        TSnackbar snackbar = TSnackbar.make(coordinatorLayout, message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.parseColor("#5e8d93"));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#5e8d93"));
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    public static void notificationStatus(final Context activity, final String notificationId) {
        if (Utils.isNetworkAvailable(activity)) {

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
                    Session session = new Session(activity);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(activity).addToRequestQueue(multipartRequest);
        }
    }

}
