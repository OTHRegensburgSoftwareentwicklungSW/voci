/*
 * This file is my approach to voice streaming.
 * I tried to use WebRTC for audio streams following mainly this tutorial: https://www.html5rocks.com/en/tutorials/webrtc/infrastructure/
 * RTC communication is peer-to-peer between the clients and the server only gives the clients signals with whom to communicate.
 * These signal messages can be exchanged via sockets.
 *
 * This file is never used or referenced by any other files, because voice-streaming does not work, yet.
 * I left it out, because it is no requirement, but a lot of work. The voice-streaming part of my project is only "simulated".
 */

/*

function subscribeToSignalMessages(voiceChannelID, userID, call) {
    'use strict';

    var isChannelReady = false;
    var isInitiator = false;
    var isStarted = false;
    var localStream;
    var pc;
    var remoteStream;
    var turnReady;
    let room = voiceChannelID;

    var pcConfig = {
        'iceServers': [{
            'urls': 'stun:stun.l.google.com:19302' // replace with OTH server
        }]
    };

// Set up audio and video regardless of what devices are present.
    var sdpConstraints = {
        offerToReceiveAudio: true
    };

/////////////////////////////////////////////

// Could prompt for room name:
// room = prompt('Enter room name:');

    if (room !== '') {
        stompClient.send('/ws/signal/' + voiceChannelID + '/createOrJoin', {}, JSON.stringify(room));
        console.log('Attempted to create or  join room', room);
    }

    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/created', function (room) {
        if (room.body) {
            room = JSON.parse(room.body);
            console.log('Created room ' + room);
            isInitiator = true;
        }
    });

    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/full', function (room) {
        if (room.body) {
            room = JSON.parse(room.body);
            console.log('Room ' + room + ' is full');
        }
    });

    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/join', function (room) {
        if (room.body) {
            room = JSON.parse(room.body);
            console.log('Another peer made a request to join room ' + room);
            console.log('This peer is the initiator of room ' + room + '!');
            isChannelReady = true;
        }
    });

    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/joined', function (room) {
        if (room.body) {
            room = JSON.parse(room.body);
            console.log('joined: ' + room);
            isChannelReady = true;
        }
    });

    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/log', function (array) {
        if (array.body) {
            array = JSON.parse(array.body)
            console.log.apply(console, array);
        }
    });

////////////////////////////////////////////////

    function sendMessage(message) {
        console.log('Client sending message: ', message);
        stompClient.send('/ws/signal/' + voiceChannelID + '/message', {}, JSON.stringify(message));
    }

// This client receives a message
    stompClient.subscribe('/broker/signal/' + voiceChannelID + '/message', function (message) {
        if (message.body) {
            message = JSON.parse(message.body)
            console.log('Client received message:', message);
            if (message === 'got user media') {
                maybeStart();
            } else if (message.type === 'offer') {
                if (!isInitiator && !isStarted) {
                    maybeStart();
                }
                pc.setRemoteDescription(new RTCSessionDescription(message));
                doAnswer();
            } else if (message.type === 'answer' && isStarted) {
                pc.setRemoteDescription(new RTCSessionDescription(message));
            } else if (message.type === 'candidate' && isStarted) {
                var candidate = new RTCIceCandidate({
                    sdpMLineIndex: message.label,
                    candidate: message.candidate
                });
                pc.addIceCandidate(candidate);
            } else if (message === 'bye' && isStarted) {
                handleRemoteHangup();
            }
        }
    });

////////////////////////////////////////////////////

    var localAudio = document.querySelector('#localAudio');
    var remoteAudio = document.querySelector('#remoteAudio');

    navigator.mediaDevices.getUserMedia({
        audio: true,
        video: false
    })
        .then(gotStream)
        .catch(function (e) {
            alert('getUserMedia() error: ' + e.name);
        });

    function gotStream(stream) {
        console.log('Adding local stream.');
        localStream = stream;
        localAudio.srcObject = stream;
        sendMessage('got user media');
        if (isInitiator) {
            maybeStart();
        }
    }

    var constraints = {
        audio: true
    };

    console.log('Getting user media with constraints', constraints);

    // if (location.hostname !== 'localhost') {
    //     requestTurn(
    //         'https://computeengineondemand.appspot.com/turn?username=41784574&key=4080218913'
    //     );
    // }

    function maybeStart() {
        console.log('>>>>>>> maybeStart() ', isStarted, localStream, isChannelReady);
        if (!isStarted && typeof localStream !== 'undefined' && isChannelReady) {
            console.log('>>>>>> creating peer connection');
            createPeerConnection();
            pc.addStream(localStream);
            isStarted = true;
            console.log('isInitiator', isInitiator);
            if (isInitiator) {
                doCall();
            }
        }
    }

    window.onbeforeunload = function () {
        sendMessage('bye');
    };

/////////////////////////////////////////////////////////

    function createPeerConnection() {
        try {
            pc = new RTCPeerConnection(null);
            pc.onicecandidate = handleIceCandidate;
            pc.onaddstream = handleRemoteStreamAdded;
            pc.onremovestream = handleRemoteStreamRemoved;
            console.log('Created RTCPeerConnnection');
        } catch (e) {
            console.log('Failed to create PeerConnection, exception: ' + e.message);
            alert('Cannot create RTCPeerConnection object.');
            return;
        }
    }

    function handleIceCandidate(event) {
        console.log('icecandidate event: ', event);
        if (event.candidate) {
            sendMessage({
                type: 'candidate',
                label: event.candidate.sdpMLineIndex,
                id: event.candidate.sdpMid,
                candidate: event.candidate.candidate
            });
        } else {
            console.log('End of candidates.');
        }
    }

    function handleCreateOfferError(event) {
        console.log('createOffer() error: ', event);
    }

    function doCall() {
        console.log('Sending offer to peer');
        pc.createOffer(setLocalAndSendMessage, handleCreateOfferError);
    }

    function doAnswer() {
        console.log('Sending answer to peer.');
        pc.createAnswer().then(
            setLocalAndSendMessage,
            onCreateSessionDescriptionError
        );
    }

    function setLocalAndSendMessage(sessionDescription) {
        pc.setLocalDescription(sessionDescription);
        console.log('setLocalAndSendMessage sending message', sessionDescription);
        sendMessage(sessionDescription);
    }

    function onCreateSessionDescriptionError(error) {
        trace('Failed to create session description: ' + error.toString());
    }

    function requestTurn(turnURL) {
        var turnExists = false;
        for (var i in pcConfig.iceServers) {
            if (pcConfig.iceServers[i].urls.substr(0, 5) === 'turn:') {
                turnExists = true;
                turnReady = true;
                break;
            }
        }
        if (!turnExists) {
            console.log('Getting TURN server from ', turnURL);
            // No TURN server. Get one from computeengineondemand.appspot.com:
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var turnServer = JSON.parse(xhr.responseText);
                    console.log('Got TURN server: ', turnServer);
                    pcConfig.iceServers.push({
                        'urls': 'turn:' + turnServer.username + '@' + turnServer.turn,
                        'credential': turnServer.password
                    });
                    turnReady = true;
                }
            };
            xhr.open('GET', turnURL, true);
            xhr.send();
        }
    }

    function handleRemoteStreamAdded(event) {
        console.log('Remote stream added.');
        remoteStream = event.stream;
        remoteAudio.srcObject = remoteStream;
    }

    function handleRemoteStreamRemoved(event) {
        console.log('Remote stream removed. Event: ', event);
    }

    function hangup() {
        console.log('Hanging up.');
        stop();
        sendMessage('bye');
    }

    function handleRemoteHangup() {
        console.log('Session terminated.');
        stop();
        isInitiator = false;
    }

    function stop() {
        isStarted = false;
        pc.close();
        pc = null;
    }

}

*/
