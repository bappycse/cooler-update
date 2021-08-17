package com.csi.meghnacooler.Technician;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.csi.meghnacooler.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahid on 5/2/2019.
 */

public class SQLiteListAdapterProblem extends BaseAdapter {

    final ActionStatus.status statusAction= new ActionStatus.status();
    Context context;
    ArrayList<String> id;
    ArrayList<String> problemName;
    ArrayList<String> quantity;

    public SQLiteListAdapterProblem(Context context2, ArrayList<String> id, ArrayList<String> problemName, ArrayList<String> quantity) {
        this.context = context2;
        this.id = id;
        this.problemName = problemName;
        this.quantity = quantity;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return id.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final Holder holder;
        LayoutInflater layoutInflater;
        final String[] updateRequisitionList = {"Select", "Done", "Pending", "Workshop", "NoProblem"};

        if (view == null) {
            holder = new Holder();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.problem_card, null);
            holder.spinnerUpdateRequisition= view.findViewById(R.id.spinner_update_requisition);
            holder.textViewProblemName = (TextView) view.findViewById(R.id.textViewProblemName);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, updateRequisitionList);
        holder.spinnerUpdateRequisition.setAdapter(adapter);
        holder.textViewProblemName.setText(problemName.get(position));

        holder.spinnerUpdateRequisition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectItem= updateRequisitionList[i];
                switch (selectItem) {
                    case "Done":
                        statusAction.doneIdList.add(quantity.get(position));
                        if (statusAction.pendingIdList.contains(quantity.get(position))){
                            statusAction.pendingIdList.remove(quantity.get(position));
                        }
                        if (statusAction.workshopIdList.contains(quantity.get(position))){
                            statusAction.workshopIdList.remove(quantity.get(position));
                        }
                        if (statusAction.noProblemIdList.contains(quantity.get(position))){
                            statusAction.noProblemIdList.remove(quantity.get(position));
                        }
                        statusAction.selectList.remove("Select");
                        Log.d("check_done", "pending_list: "+statusAction.pendingIdList.toString()+"workshop_list: "+statusAction.workshopIdList.toString()+"noProblem_list: "+statusAction.noProblemIdList.toString());
                        break;
                    case "Pending":
                        statusAction.pendingIdList.add(quantity.get(position));
                        if (statusAction.workshopIdList.contains(quantity.get(position))){
                            statusAction.workshopIdList.remove(quantity.get(position));
                        }
                        if (statusAction.noProblemIdList.contains(quantity.get(position))){
                            statusAction.noProblemIdList.remove(quantity.get(position));
                        }
                        if (statusAction.doneIdList.contains(quantity.get(position))){
                            statusAction.doneIdList.remove(quantity.get(position));
                        }
                        statusAction.selectList.remove("Select");
                        Log.d("check_pending", "workshop_list: "+statusAction.workshopIdList.toString()+"noProblem_list: "+statusAction.noProblemIdList.toString()+"done_list: "+statusAction.doneIdList.toString()+"pending_list: "+statusAction.pendingIdList.toString());
                        break;
                    case "Workshop":
                        statusAction.workshopIdList.add(quantity.get(position));
                        if (statusAction.noProblemIdList.contains(quantity.get(position))){
                            statusAction.noProblemIdList.remove(quantity.get(position));
                        }
                        if (statusAction.doneIdList.contains(quantity.get(position))){
                            statusAction.doneIdList.remove(quantity.get(position));
                        }
                        if (statusAction.pendingIdList.contains(quantity.get(position))){
                            statusAction.pendingIdList.remove(quantity.get(position));
                        }
                        statusAction.selectList.remove("Select");
                        Log.d("check_workshop", "noProblem_list: "+statusAction.noProblemIdList.toString()+"done_list: "+statusAction.doneIdList.toString()+"pending_list: "+statusAction.pendingIdList.toString()+"workshop_list: "+statusAction.workshopIdList.toString());
                        break;
                    case "NoProblem":
                        statusAction.noProblemIdList.add(quantity.get(position));
                        if (statusAction.doneIdList.contains(quantity.get(position))){
                            statusAction.doneIdList.remove(quantity.get(position));
                        }
                        if (statusAction.pendingIdList.contains(quantity.get(position))){
                            statusAction.pendingIdList.remove(quantity.get(position));
                        }
                        if (statusAction.workshopIdList.contains(quantity.get(position))){
                            statusAction.workshopIdList.remove(quantity.get(position));
                        }
                        statusAction.selectList.remove("Select");
                        Log.d("check_workshop", "done_list: "+statusAction.doneIdList.toString()+"pending_list: "+statusAction.pendingIdList.toString()+"workshop_list: "+statusAction.workshopIdList.toString()+"no_problem_list"+statusAction.noProblemIdList.toString());
                        break;
                    case "Select":
                        statusAction.selectList.add("Select");
                        Log.d("check_selected: ", statusAction.selectList.toString());
                        if (statusAction.doneIdList.contains(quantity.get(position))){
                            statusAction.doneIdList.remove(quantity.get(position));
                        }
                        if (statusAction.pendingIdList.contains(quantity.get(position))){
                            statusAction.pendingIdList.remove(quantity.get(position));
                        }
                        if (statusAction.workshopIdList.contains(quantity.get(position))){
                            statusAction.workshopIdList.remove(quantity.get(position));
                        }
                        if (statusAction.noProblemIdList.contains(quantity.get(position))){
                            statusAction.noProblemIdList.remove(quantity.get(position));
                        }
                    default:
                        //statusAction.select= "Select";
                }

                Log.i("your_choose:", quantity.get(position)+" and status "+updateRequisitionList[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    public static class Holder {
        TextView textViewProblemName;
        Spinner spinnerUpdateRequisition;
    }

}
