package me.sankalpchauhan.kanbanboard.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Keep
public class TeamBoard implements Serializable {
    private String title;
    private String type;
    private @ServerTimestamp
    Date createdAt;
    private String createdByUserEmail;
    private String createdByUserId;
    private Map<String, Object> engagedUsers;

    public TeamBoard(){}

    public TeamBoard(String title, String type, String createdByUserEmail, String createdByUserId, Map<String, Object> engagedUsers) {
        this.title = title;
        this.type = type;
        this.createdByUserEmail = createdByUserEmail;
        this.createdByUserId = createdByUserId;
        this.engagedUsers = engagedUsers;
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

    public String getCreatedByUserEmail() {
        return createdByUserEmail;
    }

    public void setCreatedByUserEmail(String createdByUserEmail) {
        this.createdByUserEmail = createdByUserEmail;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Map<String, Object> getEngagedUsers() {
        return engagedUsers;
    }

    public void setEngagedUsers(Map<String, Object> engagedUsers) {
        this.engagedUsers = engagedUsers;
    }
}
