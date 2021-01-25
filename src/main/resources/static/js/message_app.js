function subscribeToTextChannel(userID, textChannelID) {
    stompClient.subscribe('/broker/' + textChannelID + '/receivedMessage', function (msg) {
        let message = JSON.parse(msg.body);
        if (message) {
            if (message.type === 'textMessage')
                addTextMessage(message, userID);
            else if(message.type === 'dropsiFileMessage'){
                addDropsiFileMessage(message, userID, textChannelID)
            } else if(message.type === 'errorMessage') {
                showError(message.errorCode, message.message);
            }

            scrollSmoothToBottom('msg-container');
        }
    });
    stompClient.subscribe('/broker/' + textChannelID + '/receivedDropsiMessage', function (msg) {
        if (msg.body) {
            let message = JSON.parse(msg.body);
            if (message.type === "dropsiFileMessage") {
                let data = base64ToArrayBuffer(message.payload);
                let blob = new Blob([data], {type: "application/" + message.dropsiFileType});
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = message.dropsiFileName.substr(message.dropsiFileName.lastIndexOf('/') + 1);
                link.click();
            } else if (message.type === 'errorMessage') {
                showError(message.errorCode, message.message)
            }
        }
    });
}

function showError(errorCode, message) {
    console.error(errorCode + ': ' + message);
    document.getElementById("error-msg").innerText = message;
}

function clearError() {
    document.getElementById("error-msg").innerText = "";
}

// src: https://stackoverflow.com/questions/35038884/download-file-from-bytes-in-javascript
function base64ToArrayBuffer(base64) {
    let binaryString = window.atob(base64);
    let binaryLen = binaryString.length;
    let bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
}

function sendMessage(textChannelID, msgInput) {
    clearError();
    let msg = msgInput.value.trim();
    if (msg !== "") {
        stompClient.send('/ws/' + textChannelID + '/sendMessage', {}, msg);
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
    name_par.innerText = senderName;

    msgInnerContainer.appendChild(name_par);
    msgInnerContainer.appendChild(content_element);

    let time_par = document.createElement("small");
    time_par.classList.add('pb-1', 'border-top', 'secondary-border', 'text-secondary');
    time_par.innerText = date;
    msgInnerContainer.appendChild(time_par);
}

function addTextMessage(message, userID) {
    let content_par = document.createElement("p");
    content_par.classList.add('mb-1');
    content_par.innerText = message.content;
    content_par.style.overflowWrap = 'break-word';

    addMessage(message.sender.userName, content_par, message.formatDate, message.sender.id === userID);
}

function addDropsiFileMessage(message, userID, textChannelID) {
    let content_ref = document.createElement("a");
    content_ref.classList.add('mb-1', 'btn', 'btn-link', 'p-0');
    content_ref.innerText = message.dropsiFileName.substr(message.dropsiFileName.lastIndexOf('/') + 1);
    content_ref.style.overflowWrap = 'break-word';
    content_ref.onclick = function () {
        stompClient.send("/ws/download/" + textChannelID, {}, JSON.stringify(message));
    }
    addMessage(message.sender.userName, content_ref, message.formatDate, message.sender.id === userID);
}