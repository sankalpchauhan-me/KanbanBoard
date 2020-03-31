package me.sankalpchauhan.kanbanboard.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.Map;

import me.sankalpchauhan.kanbanboard.model.Card;

import static me.sankalpchauhan.kanbanboard.util.Constants.ARCHIVE_CARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.CARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.PERSONAL_BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.hideProgressDialog;
import static me.sankalpchauhan.kanbanboard.util.HelperClass.showProgressDialog;

public class CardActivityRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    private CollectionReference databaseBoard = rootRef.collection(BOARDS);
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    public MutableLiveData<String> uploadImage(Context context, File destination, String listId) {
        MutableLiveData<String> newUrlMutableLiveData = new MutableLiveData<>();
        final StorageReference ref = mStorageReference.child("attachments/" + listId);
        Task uploadTask = ref.putFile(Uri.fromFile(destination));
//        showProgressDialog(context, "Uploading Attachment...");
        Toast.makeText(context, "Uploading Attachment...", Toast.LENGTH_LONG).show();
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = downloadUri.toString();
                  //  hideProgressDialog();
                    newUrlMutableLiveData.setValue(downloadURL);
                    Toast.makeText(context, "Attachment Uploaded", Toast.LENGTH_LONG).show();
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return newUrlMutableLiveData;
    }

    public MutableLiveData<Card> createCard(Context context, String boardId, String title, String listId, String attachmentUrl, Date dueDate){
        MutableLiveData<Card> newBoardMutableLiveData = new MutableLiveData<>();
        CollectionReference cardreference = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST);
        Card c = new Card(title,dueDate, attachmentUrl);
        cardreference.add(c).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                newBoardMutableLiveData.setValue(c);
                Toast.makeText(context, "Card "+title+" Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return newBoardMutableLiveData;
    }

    public void updateCard(Context context, Map<String, Object> updateMap, String boardId, String listId, String cardId){
        //MutableLiveData<Card> newBoardMutableLiveData = new MutableLiveData<>();
        DocumentReference cardreference = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST).document(cardId);
        cardreference.update(updateMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //newBoardMutableLiveData.setValue(c);
                Toast.makeText(context, "Card Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //return newBoardMutableLiveData;
    }

    public void archiveCard(Context context, String boardId, String listId, String cardId, Card card){
        Log.e("TESTING", boardId+" "+listId+" "+cardId+" "+card);
        CollectionReference archiveCardRef = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardId).collection(BOARD_LIST).document(listId).collection(ARCHIVE_CARD_LIST);
        DocumentReference oldCardRef = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST).document(cardId);
        archiveCardRef.add(card).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               oldCardRef.delete().addOnCompleteListener(task1 -> {
                   Toast.makeText(context, "Card Archived", Toast.LENGTH_LONG).show();
               });
           }
           else {
               Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
           }
        });
    }

    //TODO: Do something to remove this repetition only reference is changing

    public MutableLiveData<Card> createTeamCard(Context context, String boardId, String title, String listId, String attachmentUrl, Date dueDate){
        MutableLiveData<Card> newBoardMutableLiveData = new MutableLiveData<>();
        CollectionReference cardreference = databaseBoard.document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST);
        Card c = new Card(title,dueDate, attachmentUrl);
        cardreference.add(c).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                newBoardMutableLiveData.setValue(c);
                Toast.makeText(context, "Card "+title+" Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return newBoardMutableLiveData;
    }

    public void updateTeamCard(Context context, Map<String, Object> updateMap, String boardId, String listId, String cardId){
        //MutableLiveData<Card> newBoardMutableLiveData = new MutableLiveData<>();
        DocumentReference cardreference = databaseBoard.document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST).document(cardId);
        cardreference.update(updateMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //newBoardMutableLiveData.setValue(c);
                Toast.makeText(context, "Card Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //return newBoardMutableLiveData;
    }

    public void archiveTeamCard(Context context, String boardId, String listId, String cardId, Card card){
        Log.e("TESTING", boardId+" "+listId+" "+cardId+" "+card);
        CollectionReference archiveCardRef = databaseBoard.document(boardId).collection(BOARD_LIST).document(listId).collection(ARCHIVE_CARD_LIST);
        DocumentReference oldCardRef = databaseBoard.document(boardId).collection(BOARD_LIST).document(listId).collection(CARD_LIST).document(cardId);
        archiveCardRef.add(card).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                oldCardRef.delete().addOnCompleteListener(task1 -> {
                    Toast.makeText(context, "Card Archived", Toast.LENGTH_LONG).show();
                });
            }
            else {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
