package com.csi.meghnacooler.PersonalInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csi.meghnacooler.R;
import com.csi.meghnacooler.Utility.Constant;

public class PersonalInfoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    LinearLayout linearLayoutForm;
    Animation animationLeftRight;
    TextView textViewUserName,textViewUserId,textViewDesignation,textViewUserGroup,textViewEmail,textViewPhone,textViewNote;
    SharedPreferences sharedPreferencesUser;
    public PersonalInfoFragment() {
        // Required empty public constructor
    }

    public static PersonalInfoFragment newInstance(String param1, String param2) {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        animationLeftRight = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.left_enter);
        initUI(view);
        sharedPreferencesUser = getActivity().getSharedPreferences(Constant.sharedPrefItems.globalPreferenceNameForUser,Context.MODE_PRIVATE);
        textViewUserName.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_NAME,""));
        textViewUserId.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.USER_NAME_ID,""));
        textViewDesignation.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.DESIGNATION,""));
        textViewUserGroup.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.GROUP,""));
        textViewEmail.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.EMAIL,""));
        textViewPhone.setText(sharedPreferencesUser.getString(Constant.sharedPrefItems.PHONE,""));
        linearLayoutForm.setAnimation(animationLeftRight);
        return view;
    }

    private void initUI(View view) {
        linearLayoutForm = (LinearLayout) view.findViewById(R.id.layoutView);
        textViewUserId = (TextView) view.findViewById(R.id.textViewUserId);
        textViewUserName = (TextView) view.findViewById(R.id.textViewName);
        textViewDesignation = (TextView) view.findViewById(R.id.textViewDesignation);
        textViewUserGroup = (TextView) view.findViewById(R.id.textViewUserGroup);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewPhone = (TextView) view.findViewById(R.id.textViewPhone);
        textViewNote = (TextView) view.findViewById(R.id.textViewNote);
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
