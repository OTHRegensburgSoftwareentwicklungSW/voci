package de.majaf.voci.control.exceptions.room;

public class RoomDoesNotExistException extends Exception {
    private long roomID;

    public RoomDoesNotExistException() {
        super();
    }

    public RoomDoesNotExistException(String message) {
        super(message);
    }

    public RoomDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoomDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public RoomDoesNotExistException(long roomID) {
        this();
        this.roomID = roomID;
    }

    public RoomDoesNotExistException(long roomID, String message) {
        this(message);
        this.roomID = roomID;
    }

    public RoomDoesNotExistException(long roomID, String message, Throwable cause) {
        this(message, cause);
        this.roomID = roomID;
    }

    public RoomDoesNotExistException(long roomID, Throwable cause) {
        this(cause);
        this.roomID = roomID;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }
}
