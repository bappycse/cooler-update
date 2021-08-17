package com.csi.meghnacooler.HeadOffice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Utility.Constant;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import info.hoang8f.android.segmented.SegmentedGroup;

public class StockFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerViewStockList;
    SegmentedGroup segmentedGroupSwitcher;
    ACProgressFlower dialog;
    String userId,userGroup;
    SharedPreferences sharedPreferences;
    RadioButton radioButtonParts,radioButtonProduct,radioButtonAll;
    List<Stock> stockList = new ArrayList<>();
    String main = "main", spare = "spare", products = "Products", parts = "Parts";
    private OnFragmentInteractionListener mListener;

    public StockFragment() {

    }
    public static StockFragment newInstance(String param1, String param2) {
        StockFragment fragment = new StockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constant.sharedPrefItems.USER_ID,"");
        userGroup = sharedPreferences.getString(Constant.sharedPrefItems.GROUP,"");
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        initUI(view);
        sendData(view);
        if ( userGroup.matches("technician")){
            segmentedGroupSwitcher.setVisibility(View.GONE);
        }

        recyclerViewStockList.setHasFixedSize(true);
        recyclerViewStockList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewStockList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());

        segmentedGroupSwitcher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if ( checkId == R.id.radioButtonProducts){
                    filteredTicketList(products);
                }
                else if ( checkId == R.id.radioButtonParts){
                    filteredTicketList(parts);
                }
                else {
                    filteredTicketList("");
                }
            }
        });
        return view;
    }

    private void filteredTicketList(String category) {
        Collection<Stock> filtered = Collections2.filter(stockList, new customPredicate( category ));
        ArrayList<Stock> filteredList = new ArrayList<Stock>(filtered);
        generateStockView( filteredList );
    }

    private void sendData(View view) {
        dialog.show();
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.API.STOCK_LIST +"/"+ userId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = jsonObjectData.getJSONArray("stock_info_list");
                                if ( 0 == jsonArray.length() ) {
                                    Toast.makeText(getActivity().getApplicationContext(), "No Stock Available", Toast.LENGTH_LONG).show();
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String type;
                                    type = jsonObject1.getString("type").toLowerCase();
                                    if ( type.matches(main) ){
                                        type = "Products";
                                    }
                                    else {
                                        type = "Parts";
                                    }
                                    stockList.add(new Stock(
                                            jsonObject1.getString("name"),
                                            jsonObject1.getString("quantity"),
                                            type
                                    ));
                                }
                                generateStockView( stockList );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            //Log.d("error", error.getLocalizedMessage());
                        }
                    });
            Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
        }catch (Exception e){}
    }

    private void generateStockView(List<Stock> stockList) {
        StockAdapter stockAdapter = new StockAdapter(getActivity().getApplicationContext(), stockList);
        recyclerViewStockList.setAdapter(stockAdapter);
    }

    private void initUI(View view) {
        recyclerViewStockList = (RecyclerView) view.findViewById(R.id.recylcerViewStockList);
        segmentedGroupSwitcher = (SegmentedGroup) view.findViewById(R.id.segmentedGroupSwitcher);
        radioButtonParts = (RadioButton) view.findViewById(R.id.radioButtonParts);
        radioButtonAll = (RadioButton) view.findViewById(R.id.radioButtonAll);
        radioButtonProduct = (RadioButton) view.findViewById(R.id.radioButtonProducts);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder>{
        private Context context;
        private List<Stock> stockList;

        public StockAdapter(Context context, List<Stock> stockList) {
            this.context = context;
            this.stockList = stockList;
        }
        @Override
        public StockAdapter.StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.stock_card, null);
            return new StockAdapter.StockViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final StockAdapter.StockViewHolder holder, int position) {
            final Stock stock = stockList.get(position);
            holder.textViewName.setText(stock.getName());
            holder.textViewQuantity.setText(stock.getQuantity());
            holder.textViewCategory.setText(stock.getCategory());
        }
        @Override
        public int getItemCount() {
            return stockList.size();
        }

        public class StockViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewQuantity, textViewCategory;

            public StockViewHolder(View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.textViewProductName);
                textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
                textViewCategory = (TextView) itemView.findViewById(R.id.textViewCategory);
            }
        }
    }

    private static class customPredicate implements Predicate<Stock> {
        private String category = "";
        public customPredicate(String category) {
            this.category = category;
        }
        @Override
        public boolean apply(Stock stock) {
            if ( this.category.length() > 0 ) {
                return stock.getCategory().toLowerCase().equals( this.category.toLowerCase() );
            }
            else {
                return true;
            }
        }
    }
}
