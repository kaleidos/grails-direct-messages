grails-direct-messages
======================

Grails plugin for direct messages between users

### Overview

This plugin offer the backend functionality for sending and receiving direct messages between users.
It is similar to twitter or facebook direct messages, that is, all the messages between two users form a unique
conversation.

### Use ids, not objects

In order to be independent of the classes that you use to model your users, the messages use only the id of the users,
not the actual objects.

### Service

All the functionality of the plugin should be accessed by the service called directMessageService.



### Usage

    import net.kaleidos.directmessages.DirectMessageService

    def directMessageService // instanciate the service

    //get conversations for an user (i.e. the last message with every other user)
    def messages = directMessageService.getLastMessages(user1.id)
    
    //get all messages between two users
    def messagesWithUser = directMessageService.getMessages(user1.id, user2.id)
    
    //send a message to an user
    directMessageService.sendMessage(user1.id, user2.id, "Message text here")
    
## Example
    
There is an example project for the use of the plugin here:
https://github.com/kaleidos/grails-direct-messages-sample

### Available service methods

You can see the javadoc of the method services at:

http://htmlpreview.github.io/?http://github.com/kaleidos/grails-direct-messages/blob/master/doc/DirectMessageService.html
