package de.majaf.voci.entity;

import de.mschoettle.entity.dto.FileDTO;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class DropsiFileMessage extends Message{

    private long dropsiFileId;
    private String dropsiFileName;
    private String dropsiFileType;

    @Transient
    private byte[] payload;

    public DropsiFileMessage() {
        super();
    }

    public DropsiFileMessage(FileDTO file, User user) {
        super(user);
        this.dropsiFileId = file.getId();
        this.dropsiFileName = file.getName();
        this.dropsiFileType = file.getFileType();
    }

    public long getDropsiFileId() {
        return dropsiFileId;
    }

    public void setDropsiFileId(long dropsiFileID) {
        this.dropsiFileId = dropsiFileID;
    }

    public String getDropsiFileName() {
        return dropsiFileName;
    }

    public void setDropsiFileName(String dropsiFileName) {
        this.dropsiFileName = dropsiFileName;
    }

    public String getDropsiFileType() {
        return dropsiFileType;
    }

    public void setDropsiFileType(String dropsiFileType) {
        this.dropsiFileType = dropsiFileType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
