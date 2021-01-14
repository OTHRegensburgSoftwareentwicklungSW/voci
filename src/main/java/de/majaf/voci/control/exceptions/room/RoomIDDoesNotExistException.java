package de.majaf.voci.control.exceptions.room;

public class RoomIDDoesNotExistException extends Exception {
    private long roomID;

    public RoomIDDoesNotExistException() {
        super();
    }

    public RoomIDDoesNotExistException(String message) {
        super(message);
    }

    public RoomIDDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoomIDDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public RoomIDDoesNotExistException(long roomID) {
        this();
        this.roomID = roomID;
    }

    public RoomIDDoesNotExistException(long roomID, String message) {
        this(message);
        this.roomID = roomID;
    }

    public RoomIDDoesNotExistException(long roomID, String message, Throwable cause) {
        this(message, cause);
        this.roomID = roomID;
    }

    public RoomIDDoesNotExistException(long roomID, Throwable cause) {
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
