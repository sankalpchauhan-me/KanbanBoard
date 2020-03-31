package me.sankalpchauhan.kanbanboard.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.view.MainActivity;

public class BoardCreateBottomSheet extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {
    EditText mBoardTitle;
    Button mCreateBTN;
    Spinner mSelector;
    String mType = "Personal";
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.board_create_bottomsheet, container, false);
        mBoardTitle = v.findViewById(R.id.board_title);
        mCreateBTN = v.findViewById(R.id.create_BTN);
        mSelector = v.findViewById(R.id.spinner_selection);
        mSelector.setOnItemSelectedListener(this);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Create Board");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        List<String> boardType = new ArrayList<String>();
        boardType.add("Personal");
        boardType.add("Team");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, boardType);
        mSelector.setAdapter(dataAdapter);

        mCreateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mBoardTitle.getText())) {
                    Activity act = getActivity();
                    if (act instanceof MainActivity) {
                        if(mType.equals("Personal")) {
                            ((MainActivity) act).addBoardToDB(mBoardTitle.getText().toString(), mType);
                        } else if(mType.equals("Team")){
                            ((MainActivity) act).addTeamBoardToDB(mBoardTitle.getText().toString(), mType);
                        }
                        dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "A board must have a title", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        mType = adapterView.getItemAtPosition(position).toString();
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
