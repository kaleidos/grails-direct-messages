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
    Message sendMessage(long fromId, long toId, String text) {
        def messages = getMessages(fromId, toId, true)
        Message m = new Message(fromId:fromId, toId: toId, text: text.trim(), last:true)
        if (m.save()){
            //If save is ok, the old last message isn't last anymore
            if (messages){
                messages[0].last = false
                messages[0].save()
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
        def list = Message.createCriteria().list{
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
        return list
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
        def list = Message.createCriteria().list{
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
        return list
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
    * @return a list of Messages
    */
    List<Message> getMessagesBetweenUsers(long fromId, long toId, int offset = 0, int itemsByPage = -1){
        def list = Message.createCriteria().list{
            eq('fromId', fromId)
            eq('toId', toId)

            if (itemsByPage > -1) {
				maxResults(itemsByPage)
			}
            firstResult(offset)

            order('dateCreated', 'desc')
        }
        return list
    }

    /**
    * Count the number of messages that the user has received, and hasn't read
    * @param id Id of the user
    * @param onlyLast Count only the last message
    * @param filterIds List of ids of users of with do not want to get the messages
    * @return long
    */
    long countUnreadedMessages(long id, boolean onlyLast=false, List<Long> filterIds = []){
        long num = Message.createCriteria().count{
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

        return num
    }
    
    
    /**
    * Count the number of unread messages between two users, mixing sended and received messages
    * @param fromId Id of the user that send the message
    * @param toId Id of the user that receives the message
    * @return long
    */
    long countUnreadMessagesBetweenUsers(long fromId, long toId){
        def num = Message.createCriteria().count{
            eq('readed', false)
            eq('fromId', fromId)
            eq('toId', toId)
        }
        return num
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
}
