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

    directMessageService.getLastMessages(user1.id) //usage example

### Available service methods

You can see the javadoc of the method services at:

http://htmlpreview.github.io/?http://github.com/kaleidos/grails-direct-messages/blob/master/doc/DirectMessageService.html
