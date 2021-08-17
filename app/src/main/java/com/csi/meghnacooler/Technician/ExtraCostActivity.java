package com.csi.meghnacooler.Technician;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Sqlite.DataCost;
import com.csi.meghnacooler.Sqlite.SqlitieFunction;
import com.csi.meghnacooler.Utility.Constant;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ExtraCostActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button buttonAdd,buttonSend;
    LinearLayout linearLayoutList;
    TextView textViewTitle;
    EditText editTextQuantity, editTextAmount, editTextUnitPrice, editTextCostTitle, editTextNote;
    SqlitieFunction sqlitieFunction;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    SQLiteListAdapterCost listAdapterCost;
    ArrayList<String> productArrayList = new ArrayList();
    ArrayList<String> idArrayList = new ArrayList();
    ArrayList<String> quantityArrayList = new ArrayList();
    ArrayList<String> unitPriceArrayList = new ArrayList();
    ArrayList<String> amountArrayList = new ArrayList();
    ArrayList<String> noteArrayList = new ArrayList();
    ArrayList<String> titleArrayList = new ArrayList();
    ArrayList<String> product = new ArrayList();
    ExpandableHeightListView expandableListView;
    int itemCount,spinnerPosition;
    Animation animationLeftRight;
    LinearLayout linearLayoutForm;
    Context context = this;
    Spinner spinnerProduct;
    JSONObject jsonObjectData;
    JSONArray jsonArrayProduct;
    String stringProductName = "", stringProductId = "", stringQuantity = "", stringAmount = "", stringUnitPrice = "", stringNote = "", stringTitle = "",complainId,userId;
    double doubleQty,doubleUnit,doubleAmount;
    ACProgressFlower dialog;
    SharedPreferences sharedPreferencesUser;
    JSONArray jsonArrayFormData,jsonArrayRequisitionList;
    JSONObject jsonObjectFormData,jsonObjectRequisitionProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_cost);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        initUI();
        initToolBar();
        loadProduct();
        sharedPreferencesUser = ExtraCostActivity.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        complainId = getIntent().getExtras().getString("complainId");
        Log.d("ids",userId+" "+complainId);
        sqlitieFunction = new SqlitieFunction(context);
        animationLeftRight = AnimationUtils.loadAnimation(ExtraCostActivity.this,R.anim.left_enter);
        linearLayoutForm.setAnimation(animationLeftRight);
        showSqliteData();
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
        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    doubleQty = Double.parseDouble(editTextQuantity.getText().toString());
                    doubleUnit = Double.parseDouble(editTextUnitPrice.getText().toString());
                    doubleAmount = doubleQty * doubleUnit;
                    editTextAmount.setText(String.valueOf(doubleAmount));
                }catch (Exception e){}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    doubleQty = Double.parseDouble(editTextQuantity.getText().toString());
                    doubleUnit = Double.parseDouble(editTextUnitPrice.getText().toString());
                    doubleAmount = doubleQty * doubleUnit;
                    editTextAmount.setText(String.valueOf(doubleAmount));
                }catch (Exception e){}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringQuantity = editTextQuantity.getText().toString();
                stringUnitPrice = editTextUnitPrice.getText().toString();
                stringTitle = editTextCostTitle.getText().toString();
                stringNote = editTextNote.getText().toString();
                stringAmount = editTextAmount.getText().toString();
                if ( spinnerPosition == 0){
                    stringProductName = "";
                }
                if ( stringQuantity.isEmpty()){
                    editTextQuantity.setError("Enter Quantity");
                    editTextQuantity.requestFocus();
                }
                else if ( stringUnitPrice.isEmpty()){
                    editTextUnitPrice.setError("Enter Unit Price");
                    editTextUnitPrice.requestFocus();
                }

                else {
                    insertintoDatabase();
                    spinnerProduct.setSelection(0);
                    editTextQuantity.setText("");
                    editTextAmount.setText("");
                    editTextCostTitle.setText("");
                    editTextNote.setText("");
                    editTextUnitPrice.setText("");

                }
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        sqlitieFunction.dataDeleteCost(id);
                        showSqliteData();
                    }
                });
            }
        });
    }

    private void sendData() {
        dialog.show();
        Log.d("yes","yes");
        try{
            sqLiteDatabase = sqlitieFunction.getWritableDatabase();
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM costTable", null);
            jsonObjectFormData = new JSONObject();
            jsonArrayRequisitionList = new JSONArray();
            jsonObjectFormData.put("complain_id",complainId);
            if(cursor.moveToFirst())
                do {
                    final String productName = cursor.getString(cursor.getColumnIndex(sqlitieFunction.productNameCost));
                    final String productId =cursor.getString(cursor.getColumnIndex(sqlitieFunction.productIdCost));
                    final String quantity = cursor.getString(cursor.getColumnIndex(sqlitieFunction.quantityCost));
                    final String unitPrice =cursor.getString(cursor.getColumnIndex(sqlitieFunction.unitPriceCost));
                    final String amount =cursor.getString(cursor.getColumnIndex(sqlitieFunction.amountCost));
                    final String note =cursor.getString(cursor.getColumnIndex(sqlitieFunction.note));
                    final String title =cursor.getString(cursor.getColumnIndex(sqlitieFunction.costTitle));

                    jsonObjectRequisitionProduct = new JSONObject();
                    jsonObjectRequisitionProduct.put("productId", productId);
                    jsonObjectRequisitionProduct.put("quantity", quantity);
                    jsonObjectRequisitionProduct.put("unitPrice", unitPrice);
                    jsonObjectRequisitionProduct.put("amount", amount);
                    jsonObjectRequisitionProduct.put("note", note);
                    jsonObjectRequisitionProduct.put("title", title);
                    jsonArrayRequisitionList.put(jsonObjectRequisitionProduct);
                }while (cursor.moveToNext());
            jsonObjectFormData.put("requisition_list",jsonArrayRequisitionList);
            Log.d("formDATA", jsonObjectFormData.toString());
            RequestQueue requestQueue = Volley.newRequestQueue(ExtraCostActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,Constant.API.COST_ENTRY +"/"+ userId,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("type");
                                if (status.matches("success")) {
                                    Toast.makeText(context,
                                            "Successfull", Toast.LENGTH_SHORT).show();
                                    sqlitieFunction.deleteAllCost();
                                    showSqliteData();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context,
                                            "Request failed", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }catch (Exception e){
                                Log.d("Error", e.toString());
                                Log.d("Error2", response.toString());
                                dialog.dismiss();
                            }
                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> param = new HashMap<String, String>();
                    param.put("cost_requisition", jsonObjectFormData.toString());
                    Log.d("All Data",param.toString());
                    return param;

                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);

        }catch(Exception e){
            Log.d("exception", e.getMessage());
        }
        finally {
            /*Toast.makeText(context,
                    "Verification Successfull", Toast.LENGTH_SHORT).show();*/
            dialog.dismiss();
        }
    }

    private void insertintoDatabase() {
        DataCost dataCost = new DataCost(stringProductName,stringProductId,stringQuantity,stringAmount,stringUnitPrice,stringTitle,stringNote);
        sqlitieFunction.datainsertCost(dataCost);
        showSqliteData();
        listAdapterCost.notifyDataSetChanged();
    }

    private void showSqliteData() {
        sqLiteDatabase = sqlitieFunction.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM costTable", null);
        idArrayList.clear();
        productArrayList.clear();
        quantityArrayList.clear();
        amountArrayList.clear();
        unitPriceArrayList.clear();
        noteArrayList.clear();
        titleArrayList.clear();
        if (cursor.moveToFirst()) {
            do {
                idArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.id1)));
                productArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.productNameCost)));
                quantityArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.quantityCost)));
                amountArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.amountCost)));
                unitPriceArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.unitPriceCost)));
                noteArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.note)));
                titleArrayList.add(cursor.getString(cursor.getColumnIndex(sqlitieFunction.costTitle)));
            } while (cursor.moveToNext());
        }
        listAdapterCost = new SQLiteListAdapterCost(ExtraCostActivity.this,idArrayList,productArrayList,quantityArrayList,amountArrayList,unitPriceArrayList,noteArrayList,titleArrayList);
        expandableListView.setAdapter(listAdapterCost);
        itemCount = listAdapterCost.getCount();
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

    private void loadProduct() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constant.API.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObjectData = jsonObject.getJSONObject("data");
                            jsonArrayProduct = jsonObjectData.getJSONArray("spare_list");
                            Log.d("spare", jsonArrayProduct.toString());
                            getProduct(jsonArrayProduct);
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
                        spinnerProduct.setAdapter(new ArrayAdapter<String>(ExtraCostActivity.this, R.layout.spinner_item, product));
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ExtraCostActivity.this);
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

    private void initUI() {
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        editTextUnitPrice = (EditText) findViewById(R.id.editTextUnitPrice);
        editTextCostTitle = (EditText) findViewById(R.id.editTextCostTitle);
        editTextNote = (EditText) findViewById(R.id.editTextNote);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProductName);
        expandableListView = (ExpandableHeightListView) findViewById(R.id.listViewRequisitionList);
        linearLayoutList = (LinearLayout) findViewById(R.id.layoutList);
        textViewTitle = (TextView) findViewById(R.id.textViewOrderlistTitle);
        linearLayoutForm = (LinearLayout) findViewById(R.id.orderEntryForm);
    }

    private void initToolBar() {
        toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.extraCost);
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
}
