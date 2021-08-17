package com.csi.meghnacooler.Technician;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Sqlite.Datagetset;
import com.csi.meghnacooler.Sqlite.SqlitieFunction;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

public class StockRequisitionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    Button buttonAdd;
    LinearLayout linearLayoutList;
    TextView textViewTitle;
    SqlitieFunction sqlitieFunction;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    SQLiteListAdapter listAdapter;
    ArrayList<String> productArrayList = new ArrayList();
    ArrayList<String> idArrayList = new ArrayList();
    ArrayList<String> quantityArrayList = new ArrayList();
    ExpandableHeightListView expandableListView;
    int itemCount;
    Animation animationLeftRight;
    LinearLayout linearLayoutForm;
    public StockRequisitionFragment() {

    }

    public static StockRequisitionFragment newInstance(String param1, String param2) {
        StockRequisitionFragment fragment = new StockRequisitionFragment();
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
        View view = inflater.inflate(R.layout.fragment_stock_requisition, container, false);
        initUI(view);
        animationLeftRight = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.left_enter);
        linearLayoutForm.setAnimation(animationLeftRight);
        sqlitieFunction = new SqlitieFunction(getContext());
        showSqliteData(view);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertintoDatabase(view);
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
                        showSqliteData(view);
                    }
                });
            }
        });
        return view;
    }

    private void insertintoDatabase(View view) {
        Datagetset datagetset = new Datagetset("ABC","1","");
        sqlitieFunction.datainsert(datagetset);
        showSqliteData(view);
        listAdapter.notifyDataSetChanged();
    }

    private void showSqliteData(View view) {
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
        listAdapter = new SQLiteListAdapter(getActivity(),idArrayList,productArrayList,quantityArrayList);
        expandableListView.setAdapter(listAdapter);
        itemCount = listAdapter.getCount();
        expandableListView.setExpanded(true);
        if (itemCount != 0){
            linearLayoutList.setVisibility(View.VISIBLE);
            textViewTitle.setVisibility(View.VISIBLE);
        }
        if (itemCount == 0){
            linearLayoutList.setVisibility(View.GONE);
            textViewTitle.setVisibility(View.GONE);
        }
        cursor.close();
    }

    private void initUI(View view) {
        buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        expandableListView = (ExpandableHeightListView) view.findViewById(R.id.listViewRequisitionList);
        linearLayoutList = (LinearLayout) view.findViewById(R.id.layoutList);
        textViewTitle = (TextView) view.findViewById(R.id.textViewOrderlistTitle);
        linearLayoutForm = (LinearLayout) view.findViewById(R.id.orderEntryForm);
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
}
