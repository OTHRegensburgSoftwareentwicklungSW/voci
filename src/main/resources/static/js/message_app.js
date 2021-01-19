function subscribeToTextChannel(userID, textChannelID) {
    stompClient.subscribe('/broker/' + textChannelID + '/receivedMessage', function (msg) {
        let message = JSON.parse(msg.body);
        if (message) {
            addMessage(message.sender.userName, message.content, message.sentAt, message.sender.id === userID);
            scrollSmoothToBottom('msg-container');
        }
    });
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

function addMessage(senderName, content, date, ownMessage) {
    let msgContainer = document.getElementById('msg-container');

    let msgOuterContainer = document.createElement("div");
    if (ownMessage) {
        msgOuterContainer.classList.add('justify-content-end', 'text-right');
    }
    msgOuterContainer.classList.add('flex-row', 'd-flex', 'pb-1', 'pt-1');

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