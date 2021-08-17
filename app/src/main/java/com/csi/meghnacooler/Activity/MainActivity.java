package com.csi.meghnacooler.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.HeadOffice.TicketListFragment;
import com.csi.meghnacooler.PersonalInfo.PersonalInfoFragment;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.HeadOffice.StockFragment;
import com.csi.meghnacooler.Technician.ActivityComplainList;
import com.csi.meghnacooler.Technician.RequisitionListFragment;
import com.csi.meghnacooler.Technician.StockRequisitionFragment;
import com.csi.meghnacooler.Utility.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    Menu menu;
    SharedPreferences sharedPreferencesUser;
    String userGroup,userId,recentToken,oldToken;
    private static final int PERMISSION_REQUEST_CODE = 1;
    StringRequest stringRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferencesUser = MainActivity.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
        userGroup = sharedPreferencesUser.getString(Constant.sharedPrefItems.GROUP,"").toUpperCase();
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        String me = sharedPreferencesUser.getString(Constant.sharedPrefItems.REMEMBER_ME,"");
        recentToken = sharedPreferencesUser.getString(Constant.sharedPrefItems.RECENT_TOKEN,"");
        oldToken = sharedPreferencesUser.getString(Constant.sharedPrefItems.OLD_TOKEN,"");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Log.d("group",userGroup);
        Log.d("oldToken",oldToken);
        Log.d("recentToken",recentToken);
        saveTokenToServer();
        /*if ( !"".equals(recentToken) && !oldToken.equals(recentToken) && userGroup.toLowerCase().matches("technician")){
            Log.d("TokenTest","Success");
            saveTokenToServer();
            editor.putString(Constant.sharedPrefItems.OLD_TOKEN,recentToken);
            editor.commit();
        }*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        textViewUserName.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_NAME,""));
        textViewEmail.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.EMAIL,""));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new PersonalInfoFragment());
        toolbar.setTitle(R.string.personalInfoTitle);
        transaction.commit();
        showHideMenu();
    }

    private void saveTokenToServer() {
        final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        stringRequest = new StringRequest(Request.Method.PATCH, Constant.API.NOTIFICATION + "/" + userId + "/" + recentToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // loadingDialog.dismiss();
                Log.d("URL_1", stringRequest.toString());
                Log.d("response",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loadingDialog.dismiss();
                //Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("Token DAta", params.toString());
                Log.d("URL", requestQueue.toString());
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    private void showHideMenu() {
        if ( userGroup.matches("HEAD_OFFICE")){
            menu.findItem(R.id.nav_stock_requisition).setVisible(false);
            menu.findItem(R.id.nav_stock_requisition_list).setVisible(false);
            menu.findItem(R.id.nav_complain_list).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,ActivitySetting.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_personal_info) {
            selectedFragment = new PersonalInfoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            toolbar.setTitle(R.string.personalInfoTitle);
        } else if (id == R.id.nav_stock_follow_up) {
            selectedFragment = new StockFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            toolbar.setTitle(R.string.stock);
        } else if (id == R.id.nav_ticket_follow_up) {
            selectedFragment = new TicketListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            toolbar.setTitle(R.string.ticketFollowUp);
        } else if (id == R.id.nav_stock_requisition) {
            selectedFragment = new StockRequisitionFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            toolbar.setTitle(R.string.stockRequisition);
        }
        else if (id == R.id.nav_stock_requisition_list) {
            selectedFragment = new RequisitionListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            toolbar.setTitle(R.string.requisitionList);
        }else if (id == R.id.nav_complain_list) {
            if (Build.VERSION.SDK_INT >= 17) {
                if (checkPermission()) {
                    try {
                        startActivity(new Intent(MainActivity.this,ActivityComplainList.class));
                    }catch (Exception e){}
                    Log.e("value", "Permission already Granted");
                } else {
                    requestPermission();
                    //startActivity(new Intent(MainActivity.this,ActivityStartScan.class));
                }
            } else {
                Log.e("value", "Not required for requesting runtime permission");
            }
            //startActivity( new Intent(MainActivity.this,ActivityComplainList.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean checkPermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MainActivity.this, "Location permission allows us to take your current location. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    private void logoutUser() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_logout);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText("Logout & Exit the Application?");
        imageView.startAnimation(animation);

        Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo);
        Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesUser = getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                editor.clear();
                editor.commit();
                deleteAppData();
                startActivity(new Intent(MainActivity.this,ActivityLogin.class));
                finish();
            }
        });
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });

        dialog.show();
    }

    private void deleteAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
