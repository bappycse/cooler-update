package com.csi.meghnacooler.Technician;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.csi.meghnacooler.Utility.Constant;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ReceivedRequisitionActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    TextView textViewInventoryName;
    String inventoryName, inventoryID, requisitionName;
    String complainId,userId,inventoryCode;
    int spinnerPossition;
    RecyclerView recyclerViewReceivedRequisitionList,recyclerViewProductView;
    List<Requisition> requisitionArrayList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String productId,productQuantity,reuestQuantity;
    JSONArray jsonArrayRequisitionList;
    JSONObject jsonObjectFormData,jsonObjectRequisitionProduct;
    ACProgressFlower acProgressFlower;
    Dialog dialog;
    View view;
    String quantity,receivedQty,a, requisitionId;
    double doubleQuantity,doubleReceivedQty;
    Button buttonUpdateRequisition;
    List<String> quantityList = new ArrayList();
    List<String> idList = new ArrayList();
    JSONArray jsonArrayProduct,jsonArrayFormData,jsonArrayRequisitionList1,jsonArrayInventoryList;
    JSONObject jsonObjectData,jsonObjectFormData1,jsonObjectRequisitionProduct1;
    private ArrayList<String> inventoryList = new ArrayList<>();
    int viewCount=0, countRestart=0;
    List<String> requisitionList= new ArrayList<>();
    String requisitionListProductName;
    int requisitionFlag;
    SharedPreferences requisitionPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_requisition);
        sharedPreferences = ReceivedRequisitionActivity.this.getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.sharedPrefItems.USER_ID,"");
        initToolBar();
        initUI();
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        acProgressFlower = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        recyclerViewReceivedRequisitionList.setHasFixedSize(true);
        recyclerViewReceivedRequisitionList.setLayoutManager(new LinearLayoutManager(ReceivedRequisitionActivity.this));
        recyclerViewReceivedRequisitionList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReceivedRequisitionActivity.this).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
        complainId = getIntent().getExtras().getString("complainId");
        Log.d("check_complainId",complainId);

        SharedPreferences sharedPref = this.getSharedPreferences("inventoryData",Context.MODE_PRIVATE);
        inventoryID= sharedPref.getString("inventoryID","");
        inventoryName= sharedPref.getString("inventoryName","");
        Log.d("check_received: ", inventoryName.toString()+" and "+inventoryID);

        //sendData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceivedRequisitionActivity.this,RequisitionActivity.class);
                intent.putExtra("complainId",complainId);
                startActivity(intent);
            }
        });
    }

    private void sendData() {
        requisitionArrayList.clear();
        acProgressFlower.show();
        requisitionList.clear();
        Log.d("requisition_url:",Constant.API.REQUISITION_ENTRY+"/"+userId+"/"+complainId);
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.API.REQUISITION_ENTRY +"/"+userId +"/"+complainId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                acProgressFlower.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                JSONArray jsonArrayRequisitionList = jsonObjectData.getJSONArray("requisition_product_name");
                                Log.d("all_data_show: ", jsonObjectData.toString());
                                JSONArray jsonArrayList = jsonObjectData.getJSONArray("requisition_list");
                                //inventoryLoadFromUrl= jsonObjectData.getString("inventory_name");
                                //inventoryIdLoadFromUrl= jsonObjectData.getInt("inventory_id");

                                for(int ir = 0; ir < jsonArrayRequisitionList.length(); ir++){
                                    requisitionList.add(jsonArrayRequisitionList.get(ir).toString());
                                    Log.d("check_requisitionName:", "name are "+requisitionList.toString()+" ;");
                                }

                                for (int i1 = 0; i1 < jsonArrayList.length(); i1++) {
                                    JSONObject jsonObjectStatus = jsonArrayList.getJSONObject(i1);
                                    JSONArray jsonArray = jsonObjectStatus.getJSONArray("requisition_list_product");
                                    for (int ii1 = 0; ii1 < jsonArray.length(); ii1++) {
                                        JSONObject jsonObjectQuantity = jsonArray.getJSONObject(ii1);
                                        String status= jsonObjectQuantity.getString("status");
                                        Log.d("check_req_status: ", status);
                                        String requestQn= jsonObjectQuantity.getString("quantity");
                                        String approvedQn= jsonObjectQuantity.getString("approved_quantity");

                                        requisitionArrayList.add(new Requisition(requisitionList, jsonObjectStatus.getString("id"), "", "", status, jsonArray, requestQn, approvedQn));
                                    }
                                }

                                RequisitionAdapter requisitionAdapter = new RequisitionAdapter(ReceivedRequisitionActivity.this, requisitionArrayList);
                                recyclerViewReceivedRequisitionList.setAdapter(requisitionAdapter);
                                if (requisitionArrayList.isEmpty()){
                                    requisitionFlag= 0;
                                    Log.e("if_requisitionFlag: ", String.valueOf(requisitionFlag));
                                    requisitionPreferences = getSharedPreferences("STORE_REQUISITION", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor= sharedPreferences.edit();
                                    editor.putInt("requisition", requisitionFlag);
                                    editor.apply();
                                } else{
                                    requisitionFlag++;
                                    Log.e("else_requisitionFlag: ", String.valueOf(requisitionFlag));
                                    requisitionPreferences = getSharedPreferences("STORE_REQUISITION", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor= sharedPreferences.edit();
                                    editor.putInt("requisition", requisitionFlag);
                                    editor.apply();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                acProgressFlower.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("error", error.getLocalizedMessage());
                            acProgressFlower.dismiss();
                        }
                    });
            Volley.newRequestQueue(this).add(stringRequest);
        }catch (Exception ignored){}
    }

    private void initUI() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabButton);
        recyclerViewReceivedRequisitionList = (RecyclerView) findViewById(R.id.recylcerViewReceivedRequisitionList);
    }

    private void initToolBar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.requisitionList);
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

    private class RequisitionAdapter extends RecyclerView.Adapter<RequisitionAdapter.RequisitionViewHolder>{
        Context context;
        List<Requisition> requisitionList;
        String nullObject = "null";

        public RequisitionAdapter(Context context, List<Requisition> requisitionList) {
            this.context = context;
            this.requisitionList = requisitionList;
        }

        @NonNull
        @Override
        public RequisitionAdapter.RequisitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.received_requisition_card, null, false);
            return new RequisitionAdapter.RequisitionViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final RequisitionAdapter.RequisitionViewHolder holder, final int position) {
            final Requisition requisition = requisitionList.get(position);

            holder.textViewTProductName.setText(requisition.getRequisitionName().get(position));
            holder.textViewTProductNo.setText("0000"+requisition.getProductName());
            holder.textViewOrderQuantity.setText(requisition.getOrderQuantity());
            String rQuantity= requisition.getRequestQuantity();
            double conRQuantity= Double.parseDouble(rQuantity);
            int convertInt= (int) conRQuantity;
            Log.d("check_convert: ", String.valueOf(conRQuantity+" "+convertInt));
            holder.requestQuantity.setText(String.valueOf(convertInt));
            holder.approvedQuantity.setText(requisition.getApprovedQuantity());
            String upperString = requisition.getStatus().substring(0,1).toUpperCase() + requisition.getStatus().substring(1);
            Log.d("check upperstring: ", upperString);
            holder.textViewStatus.setText(requisition.status);
            String statusString = holder.textViewStatus.getText().toString().toLowerCase();


            if (statusString.matches("complete")){
                Constant.requisitionFlag= true;
                holder.buttonAction.setVisibility(View.GONE);
            }else if ( statusString.matches("pending")){
                Constant.requisitionFlag= false;
                holder.buttonAction.setVisibility(View.GONE);
                if (Constant.requisitionFlag == false){
                    //Toast.makeText(context, "okey pending done", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Constant.requisitionFlag= false;
                holder.textViewStatus.setTextColor(Color.BLUE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("check_position_name: ", requisition.getRequisitionName().get(position));
                        requisitionListProductName= requisition.getRequisitionName().get(position);

                        LayoutInflater layoutInflater = LayoutInflater.from(ReceivedRequisitionActivity.this);
                        final View promptsView = layoutInflater.inflate(R.layout.requisition_prompts, null);
                        dialog = new Dialog(ReceivedRequisitionActivity.this, R.style.MyDialogTheme);
                        buttonUpdateRequisition = (Button) promptsView.findViewById(R.id.buttonUpdateRequisition);
                        textViewInventoryName =  promptsView.findViewById(R.id.textView_inventory_name);
                        textViewInventoryName.setText(inventoryName);

                        requisitionId = requisition.getProductName();
                        String array = String.valueOf(requisition.getJsonArray());
                        JSONArray jsonArray = requisition.getJsonArray();

                        recyclerViewProductView = promptsView.findViewById(R.id.recylcerViewRequisitionList);
                        recyclerViewProductView.setHasFixedSize(true);
                        recyclerViewProductView.setLayoutManager(new LinearLayoutManager(ReceivedRequisitionActivity.this));
                        recyclerViewProductView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ReceivedRequisitionActivity.this).color(Color.TRANSPARENT).sizeResId(R.dimen.dividerLess).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());
                        try {
                            Log.d("jSONArrAY",jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String productInfo = jsonObject1.getString("product_info");
                                JSONObject jsonObjectProductInfo = new JSONObject(productInfo);
                                String itemName= jsonObjectProductInfo.getString("name");

                                if(itemName.equals(requisitionListProductName)){
                                    productList.add(new Product(
                                            jsonObject1.getString("product_id"),
                                            jsonObjectProductInfo.getString("name"),
                                            jsonObject1.getString("quantity"),
                                            jsonObject1.getString("approved_quantity")
                                    ));
                                }
                            }
                            ProductAdapter productAdapter = new ProductAdapter(ReceivedRequisitionActivity.this, productList);
                            recyclerViewProductView.setAdapter(productAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(promptsView);
                        ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                        imageViewClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                                productList.clear();
                                quantityList.clear();
                                idList.clear();
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
            holder.buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(ReceivedRequisitionActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_received);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade);
                    final EditText editTextReceivedQuantity = (EditText) dialog.findViewById(R.id.editTextReceivedQuantity);
                    final TextView textViewOrderQuantity = (TextView) dialog.findViewById(R.id.textViewOrderQuantity);
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
                    imageView.startAnimation(animation);
                    textViewOrderQuantity.setText("Total requisition quantity: "+requisition.getOrderQuantity());
                    Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                    Button buttonSubmit = (Button) dialog.findViewById(R.id.buttonSubmit);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    buttonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            double orderQuantityDouble, productQuantityDouble;
                            productId = requisition.getProductId();
                            productQuantity = editTextReceivedQuantity.getText().toString();
                            orderQuantityDouble = Double.parseDouble(requisition.getOrderQuantity());
                            reuestQuantity = requisition.getOrderQuantity();
                            productQuantityDouble = Double.parseDouble(productQuantity);
                            if ( productQuantity.isEmpty()){
                                editTextReceivedQuantity.setError("Enter Received Quantity");
                                editTextReceivedQuantity.requestFocus();
                            }
                            else if ( productQuantityDouble > orderQuantityDouble){
                                editTextReceivedQuantity.setError("Invalid Quantity");
                                editTextReceivedQuantity.requestFocus();
                            }
                            else {
                                sendRequisition();
                                dialog.dismiss();
                            }
                        }
                    });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
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
            });

        }

        @Override
        public int getItemCount() {
            Log.d("requisitionList.size: ", String.valueOf(requisitionList.size()));
            viewCount= requisitionList.size();
            return requisitionList.size();
        }

        public class RequisitionViewHolder extends RecyclerView.ViewHolder{
            TextView textViewTProductName, textViewTProductNo,textViewOrderQuantity,textViewStatus, requestQuantity, approvedQuantity;
            Button buttonAction;
            public RequisitionViewHolder(View itemView) {
                super(itemView);
                textViewTProductName = (TextView) itemView.findViewById(R.id.textViewRequisitionName);
                textViewTProductNo = (TextView) itemView.findViewById(R.id.textViewProductNo);
                textViewOrderQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
                requestQuantity = (TextView) itemView.findViewById(R.id.requestQuantity);
                approvedQuantity = (TextView) itemView.findViewById(R.id.approvedQuantity);
                textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
                buttonAction = (Button) itemView.findViewById(R.id.buttonAction);
            }
        }

    }

    private void loadInventory() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constant.API.REQUISITION_ENTRY+"/"+userId+"/"+complainId,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("check_execute_url", Constant.API.REQUISITION_ENTRY+"/"+userId+"/"+complainId);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    jsonArrayInventoryList = jsonObjectData.getJSONArray("investory_list");
                    getInventory(jsonArrayInventoryList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            private void getInventory(JSONArray jsonArrayInventoryList) {
                inventoryList.add("Select Inventory");
                for(int i = 0; i<jsonArrayInventoryList.length(); i++) {
                    try {
                        JSONObject json = jsonArrayInventoryList.getJSONObject(i);
                        //textViewInventoryName.setText(json.getString("name"));
                        inventoryList.add(json.getString("name"));
                        //Log.d("checkInventory:", textViewInventoryName.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getInventoryCode(int position){
        String code="";
        try {
            JSONObject json = jsonArrayInventoryList.getJSONObject(position-1);
            code = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    private void sendRequisition() {
        acProgressFlower.show();
        try{
            jsonObjectFormData = new JSONObject();
            jsonArrayRequisitionList = new JSONArray();
            jsonObjectFormData.put("complain_id",complainId);
            jsonObjectRequisitionProduct = new JSONObject();
            jsonObjectRequisitionProduct.put("id", productId);
            jsonObjectRequisitionProduct.put("receive_quantity", productQuantity);
            jsonObjectRequisitionProduct.put("request_quantity", reuestQuantity);
            jsonArrayRequisitionList.put(jsonObjectRequisitionProduct);
            jsonObjectFormData.put("requisition_list",jsonArrayRequisitionList);
        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedRequisitionActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT,Constant.API.REQUISITION_ENTRY +"/"+ userId,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("type");
                            requisitionArrayList.clear();
                            sendData();
                            if (status.matches("success")) {
                                Toast.makeText(ReceivedRequisitionActivity.this,
                                        "Successful", Toast.LENGTH_SHORT).show();
                                acProgressFlower.dismiss();
                            } else {
                                Toast.makeText(ReceivedRequisitionActivity.this,
                                        "Request failed", Toast.LENGTH_SHORT).show();
                               acProgressFlower.dismiss();
                            }
                        }catch (Exception e){
                            Log.d("Error", e.toString());
                            Log.d("Error2", response.toString());
                            acProgressFlower.dismiss();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                acProgressFlower.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> param = new HashMap<String, String>();
                param.put("product_requisition", jsonObjectFormData.toString());
                Log.d("All Data",param.toString());
                return param;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }catch(Exception e){}
        finally {
            /*Toast.makeText(context,
                    "Verification Successfull", Toast.LENGTH_SHORT).show();*/
            acProgressFlower.dismiss();
    }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ReceivedRequisitionActivity.ProductAdapter.ProductViewHolder>{
        Context context;
        List<Product> productList;

        public ProductAdapter(Context context, List<Product> productList) {
            this.context = context;
            this.productList = productList;
        }

        @NonNull
        @Override
        public ReceivedRequisitionActivity.ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.update_requisition_card, null);
            return new ProductViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ReceivedRequisitionActivity.ProductAdapter.ProductViewHolder holder,final int position) {

            final Product product = productList.get(position);

            holder.textViewName.setText(product.getProductName());
            double convertProductQuantity= Double.parseDouble(product.getQuantity());
            int convertProductQuantity_to_int= (int) convertProductQuantity;
            holder.textViewQuantity.setText(String.valueOf(convertProductQuantity_to_int));
            holder.textViewReceived.setText(product.getApproved_quantity());
            holder.checkBoxMultiSelect.setOnCheckedChangeListener(null);

            receivedQty = product.getApproved_quantity();

            holder.checkBoxMultiSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                    if (isCheked){
                        quantityList.add(product.getApproved_quantity());
                        idList.add(product.id);
                        Log.d("ID", String.valueOf(idList.size()));
                    }
                }
            });

            buttonUpdateRequisition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    jsonObjectFormData1 = new JSONObject();
                    jsonArrayRequisitionList1 = new JSONArray();
                    try {
                        jsonObjectFormData1.put("requisition_id", requisitionId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (quantityList.size() <= 0) {
                            Toast.makeText(ReceivedRequisitionActivity.this, "Please select the Checkbox", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("product_id_list", String.valueOf(idList.size()));
                            for (int i = 0; i < idList.size(); i++) {
                                jsonObjectRequisitionProduct1 = new JSONObject();
                                jsonObjectRequisitionProduct1.put("requisition_product_info_id", idList.get(i));
                                jsonObjectRequisitionProduct1.put("receive_quantity", quantityList.get(i));
                                Log.d("execute_loop", "success "+i+" "+idList.size()+ "jsonObjectRequisitionProduct1 "+jsonObjectRequisitionProduct1.toString());
                                jsonArrayRequisitionList1.put(jsonObjectRequisitionProduct1);

                                if (i+1 == idList.size()){
                                    jsonObjectFormData1.put("inventory_id", inventoryID);
                                    jsonObjectFormData1.put("requisition_list",jsonArrayRequisitionList1);
                                    Log.d("final_accept_update", jsonObjectFormData1.toString());
                                    if ( quantityList.size() > productList.size()){
                                        Toast.makeText(ReceivedRequisitionActivity.this,"Something went wrong! Try again",Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                    Log.d("update_execute", "update_execute_preview");
                                    sendUpdate();
                                }

                            }
                        }
                    }catch (Exception ignored){}
                }
            });
        }
        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName,textViewQuantity, textViewReceived;
            CheckBox checkBoxMultiSelect;
            public ProductViewHolder(View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewProductName);
                textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
                textViewReceived = itemView.findViewById(R.id.textView_received);
                checkBoxMultiSelect = (CheckBox) itemView.findViewById(R.id.chekboxMultiSelect);
            }
        }
    }

    private void sendUpdate(){
        Log.d("update_execute", "update_execute_done");
        acProgressFlower.show();

        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedRequisitionActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, Constant.API.REQUISITION_UPDATE + "/" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("type");
                            Log.d("status_update",status);
                            if (status.equals("success")){
                                Toast.makeText(ReceivedRequisitionActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                acProgressFlower.dismiss();
                                idList.clear();
                                quantityList.clear();
                                requisitionArrayList.clear();
                                //sendData();
                                dialog.cancel();
                                Intent intent= new Intent(ReceivedRequisitionActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ReceivedRequisitionActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                acProgressFlower.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSONException_is: ", e.getMessage());
                            acProgressFlower.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReceivedRequisitionActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.d("request_failed",error.getMessage());
                idList.clear();
                quantityList.clear();
                acProgressFlower.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param= new HashMap<String, String>();
                param.put("product_requisition", jsonObjectFormData1.toString());
                Log.d("All_Data_UPDATE", param.toString());
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        complainId = getIntent().getExtras().getString("complainId");
        Log.d("check_complainId_resume",complainId);
        sendData();
    }
}
