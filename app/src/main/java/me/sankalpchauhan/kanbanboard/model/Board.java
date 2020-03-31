package me.sankalpchauhan.kanbanboard.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

@Keep
public class Board implements Serializable {
    private String title;
    private String type;
    private @ServerTimestamp
    Date createdAt;
    public Board(){}

    public Board(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
