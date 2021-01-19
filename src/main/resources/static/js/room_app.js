function connectRoomSocket(userID, textChannelID) {
    connectSocket(function () {
        subscribeToTextChannel(userID, textChannelID);
    });
}