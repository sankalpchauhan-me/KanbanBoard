package me.sankalpchauhan.kanbanboard.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.view.BoardActivity;

public class ListCreateBottomSheet extends BottomSheetDialogFragment {
    EditText mListTitle;
    Button mCreateButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_create_bottomsheet, container, false);
        mListTitle = v.findViewById(R.id.list_title);
        mCreateButton = v.findViewById(R.id.create_BTN);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mListTitle.getText())) {
                    Activity act = getActivity();
                    if (act instanceof BoardActivity) {
                        ((BoardActivity) act).addListToDB(mListTitle.getText().toString());
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "A board must have a title", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }
}
