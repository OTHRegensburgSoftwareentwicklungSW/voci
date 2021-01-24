let stompClient = null; // TODO maybe recycle old

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

function removeName(container_id, name_id, username) {
    let invited_par = document.getElementsByClassName(name_id);

    for (let i = 0; i < invited_par.length; i++) {
        if (invited_par[i].innerText === username) {
            let invitedContainer = document.getElementById(container_id);
            invitedContainer.removeChild(invited_par[i].parentNode.parentNode);
            break;
        }
    }
}

function addName(container_id, name_id, username) {
    let names_par = document.getElementsByClassName(name_id);

    for (let i = 0; i < names_par.length; i++) {
        if (names_par[i].innerText === username) {
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
    name_paragraph.innerText = username;
    name_inner_container.appendChild(name_paragraph);
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