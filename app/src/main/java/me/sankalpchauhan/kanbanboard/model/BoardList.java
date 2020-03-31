package me.sankalpchauhan.kanbanboard.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

import me.sankalpchauhan.kanbanboard.util.PositionAwareDocument;

@Keep
public class BoardList implements Serializable, PositionAwareDocument {
    public String title;
    public Double position=1001.1;

    @ServerTimestamp
    public Date createdAt;

    BoardList(){}

    public BoardList(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Double getPosition() {
        return position;
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
    }
}
