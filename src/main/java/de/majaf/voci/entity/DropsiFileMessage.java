package de.majaf.voci.entity;

import de.mschoettle.entity.dto.FileDTO;

import javax.persistence.Entity;

@Entity
public class DropsiFileMessage extends Message{

    private long dropsiFileId;
    private String dropsiFileName;

    public DropsiFileMessage() {
    }

    public DropsiFileMessage(long fileID, String fileName, User user) {
        super(user);
        this.dropsiFileId = fileID;
        this.dropsiFileName = fileName;
    }

    public DropsiFileMessage(FileDTO file, User user) {
        super(user);
        this.dropsiFileId = file.getId();
        this.dropsiFileName = file.getName();
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
}
