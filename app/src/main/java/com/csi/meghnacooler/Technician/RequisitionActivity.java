package com.csi.meghnacooler.Technician;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Sqlite.Datagetset;
import com.csi.meghnacooler.Sqlite.SqlitieFunction;
import com.csi.meghnacooler.Utility.Constant;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RequisitionActivity extends AppCompatActivity {
    Button buttonAdd,buttonSend;
    LinearLayout linearLayoutList;
    TextView textViewTitle, reqInventoryName;
    EditText editTextQuantity;
    SqlitieFunction sqlitieFunction;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    SQLiteListAdapter listAdapter;
    ArrayList<String> productArrayList = new ArrayList();
    ArrayList<String> idArrayList = new ArrayList();
    ArrayList<String> quantityArrayList = new ArrayList();
    ArrayList<String> product = new ArrayList();
    ArrayList<String> depo = new ArrayList();
    ExpandableHeightListView expandableListView;
    int itemCount,spinnerPosition, depoSpinnerPosition;
    Animation animationLeftRight;
    LinearLayout linearLayoutForm;
    Context context = this;
    Toolbar toolbar;
    Spinner spinnerProduct, spinnerDepo;
    JSONArray jsonArrayProduct,jsonArrayFormData,jsonArrayRequisitionList, jsonArrayDepo;
    JSONObject jsonObjectData,jsonObjectFormData,jsonObjectRequisitionProduct;
    String stringProductName,stringProductId,stringQuantity,complainId,userId;
    SharedPreferences sharedPreferencesUser;
    ACProgressFlower dialog;
    //String depoID;
    String inventoryID, inventoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        initUI();
        initToolBar();
        loadProduct();
        sharedPreferencesUser = RequisitionActivity.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        animationLeftRight = AnimationUtils.loadAnimation(RequisitionActivity.this,R.anim.left_enter);
        linearLayoutForm.setAnimation(animationLeftRight);
        sqlitieFunction = new SqlitieFunction(context);
        complainId = getIntent().getExtras().getString("complainId");
        showSqliteData();

        SharedPreferences sharedPref = context.getSharedPreferences("inventoryData",Context.MODE_PRIVATE);
        inventoryID= sharedPref.getString("inventoryID","");
        inventoryName= sharedPref.getString("inventoryName","");
        reqInventoryName.setText(inventoryName.toString());

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringQuantity = editTextQuantity.getText().toString();
                if ( spinnerPosition == 0){
                    Toast.makeText(RequisitionActivity.this, "Select product", Toast.LENGTH_SHORT).show();
                }
               /* else if (depoSpinnerPosition == 0){
                    Toast.makeText(RequisitionActivity.this, "Select Inventory", Toast.LENGTH_SHORT).show();
                }*/
                else if ( stringQuantity.isEmpty()){
                    editTextQuantity.setError("Enter Quantity");
                    editTextQuantity.requestFocus();
                }
                else if ( sqlitieFunction.isExist(stringProductId) == true){
                    Toast.makeText(RequisitionActivity.this, "Already Added", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    insertintoDatabase();
                    editTextQuantity.setText("");
                    spinnerProduct.setSelection(0);
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                sendData();
            }
        });

        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                ImageView imageViewDelete = (ImageView) view.findViewById(R.id.imageViewDelete);
                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = idArrayList.get(i);
                        sqlitieFunction.dataDelete(id);
                        showSqliteData();
                    }
                });
            }
        });

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stringProductName = spinnerProduct.getSelectedItem().toString();
                stringProductId = getProductCode(i);
                spinnerPosition = spinnerProduct.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       /* spinnerDepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, "select: "+spinnerDepo.getSelectedItem(), Toast.LENGTH_SHORT).show();
                depoID= String.valueOf(spinnerDepo.getSelectedItemId());
                depoSpinnerPosition= spinnerDepo.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        */

    }

    private void sendData() {
        dialog.show();
        try{
            sqLiteDatabase = sqlitieFunction.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM productTable", null);
            jsonObjectFormData = new JSONObject();
            jsonArrayRequisitionList = new JSONArray();
            jsonObjectFormData.put("complain_id",complainId);
            if(cursor.moveToFirst())
                do {
                    final String productName = cursor.getString(cursor.getColumnIndex(SqlitieFunction.productName));
                    final String quantity = cursor.getString(cursor.getColumnIndex(SqlitieFunction.quantity));
                    final String productId =cursor.getString(cursor.getColumnIndex(SqlitieFunction.productId));
                    Log.d("show_product_id", productId);

                    jsonObjectRequisitionProduct = new JSONObject();
                    jsonObjectRequisitionProduct.put("id", productId);
                    jsonObjectRequisitionProduct.put("quantity", quantity);
                    jsonObjectRequisitionProduct.put("inventory_id", inventoryID);
                    jsonArrayRequisitionList.put(jsonObjectRequisitionProduct);
                  }while (cursor.moveToNext());

                  jsonObjectFormData.put("requisition_list",jsonArrayRequisitionList);
                  Log.d("show_data_post", jsonObjectFormData.toString());

                  RequestQueue requestQueue = Volley.newRequestQueue(RequisitionActivity.this);
                  StringRequest stringRequest= new StringRequest(Request.Method.POST, Constant.API.REQUISITION_ENTRY + "/" + userId,
                          new Response.Listener<String>() {
                              @Override
                              public void onResponse(String response) {

                                  try {
                                      JSONObject jsonObject = new JSONObject(response);
                                      String status = jsonObject.getString("type");
                                      Log.d("check_status", status);

                                      if(status.matches("success")) {
                                         Log.d( "called_successfully", response.toString());
                                          sqlitieFunction.deleteAll();
                                          showSqliteData();
                                          dialog.dismiss();

                                          /*Intent intent = new Intent(RequisitionActivity.this, ActivityComplainList.class);
                                          intent.putExtra("complainId",complainId);
                                          startActivity(intent);
                                          finish();*/

                                      } else {
                                          Toast.makeText(context, "Request failed", Toast.LENGTH_SHORT).show();
                                          dialog.dismiss();
                                      }

                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                          }, new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                          Log.d("error_response:", error.getMessage());
                      }
                  }){
                      @Override
                      protected Map<String, String> getParams() throws AuthFailureError {
                          Map<String, String> param = new HashMap<String, String>();
                          param.put("product_requisition", jsonObjectFormData.toString());
                          Log.d("All_Data",param.toString());
                          return param;
                      }

                  };
                  requestQueue.add(stringRequest);

        }catch(Exception ignored){}
        finally {
            dialog.dismiss();
        }
    }

    private void loadProduct() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constant.API.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Product_list",response.toString());
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObjectData = jsonObject.getJSONObject("data");
                            jsonArrayProduct = jsonObjectData.getJSONArray("spare_list");
                            jsonArrayDepo = jsonObjectData.getJSONArray("inventory_list");
                            Log.d("spare", jsonArrayProduct.toString()+"and "+jsonArrayDepo.toString());
                            Log.d("depolist", jsonArrayDepo.toString());
                            getProduct(jsonArrayProduct);
                            //getDepo(jsonArrayDepo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    private void getProduct(JSONArray jsonArray) {
                        product.add("Select Product");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                product.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerProduct.setAdapter(new ArrayAdapter<String>(RequisitionActivity.this, R.layout.spinner_item, product));
                    }

                    /*private void getDepo(JSONArray jsonArray) {
                        depo.add("Select Inventory");
                        for(int i = 0; i<jsonArray.length(); i++) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                depo.add(json.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        spinnerDepo.setAdapter(new ArrayAdapter<String>(RequisitionActivity.this, R.layout.spinner_item, depo));
                    }*/

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(RequisitionActivity.this);
        requestQueue.add(stringRequest);
    }

    public String getProductCode(int position){
        String code="";
        try {
            JSONObject json = jsonArrayProduct.getJSONObject(position-1);
            code = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.stockRequisition);
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

    private void insertintoDatabase() {
        Datagetset datagetset = new Datagetset(stringProductName,stringQuantity,stringProductId);
        sqlitieFunction.datainsert(datagetset);
        showSqliteData();
        listAdapter.notifyDataSetChanged();
    }

    private void showSqliteData() {
        sqLiteDatabase = sqlitieFunction.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM productTable", null);
        idArrayList.clear();
        productArrayList.clear();
        quantityArrayList.clear();
        if (cursor.moveToFirst()) {
            do {
                idArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.id)));
                productArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.productName)));
                quantityArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.quantity)));
            } while (cursor.moveToNext());
        }
        listAdapter = new SQLiteListAdapter(RequisitionActivity.this, idArrayList, productArrayList, quantityArrayList);
        expandableListView.setAdapter(listAdapter);
        itemCount = listAdapter.getCount();
        expandableListView.setExpanded(true);

        if (itemCount != 0){
            linearLayoutList.setVisibility(View.VISIBLE);
            textViewTitle.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.VISIBLE);
        }
        if (itemCount == 0){
            linearLayoutList.setVisibility(View.GONE);
            textViewTitle.setVisibility(View.GONE);
            buttonSend.setVisibility(View.GONE);
        }
        cursor.close();
    }

    private void initUI() {
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        expandableListView = (ExpandableHeightListView) findViewById(R.id.listViewRequisitionList);
        linearLayoutList = (LinearLayout) findViewById(R.id.layoutList);
        textViewTitle = (TextView) findViewById(R.id.textViewOrderlistTitle);
        linearLayoutForm = (LinearLayout) findViewById(R.id.orderEntryForm);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProductName);
        //spinnerDepo = (Spinner) findViewById(R.id.spinnerDepoName);
        reqInventoryName= findViewById(R.id.req_inventory_name);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
    }
}
