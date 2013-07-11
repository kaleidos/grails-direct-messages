grails-direct-messages
======================

Grails plugin for direct messages between users

### Overview

This plugin offer the backend funcionality for send and receive direct messages between users. 
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
    
    
