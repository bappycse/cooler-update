package com.csi.meghnacooler.FireBase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.csi.meghnacooler.Utility.Constant;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jahid on 18/2/19.
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String TAG = "TOKEN";
    SharedPreferences sharedPreferencesUser;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d("HI","HI");
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sharedPreferencesUser = getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferencesUser.edit();
        Intent registrationComplete = null;
        /*sharedPreferencesUser = this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        String string = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");*/
        try {
            String recent_token = FirebaseInstanceId.getInstance().getToken();
            Log.d("Token",recent_token);
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token",recent_token);
            String oldToken = sharedPreferences.getString(TAG,"");
            Log.d("OldToken",oldToken);
            editor1.putString(Constant.sharedPrefItems.RECENT_TOKEN,recent_token);
            editor1.putString(Constant.sharedPrefItems.OLD_TOKEN,oldToken);
            Log.d("TokenOl",oldToken);
            editor1.commit();
            if (!"".equals(recent_token) && !oldToken.equals(recent_token)){
                //saveTokenToServer(recent_token);
                editor1.putString(Constant.sharedPrefItems.RECENT_TOKEN, recent_token);
                editor1.commit();
                Log.d("_if",sharedPreferencesUser.getString(Constant.sharedPrefItems.RECENT_TOKEN,""));
            }
            else{
                Log.w("GCMRegistrationService", "Old token");
            }
        }catch (Exception e){
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void saveTokenToServer(String recent_token) {
        Log.d("MLF","MLF");
        Map paramPost = new HashMap();
        paramPost.put("action","post");
        paramPost.put("token", recent_token);
        try {
            String msgResult = getStringResultFromService_POST(Constant.API.NOTIFICATION, paramPost);
            //Log.w("ServiceResponseMsg", msgResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getStringResultFromService_POST(String serviceURL, Map<String, String> params) {
        HttpURLConnection cnn = null;
        String line = null;
        URL url;
        try{
            url = new URL(serviceURL);
        } catch (MalformedURLException e){
            throw  new IllegalArgumentException("URL invalid:"+serviceURL);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        //Construct the post body using the parameter
        while (iterator.hasNext()){
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if(iterator.hasNext()){
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString(); //format same to arg1=val1&arg2=val2
        //Log.w("AccessService", "param:" + body);
        byte[]bytes = body.getBytes();
        try{
            cnn = (HttpURLConnection)url.openConnection();
            cnn.setDoOutput(true);
            cnn.setUseCaches(false);
            cnn.setFixedLengthStreamingMode(bytes.length);
            cnn.setRequestMethod("POST");
            cnn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            //Post the request
            OutputStream outputStream = cnn.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();
            //Handle the response
            int status = cnn.getResponseCode();
            if(status!=200){
                throw  new IOException("Post fail with error code:" + status);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line+'\n');
            }
            return stringBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
