package net.kaleidos.directmessages

/**
 * Direct message sent between two users
 * Uses ids of the user instead of objects, in order to independice the plugin from the project user system
 * @author Pablo Alba <pablo.alba@kaleidos.net>
 */
class Message {
    /**
     * Id of the user that generates the message
     */
    Long fromId

    /**
     * Id of the user that receives the message
     */
    Long toId

    /**
     * Message text
     */
    String text

    /**
     * Is this the last message between those users?
     */
    Boolean last

    /**
     * The message has been readed
     */
    Boolean readed = false

    /**
     * Subject of the message. Optional, only for "mail" type messages
     */
    String subject

    /**
     * This message is a reply. Useful for "mail" type messages
     */
    Boolean reply = false

    /**
     * Is this the last message between those users on this subject?. Useful for "mail" type messages
     */
    Boolean lastOnSubject = false



    /**
     * Date when the message was created
     */
    Date dateCreated

    static constraints = {
        subject nullable: true, blank: true
    }

    static mapping = {
        table "directmessages_message"
        text type:"text"
        subject type:"text", index: 'directmessages_message_subject_idx'
    }
}
