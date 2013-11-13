package net.kaleidos.directmessages

/**
 * Service for the direct messages
 * @author Pablo Alba <pablo.alba@kaleidos.net>
 */
class DirectMessageService {

    /**
     * Send a message from an user to another
     * @param fromId Id of the user that send the message
     * @param toId Id of the user that receives the message
     * @param text The text of the message
     * @return a Message
     */
    Message sendMessage(long fromId, long toId, String text, String subject=null) {
        def messages = getMessages(fromId, toId, true)
        def messagesOnSubject = null

        def reply = false
        def s = subject?.trim()

        Message m = new Message(fromId:fromId, toId: toId, text: text.trim(), last:true, lastOnSubject:true, subject:s)

        if (s) {
            //Find messages between those users with same subject
            messagesOnSubject = findAllMessagesOnSubject(m)
            if (messagesOnSubject) {
                m.reply = true
                m.numberOfMessagesOnSubject = messagesOnSubject.size() +1
            }
        }


        if (m.save()){
            //If save is ok, the old last message isn't last anymore
            if (messages){
                messages[0].last = false
                messages[0].save()
            }


            messagesOnSubject.each{
                 it.numberOfMessagesOnSubject = messagesOnSubject.size() +1
                 it.lastOnSubject = false
                 it.save()
             }

            return m
        }
        return null
    }

    /**
     * Get a list of the conversations of the user, that is, 'last' messages. It is a list that mix sended and received messages, order by date
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param filterIds List of ids of users of with do not wat to get the messages
     * @return a list of Messages
     */
    List<Message> getLastMessages(long id, int offset = 0, int itemsByPage = -1, List<Long> filterIds = []){
        return Message.createCriteria().list{
            or{
                eq('fromId', id)
                eq('toId', id)
            }

            if (filterIds) {
                not{
                    or{
                        'in' 'fromId', filterIds
                        'in' 'toId', filterIds
                    }
                }
            }

            eq('last', true)
            order('dateCreated', 'desc')
            if (itemsByPage > -1) {
                maxResults(itemsByPage)
            }
            firstResult(offset)
        }
    }

    /**
     * Get a list of the messages between two users. It is a list that mix sended and received messages, order by date
     * Admit pagination
     * @param id1 Id of one of the users
     * @param id2 Id of the other of the users
     * @param onlyLast Return only the last message
     * @param offset For pagination, first
     * @param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
     * @return a list of Messages
     */
    List<Message> getMessages(long id1, long id2, boolean onlyLast=false, int offset = 0, int itemsByPage = -1){
        return Message.createCriteria().list{
            or{
                and {
                    eq('fromId', id1)
                    eq('toId', id2)
                }
                and {
                    eq('fromId', id2)
                    eq('toId', id1)
                }
            }

            if (onlyLast){
                eq('last', true)
            }

            if (itemsByPage > -1) {
                maxResults(itemsByPage)
            }
            firstResult(offset)

            order('dateCreated', 'asc')
        }
    }

    /**
     * Get a list of the messages between the same users that a given message. It is a list that mix sended and received messages, order by date
     * @param messageId Id of the message
     * @return a list of Messages
     */
    List<Message> getMessages(long messageId){
        Message m = Message.get(messageId)

        if (m){
            return getMessages(m.fromId, m.toId)
        }
    }

    /**
     * Get a list of the messages from an user to another, order by date
     * Admit pagination
     * @param fromId Id of the user that send the message
     * @param toId Id of the user that receives the message
     * @param offset For pagination, first
     * @param itemsByPage For pagination, maximun number of messages to return. If it is -1, returns all messages.
     * @return a list of Messages
     */
    List<Message> getMessagesBetweenUsers(long fromId, long toId, int offset = 0, int itemsByPage = -1){
        return Message.createCriteria().list{
            eq('fromId', fromId)
            eq('toId', toId)

            if (itemsByPage > -1) {
                maxResults(itemsByPage)
            }
            firstResult(offset)

            order('dateCreated', 'desc')
        }
    }

    /**
     * Count the number of messages that the user has received, and hasn't read
     * @param id Id of the user
     * @param onlyLast Count only the last message
     * @param filterIds List of ids of users of with do not want to get the messages
     * @return long
     */
    long countUnreadedMessages(long id, boolean onlyLast=false, List<Long> filterIds = []){
        return Message.createCriteria().count{
            eq('toId', id)
            eq('readed', false)
            if (onlyLast){
                eq('last', true)
            }
            if (filterIds) {
                not{
                    or{
                        'in' 'fromId', filterIds
                        'in' 'toId', filterIds
                    }
                }
            }
        }
    }

    /**
     * Count the number of unread messages between two users, mixing sended and received messages
     * @param fromId Id of the user that send the message
     * @param toId Id of the user that receives the message
     * @return long
     */
    long countUnreadMessagesBetweenUsers(long fromId, long toId){
        return Message.createCriteria().count{
            eq('readed', false)
            eq('fromId', fromId)
            eq('toId', toId)
        }
    }

    /**
     * Mark a list of messages as read
     * @param messages List of messages
     */
    void markAsRead(List<Message> messages){
        messages.each{
            if (!it.readed) {
                it.readed = true
                it.save()
            }
        }
    }


    /**
     * Get a list of the messages received by the user, grouping by subject, that is, 'last' messages of every subject.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'user
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
     */
    Map getReceivedMessagesBySubject(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){
        def result = [:]
        def resultMessages = getMessagesBySubject (id, true)
        result.totalNum = resultMessages.size()
        result.unreadedNum = resultMessages.count{ it.readed == false }
        result.messages = messagesSortAndPagination (resultMessages, offset, itemsByPage, sort, order)
        return result
    }

    /**
     * Get a list of the messages sent by the user, grouping by subject, that is, 'last' messages of every subject.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'user
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination)
     */
    Map getSentMessagesBySubject(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){
        def result = [:]
        def resultMessages = getMessagesBySubject (id, false)
        result.totalNum = resultMessages.size()
        result.messages = messagesSortAndPagination (resultMessages, offset, itemsByPage, sort, order)
        return result
    }


    /**
     * This method is intended to be used only privately
     * Get a list of the messages sent or received by the user, grouping by subject, that is, 'last' messages of every subject.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'user
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a list of Messages
     */
    List<Message> getMessagesBySubject(long id, boolean received){
        def resultMessages = []

        def messages = received?Message.findAllByToId(id):Message.findAllByFromId(id)

        while (messages) {
            def message = messages[0]
            def subjectGroup
            if (received) {
                subjectGroup = messages.findAll{it.fromId == message.fromId && it.subject == message.subject}.sort{it.dateCreated}
            } else {
                subjectGroup = messages.findAll{it.toId == message.toId && it.subject == message.subject}.sort{it.dateCreated}
            }
            resultMessages << subjectGroup.last()
            messages = messages - subjectGroup
        }

        return resultMessages

    }


    /**
     * This method is intended to be used only privately
     * Sorts and paginates in memory a list of messages
     * @param messages the list of messages to sort and paginate
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'user
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a list of Messages
     **/
    List<Message> messagesSortAndPagination(List<Message> messages, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){

        //Sort
        if (sort == 'fromId') {
            messages = messages.sort{it.fromId}
        } else if (sort == 'toId') {
            messages = messages.sort{it.toId}
        } else if (sort == 'subject') {
            messages = messages.sort{it.subject}
        } else if (sort == 'dateCreated') {
            messages = messages.sort{it.dateCreated}
        } else {
            //If the user hadn't ask for a sort, make a special sort with the unreaded messages first
            messages = messages.sort{a,b->
                if (a.readed) {
                    if (b.readed) {
                        return a.dateCreated < b.dateCreated?-1:1
                    } else {
                        return -1
                    }
                } else {
                    if (b.readed) {
                        return 1
                    } else {
                        return a.dateCreated < b.dateCreated?-1:1
                    }
                }
            }
        }


        if (order == 'desc') {
            messages = messages.reverse()
        }

        //Pagination
        if (itemsByPage != -1) {
            if (offset < messages.size()) {
                def max = Math.min(offset+itemsByPage, messages.size()) - 1

                messages = messages[offset..max]
            } else {
                messages = []
            }
        }

        return messages

    }


    /**
     * Find all the messages between those same users with the same subject
     * @param message the model message
     * @return a list of Messages
     */
     List<Message> findAllMessagesOnSubject(Message message){
         return Message.createCriteria().list{
             or{
                 and {
                     eq 'fromId', message.fromId
                     eq 'toId', message.toId
                 }
                 and {
                     eq 'fromId', message.toId
                     eq 'toId', message.fromId
                 }
             }
             eq 'subject', message.subject
         }

     }
}
