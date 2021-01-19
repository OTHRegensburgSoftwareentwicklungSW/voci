function connectCallSocket(invitationID, userID, textChannelID) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/leftCall', function (left) {
            if (left.body)
                window.location.href = "/main";
        });
        stompClient.subscribe('/broker/' + invitationID + '/addedCallMember', function (user) {
            if (user.body) {
                let u = JSON.parse(user.body);
                addCallMember(u.userName);
            }
        });
        stompClient.subscribe('/broker/' + invitationID + '/removedCallMember', function (username) {
            if (username.body != null)
                removeCallMember(username.body);
        });
        stompClient.subscribe('/broker/' + invitationID + '/endedCall', function (ended) {
            if (ended.body) {
                window.location.href = "/call";
            }
        });
        subscribeToTextChannel(userID, textChannelID);
    });
}

function addCallMember(username) {
    addName('member_container', 'member_name', username);
    removeName('invited_container', 'invited_name', username);
}

function removeCallMember(username) {
    let emptyMessage = document.getElementById('empty-message');
    if(emptyMessage !== null) {
        emptyMessage.parentNode.removeChild(emptyMessage);
    }
    addName('invited_container', 'invited_name', username);
    removeName('member_container', 'member_name', username);
}