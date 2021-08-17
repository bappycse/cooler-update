package com.csi.meghnacooler.HeadOffice;

import android.app.DatePickerDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.csi.meghnacooler.Activity.ActivitySetting;
import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Technician.ActivityComplainList;
import com.csi.meghnacooler.Technician.ReceivedRequisitionActivity;
import com.csi.meghnacooler.Utility.Constant;
import com.csi.meghnacooler.Utility.MonthToText;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import info.hoang8f.android.segmented.SegmentedGroup;

public class TicketListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    SegmentedGroup segmentedGroupSwitcher;
    Animation animationLeftRight;
    EditText editTextStartDate,editTextEndDate;
    RecyclerView recyclerViewTicketList;
    Calendar calendar;
    int year,day,month;
    String startDate,endDate,userId;
    String stringSolved = "solved", stringUnsolved = "unsolved", stringCompleted = "completed", stringOpen = "pending", stringResponded = "accepted", stringAttended = "ongoing", stringAssigned = "assigned";
    Button buttonSearch;
    SharedPreferences sharedPreferencesUser;
    List<Ticket> ticketList = new ArrayList<>();
    ACProgressFlower dialog;
    private OnFragmentInteractionListener mListener;

    public TicketListFragment() {
    }
    public static TicketListFragment newInstance(String param1, String param2) {
        TicketListFragment fragment = new TicketListFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);
        animationLeftRight = AnimationUtils.loadAnimation(getActivity(),R.anim.left_enter);
        sharedPreferencesUser = getActivity().getApplicationContext().getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        userId = sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_ID,"");
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLUE).build();
        initUI(view);
        selectDate(view);
        recyclerViewTicketList.setHasFixedSize(true);
        recyclerViewTicketList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewTicketList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.divider).marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());

        startDate = editTextStartDate.getText().toString();
        endDate = editTextEndDate.getText().toString();
        segmentedGroupSwitcher.setAnimation(animationLeftRight);
        sendData(view);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate = editTextStartDate.getText().toString();
                endDate = editTextEndDate.getText().toString();
                ticketList.clear();
                sendData(view);
            }
        });
        segmentedGroupSwitcher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if ( checkId == R.id.radioButtonOpen){
                    filteredTicketList(stringOpen);
                }
                else if ( checkId == R.id.radioButtonResponded){
                    filteredTicketList(stringResponded);
                }
                else if ( checkId == R.id.radioButtonAttended){
                    filteredTicketList(stringSolved);
                }
                else if ( checkId == R.id.radioButtonClosed){
                    filteredTicketList(stringCompleted) ;
                }
                else {
                    filteredTicketList("");
                }
            }
        });
        return view;
    }

    private void filteredTicketList(String status) {
        Collection<Ticket> filtered = Collections2.filter(ticketList, new customPredicate( status ));
        ArrayList<Ticket> filteredList = new ArrayList<Ticket>(filtered);
        generateStakeholderView( filteredList );
    }

    private void sendData(View view) {
        dialog.show();
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.API.TICKET_LIST + "/" + userId + "?" + "fromDate="+ startDate + "&toDate=" + endDate,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response", response);
                            try {
                                dialog.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = jsonObjectData.getJSONArray("complain_list");
                                if ( 0 == jsonArray.length() ) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Ticket not available", Toast.LENGTH_LONG).show();
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String shopInfo = jsonObject1.getString("shop__");
                                    JSONObject jsonObjectShopInfo = new JSONObject(shopInfo);
                                    String statusFilter = jsonObject1.getString("status").toLowerCase();
                                    if (statusFilter.matches(stringUnsolved)){
                                        statusFilter = stringSolved;
                                    }
                                    if ( statusFilter.matches(stringAttended)){
                                        statusFilter = stringSolved;
                                    }
                                    if ( statusFilter.matches(stringAssigned)){
                                        statusFilter = stringResponded;
                                    }
                                    /*else if (statusFilter.matches(stringSolved)){
                                        statusFilter = stringSolved;
                                    }*/
                                    /*else {
                                        statusFilter = stringSolved;
                                    }*/
                                    ticketList.add(new Ticket(
                                            jsonObject1.getString("ticket_no"),
                                            jsonObject1.getString("status"),
                                            jsonObject1.getString("priority"),
                                            jsonObjectShopInfo.getString("name"),
                                            jsonObjectShopInfo.getString("contact_person"),
                                            jsonObjectShopInfo.getString("phone"),
                                            jsonObjectShopInfo.getString("shop_address"),
                                            jsonObject1.getString("date"),
                                            statusFilter
                                    ));
                                }
                                generateStakeholderView( ticketList );
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

    private void generateStakeholderView(List<Ticket> ticketList) {
        TicketAdapter ticketAdapter = new TicketAdapter(getActivity().getApplicationContext(), ticketList);
        recyclerViewTicketList.setAdapter(ticketAdapter);
    }

    private void selectDate(View view) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);

        editTextStartDate.setText(MonthToText.mothNameText(day + "-" + (month + 1) + "-" + year));
        editTextEndDate.setText(MonthToText.mothNameText(day + "-" + (month + 1) + "-" + year));
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {
                        editTextStartDate.setText(MonthToText.mothNameText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + mYear));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {
                        editTextEndDate.setText(MonthToText.mothNameText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + mYear));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void initUI(View view) {
        segmentedGroupSwitcher = (SegmentedGroup) view.findViewById(R.id.segmentedGroupSwitcher);
        editTextStartDate = (EditText) view.findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) view.findViewById(R.id.editTextEndDate);
        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        recyclerViewTicketList = (RecyclerView) view.findViewById(R.id.recylcerViewTicketList);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder>{
       private  Context context;
       private List<Ticket> ticketList ;

        public TicketAdapter(Context context, List<Ticket> ticketList) {
            this.context = context;
            this.ticketList = ticketList;
        }

        @Override
        public TicketAdapter.TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.ticket_card, null);
            return new TicketAdapter.TicketViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TicketAdapter.TicketViewHolder holder, int position) {
            final Ticket ticket = ticketList.get(position);
            holder.textViewShopName.setText(ticket.getShopName());
            holder.textViewTicketNo.setText(ticket.getTicketNo());
            holder.textViewShopAddress.setText(ticket.getShopAddress());
            String upperString = ticket.getStatus().substring(0,1).toUpperCase() + ticket.getStatus().substring(1);
            if (upperString.matches("Forwarded")){
                upperString = "Forwarded";
            }
            if (upperString.matches("Fr_assigned")){
                upperString = "Workshop Assigned";
            }
            holder.textViewStatus.setText(upperString);
            holder.textViewDate.setText(ticket.getDate());
        }

        @Override
        public int getItemCount() {
            return ticketList.size();
        }

        public class TicketViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTicketNo, textViewShopName, textViewStatus, textViewShopAddress, textViewDate;

            public TicketViewHolder(View itemView) {
                super(itemView);
                textViewTicketNo = (TextView) itemView.findViewById(R.id.textViewTicketNo);
                textViewShopName = (TextView) itemView.findViewById(R.id.textViewShopName);
                textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
                textViewShopAddress = (TextView) itemView.findViewById(R.id.textViewShopAddress);
                textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            }
        }
    }
    private static class customPredicate implements Predicate<Ticket> {
        private String status = "";
        public customPredicate(String status) {
            this.status = status;
        }
        @Override
        public boolean apply(Ticket ticket) {
            if ( this.status.length() > 0 ) {
                return ticket.getStatusFilter().toLowerCase().equals( this.status.toLowerCase() );
            }
            else {
                return true;
            }
        }
    }
}
