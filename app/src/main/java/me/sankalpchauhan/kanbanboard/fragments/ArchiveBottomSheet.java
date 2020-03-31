package me.sankalpchauhan.kanbanboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.adapters.ArchiveAdapter;

import static me.sankalpchauhan.kanbanboard.util.Constants.BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.CARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.PERSONAL_BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;

public class ArchiveBottomSheet extends BottomSheetDialogFragment {
    String s;
    ArchiveAdapter adapter;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    private CollectionReference databaseBoard = rootRef.collection(BOARDS);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.archive_bottom_sheet, container, false);
        s= this.getArguments().getString("isTeam");
//        if(s.equals("yes")){
//            boardListCollection = databaseBoard.document(boardid).collection(BOARD_LIST).document(getSnapshots().getSnapshot(position).getId()).collection(CARD_LIST);
//        } else {
//            boardListCollection = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardid).collection(BOARD_LIST).document(getSnapshots().getSnapshot(position).getId()).collection(CARD_LIST);
//        }
//
//        //Log.e(Constants.TAG, id);
//        FirestoreRecyclerOptions<Card> cardOptions = new FirestoreRecyclerOptions.Builder<Card>()
//                .setQuery(boardListCollection.orderBy("position"), Card.class)
//                .build();
//        cardAdapter = new CardAdapter(cardOptions);
//        listViewHolder.cardRv.setHasFixedSize(false);
//        listViewHolder.cardRv.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
//        listViewHolder.cardRv.setRecycledViewPool(recycledViewPool);
//        listViewHolder.cardRv.setAdapter(cardAdapter = new CardAdapter(cardOptions) {
//            @Override
//            public void onDataChanged() {
//                super.onDataChanged();
//                //setupEmptyView(mExperienceList, mExperienceEmpty, getItemCount());
//            }
//        });
//        cardAdapter.startListening();
//        BoardActivity boardActivity = (BoardActivity) mContext;

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
