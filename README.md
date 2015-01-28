grails-direct-messages v1.0
===========================

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

### Installation

Simply add this line on the plugins section of BuildConfig.groovy

```groovy
compile ":grails-direct-messages:1.0"
```

### Usage

#### Direct Messages

```groovy
import net.kaleidos.directmessages.DirectMessageService

def directMessageService // instanciate the service

// get conversations for an user (i.e. the last message with every other user)
def messages = directMessageService.getLastMessages(user1.id)

// get all messages between two users
def messagesWithUser = directMessageService.getMessages(user1.id, user2.id)

// send a message to an user
directMessageService.sendMessage(user1.id, user2.id, "Message text here")
```

#### Threaded Messages

```groovy
import net.kaleidos.directmessages.ThreadMessageService

def threadMessageService // instanciate the service

// get received threads for an user (i.e. the last received message of every thread)
def messages = threadMessageService.getReceivedByThread(user1.id)

// get sent threads for an user (i.e. the last sent message of every thread)
def messages = threadMessageService.getSentByThread(user1.id)

// get all threads for an user (i.e. the last message of every thread)
def messages = threadMessageService.getAllByThread(user1.id)

// get all messages on a thread. The message is any of the messages of the thread
def messages = threadMessageService.findAllMessagesOnThread(message)

// delete all messages on a thread. The message is any of the messages of the thread
def messages = threadMessageService.deleteMessagesOnThread(message)

// send a message to an user
threadMessageService.sendThreadMessage(user1.id, user2.id, user1.name, user2.name, "Message text here", "Subject here")
```


### Example

There is an example project for the use of the plugin here:

#### Direct Messages

https://github.com/kaleidos/grails-direct-messages-sample

#### Threaded Messages

https://github.com/kaleidos/grails-direct-messages-mail-sample

### Backward Compatibility with version 0.1

All the new fields of the domain class are nullabe, so as long as you don't use the new threadMessageService,
you probably won't have problems.

However, it is advisable to upgrade the database in order to add the new fields. The sql sentences are:

```sql
ALTER TABLE directmessages_message ADD from_deleted_on_thread boolean NULL
ALTER TABLE directmessages_message ADD to_deleted_on_thread boolean NULL
ALTER TABLE directmessages_message ADD from_name varchar(255) NULL
ALTER TABLE directmessages_message ADD to_name varchar(255) NULL
ALTER TABLE directmessages_message ADD last_on_thread boolean NULL
ALTER TABLE directmessages_message ADD number_of_messages_on_thread integer NULL
ALTER TABLE directmessages_message ADD reply boolean NULL
ALTER TABLE directmessages_message ADD subject longvarchar NULL
```


### Available service methods

#### Direct Messages
<pre>
Send a message from an user to another
@param fromId Id of the user that send the message
@param toId Id of the user that receives the message
@param text The text of the message
@return a Message
</pre>
```groovy
Message sendMessage(long fromId, long toId, String text)
```



<pre>
Get a list of the conversations of the user, that is, 'last' messages. It is a list that mix
sended and received messages, order by date
@param id Id of the user
@param offset Number of messages to skip (for pagination)
@param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
@param filterIds List of ids of users of with do not wat to get the messages
@return a list of Messages
</pre>
```groovy
List<Message> getLastMessages(long id, int offset = 0, int itemsByPage = -1, List<Long> filterIds = [])
```


<pre>
Get a list of the messages between two users. It is a list that mix sended and received
messages, order by date
Admit pagination
@param id1 Id of one of the users
@param id2 Id of the other of the users
@param onlyLast Return only the last message
@param offset For pagination, first
@param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
@return a list of Messages
</pre>
```groovy
List<Message> getMessages(long id1, long id2, boolean onlyLast=false, int offset = 0, int itemsByPage = -1)
```


<pre>
Get a list of the messages between the same users that a given message. It is a list that mix
sended and received messages, order by date
@param messageId Id of the message
@return a list of Messages
</pre>
```groovy
List<Message> getMessages(long messageId)
```


<pre>
Get a list of the messages from an user to another, order by date
Admit pagination
@param fromId Id of the user that send the message
@param toId Id of the user that receives the message
@param offset For pagination, first
@param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
@return a list of Messages
</pre>
```groovy
List<Message> getMessagesBetweenUsers(long fromId, long toId, int offset = 0, int itemsByPage = -1)
```



<pre>
Count the number of messages that the user has received, and hasn't read
@param id Id of the user
@param onlyLast Count only the last message
@param filterIds List of ids of users of with do not want to get the messages
@return long
</pre>
```groovy
long countUnreadedMessages(long id, boolean onlyLast=false, List<Long> filterIds = [])
```



<pre>
Count the number of unread messages between two users, mixing sended and received messages
@param fromId Id of the user that send the message
@param toId Id of the user that receives the message
@return long
</pre>
```groovy
long countUnreadMessagesBetweenUsers(long fromId, long toId)
```


<pre>
Mark a list of messages as read
@param messages List of messages
</pre>
```groovy
void markAsRead(List<Message> messages)
```




#### Threaded Messages

<pre>
Send a message from an user to another
@param fromId Id of the user that send the message
@param toId Id of the user that receives the message
@param fromName Name the user that send the message, for sorting purposes
@param toName Name of the user that receives the message, for sorting purposes
@param text The text of the message
@param subject The subject of the message
@return a Message
</pre>
```groovy
Message sendThreadMessage(long fromId, long toId, String fromName, String toName, String text, String subject)
```



<pre>
Get a list of the messages by the user, grouping by thread, that is, 'last' messages of every thread.
Those messages could be sent or received messages
@param id Id of the user
@param offset Number of messages to skip (for pagination)
@param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
@param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
@param order 'asc' or 'desc' for ascendig or descending order.
@return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
</pre>
```groovy
Map getAllByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')
```



<pre>
Get a list of the messages received by the user, grouping by thread, that is, 'last' received messages of every thread.
@param id Id of the user
@param offset Number of messages to skip (for pagination)
@param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
@param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
@param order 'asc' or 'desc' for ascendig or descending order.
@return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
</pre>
```groovy
Map getReceivedByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')
```



<pre>
Get a list of the messages sent by the user, grouping by thread, that is, 'last' sent messages of every thread.
@param id Id of the user
@param offset Number of messages to skip (for pagination)
@param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
@param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
@param order 'asc' or 'desc' for ascendig or descending order.
@return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination)
</pre>
```groovy
Map getSentByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc')
```


<pre>
Find all the messages on this thread (between those same users with the same subject)
@param message the model message
@return a list of Messages
</pre>
```groovy
List<Message> findAllMessagesOnThread(Message message)
```



<pre>
Delete messages on a thread from the point of view of an user.
On a thread between Alice and Bob, if Alice delete the thread, it is only deleted
from Alice's point of view. From Bob's point of view the thread isn't deleted.
@param userId the id of the user that wants to delete the thread
@param message any of the messages on the thread
</pre>
```groovy
void deleteMessagesOnThread(long userId, Message message)
```

### Notes

#### Note for MySQL users

There is an incompability between hibernate and mysql indexes over blob fields. So the index directmessages_message_subject_idx is not created automatically. You should create it on your application Bootstrap, or manually with something like:

```sql
create index directmessages_message_subject_idx on directmessages_message(subject(255))
```
