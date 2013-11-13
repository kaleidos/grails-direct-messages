package net.kaleidos.directmessages

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(DirectMessageService)
@Mock([Message])
class DirectMessageServiceTests {

    def directMessageService = new DirectMessageService()

    void testSendMessage() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test'))
    }

    void testGetLastMessages() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(1,3,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))

        assertEquals(directMessageService.getLastMessages(1).size(), 2)
        assertEquals(directMessageService.getLastMessages(2).size(), 1)
        assertEquals(directMessageService.getLastMessages(3).size(), 1)
        assertEquals(directMessageService.getLastMessages(4).size(), 0)
    }

    void testGetMessages() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1')) //The messages are ordered by date desc, so this is the Third message
        assertNotNull (directMessageService.sendMessage(1,3,'Test 2')) //The messages are ordered by date desc, so this is the Second message
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3')) //The messages are ordered by date desc, so this is the First message

        def lastMessages
        def lastMessage

        //Get last messages for user 1. There must be 2 messages, because we only get the last message for every user
        //The messages are ordered by date desc
        lastMessages = directMessageService.getLastMessages(1)
        assertEquals(lastMessages.size(), 2)

        //Get last messages for user 1. First one is with user 2
        lastMessage = lastMessages[0]
        assertEquals(lastMessage.fromId, 2)
        //Get all messages of this conversation. Should be 2
        assertEquals(directMessageService.getMessages(lastMessage.id).size(), 2)

        //Get last messages for user 1. Second one is with user 3
        lastMessage = lastMessages[1]
        assertEquals(lastMessage.toId, 3)
        //Get all messages of this conversation. Should be 1
        assertEquals(directMessageService.getMessages(lastMessage.id).size(), 1)

        //Get last messages for user 2. There must be 1 messages, because we only get the last message for every user
        //The messages are ordered by date desc
        lastMessages = directMessageService.getLastMessages(2)
        assertEquals(lastMessages.size(), 1)

        //Get last messages for user 2. First one is with user 1
        lastMessage = lastMessages[0]
        assertEquals(lastMessage.toId, 1)
        //Get all messages of this conversation. Should be 2
        assertEquals(directMessageService.getMessages(lastMessage.id).size(), 2)

        //Get last messages for user 3. There must be 1 messages
        //The messages are ordered by date desc
        lastMessages = directMessageService.getLastMessages(3)
        assertEquals(lastMessages.size(), 1)

        //Get last messages for user 3. First one is with user 1
        lastMessage = lastMessages[0]
        assertEquals(lastMessage.fromId, 1)
        //Get all messages of this conversation. Should be 1
        assertEquals(directMessageService.getMessages(lastMessage.id).size(), 1)
    }


    void testGetMessagesPagination() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 5'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 6'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 7'))

        def messages

        //Get messages for users 1 and 2, without pagination
        //The messages are ordered by date asc
        messages = directMessageService.getMessages(1, 2, false)
        assertEquals(messages.size(), 7)

        //Get first page messages for users 1 and 2
        //The messages are ordered by date asc
        messages = directMessageService.getMessages(1, 2, false, 0, 5)
        assertEquals(messages.size(), 5)
        assertEquals(messages.text, ['Test 1', 'Test 2', 'Test 3', 'Test 4', 'Test 5'])

        //Get first page messages for users 1 and 2
        //The messages are ordered by date asc
        messages = directMessageService.getMessages(1, 2, false, 5, 5)
        assertEquals(messages.size(), 2)
        assertEquals(messages.text, ['Test 6', 'Test 7'])
    }

    void testGetMessagesFilter() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(1,3,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(4,1,'Test 4'))

        def lastMessages
        def lastMessage

        //Get last messages for user 1, filtering messages with user 3. There must be 2 messages
        //The messages are ordered by date desc
        lastMessages = directMessageService.getLastMessages(1, 0, -1, [3])
        assertEquals(lastMessages.size(), 2)


        //Get last messages for user 1. First one is with user 4
        lastMessage = lastMessages[0]
        assertEquals(lastMessage.fromId, 4)

        //Get last messages for user 1. Second one is with user 2
        lastMessage = lastMessages[1]
        assertEquals(lastMessage.fromId, 2)


        //Get last messages for user 1, filtering messages with user 2 and 3. There must be 1 messages
        //The messages are ordered by date desc
        lastMessages = directMessageService.getLastMessages(1, 0, -1, [2, 3])
        assertEquals(lastMessages.size(), 1)


        //Get last messages for user 1. First one is with user 4
        lastMessage = lastMessages[0]
        assertEquals(lastMessage.fromId, 4)
    }

    void testCountMessages() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))

        //Count unreaded messages for user 1
        assertEquals(1, directMessageService.countUnreadedMessages(1))
        //Count unreaded messages for user 2
        assertEquals(3, directMessageService.countUnreadedMessages(2))
    }

    void testCountMessagesOnlyLast() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))

        //Count unreaded messages for user 1
        assertEquals(0, directMessageService.countUnreadedMessages(1, true))
        //Count unreaded messages for user 2
        assertEquals(2, directMessageService.countUnreadedMessages(2, true))
    }

    void testCountMessagesFilter() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 4'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 5'))
        assertNotNull (directMessageService.sendMessage(3,1,'Test 6'))
        assertNotNull (directMessageService.sendMessage(4,1,'Test 7'))

        //Count unreaded messages for user 1, filtering messages with user 3
        assertEquals(directMessageService.countUnreadedMessages(1, false, [3]), 4)

        //Count unreaded messages for user 1, filtering messages with user 2
        assertEquals(directMessageService.countUnreadedMessages(1, false, [2]), 2)

        //Count unreaded messages for user 1, filtering messages with user 2 and 3
        assertEquals(directMessageService.countUnreadedMessages(1, false, [2, 3]), 1)
    }

    void testMarkAsReadMessages() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))

        //Mark as read the message for user 1
        def messages = Message.findAllByFromIdAndToId(2,1)
        directMessageService.markAsRead(messages)

        //Mark as read one message for user 2
        messages = Message.findAllByFromIdAndToId(3,2)
        directMessageService.markAsRead(messages)

        //Count unreaded messages for user 1
        assertEquals(0,directMessageService.countUnreadedMessages(1))
        //Count unreaded messages for user 2
        assertEquals(1,directMessageService.countUnreadedMessages(2))
    }

    void testGetLastMessagesPagination() {

        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(1,3,'Test 2'))
        assertNotNull (directMessageService.sendMessage(4,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,5,'Test 4'))
        assertNotNull (directMessageService.sendMessage(1,6,'Test 5'))
        assertNotNull (directMessageService.sendMessage(7,1,'Test 6'))

        assertEquals(6, directMessageService.getLastMessages(1).size())
        assertEquals(3, directMessageService.getLastMessages(1,0,3).size())
        assertEquals(3, directMessageService.getLastMessages(1,2,3).size())
        assertEquals(1, directMessageService.getLastMessages(1,5,3).size())
    }

    void testCountUnreadMessagesBetweenUsers() {
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 5'))

        //Set message 'Test 4' read
        def m = Message.findByText('Test 4')
        directMessageService.markAsRead([m])

        assertEquals(2, directMessageService.countUnreadMessagesBetweenUsers(1L,2L))
    }

    void testGetMessagesBetweenUsers(){
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 5'))

        //Set message 'Test 4' read
        def m = Message.findByText('Test 4')
        directMessageService.markAsRead([m])

        def messages = directMessageService.getMessagesBetweenUsers(1L,2L)
        assertEquals(3, messages.size())
    }

    void testGetMessagesBetweenUsersPaginated(){
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        assertNotNull (directMessageService.sendMessage(3,2,'Test 2'))
        assertNotNull (directMessageService.sendMessage(2,1,'Test 3'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 4'))
        assertNotNull (directMessageService.sendMessage(1,2,'Test 5'))

        //Set message 'Test 4' read
        def m = Message.findByText('Test 4')
        directMessageService.markAsRead([m])

        def messages = directMessageService.getMessagesBetweenUsers(1L,2L,0,2)
        assertEquals(2, messages.size())
    }

    void testGetMessagesNonExistant(){
        def m = directMessageService.getMessages(1000)
        assertNull(m)
    }

    void testMarkAsReadAReadedMessage(){
        assertNotNull (directMessageService.sendMessage(1,2,'Test 1'))
        def messages = Message.list()
        directMessageService.markAsRead(messages)

        //Call again markAsRead and does not fail
        directMessageService.markAsRead(messages)
    }

}
