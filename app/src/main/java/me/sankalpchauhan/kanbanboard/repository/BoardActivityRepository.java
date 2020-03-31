package me.sankalpchauhan.kanbanboard.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Map;

import me.sankalpchauhan.kanbanboard.model.BoardList;

import static me.sankalpchauhan.kanbanboard.util.Constants.BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.PERSONAL_BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;

public class BoardActivityRepository {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    private CollectionReference databaseBoardRef = rootRef.collection(BOARDS);

    public MutableLiveData<BoardList> createPersonalList(Context context, String boardId, String title){
        MutableLiveData<BoardList> newBoardMutableLiveData = new MutableLiveData<>();
        CollectionReference boardreference = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardId).collection(BOARD_LIST);
        BoardList b = new BoardList(title);
        boardreference.add(b).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                newBoardMutableLiveData.setValue(b);
                Toast.makeText(context, "List "+title+" Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return newBoardMutableLiveData;
    }

    public MutableLiveData<BoardList> createTeamList(Context context, String boardId, String title){
        MutableLiveData<BoardList> newBoardMutableLiveData = new MutableLiveData<>();
        CollectionReference boardreference = databaseBoardRef.document(boardId).collection(BOARD_LIST);
        BoardList b = new BoardList(title);
        boardreference.add(b).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                newBoardMutableLiveData.setValue(b);
                Toast.makeText(context, "List "+title+" Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return newBoardMutableLiveData;
    }

    public void addUserToBoard(Context context, String boardId, Map<String, Object> updatedMap){
        databaseBoardRef.document(boardId).update(updatedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "User Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public CollectionReference getBoardList(String DocumentId){
        return database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(DocumentId).collection(BOARD_LIST);
    }

}
