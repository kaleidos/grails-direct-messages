package net.kaleidos.directmessages

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(ThreadMessageService)
@Mock([Message])
class ThreadMessageServiceTests {

    def threadMessageService = new ThreadMessageService()

    void testSendMessageWithSubject() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test', 'The subject'))
        def message = Message.findByFromId(1)
        assert  message.subject == 'The subject'
        assert  message.reply == false
    }

    void testSendMessageWithSubjectAndReply() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test', 'The subject'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test', 'The subject'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test', 'The subject'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test', 'The subject'))

        def messages = Message.findAllByFromId(1)
        assert  messages[0].subject == 'The subject'
        assert  messages[0].reply == false
        assert  messages[0].lastOnThread == false
        assert  messages[1].subject == 'The subject'
        assert  messages[1].reply == true
        assert  messages[1].lastOnThread == false
        assert  messages[2].subject == 'The subject'
        assert  messages[2].reply == true
        assert  messages[2].lastOnThread == false



        def message = Message.findByFromId(2)
        assert  message.subject == 'The subject'
        assert  message.reply == true
        assert  message.lastOnThread == true

    }


    void testGetReceivedByThread() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getReceivedByThread(1)
        def messages = result.messages

        assert messages.size() == 5

        assert messages[0].subject == 'Subject 1'
        assert messages[0].text == 'Test 3'
        assert messages[0].fromId == 2
        assert messages[0].toId == 1
        assert messages[0].reply == true

        assert messages[1].subject == 'Subject 2'
        assert messages[1].text == 'Test 4'
        assert messages[1].fromId == 2
        assert messages[1].toId == 1
        assert messages[1].reply == false

        assert messages[2].subject == 'Subject 3'
        assert messages[2].text == 'Test 9'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == true

        assert messages[3].subject == 'Subject 1'
        assert messages[3].text == 'Test 11'
        assert messages[3].fromId == 3
        assert messages[3].toId == 1
        assert messages[3].reply == true

        assert messages[4].subject == 'Subject 2'
        assert messages[4].text == 'Test 15'
        assert messages[4].fromId == 3
        assert messages[4].toId == 1
        assert messages[4].reply == true

    }

    void testGetReceivedByThreadOrder() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getReceivedByThread(1, 0, -1, 'dateCreated', 'desc')
        def messages = result.messages

        assert messages.size() == 5

        assert messages[4].subject == 'Subject 1'
        assert messages[4].text == 'Test 3'
        assert messages[4].fromId == 2
        assert messages[4].toId == 1
        assert messages[4].reply == true

        assert messages[3].subject == 'Subject 2'
        assert messages[3].text == 'Test 4'
        assert messages[3].fromId == 2
        assert messages[3].toId == 1
        assert messages[3].reply == false

        assert messages[2].subject == 'Subject 3'
        assert messages[2].text == 'Test 9'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == true

        assert messages[1].subject == 'Subject 1'
        assert messages[1].text == 'Test 11'
        assert messages[1].fromId == 3
        assert messages[1].toId == 1
        assert messages[1].reply == true

        assert messages[0].subject == 'Subject 2'
        assert messages[0].text == 'Test 15'
        assert messages[0].fromId == 3
        assert messages[0].toId == 1
        assert messages[0].reply == true

    }

    void testGetReceivedByThreadSort() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'CCC'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'AAA'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'DDD'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'BBB'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'EEE'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'FFF'))


        def result = threadMessageService.getReceivedByThread(1, 0, -1, 'subject', 'asc')
        def messages = result.messages

        assert messages.size() == 5


        assert messages[0].subject == 'BBB'
        assert messages[0].text == 'Test 9'
        assert messages[0].fromId == 2
        assert messages[0].toId == 1
        assert messages[0].reply == true

        assert messages[1].subject == 'CCC'
        assert messages[1].text == 'Test 3'
        assert messages[1].fromId == 2
        assert messages[1].toId == 1
        assert messages[1].reply == true

        assert messages[2].subject == 'DDD'
        assert messages[2].text == 'Test 4'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == false

        assert messages[3].subject == 'EEE'
        assert messages[3].text == 'Test 11'
        assert messages[3].fromId == 3
        assert messages[3].toId == 1
        assert messages[3].reply == true

        assert messages[4].subject == 'FFF'
        assert messages[4].text == 'Test 15'
        assert messages[4].fromId == 3
        assert messages[4].toId == 1
        assert messages[4].reply == true

    }

    void testGetReceivedByThreadPagination() {
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 11'))

        Message.findAllByFromId(3).each{
            it.readed = true
            it.save()
        }



        def result = threadMessageService.getReceivedByThread(1, 6, 3)
        def messages = result.messages

        assert result.totalNum == 11
        assert result.unreadedNum == 6
        assert messages.size() == 3

        assert messages[0].subject == 'Subject 7'
        assert messages[1].subject == 'Subject 8'
        assert messages[2].subject == 'Subject 9'

    }

    void testGetReceivedByThreadPaginationFirstPage() {
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 11'))



        def result = threadMessageService.getReceivedByThread(1, 0, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 3

        assert messages[0].subject == 'Subject 1'
        assert messages[1].subject == 'Subject 2'
        assert messages[2].subject == 'Subject 3'

    }

    void testGetReceivedByThreadPaginationLastPage() {
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 11'))



        def result = threadMessageService.getReceivedByThread(1, 9, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 2

        assert messages[0].subject == 'Subject 10'
        assert messages[1].subject == 'Subject 11'

    }

    void testGetReceivedByThreadPaginationOutOfRange() {
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 11'))



        def result = threadMessageService.getReceivedByThread(1, 15, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 0


    }


    void testGetSentByThread() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getSentByThread(1)
        def messages = result.messages

        assert messages.size() == 5

        assert messages[0].subject == 'Subject 1'
        assert messages[0].text == 'Test 2'
        assert messages[0].fromId == 1
        assert messages[0].toId == 2
        assert messages[0].reply == true

        assert messages[1].subject == 'Subject 5'
        assert messages[1].text == 'Test 16'
        assert messages[1].fromId == 1
        assert messages[1].toId == 2
        assert messages[1].reply == false

        assert messages[2].subject == 'Subject 3'
        assert messages[2].text == 'Test 7'
        assert messages[2].fromId == 1
        assert messages[2].toId == 2
        assert messages[2].reply == false

        assert messages[3].subject == 'Subject 1'
        assert messages[3].text == 'Test 12'
        assert messages[3].fromId == 1
        assert messages[3].toId == 3
        assert messages[3].reply == true

        assert messages[4].subject == 'Subject 2'
        assert messages[4].text == 'Test 14'
        assert messages[4].fromId == 1
        assert messages[4].toId == 3
        assert messages[4].reply == true

    }

    void testGetSentByThreadOrder() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getSentByThread(1, 0, -1, 'dateCreated', 'desc')
        def messages = result.messages

        assert messages.size() == 5

        assert messages[4].subject == 'Subject 1'
        assert messages[4].text == 'Test 2'
        assert messages[4].fromId == 1
        assert messages[4].toId == 2
        assert messages[4].reply == true

        assert messages[3].subject == 'Subject 5'
        assert messages[3].text == 'Test 16'
        assert messages[3].fromId == 1
        assert messages[3].toId == 2
        assert messages[3].reply == false

        assert messages[2].subject == 'Subject 3'
        assert messages[2].text == 'Test 7'
        assert messages[2].fromId == 1
        assert messages[2].toId == 2
        assert messages[2].reply == false

        assert messages[1].subject == 'Subject 1'
        assert messages[1].text == 'Test 12'
        assert messages[1].fromId == 1
        assert messages[1].toId == 3
        assert messages[1].reply == true

        assert messages[0].subject == 'Subject 2'
        assert messages[0].text == 'Test 14'
        assert messages[0].fromId == 1
        assert messages[0].toId == 3
        assert messages[0].reply == true

    }

    void testGetSentByThreadSort() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'CCC'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'AAA'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'DDD'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'BBB'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'EEE'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'FFF'))


        def result = threadMessageService.getSentByThread(1, 0, -1, 'subject', 'asc')
        def messages = result.messages

        assert messages.size() == 5

        assert messages[0].subject == 'AAA'
        assert messages[0].text == 'Test 16'
        assert messages[0].fromId == 1
        assert messages[0].toId == 2
        assert messages[0].reply == false


        assert messages[1].subject == 'BBB'
        assert messages[1].text == 'Test 7'
        assert messages[1].fromId == 1
        assert messages[1].toId == 2
        assert messages[1].reply == false

        assert messages[2].subject == 'CCC'
        assert messages[2].text == 'Test 2'
        assert messages[2].fromId == 1
        assert messages[2].toId == 2
        assert messages[2].reply == true

        assert messages[3].subject == 'EEE'
        assert messages[3].text == 'Test 12'
        assert messages[3].fromId == 1
        assert messages[3].toId == 3
        assert messages[3].reply == true

        assert messages[4].subject == 'FFF'
        assert messages[4].text == 'Test 14'
        assert messages[4].fromId == 1
        assert messages[4].toId == 3
        assert messages[4].reply == true

    }

    void testGetSentByThreadPagination() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getSentByThread(1, 6, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 3

        assert messages[0].subject == 'Subject 7'
        assert messages[1].subject == 'Subject 8'
        assert messages[2].subject == 'Subject 9'

    }

    void testGetSentByThreadPaginationFirstPage() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getSentByThread(1, 0, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 3

        assert messages[0].subject == 'Subject 1'
        assert messages[1].subject == 'Subject 2'
        assert messages[2].subject == 'Subject 3'

    }

    void testGetSentByThreadPaginationLastPage() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getSentByThread(1, 9, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 2

        assert messages[0].subject == 'Subject 10'
        assert messages[1].subject == 'Subject 11'

    }

    void testGetSentByThreadPaginationOutOfRange() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getSentByThread(1, 15, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 0


    }



    void testGetAllByThread() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getAllByThread(1)
        def messages = result.messages

        assert messages.size() == 6

        assert messages[0].subject == 'Subject 1'
        assert messages[0].text == 'Test 3'
        assert messages[0].fromId == 2
        assert messages[0].toId == 1
        assert messages[0].reply == true

        assert messages[1].subject == 'Subject 5'
        assert messages[1].text == 'Test 16'
        assert messages[1].fromId == 1
        assert messages[1].toId == 2
        assert messages[1].reply == false

        assert messages[2].subject == 'Subject 2'
        assert messages[2].text == 'Test 4'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == false

        assert messages[3].subject == 'Subject 3'
        assert messages[3].text == 'Test 9'
        assert messages[3].fromId == 2
        assert messages[3].toId == 1
        assert messages[3].reply == true

        assert messages[4].subject == 'Subject 1'
        assert messages[4].text == 'Test 12'
        assert messages[4].fromId == 1
        assert messages[4].toId == 3
        assert messages[4].reply == true

        assert messages[5].subject == 'Subject 2'
        assert messages[5].text == 'Test 15'
        assert messages[5].fromId == 3
        assert messages[5].toId == 1
        assert messages[5].reply == true

    }

    void testgetAllByThreadOrder() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2'))


        def result = threadMessageService.getAllByThread(1, 0, -1, 'dateCreated', 'desc')
        def messages = result.messages

        assert messages.size() == 6

        assert messages[5].subject == 'Subject 1'
        assert messages[5].text == 'Test 3'
        assert messages[5].fromId == 2
        assert messages[5].toId == 1
        assert messages[5].reply == true

        assert messages[4].subject == 'Subject 5'
        assert messages[4].text == 'Test 16'
        assert messages[4].fromId == 1
        assert messages[4].toId == 2
        assert messages[4].reply == false

        assert messages[3].subject == 'Subject 2'
        assert messages[3].text == 'Test 4'
        assert messages[3].fromId == 2
        assert messages[3].toId == 1
        assert messages[3].reply == false

        assert messages[2].subject == 'Subject 3'
        assert messages[2].text == 'Test 9'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == true

        assert messages[1].subject == 'Subject 1'
        assert messages[1].text == 'Test 12'
        assert messages[1].fromId == 1
        assert messages[1].toId == 3
        assert messages[1].reply == true

        assert messages[0].subject == 'Subject 2'
        assert messages[0].text == 'Test 15'
        assert messages[0].fromId == 3
        assert messages[0].toId == 1
        assert messages[0].reply == true

    }

    void testgetAllByThreadSort() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'CCC'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'CCC'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'AAA'))

        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'DDD'))

        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'BBB'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'BBB'))

        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'EEE'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'EEE'))

        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'FFF'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'FFF'))


        def result = threadMessageService.getAllByThread(1, 0, -1, 'subject', 'asc')
        def messages = result.messages

        assert messages.size() == 6

        assert messages[0].subject == 'AAA'
        assert messages[0].text == 'Test 16'
        assert messages[0].fromId == 1
        assert messages[0].toId == 2
        assert messages[0].reply == false


        assert messages[1].subject == 'BBB'
        assert messages[1].text == 'Test 9'
        assert messages[1].fromId == 2
        assert messages[1].toId == 1
        assert messages[1].reply == true

        assert messages[2].subject == 'CCC'
        assert messages[2].text == 'Test 3'
        assert messages[2].fromId == 2
        assert messages[2].toId == 1
        assert messages[2].reply == true

        assert messages[3].subject == 'DDD'
        assert messages[3].text == 'Test 4'
        assert messages[3].fromId == 2
        assert messages[3].toId == 1
        assert messages[3].reply == false

        assert messages[4].subject == 'EEE'
        assert messages[4].text == 'Test 12'
        assert messages[4].fromId == 1
        assert messages[4].toId == 3
        assert messages[4].reply == true

        assert messages[5].subject == 'FFF'
        assert messages[5].text == 'Test 15'
        assert messages[5].fromId == 3
        assert messages[5].toId == 1
        assert messages[5].reply == true

    }

    void testgetAllByThreadPagination() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one', 'Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three', 'one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getAllByThread(1, 6, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 3

        assert messages[0].subject == 'Subject 7'
        assert messages[1].subject == 'Subject 8'
        assert messages[2].subject == 'Subject 9'

    }

    void testgetAllByThreadPaginationFirstPage() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one', 'Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three', 'one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getAllByThread(1, 0, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 3

        assert messages[0].subject == 'Subject 1'
        assert messages[1].subject == 'Subject 2'
        assert messages[2].subject == 'Subject 3'

    }

    void testgetAllByThreadPaginationLastPage() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one', 'Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three', 'one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getAllByThread(1, 9, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 2

        assert messages[0].subject == 'Subject 10'
        assert messages[1].subject == 'Subject 11'

    }

    void testgetAllByThreadPaginationOutOfRange() {
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one', 'Test 2', 'Subject 2'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 3', 'Subject 3'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 4', 'Subject 4'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 5', 'Subject 5'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 6', 'Subject 6'))
        assertNotNull (threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 7'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 8', 'Subject 8'))
        assertNotNull (threadMessageService.sendThreadMessage(2,1,'two', 'one','Test 9', 'Subject 9'))
        assertNotNull (threadMessageService.sendThreadMessage(3,1,'three', 'one','Test 10', 'Subject 10'))
        assertNotNull (threadMessageService.sendThreadMessage(1,3,'one','three','Test 11', 'Subject 11'))



        def result = threadMessageService.getAllByThread(1, 15, 3)
        def messages = result.messages
        assert result.totalNum == 11

        assert messages.size() == 0


    }


    void testFindAllMessagesOnThread() {
        def message1 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1')
        def message2 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1')
        def message3 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1')

        def message4 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5')

        def message5 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2')

        def message6 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3')
        def message7 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3')
        def message8 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3')

        def message9 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1')
        def message10 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1')
        def message11 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1')

        def message12 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2')
        def message13 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2')
        def message14 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2')


        assert threadMessageService.findAllMessagesOnThread(message1).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message2).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message3).size() == 3

        assert threadMessageService.findAllMessagesOnThread(message4).size() == 1

        assert threadMessageService.findAllMessagesOnThread(message5).size() == 1

        assert threadMessageService.findAllMessagesOnThread(message6).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message7).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message8).size() == 3

        assert threadMessageService.findAllMessagesOnThread(message9).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message10).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message11).size() == 3

        assert threadMessageService.findAllMessagesOnThread(message12).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message13).size() == 3
        assert threadMessageService.findAllMessagesOnThread(message14).size() == 3
    }

    void testDeleteMessage() {
        def message = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1')
        assert message.fromDeletedOnThread == false
        assert message.toDeletedOnThread == false
        threadMessageService.deleteMessagesOnThread(1, message)
        assert message.fromDeletedOnThread == true
        assert message.toDeletedOnThread == false
        threadMessageService.deleteMessagesOnThread(2, message)
        assert message.fromDeletedOnThread == true
        assert message.toDeletedOnThread == true
    }

    void testNotShowDeleteMessageOnGetThreads() {
        def message1 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 1', 'Subject 1')
        def message2 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 2', 'Subject 1')
        def message3 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 3', 'Subject 1')

        def message4 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 16', 'Subject 5')

        def message5 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 4', 'Subject 2')

        def message6 = threadMessageService.sendThreadMessage(1,2,'one', 'two','Test 7', 'Subject 3')
        def message7 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 8', 'Subject 3')
        def message8 = threadMessageService.sendThreadMessage(2,1,'two','one','Test 9', 'Subject 3')

        def message9 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 10', 'Subject 1')
        def message10 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 11', 'Subject 1')
        def message11 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 12', 'Subject 1')

        def message12 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 13', 'Subject 2')
        def message13 = threadMessageService.sendThreadMessage(1,3,'one','three','Test 14', 'Subject 2')
        def message14 = threadMessageService.sendThreadMessage(3,1,'three','one','Test 15', 'Subject 2')

        assert threadMessageService.getThreads(1, true, false).size() == 5
        assert threadMessageService.getThreads(2, true, false).size() == 3
        assert threadMessageService.getThreads(3, true, false).size() == 2

        assert threadMessageService.getThreads(1, false, true).size() == 5
        assert threadMessageService.getThreads(2, false, true).size() == 3
        assert threadMessageService.getThreads(3, false, true).size() == 2

        assert threadMessageService.getThreads(1, true, true).size() == 6
        assert threadMessageService.getThreads(2, true, true).size() == 4
        assert threadMessageService.getThreads(3, true, true).size() == 2

        //try to delete a thread that doesn't belongs to the user
        threadMessageService.deleteMessagesOnThread(3, message1)
        assert threadMessageService.getThreads(1, true, false).size() == 5
        assert threadMessageService.getThreads(2, true, false).size() == 3
        assert threadMessageService.getThreads(3, true, false).size() == 2

        assert threadMessageService.getThreads(1, false, true).size() == 5
        assert threadMessageService.getThreads(2, false, true).size() == 3
        assert threadMessageService.getThreads(3, false, true).size() == 2

        assert threadMessageService.getThreads(1, true, true).size() == 6
        assert threadMessageService.getThreads(2, true, true).size() == 4
        assert threadMessageService.getThreads(3, true, true).size() == 2


        //Delete a thread for an user, the other user keep seeing the thread
        threadMessageService.deleteMessagesOnThread(1, message1)
        assert threadMessageService.getThreads(1, true, false).size() == 4
        assert threadMessageService.getThreads(2, true, false).size() == 3

        assert threadMessageService.getThreads(1, false, true).size() == 4
        assert threadMessageService.getThreads(2, false, true).size() == 3

        assert threadMessageService.getThreads(1, true, true).size() == 5
        assert threadMessageService.getThreads(2, true, true).size() == 4

        //Delete same thread for the other user
        threadMessageService.deleteMessagesOnThread(2, message1)
        assert threadMessageService.getThreads(1, true, false).size() == 4
        assert threadMessageService.getThreads(2, true, false).size() == 2

        assert threadMessageService.getThreads(1, false, true).size() == 4
        assert threadMessageService.getThreads(2, false, true).size() == 2

        assert threadMessageService.getThreads(1, true, true).size() == 5
        assert threadMessageService.getThreads(2, true, true).size() == 3


    }


}
