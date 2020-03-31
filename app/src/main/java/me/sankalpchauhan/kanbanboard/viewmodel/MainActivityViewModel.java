package me.sankalpchauhan.kanbanboard.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.Query;

import me.sankalpchauhan.kanbanboard.model.Board;
import me.sankalpchauhan.kanbanboard.model.TeamBoard;
import me.sankalpchauhan.kanbanboard.repository.LoginRepository;
import me.sankalpchauhan.kanbanboard.repository.MainActivityRepository;
import me.sankalpchauhan.kanbanboard.util.Constants;

public class MainActivityViewModel extends AndroidViewModel {
    MainActivityRepository mainActivityRepository;
    public LiveData<Board> boardLiveData;
    public LiveData<TeamBoard> teamBoardLiveData;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mainActivityRepository = new MainActivityRepository();
    }

    public void createBoard(Context context, String UserUID, String boardTitle, String boardType){
        Log.e(Constants.TAG, UserUID+" "+boardType+" "+boardTitle);
        boardLiveData = mainActivityRepository.createPersonalBoard(context, UserUID, boardTitle, boardType);
    }

    public Query getQuery(String UserId){
        return mainActivityRepository.getPersonalBoard(UserId);
    }

    public Query getTeamQuery(){
        return mainActivityRepository.getTeamBoard();
    }


    //TODO: Optimize this
    public void createTeamBoard(Context context, String boardTitle, String boardType){
        teamBoardLiveData = mainActivityRepository.createTeamBoard(context, boardTitle, boardType);
    }
}
