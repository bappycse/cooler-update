package com.csi.meghnacooler.Technician;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.Activity.MainActivity;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Sqlite.DataProblem;
import com.csi.meghnacooler.Sqlite.SqlitieFunction;
import com.csi.meghnacooler.Utility.Constant;
import com.csi.meghnacooler.Utility.Solved;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.gson.Gson;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ActivityComplainList extends AppCompatActivity {
    Toolbar toolbar;
    SegmentedGroup segmentedGroupSwitcher, segmentedGroupComplainList;
    RecyclerView recyclerViewComplainList,recyclerViewSolvedList;
    LinearLayout layoutTechnician;
    int openFlag=0;
    JSONArray jsonArrayactionSelect;

    List<Complain> complainList = new ArrayList<>();
    List<Complain> openComplainList = new ArrayList<>();
    List<Complain> acceptComplainList = new ArrayList<>();
    List<Complain> ongoingComplainList = new ArrayList<>();
    List<Complain> pendingComplainList = new ArrayList<>();
    List<Complain> solvedComplainList = new ArrayList<>();
    List<Complain> unSolvedComplainList = new ArrayList<>();

    ComplainAdapter adapter;

    SharedPreferences sharedPreferencesUser;
    String userId,stringStatus, URL, forwardUrl, complainId,note,buttonText,complainTypeId,stringComplain,problemName, problemId, tecnicianId;
    String statusPending = "pending", statusAssign = "assigned",statusOngoing = "ongoing",statusWorkPending = "workpending", statusCompleted = "completed",statusSolved = "solved", statusUnSolved = "unsolved",statusForwarded = "forwarded",statusWorkshopAssigned = "fr_assigned",statusWorkshopDelivered = "fr_delivered",statusWorkshopReceived = "fr_solved", latitudes = "", longitudes = "", address = "";
    double latitude,longitude,shopLatitude,shopLongitude,distance;
    LocationManager locationManager;
    BigDecimal bigDecimal;
    View view;
    Dialog dialog;
    int intDistance,spinnerPosition;
    TextView textViewPromptsTicketNo,textViewPromptsShopName,textViewPromptsShopAddress,textViewPromptsContactPrson,textViewPromptsMobileNo,textViewPromptsTicketStatus,textViewPromotCoolerNo,textViewPromotComplainType,textViewPromotsNote;
    Button buttonAction,buttonRequisition,buttonExtraCost, buttonComplainUpdate;
    Spinner spinnerProblemType, spinnerActionList;
    ACProgressFlower acProgressFlower;
    private ArrayList<String> complainTypeList = new ArrayList<>();
    JSONArray jsonArrayComplainType;
    boolean GpsStatus;
    SqlitieFunction sqlitieFunction;
    SQLiteDatabase sqLiteDatabase;
    Context context = this;
    Cursor cursor;
    SQLiteListAdapterProblem listAdapter;
    ArrayList<String> idArrayList = new ArrayList<String>();
    ArrayList<String> problemArrayList = new ArrayList<String>();
    ArrayList<String> problemIdArrayList = new ArrayList<String>();
    ArrayList<Solved> solvedArrayList = new ArrayList<>();
    ExpandableHeightListView listViewComplain;
    JSONObject jsonObjectData,jsonObjectFormData,jsonObjectProblem, jsonObjectComplainActionStatus, jsonObjectFinal;
    JSONArray jsonArrayProblemlist,jsonArrayComplain;
    String workshopName,workshopAddress,workshopPhone,workshopInfo,workshopAssignDate,workshopDeliveryDate, _getInventory_name, _getInventory_ID;;
    LinearLayout relativeLayoutWorkshop;
    TextView textViewWorkshopName,textViewWorkshopAddress,textViewWorkshopPhone,textViewWorkshopAssignDate,textViewWorkshopDeliverDate;
    RelativeLayout relativeLayoutAllList,relativeLayoutSolvedList;
    ActionStatus.status status;

    List<String> receiveDoneList= new ArrayList<>();
    List<String> receivePendingList= new ArrayList<>();
    List<String> receiveWorkshopList= new ArrayList<>();
    List<String> receiveNoProblemList= new ArrayList<>();
    List<String> getSelectList= new ArrayList<>();
    int workshopFlag= 0, solvedFlag= 0;
    int checkSegmentBtn= R.id.radio_button_all;
    String shopID;
    public static java.lang.String toJSONString(java.util.List list){
        return list.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_list);
        sharedPreferencesUser = ActivityComplainList.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        status= new ActionStatus.status();
        initUI();
        acProgressFlower = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).
                fadeColor(Color.BLUE).build();
        recyclerViewComplainList.setHasFixedSize(true);
        recyclerViewComplainList.setLayoutManager(new LinearLayoutManager(ActivityComplainList.this));
        recyclerViewComplainList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ActivityComplainList.this).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());

        initToolBar();
        //sendData();
        location();
        sqlitieFunction = new SqlitieFunction(context);

    }

    private void location() {
        Location location;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location!=null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latitudes = String.valueOf(latitude);
            longitudes = String.valueOf(longitude);
            getAddress(latitude,longitude);
        }
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(ActivityComplainList.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            address = obj.getAddressLine(0);
            Log.d("IGA", "Address" + address);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendData() {
        Log.d("check_unsolved_3", "execute");
        complainList.clear();
        openComplainList.clear();
        acceptComplainList.clear();
        ongoingComplainList.clear();
        solvedComplainList.clear();
        unSolvedComplainList.clear();
        pendingComplainList.clear();

        acProgressFlower.show();
        try {
            Log.d("check_unsolved_4", "execute");
            Log.d("ip",Constant.API.COMPLAIN_LIST +"/"+ userId);
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.API.COMPLAIN_LIST +"/"+ userId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("check_unsolved_5", "execute");
                            try {
                                acProgressFlower.dismiss();
                                Log.d("check_unsolved_6", "execute");
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = jsonObjectData.getJSONArray("complain_list");
                                Log.d("all",jsonArray.toString());
                                //getComplainType(jsonArray);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                        String shopInfo = jsonObject1.getString("shop__");
                                        String getStatusCheck = jsonObject1.getString("status");
                                        Log.i("getStatusCheck: ", getStatusCheck);

                                        String shop_div_id= jsonObject1.getString("shop_division_id");
                                        String tech_div_id= jsonObject1.getString("tech_division_id");
                                        Log.d("chek_shop_teck_id: ", shop_div_id +" and "+tech_div_id);

                                        if(shop_div_id.equals(tech_div_id)) {

                                            workshopInfo = jsonObject1.getString("workshop_details");
                                            workshopAssignDate = jsonObject1.getString("workshop_assign_date");
                                            workshopDeliveryDate = jsonObject1.getString("workshop_delivery_date");

                                            _getInventory_ID = jsonObject1.getString("inventory_id");
                                            _getInventory_name = jsonObject1.getString("inventory_name");
                                            Log.d("check_inventory_name", _getInventory_name+" and id: "+_getInventory_ID);
                                            _saveDataSharedPreferences(_getInventory_ID, _getInventory_name);

                                            JSONObject jsonObjectShopInfo = new JSONObject(shopInfo);

                                            if (!workshopInfo.matches("null")) {
                                                JSONObject jsonObjectWorkshopInfo = new JSONObject(workshopInfo);
                                                workshopName = jsonObjectWorkshopInfo.getString("name");
                                                workshopAddress = jsonObjectWorkshopInfo.getString("address");
                                                workshopPhone = jsonObjectWorkshopInfo.getString("phone");
                                            }

                                            complainList.add(new Complain(
                                                    jsonObject1.optString("id"),
                                                    jsonObject1.optString("ticket_no"),
                                                    jsonObject1.optString("date"),
                                                    jsonObject1.optString("status"),
                                                    jsonObject1.optString("region_name"),
                                                    jsonObject1.optString("priority"),
                                                    jsonObject1.optString("contact_name"),
                                                    jsonObject1.optString("contact_number"),
                                                    jsonObject1.optString("contact_email"),
                                                    jsonObject1.optString("note"),
                                                    jsonObject1.optString("shop_id"),
                                                    jsonObjectShopInfo.optString("name"),
                                                    jsonObjectShopInfo.optString("contact_person"),
                                                    jsonObjectShopInfo.optString("phone"),
                                                    jsonObject1.optString("shop_address"),
                                                    jsonObject1.optString("complain_type_name"),
                                                    jsonObject1.optString("cooler_serial"),
                                                    jsonObject1.optJSONArray("complain_type_arr"),
                                                    jsonObject1.optString("complain_type_feedback"),
                                                    jsonObject1.optString("last_technician_assign_id"),
                                                    jsonObject1.optString("last_technician_assign_name"),
                                                    workshopName,
                                                    workshopAddress,
                                                    workshopPhone,
                                                    workshopAssignDate,
                                                    workshopDeliveryDate,
                                                    workshopInfo,
                                                    jsonObject1.optJSONArray("solve_type_arr")
                                            ));

                                            //Log.d("show_allData_first:", complainList.toString());

                                            if (getStatusCheck.toLowerCase().equals("pending")) {
                                                openComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                            else if (getStatusCheck.toLowerCase().equals("assigned")) {
                                                acceptComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                            else if (getStatusCheck.toLowerCase().equals("ongoing")) {
                                                ongoingComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                            else if (getStatusCheck.toLowerCase().equals("workpending")) {
                                                pendingComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                            else if (getStatusCheck.toLowerCase().equals("solved")) {
                                                solvedComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                            else if (getStatusCheck.toLowerCase().equals("unsolved")) {
                                                unSolvedComplainList.add(new Complain(
                                                        jsonObject1.optString("id"),
                                                        jsonObject1.optString("ticket_no"),
                                                        jsonObject1.optString("date"),
                                                        jsonObject1.optString("status"),
                                                        jsonObject1.optString("region_name"),
                                                        jsonObject1.optString("priority"),
                                                        jsonObject1.optString("contact_name"),
                                                        jsonObject1.optString("contact_number"),
                                                        jsonObject1.optString("contact_email"),
                                                        jsonObject1.optString("note"),
                                                        jsonObject1.optString("shop_id"),
                                                        jsonObjectShopInfo.optString("name"),
                                                        jsonObjectShopInfo.optString("contact_person"),
                                                        jsonObjectShopInfo.optString("phone"),
                                                        jsonObject1.optString("shop_address"),
                                                        jsonObject1.optString("complain_type_name"),
                                                        jsonObject1.optString("cooler_serial"),
                                                        jsonObject1.optJSONArray("complain_type_arr"),
                                                        jsonObject1.optString("complain_type_feedback"),
                                                        jsonObject1.optString("last_technician_assign_id"),
                                                        jsonObject1.optString("last_technician_assign_name"),
                                                        workshopName,
                                                        workshopAddress,
                                                        workshopPhone,
                                                        workshopAssignDate,
                                                        workshopDeliveryDate,
                                                        workshopInfo,
                                                        jsonObject1.optJSONArray("solve_type_arr")
                                                ));
                                            }

                                        }
                                    }

                                    Log.d("unsolved_check: ", String.valueOf(unSolvedComplainList.size()));

                                if (checkSegmentBtn == R.id.radio_button_all){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, complainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                } else if (checkSegmentBtn == R.id.radio_button_open){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, openComplainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                } else if (checkSegmentBtn == R.id.radio_button_acceepted){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, acceptComplainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                } else if (checkSegmentBtn == R.id.radio_button_ongoing){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, ongoingComplainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                } else if (checkSegmentBtn == R.id.radio_button_solved){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, solvedComplainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                }  else if (checkSegmentBtn == R.id.radio_button_pending){
                                    ComplainAdapter adapter = new ComplainAdapter(ActivityComplainList.this, pendingComplainList);
                                    recyclerViewComplainList.setAdapter(adapter);
                                }

                                segmentedGroupComplainList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                                        if (checkId == R.id.radio_button_all) {
                                            checkSegmentBtn = R.id.radio_button_all;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, complainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        } else if (checkId == R.id.radio_button_open) {
                                            checkSegmentBtn = R.id.radio_button_open;
                                            openFlag++;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, openComplainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        } else if (checkId == R.id.radio_button_acceepted) {
                                            checkSegmentBtn = R.id.radio_button_acceepted;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, acceptComplainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        } else if (checkId == R.id.radio_button_ongoing) {
                                            checkSegmentBtn = R.id.radio_button_ongoing;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, ongoingComplainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        } else if (checkId == R.id.radio_button_pending) {
                                            checkSegmentBtn = R.id.radio_button_pending;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, pendingComplainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        } else if (checkId == R.id.radio_button_solved) {
                                            checkSegmentBtn = R.id.radio_button_solved;
                                            adapter = new ComplainAdapter(ActivityComplainList.this, solvedComplainList);
                                            recyclerViewComplainList.setAdapter(adapter);
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                acProgressFlower.dismiss();
                            }
                        }

                        private void getComplainType(JSONArray jsonArray) {
                            for(int i = 0; i<jsonArray.length(); i++) {
                                try {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    JSONArray jsonArray1 = json.getJSONArray("complain_type_arr");
                                    for ( int i1 = 0; i1<jsonArray1.length(); i1++){
                                        JSONObject jsonObject = jsonArray1.getJSONObject(i1);
                                        problemName = jsonObject.getString("name");
                                        problemId = jsonObject.getString("id");
                                        Log.d("problemName", problemName);
                                        Log.d("problemID", problemId);
                                        //problemInsert();
                                    }
                                    //complainTypeList.add(json.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                          acProgressFlower.dismiss();
                        }
                    });
                  Volley.newRequestQueue(this).add(stringRequest);
        }catch (Exception e){}
    }

    private void initUI() {
        recyclerViewComplainList = (RecyclerView) findViewById(R.id.recylcerViewComplainList);
        segmentedGroupComplainList = findViewById(R.id.segmented_group_complain_list);
        layoutTechnician= (LinearLayout) this.findViewById(R.id.linearLayout_technician);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.complainList);
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

    public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder> {
        Context context;
        List<Complain> complainList;
        String nullObject = "null";
        String getDate, formatedDate;

        public ComplainAdapter(Context context, List<Complain> complainList) {
            this.context = context;
            this.complainList = complainList;
        }

        @Override
        public ComplainAdapter.ComplainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.complain_card, null);
            return new ComplainViewHolder(view);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onBindViewHolder(final ComplainAdapter.ComplainViewHolder holder, int position) {
            /*if (openFlag!=0){
                layoutTechnician.setVisibility(View.GONE);
            }*/
            final Complain complain = complainList.get(position);
            holder.textViewTicketNo.setText(complain.getTicketNo());
            holder.textViewAddress.setText(complain.getShopAddress());
            getDate= complain.getDate();
            Log.i("total_date", getDate);
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(getDate);
                formatedDate = new SimpleDateFormat("MM.dd.yyyy").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.textViewRegion.setText(complain.getRegion_name());
            holder.textViewTechnician.setText(complain.getTechnicianName());
            //holder.textViewTechnician.setText("complain");
            Log.d("check_user:", complain.getTechnicianName());
            holder.textViewDate.setText("Received Date: "+formatedDate);
            String upperString = complain.getPriority().substring(0,1).toUpperCase() + complain.getPriority().substring(1);
            holder.textViewProblem.setText(upperString);
            holder.textViewProblem.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            stringStatus = complain.getStatus().toLowerCase();
            Log.d("check_status_fr", stringStatus);

            if (stringStatus.matches(statusPending)) {
                holder.textViewStatus.setText("Open");
                holder.textViewStatus.setTextColor(Color.RED);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.textViewTechnicianTitle.setVisibility(View.GONE);
            }
            else if ( stringStatus.matches(statusWorkPending)){
                holder.textViewStatus.setText("Pending");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(Color.BLUE);
            }
            else if (stringStatus.matches(statusAssign)){
                holder.textViewStatus.setText("Accepted");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.darkgreen));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusOngoing)){
                holder.textViewStatus.setText("Ongoing");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.signupColor));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusSolved)){
                holder.textViewStatus.setText("Solved");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusUnSolved)){
                holder.textViewStatus.setText("Unsolved");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusCompleted)){
                holder.textViewStatus.setText("Completed");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusForwarded)){
                holder.textViewStatus.setText("Forwarded");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusWorkshopAssigned)){
                holder.textViewStatus.setText("Workshop Assigned");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusWorkshopDelivered)){
                holder.textViewStatus.setText("Delivered From Workshop");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            else if (stringStatus.matches(statusWorkshopReceived)){
                holder.textViewStatus.setText("Solved");
                holder.textViewTechnicianTitle.setVisibility(View.VISIBLE);
                holder.textViewStatus.setTextColor(getResources().getColor(R.color.textColorTitleSmall));
                holder.imageView.setVisibility(View.INVISIBLE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater layoutInflater = LayoutInflater.from(ActivityComplainList.this);
                    final View promptsView = layoutInflater.inflate(R.layout.complain_prompts1, null,false);
                    Fragment selectedFragment = null;
                    textViewPromptsTicketNo = (TextView) promptsView.findViewById(R.id.textViewTicketNo);
                    textViewPromptsTicketStatus = (TextView) promptsView.findViewById(R.id.textViewShopTicketStatus);
                    textViewPromptsShopName = (TextView) promptsView.findViewById(R.id.textViewShopName);
                    textViewPromptsShopAddress = (TextView) promptsView.findViewById(R.id.textViewShopAddress);
                    textViewPromptsContactPrson = (TextView) promptsView.findViewById(R.id.textViewShopContactPerson);
                    textViewPromptsMobileNo = (TextView) promptsView.findViewById(R.id.textViewShopMobileNo);
                    textViewPromotCoolerNo = (TextView) promptsView.findViewById(R.id.textViewCoolerNo);
                    textViewPromotComplainType = (TextView) promptsView.findViewById(R.id.textViewComplainType);
                    textViewPromotsNote = (TextView) promptsView.findViewById(R.id.textViewSupportDetails);
                    textViewWorkshopName = (TextView) promptsView.findViewById(R.id.textViewWorkshopName);
                    textViewWorkshopAddress = (TextView) promptsView.findViewById(R.id.textViewWorkshopAddress);
                    textViewWorkshopPhone = (TextView) promptsView.findViewById(R.id.textViewWorkshopPhone);
                    textViewWorkshopAssignDate = (TextView) promptsView.findViewById(R.id.textViewWorkshopAssignDate);
                    textViewWorkshopDeliverDate = (TextView) promptsView.findViewById(R.id.textViewWorkshopDeliveryDate);
                    relativeLayoutWorkshop = (LinearLayout) promptsView.findViewById(R.id.relativeWorkshop);
                    buttonAction = (Button) promptsView.findViewById(R.id.buttonAction);
                    buttonComplainUpdate = (Button) promptsView.findViewById(R.id.buttonComplainUpdate);
                    buttonRequisition = (Button) promptsView.findViewById(R.id.buttonRequisition);
                    buttonExtraCost = (Button) promptsView.findViewById(R.id.buttonExtraCost);
                    complainId = complain.getId();
                    Log.d("check_status", complain.getStatus().toLowerCase());
                    checkRequisition();

                    if ( complain.getWorkshopAssignDate().matches("null")){
                        workshopAssignDate = "";
                        textViewWorkshopAssignDate.setText(workshopAssignDate);
                    }else {
                        textViewWorkshopAssignDate.setText(complain.getWorkshopAssignDate());
                    }
                    if ( complain.getWorkshopDeliveryDate().matches("null")){
                        workshopDeliveryDate = "";
                        textViewWorkshopDeliverDate.setText(workshopDeliveryDate);
                    }
                    else {
                        textViewWorkshopDeliverDate.setText(complain.getWorkshopDeliveryDate());
                    }
                    textViewWorkshopName.setText(complain.getWorkshopName());
                    textViewWorkshopAddress.setText(complain.getWorkshopAddress());
                    textViewWorkshopPhone.setText(complain.getWorkshopMobile());

                    stringStatus = complain.getStatus().toLowerCase();
                    tecnicianId = complain.getTechnicianId();
                    if ( tecnicianId.matches("null")){
                        tecnicianId = "";
                    }
                    if ( (complain.getWorkshopInfo().matches("null"))){
                        relativeLayoutWorkshop.setVisibility(View.GONE);
                    }
                    if(stringStatus.matches(statusWorkPending)){
                        /*buttonAction.setText("Attend");
                        buttonAction.setBackgroundColor(getResources().getColor(R.color.darkgreen));*/
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusAssign) /*|| stringStatus.matches(statusWorkPending)*/){
                        buttonAction.setText("Attend");
                        buttonAction.setBackgroundColor(getResources().getColor(R.color.darkgreen));
                    }
                    if (stringStatus.matches(statusOngoing)){
                        buttonAction.setText("CLOSE TICKETS");
                        buttonAction.setBackgroundColor(Color.RED);
                        buttonRequisition.setVisibility(View.VISIBLE);
                        buttonExtraCost.setVisibility(View.GONE);
                        buttonComplainUpdate.setVisibility(View.VISIBLE);
                    }
                    if (stringStatus.matches(statusCompleted)){
                        buttonAction.setText("Completed");
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusSolved)){
                        buttonAction.setText("Solved");
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusUnSolved)){
                        buttonAction.setText("Unsolved");
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusForwarded)){
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusWorkshopAssigned)){
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusWorkshopDelivered)){
                        buttonAction.setVisibility(View.GONE);
                    }
                    if (stringStatus.matches(statusWorkshopReceived)){
                        buttonAction.setVisibility(View.GONE);
                    }
                    if ( !tecnicianId.matches(userId) && !tecnicianId.matches("")){
                        buttonAction.setVisibility(View.GONE);
                        Log.d("HI","HI");
                    }

                    textViewPromptsTicketNo.setText(complain.getTicketNo());
                    textViewPromptsShopName.setText(complain.getShopName());
                    textViewPromptsShopAddress.setText(complain.getShopAddress());
                    textViewPromptsContactPrson.setText(complain.getShopContactPerson());
                    textViewPromotCoolerNo.setText(complain.getCoolerSerial());
                    textViewPromotComplainType.setText(complain.getComplainType());

                    if ( complain.getNote().matches("null")){
                        textViewPromotsNote.setText("");
                    }else {
                        textViewPromotsNote.setText(complain.getNote());
                    }
                    String mobileNo = complain.getContactNumber();
                    if ( mobileNo.matches("null")){
                        textViewPromptsMobileNo.setText("");
                    }else {
                        textViewPromptsMobileNo.setText(complain.getContactNumber());
                    }
                    if (stringStatus.matches(statusPending)) {
                        textViewPromptsTicketStatus.setText("Open");
                        textViewPromptsTicketStatus.setTextColor(Color.RED);
                    } else if (stringStatus.matches(statusAssign)){
                        textViewPromptsTicketStatus.setText("Accepted");
                        textViewPromptsTicketStatus.setTextColor(getResources().getColor(R.color.darkgreen));
                    } else if (stringStatus.matches(statusOngoing)){
                        textViewPromptsTicketStatus.setText("Ongoing");
                    }else if (stringStatus.matches(statusCompleted)){
                        textViewPromptsTicketStatus.setText("Completed");
                    }else if (stringStatus.matches(statusUnSolved)){
                        textViewPromptsTicketStatus.setText("Unsolved");
                    }else if (stringStatus.matches(statusSolved)){
                        textViewPromptsTicketStatus.setText("Solved");
                    }
                    buttonRequisition.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            complainId = complain.getId();
                            Intent intent = new Intent(ActivityComplainList.this, ReceivedRequisitionActivity.class);
                            intent.putExtra("complainId",complainId);
                            Log.d("get_complain_id", complainId.toString());
                            startActivity(intent);
                        }
                    });
                    buttonExtraCost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            complainId = complain.getId();
                            Intent intent = new Intent(ActivityComplainList.this, ExtraCostActivity.class);
                            intent.putExtra("complainId",complainId);
                            startActivity(intent);
                        }
                    });

                    buttonComplainUpdate.setOnClickListener(new View.OnClickListener() {

                        private void loadSolvedProblem() {
                            solvedArrayList.clear();
                            recyclerViewSolvedList.setHasFixedSize(true);
                            recyclerViewSolvedList.setLayoutManager(new LinearLayoutManager(ActivityComplainList.this));
                            recyclerViewSolvedList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ActivityComplainList.this).color(Color.TRANSPARENT).sizeResId(R.dimen.dividerLess).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
                            JSONArray jsonArraySolved = complain.getSolved();
                            Log.d("solved_list_length:", String.valueOf(jsonArraySolved.length()));
                            try {
                                for ( int i = 0; i<jsonArraySolved.length();i++){
                                    JSONObject jsonObject = jsonArraySolved.getJSONObject(i);
                                    solvedArrayList.add(new Solved(jsonObject.getString("name")));
                                }

                            }catch (Exception ignored){}
                            SolvedAdapter solvedAdapter = new SolvedAdapter(ActivityComplainList.this, solvedArrayList);
                            recyclerViewSolvedList.setAdapter(solvedAdapter);
                        }

                        @Override
                        public void onClick(View view) {
                            final Dialog dialogComplain = new Dialog(ActivityComplainList.this);
                            Toast.makeText(ActivityComplainList.this, "Please Select Action", Toast.LENGTH_SHORT).show();
                            dialogComplain.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogComplain.setCancelable(false);
                            dialogComplain.setContentView(R.layout.dialog_complain);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                            //final EditText editTextComplainList = (EditText) dialog.findViewById(R.id.editTextComplain);
                            //editTextComplainList.setText(textViewPromotComplainType.getText().toString());

                            spinnerActionList = findViewById(R.id.spinner_update_requisition);
                            spinnerProblemType = (Spinner) dialogComplain.findViewById(R.id.spinnerProblemType);
                            listViewComplain = (ExpandableHeightListView) dialogComplain.findViewById(R.id.listViewComplain);
                            segmentedGroupSwitcher = (SegmentedGroup) dialogComplain.findViewById(R.id.segmentedGroupSwitcher);
                            relativeLayoutAllList = (RelativeLayout) dialogComplain.findViewById(R.id.scrollView);
                            relativeLayoutSolvedList = (RelativeLayout) dialogComplain.findViewById(R.id.relativeSolvedList);
                            recyclerViewSolvedList = (RecyclerView) dialogComplain.findViewById(R.id.recyclerViewSolvedList);
                            ImageView imageView = (ImageView) dialogComplain.findViewById(R.id.a);
                            imageView.startAnimation(animation);
                            stringComplain = complain.getComplainType();
                            Log.d("problem_type: ", stringComplain);
                            idArrayList.clear();
                            problemArrayList.clear();
                            problemIdArrayList.clear();
                            loadComplainType();
                            loadComplain();
                            showSqliteData();
                            loadSolvedProblem();
                            Button buttonSubmit = (Button) dialogComplain.findViewById(R.id.buttonSubmit);
                            Button buttonAdd = (Button) dialogComplain.findViewById(R.id.buttonAdd);


                            segmentedGroupSwitcher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                                    if ( checkId == R.id.radioButtonSolved){
                                        relativeLayoutAllList.setVisibility(View.GONE);
                                        relativeLayoutSolvedList.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        relativeLayoutAllList.setVisibility(View.VISIBLE);
                                        relativeLayoutSolvedList.setVisibility(View.GONE);
                                    }
                                }
                            });

                            spinnerProblemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    stringComplain = complain.getComplainType();
                                    problemName = spinnerProblemType.getSelectedItem().toString();
                                    problemId = getComplainCode(position-1);
                                    Log.d("get_problem: ", "name: "+problemName+" id: "+problemId+" stringComplain: "+stringComplain);
                                    spinnerPosition = spinnerProblemType.getSelectedItemPosition();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });


                            buttonAdd.setOnClickListener(new View.OnClickListener() {

                                private boolean _checkSolvedList(){
                                    for (Solved solved: solvedArrayList) {
                                        Log.d("array_list_problem: ", solved.getProblemName());
                                        if(solved.getProblemName().toLowerCase().equals(problemName.toLowerCase())){
                                            return true;
                                        }
                                    }
                                    return false;
                                }

                                @Override
                                public void onClick(View view) {
                                    Log.d("spinner_problem_name: "+problemName.toString(), "solved_length: "+solvedArrayList.size());

                                    if(_checkSolvedList()){
                                        Toast.makeText(ActivityComplainList.this, "Already Added to Solved List", Toast.LENGTH_LONG).show();
                                    } else {

                                        if (spinnerPosition == 0) {
                                            Toast.makeText(ActivityComplainList.this, "Select Problem Type", Toast.LENGTH_SHORT).show();
                                        } /*else if (sqlitieFunction.isExistProblem(problemId) == true){
                                          Toast.makeText(ActivityComplainList.this, "Already Added", Toast.LENGTH_LONG).show();
                                          Log.d("not_insert_problem: ", "name: "+spinnerProblemType.getSelectedItem().toString()+" id: "+problemId);
                                          return;
                                        }*/
                                        else if (_checkProblemId(problemId)) {
                                            Log.d("not_insert_problem_id: ", problemId);
                                            Toast.makeText(ActivityComplainList.this, "Already Added", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.d("print_problem_list: " + spinnerProblemType.getSelectedItem().toString(), "problem_id: " + problemId.toString());
                                            problemInsert();
                                        }
                                    }
                                }
                            });

                            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogComplain.show();
                                    receiveActionStatusList();
                                    if (getSelectList.contains("Select")) {
                                        Toast.makeText(ActivityComplainList.this, "Please Select Action", Toast.LENGTH_SHORT).show();
                                    } else {
                                        postComplainActionList();
                                        receiveDoneList.clear();
                                        receivePendingList.clear();
                                        receiveWorkshopList.clear();
                                        receiveNoProblemList.clear();
                                    }
                                }
                                private void postComplainActionList(){

                                    try{
                                        complainId = complain.getId();
                                        sqLiteDatabase = sqlitieFunction.getWritableDatabase();
                                        cursor = sqLiteDatabase.rawQuery("SELECT * FROM problemTable", null);
                                        jsonObjectFormData = new JSONObject();
                                        jsonObjectFinal = new JSONObject();
                                        jsonObjectComplainActionStatus = new JSONObject();
                                        
                                        if (receiveWorkshopList.isEmpty()){
                                            Log.d("workshop_list_empty: ", receiveWorkshopList.toString());
                                        } else{
                                            workshopFlag++;
                                            SharedPreferences sharedPref = context.getSharedPreferences("workshopFlagSave",Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putInt("workshopFlag", workshopFlag);
                                            editor.apply();
                                        }

                                        if (receivePendingList.isEmpty() && !receiveDoneList.isEmpty()){
                                            solvedFlag++;
                                            Log.d("solved_list: ", receiveDoneList.toString());
                                        } else{
                                            Log.d("pending_list_empty: ", receivePendingList.toString());
                                        }

                                        jsonObjectFormData.put("technician_id",userId);
                                        jsonObjectFormData.put("complain_id",complainId);

                                        JSONArray convertJsonDoneList = new JSONArray(receiveDoneList);
                                        JSONArray convertJsonPendingList = new JSONArray(receivePendingList);
                                        JSONArray convertJsonWorkshopList = new JSONArray(receiveWorkshopList);
                                        JSONArray convertJsonNoProblemList = new JSONArray(receiveNoProblemList);

                                        Log.d("check_json_array", convertJsonDoneList.toString()+" pending "+convertJsonPendingList+" work "+convertJsonWorkshopList.toString());

                                        jsonObjectFormData.put("done_list", convertJsonDoneList);
                                        jsonObjectFormData.put("pending_list", convertJsonPendingList);
                                        jsonObjectFormData.put("workshop_list", convertJsonWorkshopList);
                                        jsonObjectFormData.put("noProblem_list", convertJsonNoProblemList);

                                        RequestQueue requestQueue = Volley.newRequestQueue(ActivityComplainList.this);
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constant.API.UPDATE_COMPLAIN, new Response.Listener<String>(){
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String status = jsonObject.getString("type");
                                                    Log.d("show_check_status", status);
                                                    if (status.matches("success")) {
                                                        Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show();
                                                        sqlitieFunction.deleteAllProblem();
                                                        showSqliteData();
                                                        dialogComplain.dismiss();
                                                        dialog.dismiss();
                                                        complainList.clear();
                                                        sendData();
                                                    } else {
                                                        Toast.makeText(context, "Request failed", Toast.LENGTH_SHORT).show();
                                                        dialogComplain.dismiss();
                                                    }
                                                }catch (Exception e){
                                                    Log.d("exception_error: ", e.toString());
                                                    Log.d("error_response: ", response.toString());
                                                    dialogComplain.dismiss();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                dialogComplain.dismiss();
                                            }
                                        }){
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> param = new HashMap<String, String>();
                                                param.put("complain_update", jsonObjectFormData.toString());
                                                Log.d("all_action_update_Data",param.toString());
                                                return param;
                                            }
                                        };
                                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        requestQueue.add(stringRequest);
                                    }catch(Exception ignored){}
                                    finally {
                                        dialogComplain.dismiss();
                                    }
                                }
                            });

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogComplain.dismiss();
                                    complainTypeList.clear();
                                    sqlitieFunction.deleteAllProblem();
                                }
                            });
                            final Handler handler  = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (dialogComplain.isShowing()) {
                                        dialogComplain.dismiss();
                                    }
                                }
                            };
                            dialogComplain.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    handler.removeCallbacks(runnable);
                                }
                            });
                            dialogComplain.show();
                        }

                        private void loadComplain() {
                            sqlitieFunction.deleteAllProblem();
                            JSONArray jsonArray = complain.getProblem();
                            Log.d("problem_list_length:", String.valueOf(jsonArray.length()));
                            try {
                                for ( int i = 0; i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    problemName = jsonObject.getString("name");
                                    problemId = jsonObject.getString("id");
                                    problemInsert();
                                }

                            }catch (Exception ignored){}
                        }
                    });

                    buttonAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            complainId = complain.getId();
                            buttonText =buttonAction.getText().toString().toLowerCase();
                            if ( buttonText.matches("accept") ){
                                URL = Constant.API.COMPLAIN_ACTION + "/" + userId + "/accept" + "/" + complainId;
                                Log.i("button_action: ", URL);
                                sendAction();
                            }
                            if ( buttonText.matches("attend") ){
                                URL = Constant.API.COMPLAIN_ACTION + "/" + userId + "/attend" + "/" + complainId;
                                Log.d("check_pass_url:", URL.toString());
                                address = complain.getShopAddress();
                                sendAction();
                            }

                            if ( buttonText.matches("close tickets")){
                                final Dialog dialog = new Dialog(ActivityComplainList.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.dialog_close);

                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                                final EditText editTextNote = (EditText) dialog.findViewById(R.id.editTextNote);
                                spinnerProblemType = (Spinner) dialog.findViewById(R.id.spinnerProblemType);
                                ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
                                EditText editTextProblemList = (EditText) dialog.findViewById(R.id.editTextProblemList);
                                EditText editTextLastFeedback = (EditText) dialog.findViewById(R.id.editTextLastFeedback);
                                TextView textViewLastFeedback = (TextView) dialog.findViewById(R.id.textViewLastFeedBack);
                                editTextProblemList.setText(complain.getComplainType());
                                editTextLastFeedback.setText(complain.getFeedBack());
                                String feedBack = complain.getFeedBack();

                                if (feedBack.matches("null") || feedBack.matches("")){
                                    feedBack = "";
                                    editTextLastFeedback.setVisibility(View.GONE);
                                    textViewLastFeedback.setVisibility(View.GONE);
                                }else {
                                    feedBack = complain.getFeedBack();
                                    editTextNote.setText(feedBack+",");
                                }

                                imageView.startAnimation(animation);
                                stringComplain = complain.getComplainType();
                                loadComplainType();


                                Button buttonPending = (Button) dialog.findViewById(R.id.buttonPending);
                                Button buttonNotResolved = (Button) dialog.findViewById(R.id.buttonNotResolve);
                                Button buttonResolved = (Button) dialog.findViewById(R.id.buttonResolve);
                                Button buttonForwardWorkshop = (Button) dialog.findViewById(R.id.button_forward_work_shop);

                                spinnerProblemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                        complainTypeId = getComplainCode(position);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                buttonPending.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SharedPreferences sharedPref = context.getSharedPreferences("workshopFlagSave",Context.MODE_PRIVATE);
                                        int workshop= sharedPref.getInt("workshopFlag",0);
                                        if (workshop > 0){
                                            Toast.makeText(ActivityComplainList.this, "You are not able to Pending.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (solvedFlag > 0) {
                                                Toast.makeText(ActivityComplainList.this, "You are not able to Pending.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String feedBack = editTextNote.getText().toString();
                                                if (feedBack.isEmpty()) {
                                                    editTextNote.setError("Enter Feedback");
                                                    editTextNote.requestFocus();
                                                } else {
                                                    JSONArray jsonArray = complain.getProblem();
                                                    Log.i("check_json_data", jsonArray.toString());
                                                    jsonArrayComplain = new JSONArray();
                                                    note = editTextNote.getText().toString();
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        try {
                                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                            String complainId = jsonObject.getString("id");
                                                            jsonArrayComplain.put(complainId);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    URL = Constant.API.COMPLAIN_ACTION + "/" + userId + "/pending" + "/" + complainId;
                                                    sendAction();
                                                    Toast.makeText(context, "Pending", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                });

                                buttonNotResolved.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SharedPreferences sharedPref = context.getSharedPreferences("workshopFlagSave",Context.MODE_PRIVATE);
                                        int workshop= sharedPref.getInt("workshopFlag",0);
                                        if (workshop > 0 || solvedFlag > 0) {
                                            Toast.makeText(ActivityComplainList.this, "You are not able to UnSolved.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            note = editTextNote.getText().toString();
                                            JSONArray jsonArray = complain.getProblem();
                                            jsonArrayComplain = new JSONArray();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                try {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    String complainId = jsonObject.getString("id");
                                                    jsonArrayComplain.put(complainId);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            URL = Constant.API.COMPLAIN_ACTION + "/" + userId + "/unsolved" + "/" + complainId;
                                            sendAction();
                                            Toast.makeText(context, "Unsolved", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });

                                buttonResolved.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SharedPreferences sharedPref = context.getSharedPreferences("workshopFlagSave",Context.MODE_PRIVATE);
                                        int workshop= sharedPref.getInt("workshopFlag",0);
                                        if (workshop > 0) {
                                            Toast.makeText(ActivityComplainList.this, "You are not able to Solved.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (solvedFlag > 0){
                                                JSONArray jsonArray = complain.getProblem();
                                                jsonArrayComplain = new JSONArray();
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    try {
                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                        String complainId = jsonObject.getString("id");
                                                        jsonArrayComplain.put(complainId);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                URL = Constant.API.COMPLAIN_ACTION + "/" + userId + "/solved" + "/" + complainId;
                                                note = editTextNote.getText().toString();
                                                sendAction();
                                                Toast.makeText(context, "Solved", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(ActivityComplainList.this, "You are not able to Solved.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                                buttonForwardWorkshop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SharedPreferences sharedPref = context.getSharedPreferences("workshopFlagSave",Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor= sharedPref.edit();
                                        int workshop= sharedPref.getInt("workshopFlag",0);
                                        if (workshop > 0) {
                                            String feedBack = editTextNote.getText().toString();
                                            if (feedBack.isEmpty()) {
                                                editTextNote.setError("Enter Message");
                                                editTextNote.requestFocus();
                                            } else {
                                                editor.putInt("workshopFlag", 0);
                                                editor.apply();
                                                forwardUrl = Constant.API.COMPLAIN_ACTION + "/" + userId + "/forward" + "/" + complainId;
                                                note = editTextNote.getText().toString();
                                                forwardSendAction();
                                                dialog.dismiss();
                                            }
                                        } else {
                                            Toast.makeText(ActivityComplainList.this, "You are not able to Forward Workshop.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        complainTypeList.clear();
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
                        }

                        private void forwardSendAction(){
                            Log.d("Forward_response_url", forwardUrl);
                            final RequestQueue requestQueue= Volley.newRequestQueue(ActivityComplainList.this);
                            final StringRequest stringRequest= new StringRequest(Request.Method.PUT, forwardUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Forward_response", "Forward Response successfully");
                                    Toast.makeText(context, "Forwarded", Toast.LENGTH_SHORT).show();
                                    complainList.clear();
                                    sendData();
                                    dialog.cancel();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse response = error.networkResponse;
                                    if (error instanceof ServerError && response != null) {
                                        try {
                                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                            Log.d("ErrorResponse", res.toString());
                                            JSONObject jsonObject = new JSONObject(res);
                                            String status = jsonObject.getString("message");
                                            Toast.makeText(ActivityComplainList.this, status.toString(), Toast.LENGTH_SHORT).show();
                                        } catch (UnsupportedEncodingException | JSONException e1) {
                                            e1.printStackTrace();
                                        } finally {
                                            dialog.cancel();
                                        }
                                    }
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    return headers;
                                }
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    if ( buttonText.matches("close tickets")){
                                        params.put("note",note);
                                        Log.d("postNote", note);
                                    }
                                    Log.d("All data", params.toString());
                                    return params;
                                }
                            };
                            requestQueue.add(stringRequest);
                        }

                        private void sendAction() {
                            solvedFlag= 0;
                            Log.d("check_unsolved", "execute");
                            final RequestQueue requestQueue = Volley.newRequestQueue(ActivityComplainList.this);
                            final StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        complainList.clear();
                                        sendData();
                                        dialog.cancel();
                                    } catch (Exception e) {
                                        Log.d("____Error", e.toString());
                                        Log.d("_____Error2", response.toString());
                                    } finally {
                                        dialog.cancel();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse response = error.networkResponse;
                                    if (error instanceof ServerError && response != null) {
                                        try {
                                            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                            Log.d("ErrorResponse", res.toString());
                                            JSONObject jsonObject = new JSONObject(res);
                                            String status = jsonObject.getString("message");
                                            Toast.makeText(ActivityComplainList.this, status.toString(), Toast.LENGTH_SHORT).show();
                                        } catch (UnsupportedEncodingException | JSONException e1) {
                                            e1.printStackTrace();
                                        } finally {
                                            dialog.cancel();
                                        }
                                    }
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    return headers;
                                }
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    if ( buttonText.matches("close tickets")){
                                        params.put("note",note);
                                        params.put("complain_type", String.valueOf(jsonArrayComplain));
                                        Log.d("jsonArrayComplain_data", String.valueOf(jsonArrayComplain));
                                    }
                                    Log.d("All_action_data", params.toString());
                                    return params;
                                }
                            };
                            requestQueue.add(stringRequest);
                        }
                    });

                    dialog = new Dialog(ActivityComplainList.this, R.style.MyDialogTheme);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(promptsView);
                    ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    imageViewClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return complainList.size();
        }

        public class ComplainViewHolder extends RecyclerView.ViewHolder{
            TextView textViewTicketNo,textViewAddress,textViewDate,textViewStatus, textViewRegion, textViewProblem,textViewCoolerNo, textViewTechnician, textViewTechnicianTitle;
            ImageView imageView;
            public ComplainViewHolder(View itemView) {
                super(itemView);
                textViewTicketNo = (TextView) itemView.findViewById(R.id.textViewTicketNo);
                textViewAddress = (TextView) itemView.findViewById(R.id.textViewAddress);
                textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
                textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
                textViewRegion = (TextView) itemView.findViewById(R.id.textViewRegion);
                textViewTechnician = itemView.findViewById(R.id.textView_technician);
                textViewTechnicianTitle = itemView.findViewById(R.id.textView_technician_title);
                textViewProblem = (TextView) itemView.findViewById(R.id.textViewProblem);
                textViewCoolerNo = (TextView) itemView.findViewById(R.id.textViewCoolerNo);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }
    }

    public void checkRequisition(){
        acProgressFlower.show();
        Log.d("check_requisition_url: ",Constant.API.REQUISITION_ENTRY+"/"+userId+"/"+complainId);
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.API.REQUISITION_ENTRY +"/"+userId +"/"+complainId,
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        acProgressFlower.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                        JSONArray jsonArrayRequisitionList = jsonObjectData.getJSONArray("requisition_list");
                        Log.d("all_requisition_list: ", jsonObjectData.toString());

                        for (int i1 = 0; i1 < jsonArrayRequisitionList.length(); i1++) {
                            JSONObject jsonObjectStatus = jsonArrayRequisitionList.getJSONObject(i1);
                            JSONArray jsonArray = jsonObjectStatus.getJSONArray("requisition_list_product");
                            for (int ii1 = 0; ii1 < jsonArray.length(); ii1++) {
                                JSONObject jsonObjectQuantity = jsonArray.getJSONObject(ii1);
                                String status= jsonObjectQuantity.getString("status");
                                Log.d("check_req_status: ", status);

                                if(status.equals("pending") || status.equals("approved")){
                                    buttonAction.setVisibility(View.GONE);
                                    //Toast.makeText(context, "Please Complete Your Requisition", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        acProgressFlower.dismiss();
                    }
                }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    acProgressFlower.dismiss();
                }
            });
            Volley.newRequestQueue(this).add(stringRequest);
        }catch (Exception ignored){}
    }

    private void loadOldComplain() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constant.API.COMPLAIN_LIST+"/"+ userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            jsonArrayComplainType = jsonObjectData.getJSONArray("complain_list");
                            getComplainType(jsonArrayComplainType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getComplainType(JSONArray jsonArrayComplainType) {
                        for(int i = 0; i<jsonArrayComplainType.length(); i++) {
                            try {
                                JSONObject json = jsonArrayComplainType.getJSONObject(i);
                                JSONArray jsonArray = json.getJSONArray("complain_type_arr");
                                for ( int i1 = 0; i1<jsonArray.length(); i1++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i1);
                                    problemName = jsonObject.getString("name");
                                    problemId = jsonObject.getString("id");
                                    problemInsert();
                                    showSqliteData();
                                }
                                //complainTypeList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void problemInsert() {
       DataProblem dataProblem = new DataProblem(problemName, problemId);
       Log.d("check_insert_plm: ", "name: "+dataProblem.getProblemName()+" id: "+dataProblem.getProblemId());
       sqlitieFunction.datainsertProblem(dataProblem);
       showSqliteData();
       listAdapter.notifyDataSetChanged();
    }

    private void showSqliteData() {
        sqLiteDatabase = sqlitieFunction.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM problemTable", null);
        idArrayList.clear();
        problemArrayList.clear();
        problemIdArrayList.clear();
        if (cursor.moveToFirst()) {
            do {
                idArrayList.add(cursor.getString(cursor.getColumnIndex(SqlitieFunction.id2)));
                problemArrayList.add(cursor.getString(cursor.getColumnIndex(SqlitieFunction.problemName)));
                problemIdArrayList.add(cursor.getString(cursor.getColumnIndex(SqlitieFunction.problemId)));
            } while (cursor.moveToNext());
        }
        listAdapter = new SQLiteListAdapterProblem(ActivityComplainList.this,idArrayList, problemArrayList, problemIdArrayList);
        receiveActionStatusList();
        listViewComplain.setAdapter(listAdapter);
        listViewComplain.setExpanded(true);
        cursor.close();
    }

    private boolean _checkProblemId(String problemID){
        Log.d("problem_id_List_size: ", String.valueOf(problemIdArrayList.size()));
        for(String problem_id: problemIdArrayList){
            if(problem_id.equals(problemID)){
                return true;
            }
        }
        return false;
    }

    private void receiveActionStatusList(){
        receiveDoneList= listAdapter.statusAction.doneIdList;
        receivePendingList= listAdapter.statusAction.pendingIdList;
        receiveWorkshopList= listAdapter.statusAction.workshopIdList;
        receiveNoProblemList= listAdapter.statusAction.noProblemIdList;
        getSelectList= listAdapter.statusAction.selectList;
        Log.d("receive_list_test: ", "getSelect: "+getSelectList+" done: "+receiveDoneList+" pending "+receivePendingList+" workshop "+receiveWorkshopList+" noProblem "+receiveNoProblemList);
    }

    private void CheckGpsStatus() {
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){}
    }

    private void loadComplainType() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constant.API.COMPLAIN_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            jsonArrayComplainType = jsonObjectData.getJSONArray("complain_type_list");
                            getComplainType(jsonArrayComplainType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getComplainType(JSONArray jsonArrayComplainType) {
                        complainTypeList.add("Select Complain Type");
                        for(int i = 0; i<jsonArrayComplainType.length(); i++) {
                            try {
                                JSONObject json = jsonArrayComplainType.getJSONObject(i);
                                complainTypeList.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerProblemType.setAdapter(new ArrayAdapter<String>(ActivityComplainList.this, R.layout.spinner_item, complainTypeList));
                         int index = 0;
                         for (int i= 0; i < spinnerProblemType.getCount(); i++){
                             if (spinnerProblemType.getItemAtPosition(i).equals(stringComplain)){
                                 index = i;
                             }
                             spinnerProblemType.setSelection(index);
                         }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getComplainCode(int position){
        String code="";
        try {
            JSONObject json = jsonArrayComplainType.getJSONObject(position);
            code = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    private void getLocationDistance() {
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);
        /*Log.d("1", String.valueOf(latitude));
        Log.d("2", String.valueOf(longitude));
        Log.d("3", String.valueOf(shopLatitude));
        Log.d("4", String.valueOf(shopLongitude));*/
        Location endPoint=new Location("locationA");
        endPoint.setLatitude(shopLatitude);
        endPoint.setLongitude(shopLongitude);
        NumberFormat formatter = new DecimalFormat("#0.000");
        //distance = Double.parseDouble(formatter.format(startPoint.distanceTo(endPoint)));
        distance = startPoint.distanceTo(endPoint);
        bigDecimal = new BigDecimal(distance);
        intDistance = (int) distance;
    }

    public void getLocationFromAddress(String address) {
        Geocoder coder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = coder.getFromLocationName(address, 5);
            if (addresses == null) {
            }
            Address location = addresses.get(0);
            shopLatitude = location.getLatitude();
            shopLongitude = location.getLongitude();
            /*Log.i("Latiii",""+lat);
            Log.i("Lngttt",""+lng);*/
            /*LatLng latLng = new LatLng(lat,lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SolvedAdapter extends RecyclerView.Adapter<SolvedAdapter.SolvedViewHolder>{
        Context context;
        ArrayList<Solved> solvedArrayList;

        public SolvedAdapter(Context context, ArrayList<Solved> solvedArrayList) {
            this.context = context;
            this.solvedArrayList = solvedArrayList;
        }

        @Override
        public SolvedAdapter.SolvedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.solved_card, null);
            return new SolvedAdapter.SolvedViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final SolvedAdapter.SolvedViewHolder holder, int position) {
            final Solved solved = solvedArrayList.get(position);
            holder.textViewName.setText(solved.getProblemName());

        }
        @Override
        public int getItemCount() {
            return solvedArrayList.size();
        }

        public static class SolvedViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;

            public SolvedViewHolder(View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewProblemName);
            }
        }
    }

    private void _saveDataSharedPreferences(String id, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences("inventoryData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("inventoryID", id);
        editor.putString("inventoryName", name);
        editor.apply();
        Log.d("check_send_share_data: ", id+" and "+name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendData();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }

        if (checkSegmentBtn == R.id.radio_button_all){}
        segmentedGroupComplainList.check(checkSegmentBtn);
    }

}
