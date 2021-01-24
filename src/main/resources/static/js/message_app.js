function subscribeToTextChannel(userID, textChannelID) {
    stompClient.subscribe('/broker/' + textChannelID + '/receivedMessage', function (msg) {
        let message = JSON.parse(msg.body);
        if (message) {
            if(message.type === 'textMessage')
                addTextMessage(message, userID);
            else {
                addDropsiFileMessage(message, userID, textChannelID)
            }

            scrollSmoothToBottom('msg-container');
        }
    });
}

function sendMessage(textChannelID, msgInput, userID) {
    let msg = msgInput.value.trim();
    if (msg !== "") {
        stompClient.send('/ws/' + textChannelID + '/' + userID + '/sendMessage', {}, msg);
        msgInput.value = "";
        msgInput.style.overflow = 'hidden';
        msgInput.style.height = 0;
        msgInput.style.height = msgInput.scrollHeight + 'px';
    }
}

function sendDropsiFile(textChannelID, file) {
    stompClient.send('/ws/' + textChannelID + '/sendDropsiFile', {}, JSON.stringify(file));
}

function addMessage(senderName, content_element, date, ownMessage) {
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
    msgInnerContainer.appendChild(content_element);

    let time_par = document.createElement("small");
    time_par.classList.add('pb-1', 'border-top', 'secondary-border', 'text-secondary');
    time_par.innerHTML = date;
    msgInnerContainer.appendChild(time_par);
}

function addTextMessage(message, userID) {
    let content_par = document.createElement("p");
    content_par.classList.add('mb-1');
    content_par.innerHTML = message.content;
    content_par.style.overflowWrap = 'break-word';

    addMessage(message.sender.userName, content_par, message.formatDate, message.sender.id === userID);
}

function addDropsiFileMessage(message, userID, textChannelID) {
    let dropsiFileName = message.dropsiFileName
    dropsiFileName = dropsiFileName.substr(dropsiFileName.lastIndexOf('/') + 1);
    let content_ref = document.createElement("a");
    content_ref.classList.add('mb-1');
    content_ref.innerHTML = dropsiFileName.substr(dropsiFileName.lastIndexOf('/') + 1);
    content_ref.style.overflowWrap = 'break-word';
    content_ref.href = "/download/" + textChannelID + '/'+ message.sender.id + '/' + message.dropsiFileId + '/' + dropsiFileName;

    addMessage(message.sender.userName, content_ref, message.formatDate, message.sender.id === userID);
}