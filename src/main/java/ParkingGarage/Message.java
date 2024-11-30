package ParkingGarage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private final MessageType type;
    private final Map<String, Object> data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public MessageType getType() {
        return type;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}