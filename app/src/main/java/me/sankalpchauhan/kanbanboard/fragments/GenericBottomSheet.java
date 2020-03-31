package me.sankalpchauhan.kanbanboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

import me.sankalpchauhan.kanbanboard.R;

public class GenericBottomSheet extends BottomSheetDialogFragment {
    TextView members;
    Map<String, Object> memberMap = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.generic_bottom_sheet, container, false);
        members = v.findViewById(R.id.member_tv);
         memberMap= (Map<String, Object>) this.getArguments().getSerializable("memberMap");
         StringBuilder s = new StringBuilder();
        for(Map.Entry<String, Object> entry:memberMap.entrySet()){
            //members.setText((String)entry.getValue());
            s.append((String) entry.getValue()+"\n \n");
        }

        members.setText(s.toString());

        return v;
    }
}
