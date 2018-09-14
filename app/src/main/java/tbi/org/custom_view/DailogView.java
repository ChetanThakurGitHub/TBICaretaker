package tbi.org.custom_view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tbi.org.R;
import tbi.org.activity.UserSelectionActivity;
import tbi.org.adapter.CaretakerReminderAdapter;
import tbi.org.fragment.caretaker.FaqsCaretakerFragment;
import tbi.org.model.AllReminderList;
import tbi.org.model.FaqList;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class DailogView {

    private Boolean is_show = true;

    public void deletelDailog(final Context context, final String reminderId, final RelativeLayout layout_for_list, final int adapterPosition, final CaretakerReminderAdapter caretakerReminderAdapter) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_for_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminderAPI(context, reminderId, layout_for_list, adapterPosition, caretakerReminderAdapter);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteReminderAPI(final Context context, final String reminderId, final RelativeLayout layout_for_list, final int adapterPosition, final CaretakerReminderAdapter caretakerReminderAdapter) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "deleteReminder", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            caretakerReminderAdapter.allReminderLists.remove(adapterPosition);
                            caretakerReminderAdapter.notifyDataSetChanged();
                            Constant.snackbar(layout_for_list, message);
                        } else {
                            Constant.snackbar(layout_for_list, message);
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
                    Constant.snackbar(layout_for_list, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("reminderId", reminderId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(layout_for_list, context.getResources().getString(R.string.check_net_connection));
        }
    }

    public void changePasswordDailog(final Context context) {
        final Session session = new Session(context);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_change_password_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        final EditText ed_for_oldPass = dialog.findViewById(R.id.ed_for_oldPass);
        final EditText ed_for_newPass = dialog.findViewById(R.id.ed_for_newPass);
        Button btn_for_updatePass = dialog.findViewById(R.id.btn_for_updatePass);
        final ImageView iv_for_showPass = dialog.findViewById(R.id.iv_for_showPass);
        //final CoordinatorLayout coordinateLay = (CoordinatorLayout) context.getActivity().dialog.findViewById(R.id.coordinateLay);


        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_for_updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = ed_for_oldPass.getText().toString().trim();
                String newPassword = ed_for_newPass.getText().toString().trim();
                if (oldPassword.equalsIgnoreCase("")) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.password_v));
                    ed_for_oldPass.requestFocus();
                } else if (oldPassword.length() < 6) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.password_required));
                    ed_for_oldPass.requestFocus();
                } else if (!oldPassword.equals(session.getPassword())) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.oldpass_v));
                    ed_for_oldPass.requestFocus();
                } else if (newPassword.equalsIgnoreCase("")) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.password_v));
                    ed_for_newPass.requestFocus();
                } else if (newPassword.length() < 6) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.password_required));
                    ed_for_newPass.requestFocus();
                } else if (oldPassword.equals(newPassword)) {
                    Constant.snackbar(ed_for_newPass, context.getResources().getString(R.string.password_change));
                } else {
                    showAlertDialog(context, ed_for_newPass, oldPassword, newPassword, session);
                    dialog.dismiss();
                }
            }
        });

        iv_for_showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_show) {
                    ed_for_newPass.setTransformationMethod(null);
                    iv_for_showPass.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eye));
                    is_show = false;
                } else {
                    ed_for_newPass.setTransformationMethod(new PasswordTransformationMethod());
                    iv_for_showPass.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_blind));
                    is_show = true;
                }

            }
        });
        dialog.show();
    }

    private void showAlertDialog(final Context context, final EditText ed_for_newPass, final String oldPassword, final String newPassword, final Session session) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Alert!");
        builder1.setMessage("If you change password then your session will expire");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                changePasswordAPI(context, ed_for_newPass, oldPassword, newPassword, session);
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void changePasswordAPI(final Context context, final EditText mainLayout, final String oldPassword, final String newPassword, final Session session) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "changePassword", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(context, UserSelectionActivity.class);
                            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            session.logout(context);
                            session.logoutMyPre();
                            context.startActivity(intent);
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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("oldPassword", oldPassword);
                    params.put("newPassword", newPassword);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, context.getResources().getString(R.string.check_net_connection));
        }
    }

    public void bloodGroupDailog(Activity context, final TextView et_for_bloodGroup) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_age_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        et_for_bloodGroup.setText(R.string.a);

        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        TextView tv_for_yr = dialog.findViewById(R.id.tv_for_yr);
        TextView txt = dialog.findViewById(R.id.txt);
        tv_for_yr.setVisibility(View.GONE);

        numberPicker.setMaxValue(0);
        numberPicker.setMaxValue(7);
        numberPicker.setDisplayedValues(new String[]{"A-positive", "A-negative", "O-positive", "O-negative", "B-positive", "B-negative", "AB-positive", "AB-negative"});
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0) {
                    et_for_bloodGroup.setText("A-positive");
                } else if (newVal == 1) {
                    et_for_bloodGroup.setText("A-negative");
                } else if (newVal == 2) {
                    et_for_bloodGroup.setText("O-positive");
                } else if (newVal == 3) {
                    et_for_bloodGroup.setText("O-negative");
                } else if (newVal == 4) {
                    et_for_bloodGroup.setText("B-positive");
                } else if (newVal == 5) {
                    et_for_bloodGroup.setText("B-negative");
                } else if (newVal == 6) {
                    et_for_bloodGroup.setText("AB-positive");
                } else if (newVal == 7) {
                    et_for_bloodGroup.setText("AB-negative");
                }
            }
        });

        txt.setText("Blood Group");

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

    public void click(final Context mContext, final View view_for_color, final AllReminderList allReminderList) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_sufferer_reminder_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tv_for_dateDailog = dialog.findViewById(R.id.tv_for_dateDailog);
        TextView tv_for_timeDailog = dialog.findViewById(R.id.tv_for_timeDailog);
        TextView tv_for_status = dialog.findViewById(R.id.tv_for_status);
        TextView tv_for_titleDailog = dialog.findViewById(R.id.tv_for_titleDailog);
        final TextView tv_for_descriptionDailog = dialog.findViewById(R.id.tv_for_descriptionDailog);
        TextView tv_for_statusTxt = dialog.findViewById(R.id.tv_for_statusTxt);
        Button btn_for_done = dialog.findViewById(R.id.btn_for_done);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        RelativeLayout layout_for_rememberHeader = dialog.findViewById(R.id.layout_for_rememberHeader);

        tv_for_dateDailog.setText(allReminderList.date);
        tv_for_timeDailog.setText(allReminderList.time);
        if (allReminderList.is_done.equals("1")) {
            layout_for_rememberHeader.setBackgroundResource(R.drawable.dailog_bg_top_active);
            tv_for_statusTxt.setVisibility(View.VISIBLE);
            tv_for_status.setVisibility(View.VISIBLE);
            btn_for_done.setVisibility(View.GONE);

        } else {
            tv_for_statusTxt.setVisibility(View.GONE);
            tv_for_status.setVisibility(View.GONE);
            btn_for_done.setVisibility(View.VISIBLE);
        }

        tv_for_titleDailog.setText(allReminderList.title);
        tv_for_descriptionDailog.setText(allReminderList.description);
        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_for_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                Date d = new Date();
                Date strToDate = null, currentDate = null;
                CharSequence s = DateFormat.format("dd MMM yyyy", d.getTime());
                String sDate = (String) s;

                try {
                    strToDate = dateFormat.parse(allReminderList.date);
                    currentDate = dateFormat.parse(sDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (allReminderList.date.equals(sDate)) {
                    //current date
                    reminderStatusAPI(mContext, view_for_color, allReminderList, tv_for_descriptionDailog, dialog);
                    dialog.dismiss();
                } else if (strToDate.before(currentDate)) {
                    //before current day simple call api
                    reminderStatusAPI(mContext, view_for_color, allReminderList, tv_for_descriptionDailog, dialog);
                    dialog.dismiss();
                } else {
                    Toast.makeText(mContext, "You can't done further reminder ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    private void reminderStatusAPI(final Context context, final View view_for_color, final AllReminderList allReminderList, final TextView mainLayout, final Dialog dialog) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "reminderStatus", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            view_for_color.setBackgroundResource(R.drawable.circle_notificatioan_active_bg);
                            allReminderList.is_done = 1 + "";
                            dialog.dismiss();
                        } else {
                            Constant.snackbar(mainLayout, message);
                            dialog.dismiss();
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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("reminderId", allReminderList.reminderId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, context.getResources().getString(R.string.check_net_connection));
        }
    }

    public void addNewFAQDailog(final Context mContext, final FaqsCaretakerFragment faqsCaretakerFragment) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_add_faq_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText et_for_title = dialog.findViewById(R.id.et_for_title);
        final TextView tv_for_titleCount = dialog.findViewById(R.id.tv_for_titleCount);
        final EditText ed_for_description = dialog.findViewById(R.id.ed_for_description);
        final TextView tv_for_desCount = dialog.findViewById(R.id.tv_for_desCount);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 250 - s.length();
                tv_for_titleCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_title.addTextChangedListener(textWatcher);


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 300 - s.length();
                tv_for_desCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        ed_for_description.addTextChangedListener(textWatcher);


        dialog.findViewById(R.id.btn_for_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_for_title.getText().toString().trim();
                String description = ed_for_description.getText().toString().trim();
                if (title.equals("")) {
                    Constant.snackbar(ed_for_description, mContext.getResources().getString(R.string.title_v));
                } else if (description.equals("")) {
                    Constant.snackbar(ed_for_description, mContext.getResources().getString(R.string.des_v));
                } else {
                    addFaqAPI(mContext, title, description, ed_for_description, faqsCaretakerFragment, dialog);
                }
            }
        });

        dialog.findViewById(R.id.layout_for_crossDailog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addFaqAPI(final Context context, final String title, final String description, final EditText mainLayout, final FaqsCaretakerFragment faqsCaretakerFragment, final Dialog dialog) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "addFaq", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            faqsCaretakerFragment.getAllFAQListAPI();
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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("description", description);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, context.getResources().getString(R.string.check_net_connection));
        }
    }

    public void deletelFAQDailog(final Context context, final FaqsCaretakerFragment faqsCaretakerFragment) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);
        tv_for_txt.setText(R.string.delete_this_faq);

        dialog.findViewById(R.id.layout_for_crossDailog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_for_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteFaqAPI(context, tv_for_txt, faqsCaretakerFragment);
            }
        });

        dialog.show();
    }

    private void deleteFaqAPI(final Context context, final TextView mainLayout, final FaqsCaretakerFragment faqsCaretakerFragment) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "deleteFaq", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            faqsCaretakerFragment.iv_for_delete.setVisibility(View.GONE);
                            faqsCaretakerFragment.getAllFAQListAPI();
                            /*faqsCaretakerFragment.caretakerFAQAdapter.isShow = false;
                            faqsCaretakerFragment.caretakerFAQAdapter.notifyDataSetChanged();*/
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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    ArrayList<FaqList> faqLists = faqsCaretakerFragment.faqLists;
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < faqLists.size(); i++) {
                        if (faqLists.get(i).checkBox) {
                            arrayList.add(faqLists.get(i).faqId);
                        }
                    }
                    params.put("faqId", arrayList.toString().replace("[", "").replace("]", ""));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, context.getResources().getString(R.string.check_net_connection));
        }
    }

    public void updateFaqDailog(final Context mContext, final FaqList faqList, final FaqsCaretakerFragment fragment) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_add_faq_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText et_for_title = dialog.findViewById(R.id.et_for_title);
        TextView tv_for_title = dialog.findViewById(R.id.tv_for_title);
        Button btn_for_add = dialog.findViewById(R.id.btn_for_add);
        btn_for_add.setText("Update");
        tv_for_title.setText("Update FAQ'S");
        final TextView tv_for_titleCount = dialog.findViewById(R.id.tv_for_titleCount);
        final EditText ed_for_description = dialog.findViewById(R.id.ed_for_description);
        final TextView tv_for_desCount = dialog.findViewById(R.id.tv_for_desCount);

        et_for_title.setText(faqList.title);
        ed_for_description.setText(faqList.description);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 250 - s.length();
                tv_for_titleCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        et_for_title.addTextChangedListener(textWatcher);


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int text = 300 - s.length();
                tv_for_desCount.setText(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        ed_for_description.addTextChangedListener(textWatcher);


        dialog.findViewById(R.id.btn_for_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_for_title.getText().toString().trim();
                String description = ed_for_description.getText().toString().trim();
                if (title.equals("")) {
                    Constant.snackbar(ed_for_description, mContext.getResources().getString(R.string.title_v));
                } else if (description.equals("")) {
                    Constant.snackbar(ed_for_description, mContext.getResources().getString(R.string.des_v));
                } else {
                    updateFaqAPI(mContext, title, description, ed_for_description, dialog, faqList, fragment);
                }
            }
        });

        dialog.findViewById(R.id.layout_for_crossDailog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateFaqAPI(final Context context, final String title, final String description, final EditText mainLayout, final Dialog dialog, final FaqList faqList, final FaqsCaretakerFragment fragment) {
        if (Utils.isNetworkAvailable(context)) {
            final Dialog pDialog = new Dialog(context);
            Constant.myDialog(context, pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.URL_WITH_LOGIN + "updateFaq", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            fragment.getAllFAQListAPI();

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
                    Constant.snackbar(mainLayout, networkResponse + "");
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("title", title);
                    params.put("description", description);
                    params.put("faqId", faqList.faqId);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Session session = new Session(context);
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(mainLayout, context.getResources().getString(R.string.check_net_connection));
        }
    }

}