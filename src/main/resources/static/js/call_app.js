function connectCallSocket(call, user, textChannel) {
    connectSocket(function () {
            stompClient.subscribe('/broker/' + call.id + '/addedCallMember', function (added_user) {
                if (added_user.body) {
                    let u = JSON.parse(added_user.body);
                    addCallMember(u.userName);
                }
            });
            stompClient.subscribe('/broker/' + call.id + '/removedCallMember', function (rem_user) {
                if (rem_user.body) {
                    let u = JSON.parse(rem_user.body);
                    removeCallMember(u.userName);
                }
            });
            stompClient.subscribe('/broker/' + call.id + '/endedCall', function (endedByTimeOut) {
                if (endedByTimeOut.body === 'false' && user.type === 'registeredUser' && user.id === call.invitation.initiator.id)
                    window.location.href = "/call";
                else window.location.href = "/call/ended";

                disconnect();
            });
            if (call.invitation != null) {
                stompClient.subscribe('/broker/' + call.id + "/initiatorLeft", function (initiator) {
                    if (initiator.body) {
                        let u = JSON.parse(initiator.body);
                        removeCallMember(u.userName);
                        removeInvitationInfo();
                    }
                });
            }
            subscribeToTextChannel(user.id, textChannel.id);

            // subscribeToSignalMessages(voiceChannel.id, user.id, call) // would be called if voice-streaming was working
        }
    )
    ;
}

function addCallMember(username) {
    addName('member_container', 'member_name', username);
    removeName('invited_container', 'invited_name', username);
}

function removeCallMember(username) {
    let emptyMessage = document.getElementById('empty-message');
    if (emptyMessage !== null) {
        emptyMessage.parentNode.removeChild(emptyMessage);
    }
    addName('invited_container', 'invited_name', username);
    removeName('member_container', 'member_name', username);
}

function removeInvitationInfo() {
    let invited_container = document.getElementById('invited_container');
    invited_container.parentNode.removeChild(invited_container);

    let access_token_container = document.getElementById('invitation_token_container');
    access_token_container.parentNode.removeChild(access_token_container);
}