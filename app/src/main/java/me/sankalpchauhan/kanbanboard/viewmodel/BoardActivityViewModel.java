package me.sankalpchauhan.kanbanboard.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Map;

import me.sankalpchauhan.kanbanboard.model.BoardList;
import me.sankalpchauhan.kanbanboard.repository.BoardActivityRepository;
import me.sankalpchauhan.kanbanboard.repository.MainActivityRepository;
import me.sankalpchauhan.kanbanboard.util.Constants;

public class BoardActivityViewModel extends AndroidViewModel {
    BoardActivityRepository boardActivityRepository;
    public LiveData<BoardList> listLiveData;

    public BoardActivityViewModel(@NonNull Application application) {
        super(application);
        boardActivityRepository = new BoardActivityRepository();
    }


    public void createList(Context context, String boardId, String title){
        //Log.e(Constants.TAG, UserUID+" "+boardType+" "+boardTitle);
        listLiveData = boardActivityRepository.createPersonalList(context, boardId, title);
    }

    public void createTeamList(Context context, String boardId, String title){
        //Log.e(Constants.TAG, UserUID+" "+boardType+" "+boardTitle);
        listLiveData = boardActivityRepository.createTeamList(context, boardId, title);
    }

    public CollectionReference getQuery(String DocumentId){
        //Log.e(Constants.TAG,"Test");
        return boardActivityRepository.getBoardList(DocumentId);
    }

    public void addTeamMember(Context context, String boardId, Map<String, Object> updatedMap){
        boardActivityRepository.addUserToBoard(context, boardId, updatedMap);
    }


}
