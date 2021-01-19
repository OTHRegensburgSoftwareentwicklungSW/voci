let invitation_username_map = new Map();

function addToInvitationMap(invitationID, initiatorName) {
    invitation_username_map.set(invitationID, initiatorName);
}

function connectInvitationSocket(userID) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/joinedCall', function (joined) {
            if (joined.body)
                document.location.href = "/call";
        });
        stompClient.subscribe('/broker/' + userID + '/invited', function (inv) {
            let invitation = JSON.parse(inv.body)
            if (invitation) {
                addInvitation(invitation);
                subscribeToInvitationEnd(invitation);
            }
        });
        invitation_username_map.forEach((value, key) => subscribeToInvitationEnd(key, value));
    });
}

function subscribeToInvitationEnd(invitation) {
    stompClient.subscribe('/broker/' + invitation.id + '/endedInvitation', function (ended) {
        if (ended.body)
            removeInvitation(invitation.initiator.userName);
    });
}

// no thymeleaf form action, because the invitation in DOM can also be created dynamical
function takeInvitation(invitationID) {
    stompClient.send('/ws/' + invitationID + '/joinCall', {});
}

function addInvitation(invitation) {
    let username = invitation.initiator.userName
    addName('invitation_container', 'invitation_name', username);

    let names_par = document.getElementsByClassName('invitation_name');

    for (let i = 0; i < names_par.length; i++) {
        if (names_par[i].innerHTML === username) {
            names_par[i].classList.add('m-0');

            let takeCallButton = document.createElement("button");
            takeCallButton.classList.add('justify-content-center', 'btn', 'text-light', 'm-0', 'p-0', 'mt-1');

            let takeCallIcon = document.createElement("i");
            takeCallIcon.classList.add('material-icons');
            takeCallIcon.innerHTML = 'ring_volume';

            takeCallButton.appendChild(takeCallIcon);
            takeCallButton.addEventListener('click', function () {
                takeInvitation(invitation.id);
            })

            names_par[i].parentNode.parentNode.appendChild(takeCallButton);
            break;
        }
    }
}

function removeInvitation(username) {
    removeName('invitation_container', 'invitation_name', username);
}