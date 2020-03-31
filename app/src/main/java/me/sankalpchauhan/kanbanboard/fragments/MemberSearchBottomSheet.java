package me.sankalpchauhan.kanbanboard.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.util.Constants;
import me.sankalpchauhan.kanbanboard.view.BoardActivity;

import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;

public class MemberSearchBottomSheet extends BottomSheetDialogFragment {

    EditText memberEmail;
    Button searchButton, addButoon;
    ProgressBar progressBar;

    // TODO: Move DB reference to repository and fetch from their
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    String userEmail, userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.member_bottomsheet, container, false);
        memberEmail = v.findViewById(R.id.member_email);
        searchButton = v.findViewById(R.id.search_BTN);
        addButoon = v.findViewById(R.id.add_BTN);
        progressBar = v.findViewById(R.id.progress_cir);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(memberEmail.getText())){
                   database.whereEqualTo("email", memberEmail.getText().toString())
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   progressBar.setVisibility(View.GONE);
                                   if (task.isSuccessful()) {
                                       if(task.getResult().isEmpty()){
                                           Toast.makeText(getContext(), "Email Not Found", Toast.LENGTH_LONG).show();
                                       }
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           Toast.makeText(getContext(), "Email Found", Toast.LENGTH_LONG).show();
                                           Log.e(Constants.TAG, document.getId() + " => " + document.get("email")+" "+task.getResult().size());
                                           addButoon.setVisibility(View.VISIBLE);
                                           userId = document.getId();
                                           userEmail = (String) document.get("email");
                                       }
                                   } else {
                                       Log.e(Constants.TAG, "Error getting documents: ", task.getException());
                                       Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                   }
                               }
                           });
                }
            }
        });

        addButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity act = getActivity();
                if (act instanceof BoardActivity){
                    ((BoardActivity) act).addMember(userId, userEmail);
                    dismiss();
                }
            }
        });

        return v;
    }
}
