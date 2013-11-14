grails-direct-messages
======================

Grails plugin for internal messages between users

### Overview

This plugin offer the backend functionality for sending and receiving direct messages between users.

It can work on two ways:

Direct Messages
---------------

In this mode, it is similar to twitter or facebook direct messages, that is, all the messages between two users form a unique
conversation.

Threaded messages
-----------------

In this mode, it is similar to gmail, that is, all the messages between two users with the same subject form a thread,
and the user can work with those threads.

### Use ids, not objects

In order to be independent of the classes that you use to model your users, the messages use only the id of the users,
not the actual objects.

### Services

All the functionality of the plugin should be accessed by one of the services:

* DirectMessageService

* ThreadMessageService



### Usage

#### Direct Messages

    import net.kaleidos.directmessages.DirectMessageService

    def directMessageService // instanciate the service

    //get conversations for an user (i.e. the last message with every other user)
    def messages = directMessageService.getLastMessages(user1.id)

    //get all messages between two users
    def messagesWithUser = directMessageService.getMessages(user1.id, user2.id)

    //send a message to an user
    directMessageService.sendMessage(user1.id, user2.id, "Message text here")

#### Threaded Messages

    import net.kaleidos.directmessages.ThreadMessageService

    def threadMessageService // instanciate the service

    //get received threads for an user (i.e. the last received message of every thread)
    def messages = threadMessageService.getReceivedByThread(user1.id)

    //get sent threads for an user (i.e. the last sent message of every thread)
    def messages = threadMessageService.getSentByThread(user1.id)

    //get all threads for an user (i.e. the last message of every thread)
    def messages = threadMessageService.getAllByThread(user1.id)

    //get all messages on a thread. The message is any of the messages of the thread
    def messages = threadMessageService.findAllMessagesOnThread(message)

    //delete all messages on a thread. The message is any of the messages of the thread
    def messages = threadMessageService.deleteMessagesOnThread(message)

    //send a message to an user
    threadMessageService.sendThreadMessage(user1.id, user2.id, user1.name, user2.name, "Message text here", "Subject here")



### Example

There is an example project for the use of the plugin here:

#### Direct Messages

https://github.com/kaleidos/grails-direct-messages-sample

#### Threaded Messages

https://github.com/kaleidos/grails-direct-messages-mail-sample


### Available service methods

#### Direct Messages

    Send a message from an user to another
    @param fromId Id of the user that send the message
    @param toId Id of the user that receives the message
    @param text The text of the message
    @return a Message
    **Message sendMessage(long fromId, long toId, String text)**



    Get a list of the conversations of the user, that is, 'last' messages. It is a list that mix sended and received messages, order by date
    @param id Id of the user
    @param offset Number of messages to skip (for pagination)
    @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
    @param filterIds List of ids of users of with do not wat to get the messages
    @return a list of Messages
    **List<Message> getLastMessages(long id, int offset = 0, int itemsByPage = -1, List<Long> filterIds = [])**



    Get a list of the messages between two users. It is a list that mix sended and received messages, order by date
    Admit pagination
    @param id1 Id of one of the users
    @param id2 Id of the other of the users
    @param onlyLast Return only the last message
    @param offset For pagination, first
    @param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
    @return a list of Messages
    **List<Message> getMessages(long id1, long id2, boolean onlyLast=false, int offset = 0, int itemsByPage = -1)**



    Get a list of the messages between the same users that a given message. It is a list that mix sended and received messages, order by date
    @param messageId Id of the message
    @return a list of Messages
    **List<Message> getMessages(long messageId)**



    Get a list of the messages from an user to another, order by date
    Admit pagination
    @param fromId Id of the user that send the message
    @param toId Id of the user that receives the message
    @param offset For pagination, first
    @param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
    @return a list of Messages
    **List<Message> getMessagesBetweenUsers(long fromId, long toId, int offset = 0, int itemsByPage = -1)**




    Count the number of messages that the user has received, and hasn't read
    @param id Id of the user
    @param onlyLast Count only the last message
    @param filterIds List of ids of users of with do not want to get the messages
    @return long
    **long countUnreadedMessages(long id, boolean onlyLast=false, List<Long> filterIds = [])**




    Count the number of unread messages between two users, mixing sended and received messages
    @param fromId Id of the user that send the message
    @param toId Id of the user that receives the message
    @return long
    **long countUnreadMessagesBetweenUsers(long fromId, long toId)**



    Mark a list of messages as read
    @param messages List of messages
    **void markAsRead(List<Message> messages)**




#### Threaded Messages


    Send a message from an user to another
    @param fromId Id of the user that send the message
    @param toId Id of the user that receives the message
    @param fromName Name the user that send the message, for sorting purposes
    @param toName Name of the user that receives the message, for sorting purposes
    @param text The text of the message
    @param subject The subject of the message
    @return a Message
    **Message sendThreadMessage(long fromId, long toId, String fromName, String toName, String text, String subject)**




    Get a list of the messages by the user, grouping by thread, that is, 'last' messages of every thread.
    Those messages could be sent or received messages
    @param id Id of the user
    @param offset Number of messages to skip (for pagination)
    @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
    @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
    @param order 'asc' or 'desc' for ascendig or descending order.
    @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
    **Map getAllByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')**




    Get a list of the messages received by the user, grouping by thread, that is, 'last' received messages of every thread.
    @param id Id of the user
    @param offset Number of messages to skip (for pagination)
    @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
    @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
    @param order 'asc' or 'desc' for ascendig or descending order.
    @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
    **Map getReceivedByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')**




    Get a list of the messages sent by the user, grouping by thread, that is, 'last' sent messages of every thread.
    @param id Id of the user
    @param offset Number of messages to skip (for pagination)
    @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
    @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
    @param order 'asc' or 'desc' for ascendig or descending order.
    @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination)
    **Map getSentByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')**



    Find all the messages on this thread (between those same users with the same subject)
    @param message the model message
    @return a list of Messages
    **List<Message> findAllMessagesOnThread(Message message)**




    Delete messages on a thread from the point of view of an user.
    On a thread between Alice and Bob, if Alice delete the thread, it is only deleted
    from Alice's point of view. From Bob's point of view the thread isn't deleted.
    @param userId the id of the user that wants to delete the thread
    @param message any of the messages on the thread
    **void deleteMessagesOnThread(long userId, Message message)**
