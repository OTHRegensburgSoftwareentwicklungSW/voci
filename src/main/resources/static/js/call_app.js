let stompClient = null;

function connectCallSocket(invitationID, userID) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/leftCall', function (left) {
            if (left.body)
                window.location.href = "/call";
        });
        stompClient.subscribe('/broker/' + invitationID + '/addedCallMember', function (username) {
            if (username.body != null)
                addCallMember(username.body);
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
    });
}

function connectInvitationSocket(userID) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/joinedCall', function (joined) {
            if (joined.body)
                document.location.href = "/call";
        });
        stompClient.subscribe('/broker/' + userID + '/startedCall', function (started) {
            if (started.body)
                window.location.href = "/call";
        });

        stompClient.subscribe('/broker/' + userID + '/invited', function (invitationInfo) {
            let info = JSON.parse(invitationInfo.body)
            if (info) {
                addInvitation(info.username, info.invitationID);
                subscribeToInvitationEnd(info.invitationID, info.username);
            }
        });
    });
}

function subscribeToInvitationEnd(invitationID, username) { // TODO doestnt work when loading page
    stompClient.subscribe('/broker/' + invitationID + '/endedInvitation', function (ended) {
        if (ended.body)
            removeInvitation(username);
    });
}

function connectSocket(connect_function) { // TODO reference this on main
    let socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connect_function);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function takeInvitation(invitationID) {
    stompClient.send('/ws/' + invitationID + '/joinCall', {});
}

function addCallMember(username) {
    addName('member_container', 'member_name', username);
    removeName('invited_container', 'invited_name', username);
}

function removeCallMember(username) {
    addName('invited_container', 'invited_name', username);
    removeName('member_container', 'member_name', username);
}

function removeName(container_id, name_id, username) {
    let invited_par = document.getElementsByClassName(name_id);

    for (let i = 0; i < invited_par.length; i++) {
        if (invited_par[i].innerHTML === username) {
            let invitedContainer = document.getElementById(container_id);
            invitedContainer.removeChild(invited_par[i].parentNode.parentNode);
            break;
        }
    }
}

function addName(container_id, name_id, username) {
    let names_par = document.getElementsByClassName(name_id);

    for (let i = 0; i < names_par.length; i++) {
        if (names_par[i].innerHTML === username) {
            return;
        }
    }

    let member_container = document.getElementById(container_id);

    let name_outer_container = document.createElement("div");
    name_outer_container.classList.add('flex-row', 'd-flex', 'justify-content-between', 'secondary-bg', 'rounded', 'mt-2', 'p-1', 'pl-3', 'pr-2');
    member_container.appendChild(name_outer_container);

    let name_inner_container = document.createElement("div");
    name_inner_container.classList.add('flex-column', 'd-flex', 'justify-content-center');
    name_outer_container.appendChild(name_inner_container);

    let name_paragraph = document.createElement("p");
    name_paragraph.classList.add('m-1', name_id);
    name_paragraph.innerHTML = username;
    name_inner_container.appendChild(name_paragraph);
}


function addInvitation(username, invitationID) {
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
                takeInvitation(invitationID);
            })

            names_par[i].parentNode.parentNode.appendChild(takeCallButton);
            break;
        }
    }
}

function removeInvitation(username) {
    removeName('invitation_container', 'invitation_name', username);
}
