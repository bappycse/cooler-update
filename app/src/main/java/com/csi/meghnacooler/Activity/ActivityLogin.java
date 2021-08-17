package com.csi.meghnacooler.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Utility.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressBaseDialog;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ActivityLogin extends AppCompatActivity {
    Button buttonLogin;
    EditText editTextUserId,editTextPassword;
    CheckBox checkBoxRememberMe;
    String userId,password;
    Animation animationShake;
    Context context = this;
    JSONObject jsonObject;
    String message,errorMessage,status,rememberMe;
    SharedPreferences sharedPreferencesUser;
    ACProgressFlower dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        animationShake = AnimationUtils.loadAnimation(context,R.anim.shake);
        sharedPreferencesUser = getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,MODE_PRIVATE);
        rememberMe = sharedPreferencesUser.getString(Constant.sharedPrefItems.REMEMBER_ME,"");
        if( rememberMe.matches("1")) {
            startActivity(new Intent(ActivityLogin.this, MainActivity.class));
            finish();
        }
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        initUI();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //startActivity(new Intent(ActivityLogin.this,MainActivity.class));
                userId = editTextUserId.getText().toString();
                password = editTextPassword.getText().toString();

                if ( userId.isEmpty() ){
                    editTextUserId.setError("Enter User Id");
                    editTextUserId.requestFocus();
                    editTextUserId.setAnimation(animationShake);
                }
                else if ( password.isEmpty() ){
                    editTextPassword.setError("Enter Password");
                    editTextPassword.requestFocus();
                    editTextPassword.setAnimation(animationShake);
                }
                else {
                    sendData();
                }
            }
        });
    }

    private void sendData() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(ActivityLogin.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.API.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseText) {
                try {
                    jsonObject = new JSONObject(responseText);
                    status = jsonObject.getString("status");
                    if ( status.matches("1")) {
                        message = jsonObject.getString("message");
                    final String userId = jsonObject.getString("id");
                    final String userNameID = jsonObject.getString("user_id");
                    final String userName = jsonObject.getString("name");
                    final String designation = jsonObject.getString("designation");
                    final String email_address = jsonObject.getString("email_address");
                    final String phone = jsonObject.getString("phone");
                    final String group = jsonObject.getString("group");
                    if ( checkBoxRememberMe.isChecked()){
                        rememberMe = "1";
                    }
                    else {
                        rememberMe = "0";
                    }
                    dialog.dismiss();
                    Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                    SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                    editor.putString(Constant.sharedPrefItems.USER_NAME,userName);
                    editor.putString(Constant.sharedPrefItems.USER_ID,userId);
                    editor.putString(Constant.sharedPrefItems.USER_NAME_ID,userNameID);
                    editor.putString(Constant.sharedPrefItems.DESIGNATION,designation);
                    editor.putString(Constant.sharedPrefItems.EMAIL,email_address);
                    editor.putString(Constant.sharedPrefItems.PHONE,phone);
                    editor.putString(Constant.sharedPrefItems.GROUP,group);
                    editor.putString(Constant.sharedPrefItems.REMEMBER_ME,rememberMe);
                    Log.d("user_id",userId);
                    editor.commit();
                        finish();
                        startActivity(intent);
                    }
                    else {
                        message = jsonObject.getString("message");
                        Toast.makeText(ActivityLogin.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityLogin.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(context, "Server Error Please contact your admin", Toast.LENGTH_SHORT).show();
                Log.d("ResponseMessage: ", "Error: "+error.getMessage());
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.d("ErrorMessage: ", "JsonString: "+jsonError.toString());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonError);
                        errorMessage = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
               /* flag = 1;
                loadingDialog.dismiss();
                showErrorDialog();*/
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", userId);
                map.put("password",password);
                return map;
            }
        };
        queue.add(stringRequest);
    }

    private void initUI() {
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        editTextUserId = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkboxRememberMe);
    }
}
