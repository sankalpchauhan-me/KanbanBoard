package me.sankalpchauhan.kanbanboard.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import me.sankalpchauhan.kanbanboard.util.PositionAwareDocument;

@Keep
public class Card implements Serializable, PositionAwareDocument {
    private String title;
    public Double position=1001.1;
    private Date dueDate;
    private String attachmentUrl;

    @ServerTimestamp
    public Date createdAt;

    Card(){}

    public Card(String title) {
        this.title = title;
    }

    public Card(String title, Date dueDate, String attachmentUrl) {
        this.title = title;
        this.dueDate = dueDate;
        this.attachmentUrl = attachmentUrl;
    }

    @Override
    public Double getPosition() {
        return position;
    }

    @Override
    public void setPosition(double position) {
        this.position=position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getAttachment() {
        return attachmentUrl;
    }

    public void setAttachment(String attachment) {
        this.attachmentUrl = attachment;
    }
}
