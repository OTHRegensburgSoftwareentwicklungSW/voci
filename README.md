# VOCI

This project is the result of the software-development course at the OTH-Regensburg. 

## Project "Voci - Chatsystem"

Voci is a chat-system for sending messages in calls or chatrooms.

### Notes on project

Updates on the "Meilenstein1"-document are in a separate pdf: "Meilenstein2_update.pdf"  
Unfortunately voice-streaming is not supported, as first planned. 

#### Deployment

As I work with environment variables for the database I wrote a bash script, which starts my program. Execute these commands on im-codd in my home-direcory:

````
    ps -aux | grep voci-0.0.1.jar
    kill <pid> # kill old project
    ./start.sh    
````

``start.sh`` is also included in my uploaded zip-folder.

#### REST-API

I imported swagger-ui in my project to visualize my REST-API. It is available at: http://im-codd:8945/swagger-ui.

To view swagger-ui, you need to be logged in with an account.


### "Tutorial"

To communicate with others create a room with text-channels or start a new call, to which you can invite your contacts or guests.

Inviting contacts should be intuitive. To invite guests, copy the access-token, after you started a call and send it to the guests per email or other messengers. 

The guest can join at http://im-codd.oth-regensburg.de:8945/invitation, where he can enter the access-token.

Now you can chat. 

If you want to send files from your [Dropsi](http://im-codd.oth-regensburg.de:8922)-account. Log in to your Dropsi-account and copy the secret-key to your clipboard.
Under [INFO](http://im-codd:8945/info), you can enter this token, to connect Dropsi and VOCI.

Now you can see an upload-button in your calls or rooms next to the send button, with which you can upload your Dropsi-files to the text-channel.

Every user in the channel can download the file. 