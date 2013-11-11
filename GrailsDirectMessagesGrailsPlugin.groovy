class GrailsDirectMessagesGrailsPlugin {
    def version = "1.0"
    def grailsVersion = "2.0 > *"
    def title = "Grails Direct Messages Plugin"
    def author = "Pablo Alba"
    def authorEmail = "pablo.alba@gmail.com"
    def description = '''\
Grails plugin for direct messages between users. Allows two messages styles.
One, similar to twitter or facebook direct messages, that is, all the messages between two users form a unique conversation.
Other, similar to gmail, all the messages between two users has a subject, and the messages between two users with the same subject
form a unique conversation.
'''

    def documentation = "https://github.com/kaleidos/grails-direct-messages"

    def license = "APACHE"
    def organization = [ name: "Kaleidos", url: "http://kaleidos.net/" ]
    def scm = [ url: "https://github.com/kaleidos/grails-direct-messages" ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/kaleidos/grails-direct-messages/issues']
}
