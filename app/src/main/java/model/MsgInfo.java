package model;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.UUID;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Conversation.class,
        parentColumns = "id",
        childColumns = "number",
        onDelete = CASCADE),
        indices=@Index(value="number"))
public class MsgInfo {
    private String cleLabel = "";
    private String statusLabel = "";
    private String messageLabel = "";
    private String content = "";

    private boolean arBut = false;
    private boolean keyBut = false;
    private boolean readBut = false;

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String number;

    private String uuid;

    public MsgInfo() {
    }


    public MsgInfo(String cleLabel, String statusLabel, String messageLabel, String content, boolean arBut, boolean keyBut, boolean readBut, int id, String number, String uuid) {
        this.cleLabel = cleLabel;
        this.statusLabel = statusLabel;
        this.messageLabel = messageLabel;
        this.content = content;
        this.arBut = arBut;
        this.keyBut = keyBut;
        this.readBut = readBut;
        this.id = id;
        this.number = number;
        this.uuid = uuid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCleLabel() {
        return cleLabel;
    }

    public void setCleLabel(String cleLabel) {
        this.cleLabel = cleLabel;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getMessageLabel() {
        return messageLabel;
    }

    public void setMessageLabel(String messageLabel) {
        this.messageLabel = messageLabel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isArBut() {
        return arBut;
    }

    public void setArBut(boolean arBut) {
        this.arBut = arBut;
    }

    public boolean isKeyBut() {
        return keyBut;
    }

    public void setKeyBut(boolean keyBut) {
        this.keyBut = keyBut;
    }

    public boolean isReadBut() {
        return readBut;
    }

    public void setReadBut(boolean readBut) {
        this.readBut = readBut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
