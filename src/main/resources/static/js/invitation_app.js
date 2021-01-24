function connectInvitationSocket(userID, invitationList) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/invited', function (call) {
            let c = JSON.parse(call.body)
            if (c) {
                addInvitation(c.invitation);
                stompClient.subscribe('/broker/' + c.id + '/endedInvitation', function (ended) {
                    if (ended.body)
                        removeInvitation(c.invitation.initiator.userName);
                });
            }
        });
        for (let i = 0; i < invitationList.length; i++) {
            if (invitationList[i].call !== null) {
                let invitation = invitationList[i];
                stompClient.subscribe('/broker/' + invitation.call.id + '/endedInvitation', function (ended) {
                    if (ended.body)
                        removeInvitation(invitation.initiator.userName);
                });
            }
        }
    });
}

function addInvitation(invitation) {
    let username = invitation.initiator.userName
    addName('invitation_container', 'invitation_name', username);

    let names_par = document.getElementsByClassName('invitation_name');

    for (let i = 0; i < names_par.length; i++) {
        if (names_par[i].innerHTML === username) {
            names_par[i].classList.add('m-0');

            let takeCallRef = document.createElement("a");
            takeCallRef.classList.add('justify-content-center', 'btn', 'text-light', 'm-0', 'p-0', 'mt-1');

            let takeCallIcon = document.createElement("i");
            takeCallIcon.classList.add('material-icons');
            takeCallIcon.innerHTML = 'ring_volume';

            takeCallRef.appendChild(takeCallIcon);
            takeCallRef.href = "/invitation?accessToken=" + invitation.accessToken;

            names_par[i].parentNode.parentNode.appendChild(takeCallRef);
            break;
        }
    }
}

function removeInvitation(username) {
    removeName('invitation_container', 'invitation_name', username);
}