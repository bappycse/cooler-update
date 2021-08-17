package com.csi.meghnacooler.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ActivitySetting extends AppCompatActivity {
    SegmentedGroup segmentedGroup;
    LinearLayout linearLayoutPersonalInfo, linearLayoutChangePassword;
    Animation animationLeftRight,animation;
    Toolbar toolbar;
    TextView textViewName,textViewDesignation,textViewEmail,textViewPhone,textViewUserId,textViewUserGroup,textViewNote;
    EditText editTextOldPassword,editTextnewPassword,editTextConfirmPassword;
    Context context = this;
    SharedPreferences sharedPreferencesUser;
    Button buttonChangePassword,buttonUpdateProfile;
    String oldPassword,newPassword,confirmPassword;
    JSONObject jsonObject;
    String message,userId,userName,email,phone,note;
    ACProgressFlower dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
        initToolBar();
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        animationLeftRight = AnimationUtils.loadAnimation(ActivitySetting.this,R.anim.left_enter);
        animation = AnimationUtils.loadAnimation(ActivitySetting.this,R.anim.slide_up);
        linearLayoutPersonalInfo.setAnimation(animation);
        sharedPreferencesUser = ActivitySetting.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        textViewName.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_NAME,""));
        textViewUserId.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,""));
        textViewDesignation.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.DESIGNATION,""));
        textViewUserGroup.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.GROUP,""));
        textViewEmail.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.EMAIL,""));
        textViewPhone.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.PHONE,""));
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        //linearLayoutChangePassword.setAnimation(animationLeftRight);
        switcher();
        promotClick();
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPassword = editTextOldPassword.getText().toString();
                newPassword = editTextnewPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                if ( oldPassword.isEmpty()){
                    editTextOldPassword.setError("Enter your current password");
                    editTextOldPassword.requestFocus();
                }
                else if ( newPassword.isEmpty()){
                    editTextnewPassword.setError("Enter new password");
                    editTextnewPassword.requestFocus();
                }
                else if ( confirmPassword.isEmpty()){
                    editTextConfirmPassword.setError("Enter confirm password");
                    editTextConfirmPassword.requestFocus();
                }
                else if ( !newPassword.matches(confirmPassword)){
                    Toast.makeText(ActivitySetting.this, "Password don't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    changePassword();
                }
            }
        });
         buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 userName = textViewName.getText().toString();
                 email = textViewEmail.getText().toString();
                 phone = textViewPhone.getText().toString();
                 note = textViewNote.getText().toString();
                 updateProfile();
             }
         });
    }

    private void updateProfile() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(ActivitySetting.this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constant.API.UPDATE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseText) {
                try {
                    jsonObject = new JSONObject(responseText);
                    String message = jsonObject.getString("message");
                    Toast.makeText(ActivitySetting.this,message, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                    editor.putString(Constant.sharedPrefItems.USER_NAME,userName);
                    editor.putString(Constant.sharedPrefItems.EMAIL,email);
                    editor.putString(Constant.sharedPrefItems.PHONE,phone);
                    editor.putString(Constant.sharedPrefItems.NOTE,note);
                    editor.commit();
                    //logoutUser();
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonError);
                        message = jsonObject.getString("message");
                        Toast.makeText(ActivitySetting.this,message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Accept", "application/json");
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;

            }
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId",userId);
                params.put("userFirstName","");
                params.put("userLastName",userName);
                params.put("userEmail",email);
                params.put("userPhone",phone);
                params.put("userNote",note);
                Log.d("All DATA", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void changePassword() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(ActivitySetting.this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constant.API.CHANGE_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseText) {
                try {
                    jsonObject = new JSONObject(responseText);
                    String message = jsonObject.getString("message");
                    Toast.makeText(ActivitySetting.this,message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    logoutUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonError);
                        message = jsonObject.getString("message");
                        Toast.makeText(ActivitySetting.this,message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Accept", "application/json");
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;

            }
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId",userId);
                params.put("newPassword",newPassword);
                params.put("oldPassword",oldPassword);
                Log.d("All DATA", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void logoutUser() {
        sharedPreferencesUser = getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(ActivitySetting.this,ActivityLogin.class));
        finish();
    }

    private void promotClick() {
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                View promptsView = layoutInflater.inflate(R.layout.prompts, null);
                final EditText editTextPromptValue = (EditText) promptsView.findViewById(R.id.editTextPromptValue);
                new AlertDialog.Builder(context,R.style.MyDialogTheme)
                        .setView(promptsView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String promotText = editTextPromptValue.getText().toString();
                                textViewName.setText(promotText);
                            }
                        })
                        .show()
                        .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreen));
            }
        });
        textViewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                View promptsView = layoutInflater.inflate(R.layout.prompts, null);
                final EditText editTextPromptValue = (EditText) promptsView.findViewById(R.id.editTextPromptValue);
                new AlertDialog.Builder(context,R.style.MyDialogTheme)
                        .setView(promptsView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String promotText = editTextPromptValue.getText().toString();
                                textViewNote.setText(promotText);
                            }
                        })
                        .show()
                        .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreen));
            }
        });
        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                View promptsView = layoutInflater.inflate(R.layout.prompts, null);
                final EditText editTextPromptValue = (EditText) promptsView.findViewById(R.id.editTextPromptValue);
                editTextPromptValue.setInputType(InputType.TYPE_CLASS_PHONE);
                new AlertDialog.Builder(context,R.style.MyDialogTheme)
                        .setView(promptsView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String promotText = editTextPromptValue.getText().toString();
                                textViewPhone.setText(promotText);
                            }
                        })
                        .show()
                        .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreen));
            }
        });
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                View promptsView = layoutInflater.inflate(R.layout.prompts, null);
                final EditText editTextPromptValue = (EditText) promptsView.findViewById(R.id.editTextPromptValue);
                editTextPromptValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                new AlertDialog.Builder(context,R.style.MyDialogTheme)
                        .setView(promptsView)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String promotText = editTextPromptValue.getText().toString();
                                textViewEmail.setText(promotText);
                            }
                        })
                        .show()
                        .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreen));

            }
        });
        textViewUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitySetting.this, "You can't change user id", Toast.LENGTH_SHORT).show();
            }
        });
        textViewDesignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitySetting.this, "You can't change designation", Toast.LENGTH_SHORT).show();
            }
        });textViewUserGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitySetting.this, "You can't change user group", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void switcher() {
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.radioButtonPassword){
                    linearLayoutChangePassword.setVisibility(View.VISIBLE);
                    linearLayoutPersonalInfo.setVisibility(View.GONE);
                }
                else {
                    linearLayoutChangePassword.setVisibility(View.GONE);
                    linearLayoutPersonalInfo.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private void initUI() {
        segmentedGroup = (SegmentedGroup) findViewById(R.id.segmentedGroupSwitcher);
        linearLayoutPersonalInfo = (LinearLayout) findViewById(R.id.layoutViewProfileInfo);
        linearLayoutChangePassword = (LinearLayout) findViewById(R.id.layoutEditPassword);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDesignation = (TextView) findViewById(R.id.textViewDesignation);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);
        textViewUserGroup = (TextView) findViewById(R.id.textViewUserGroup);
        textViewUserId = (TextView) findViewById(R.id.textViewUserId);
        textViewNote = (TextView) findViewById(R.id.textViewNote);
        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonUpdateProfile = (Button) findViewById(R.id.buttonUpdateProfile);
        editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword);
        editTextnewPassword = (EditText) findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
    }
}
