package tbi.com.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import tbi.com.model.UserFullDetail;
import tbi.com.util.Constant;


public class Session {

    private static final String PREF_NAME_R = "TBI_R";
    private static final String PREF_NAME_TOP = "TBI_TOP";
    private static final String PREF_NAME = "TBI";
    private static final String IS_LOGEDIN = "isLogedin";
    private final String SOFT_KEY = "SOFT_KEY";
    private SharedPreferences mypref, rememberMePref, top;
    private SharedPreferences.Editor editor, editor_r, editor_top;

    public Session(Context context) {
        Context mcontext = context;
        mypref = mcontext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();

        rememberMePref = mcontext.getSharedPreferences(PREF_NAME_R, Context.MODE_PRIVATE);
        editor_r = rememberMePref.edit();
        editor_r.apply();

        top = mcontext.getSharedPreferences(PREF_NAME_TOP, Context.MODE_PRIVATE);
        editor_top = top.edit();
        editor_top.apply();
    }

    public void createSession(UserFullDetail userFullDetail) {

        editor.putString(Constant.USER_ID, userFullDetail.userId);
        editor.putString(Constant.NAME, userFullDetail.name);
        editor.putString(Constant.EMAIL, userFullDetail.email);
        editor.putString(Constant.PASSWORD, userFullDetail.password);
        editor.putString(Constant.PROFILE_IMAGE, userFullDetail.profileImage);
        editor.putString(Constant.CONTACT, userFullDetail.emergency_contact_number);
        editor.putString(Constant.DEVICETYPE, userFullDetail.deviceType);
        editor.putString(Constant.DEVICE_TOKEN, userFullDetail.deviceToken);
        editor.putString(Constant.SOCIAL_ID, userFullDetail.socialId);
        editor.putString(Constant.SOCILA_TYPE, userFullDetail.socialType);
        editor.putString(Constant.AUTHTOKEN, userFullDetail.authToken);
        editor.putString(Constant.STATUS, userFullDetail.status);
        editor.putString(Constant.CRD, userFullDetail.crd);
        editor.putString(Constant.UPD, userFullDetail.upd);
        editor.putString(Constant.AGE, userFullDetail.age);
        editor.putString(Constant.BLOOD_GROUP, userFullDetail.blood_group);
        editor.putString(Constant.WEIGHT, userFullDetail.weight);
        editor.putString(Constant.HEIGHT, userFullDetail.height);
        editor.putString(Constant.GENDER, userFullDetail.gender);
        editor.putString(Constant.LOGIN, userFullDetail.login);
        editor.putString(Constant.THUMB_IMAGE, userFullDetail.thumbImage);
        editor.putString(Constant.USER_TYPE, userFullDetail.userType);

        editor.putBoolean(IS_LOGEDIN, true);
        editor.commit();

        editor_r.putString(Constant.EMAIL, userFullDetail.email);
        editor_r.putString(Constant.PASSWORD, userFullDetail.password);
        editor_r.putString(Constant.USER_TYPE, userFullDetail.userType);
        editor_r.commit();
    }


    public String getEmailR() {
        return rememberMePref.getString(Constant.EMAIL, "");
    }

    public void setEmailR(String emailR) {
        editor_r.putString(Constant.EMAIL, emailR);
        editor_r.commit();
    }

    public String getPasswordR() {
        return rememberMePref.getString(Constant.PASSWORD, "");
    }

    public String getUserTypeR() {
        return rememberMePref.getString(Constant.USER_TYPE, "");
    }

    public boolean isSoftKey() {
        return top.getBoolean(SOFT_KEY, false);
    }

    public void setSoftKey(Boolean value) {
        editor_top.putBoolean(SOFT_KEY, value);
        editor_top.commit();
    }

    public String getUserID() {
        return mypref.getString(Constant.USER_ID, "");
    }

    public String getUserType() {
        return mypref.getString(Constant.USER_TYPE, "");
    }

    public String getProfileImage() {
        return mypref.getString(Constant.PROFILE_IMAGE, "");
    }

    public String getFullName() {
        return mypref.getString(Constant.NAME, "");
    }

    public String getEmail() {
        return mypref.getString(Constant.EMAIL, "");
    }

    public void setEmail(String chatCount) {
        editor.putString(Constant.EMAIL, chatCount);
        editor.commit();
    }

    public String getLogin() {
        return mypref.getString(Constant.LOGIN, "");
    }

    public void setLogin(String value) {
        editor.putString(Constant.LOGIN, value);
        editor.commit();
    }

    public String getPassword() {
        return mypref.getString(Constant.PASSWORD, "");
    }

    public String getContact() {
        return mypref.getString(Constant.CONTACT, "");
    }

    public String getDeviceToken() {
        return mypref.getString(Constant.DEVICE_TOKEN, "");
    }

    public String getAge() {
        return mypref.getString(Constant.AGE, "");
    }

    public String getBloadGroup() {
        return mypref.getString(Constant.BLOOD_GROUP, "");
    }

    public String getWeight() {
        return mypref.getString(Constant.WEIGHT, "");
    }

    public String getHeight() {
        return mypref.getString(Constant.HEIGHT, "");
    }

    public String getGender() {
        return mypref.getString(Constant.GENDER, "");
    }

    public String getAuthToken() {
        return mypref.getString(Constant.AUTHTOKEN, "");
    }

    public boolean getIsLogedIn() {
        return mypref.getBoolean(IS_LOGEDIN, false);
    }

    public void logout(Context activity) {
        editor.clear();
        editor.apply();
        Toast.makeText(activity, "Logout sucessfully", Toast.LENGTH_SHORT).show();
    }

    public void logoutMyPre() {
        editor_r.clear();
        editor_r.apply();
    }
}
