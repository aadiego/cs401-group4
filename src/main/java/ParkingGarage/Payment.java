package ParkingGarage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;



public class Payment {
    // variables
    private int paymentId; 
    private LocalDateTime capturedDateTime;
    private User capturedBy;
    private PaymentMethod paymentMethod;
    private int value;

    // public constructor 
    public Payment(PaymentMethod paymentMethod, int value) {
        this.paymentMethod = paymentMethod;
        this.value = value;
        this.capturedDateTime = LocalDateTime.now();
    }

    public Payment(User capturedBy, PaymentMethod paymentMethod, int value) {
        this.capturedBy = capturedBy;
        this.paymentMethod = paymentMethod;
        this.value = value;
        this.capturedDateTime = LocalDateTime.now();
    }

    private Payment(int paymentId, LocalDateTime capturedDateTime, User capturedBy, PaymentMethod paymentMethod, int value) {
        this.paymentId = paymentId;
        this.capturedDateTime = capturedDateTime;
        this.capturedBy = capturedBy;
        this.paymentMethod = paymentMethod;
        this.value = value;
    }

    public int getID() {
        return paymentId;
    }

    public LocalDateTime getCapturedDateTime() {
        return capturedDateTime;
    }

    public User getCapturedBy() {
        return capturedBy;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public int getValue() {
        return value;
    }

    public static Payment load(JSONObject object) {
        // figure out JSON
    }

    public void save() {
        // figure out JSON
    }

    


}
