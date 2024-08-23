package com.aftershade.workmode.users.ui.trainers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.aftershade.workmode.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class LocationTrainersFragment extends BottomSheetDialogFragment {

    private EditText cityEditText;
    private Spinner citySpinner;
    private String city;
    private MaterialButton saveBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_trainers, container, false);

        cityEditText = view.findViewById(R.id.city_text_location);
        citySpinner = view.findViewById(R.id.city_spinner_location);
        saveBtn = view.findViewById(R.id.save_btn_location);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_single_choice, new String[]{"Anywhere",
                "Johannesburg", "Cape Town", "Vereeniging", "Vanderbijlpark"});

        citySpinner.setAdapter(arrayAdapter);
        citySpinner.setSelection(0);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCity();
            }
        });


        return view;
    }

    private void getCity() {

        city = cityEditText.getText().toString();
        String selected = citySpinner.getSelectedItem().toString();

        if (city.isEmpty()) {
            city = selected;
        }

        SharedPreferences preferences = requireActivity().getSharedPreferences("cityLocationFrag", Context.MODE_PRIVATE);
        preferences.edit().putString("cityFilter", city).apply();

    }
}