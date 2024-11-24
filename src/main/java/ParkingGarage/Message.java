package ParkingGarage;
import java.io.Serializable;

public class Message implements Serializable {

    private MessageType messageType;
    private String status;
    private String content;
    // can change message attributes 

    // dont know if we want to add some way to filter reports through messages 
    // maybe with status?

    // also maybe dont even need content variable

    public Message(MessageType messageType, String status, String content) {
        this.messageType = messageType;
        this.status = status;
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    // dont know if we need setters if
    // we just create new messages for each response 

    @Override
    public String toString() {
        return "type: " + messageType + ", Status: " + status + ", Content: " + content;
    }
}