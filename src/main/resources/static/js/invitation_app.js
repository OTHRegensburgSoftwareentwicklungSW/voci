function connectInvitationSocket(userID, invitationList) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/invited', function (inv) {
            let invitation = JSON.parse(inv.body)
            if (invitation) {
                addInvitation(invitation);
                subscribeToInvitationEnd(invitation);
            }
        });
        for(let i = 0; i < invitationList.length; i++) {
            subscribeToInvitationEnd(invitationList[0]);
        }
    });
}

function subscribeToInvitationEnd(invitation) {
    stompClient.subscribe('/broker/' + invitation.id + '/endedInvitation', function (ended) {
        if (ended.body)
            removeInvitation(invitation.initiator.userName);
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