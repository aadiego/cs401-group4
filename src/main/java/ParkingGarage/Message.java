package ParkingGarage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private final MessageType type;
    private final Map<String, Object> data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }
    
    public Message(MessageType type, Map<String, Object> data) {
        this.type = type;
        this.data = data; 
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public MessageType getMessageType() {
        return type;
    }

    public Object getData(String key) {
        return data.get(key);
    }
    
    public boolean hasKey(String key) {
    	try {
    		data.get(key);
    		return true;
    	} catch (Exception ex) {
    		return false;
    	}
    }
    
    public void removeData(String key) {
    	data.remove(key);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}