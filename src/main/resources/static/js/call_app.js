let stompClient = null;

let invitation_username_map = new Map();

function addToInvitationMap(invitationID, initiatorName) {
    invitation_username_map.set(invitationID, initiatorName);
}

function connectCallSocket(invitationID, userID, textChannelID) {
    connectSocket(function () {
        stompClient.subscribe('/broker/' + userID + '/leftCall', function (left) {
            if (left.body)
                window.location.href = "/main";
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
        subscribeToTextChannel(userID, textChannelID);
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

        stompClient.subscribe('/broker/' + userID + '/invited', function (inv) {
            let invitation = JSON.parse(inv.body)
            if (invitation) {
                addInvitation(invitation.initiator, invitation.id);
                subscribeToInvitationEnd(invitation.id, invitation.initiator);
            }
        });
        invitation_username_map.forEach((value, key) => subscribeToInvitationEnd(key, value));
    });
}

function connectRoomSocket(userID, textChannelID) {
    connectSocket(function () {
        subscribeToTextChannel(userID, textChannelID);
    });
}

function subscribeToInvitationEnd(invitationID, username) {
    stompClient.subscribe('/broker/' + invitationID + '/endedInvitation', function (ended) {
        if (ended.body)
            removeInvitation(username);
    });
}

function subscribeToTextChannel(userID, textChannelID) {
    stompClient.subscribe('/broker/' + textChannelID + '/receivedMessage', function (msg) {
        let message = JSON.parse(msg.body);
        if (message) {
            addMessage(message.senderName, message.content, message.sentAt, message.senderID === userID);
            scrollSmoothToBottom('msg-container');
        }
    });
}

function connectSocket(connect_function) {
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

function sendMessage(textChannelID, msgInput, userID) {
    let msg = msgInput.value.trim();
    if (msg !== "") {
        console.log(msg);
        stompClient.send('/ws/' + textChannelID + '/' + userID + '/sendMessage', {}, msg);
        msgInput.value = "";
        msgInput.style.overflow = 'hidden';
        msgInput.style.height = 0;
        msgInput.style.height = msgInput.scrollHeight + 'px';
    }
}

function takeInvitation(invitationID) {
    stompClient.send('/ws/' + invitationID + '/joinCall', {});
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

function addMessage(senderName, content, date, ownMessage) {
    let msgContainer = document.getElementById('msg-container');

    let msgOuterContainer = document.createElement("div");
    if (ownMessage) {
        msgOuterContainer.classList.add('justify-content-end', 'text-right');
    }
    msgOuterContainer.classList.add('flex-row', 'd-flex', 'pb-2');

    msgContainer.appendChild(msgOuterContainer);
    let msgInnerContainer = document.createElement("div");
    msgInnerContainer.classList.add('flex-column', 'd-flex', 'rounded', 'main-bg', 'p-1', 'pl-3', 'pr-3');
    msgInnerContainer.style.maxWidth = '75%';

    msgOuterContainer.appendChild(msgInnerContainer);
    let name_par = document.createElement("p");
    name_par.classList.add('text-success', 'border-bottom', 'secondary-border', 'mb-1');
    name_par.innerHTML = senderName;

    msgInnerContainer.appendChild(name_par);
    let content_par = document.createElement("p");
    content_par.classList.add('mb-1');
    content_par.innerHTML = content;
    content_par.style.overflowWrap = 'break-word';
    msgInnerContainer.appendChild(content_par);

    let time_par = document.createElement("small");
    time_par.classList.add('pb-1', 'border-top', 'secondary-border', 'text-secondary');
    time_par.innerHTML = date;
    msgInnerContainer.appendChild(time_par);
}

// src: https://stackoverflow.com/questions/270612/scroll-to-bottom-of-div?rq=1
function scrollSmoothToBottom(id) {
    var div = document.getElementById(id);
    $('#' + id).animate({
        scrollTop: div.scrollHeight - div.clientHeight
    }, 50);
}

// src: https://stackoverflow.com/questions/270612/scroll-to-bottom-of-div?rq=1
function scrollToBottom(id) {
    var div = document.getElementById(id);
    div.scrollTop = div.scrollHeight - div.clientHeight;
}