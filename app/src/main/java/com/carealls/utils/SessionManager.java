package com.carealls.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.databinding.BaseObservable;

import com.carealls.ui.components.activites.addReview.AddReviewActivity;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager extends BaseObservable {
    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;
    private static SessionManager session;

    private final String AUTH_TOKEN = "auth_token";
    private final String IS_LOGIN = "is_login";
    private final String USER_ID = "user_id";
    private final String EMAIL = "email";
    private final String NAME = "name";
    private final String MOBILE = "mobile";
    private final String ADDRESS = "address";
    private final String PROFILE_PIC = "profile_pic";
    private final String SELECT_LOCATION = "select_location";
    private final String LOCATION = "location";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String LIST_ID = "list_id";



    public static SessionManager getInstance(Context context) {
        if (session == null) {
            session = new SessionManager();
        }
        if (shared == null) {
            shared = context.getSharedPreferences("TestApp", MODE_PRIVATE);
            editor = shared.edit();
        }
        return session;
    }

    public String getAuthToken() {
        return shared.getString(AUTH_TOKEN, "");
    }

    public void setAuthToken(String authToken) {
        editor.putString(AUTH_TOKEN, authToken);
        editor.commit();
    }

    public boolean isLogin() {
        return shared.getBoolean(IS_LOGIN, false);
    }

    public void setLogin() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }


    public String getUSER_ID() {
        return shared.getString(USER_ID, "");
    }

    public void setUSER_ID(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getEMAIL() {
        return shared.getString(EMAIL, "");
    }

    public void setEMAIL(String email) {
        editor.putString(EMAIL, email);
        editor.commit();
    }
    public String getPROFILE_PIC() {
        return shared.getString(PROFILE_PIC, "");
    }

    public void setPROFILE_PIC(String profile_pic) {
        editor.putString(PROFILE_PIC, profile_pic);
        editor.commit();
    }
    public String getNAME() {
        return shared.getString(NAME, "");
    }

    public void setNAME(String name) {
        editor.putString(NAME, name);
        editor.commit();
    }

    public String getLOCATION() {
        return shared.getString(LOCATION, "");
    }

    public void setLOCATION(String location) {
        editor.putString(LOCATION, location);
        editor.commit();
    }
    public String getLATITUDE() {
        return shared.getString(LATITUDE, "");
    }

    public void setLATITUDE(String latitude) {
        editor.putString(LATITUDE, latitude);
        editor.commit();
    }
    public String getLONGITUDE() {
        return shared.getString(LONGITUDE, "");
    }

    public void setLONGITUDE(String longitude) {
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }
    public boolean isSELECT_LOCATION() {
        return shared.getBoolean(SELECT_LOCATION, false);
    }

    public void setSELECT_LOCATION() {
        editor.putBoolean(SELECT_LOCATION, true);
        editor.commit();
    }

    public String getMOBILE() {
        return shared.getString(MOBILE, "");
    }

    public void setMOBILE(String mobile) {
        editor.putString(MOBILE, mobile);
        editor.commit();
    }
    public String getADDRESS() {
        return shared.getString(ADDRESS, "");
    }

    public void setADDRESS(String address) {
        editor.putString(ADDRESS, address);
        editor.commit();
    }

    public String getLIST_ID() {
        return shared.getString(LIST_ID, "");
    }

    public void setLIST_ID(String listId) {
        editor.putString(LIST_ID, listId);
        editor.commit();
    }

    public void logout() {
        editor.putString(USER_ID, "");
        editor.putString(LIST_ID, "");
        editor.putString(NAME, "");
        editor.putString(AUTH_TOKEN, "");
        editor.putString(EMAIL, "");
        editor.putString(MOBILE, "");
        editor.putString(ADDRESS, "");
        editor.putString(PROFILE_PIC, "");
        editor.putString(LOCATION, "");
        editor.putBoolean(SELECT_LOCATION, false);
        editor.putString(LATITUDE, "");
        editor.putString(LONGITUDE, "");
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }
}