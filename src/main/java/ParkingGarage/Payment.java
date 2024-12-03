package ParkingGarage;

import java.time.LocalDateTime;
import org.json.JSONObject;

public class Payment {
    // variables
    private int paymentId; 
    private LocalDateTime capturedDateTime;
    private User capturedBy;
    private PaymentMethod paymentMethod;
    private int value;

    // public constructor 
    public Payment(PaymentMethod paymentMethod, int value) {
        this.paymentId = DataLoader.getNextId("payments");
        this.paymentMethod = paymentMethod;
        this.value = value;
        this.capturedDateTime = LocalDateTime.now();
    }

    public Payment(User capturedBy, PaymentMethod paymentMethod, int value) {
        this.paymentId = DataLoader.getNextId("payments");
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

    public int getId() {
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

    public static Payment load(int paymentId) {
        DataLoader dataLoader = new DataLoader();
        JSONObject payments = dataLoader.getJSONObject("payments");

        // search for paymentID
        JSONObject payment = payments.has(Integer.toString(paymentId))
                             ? payments.getJSONObject(Integer.toString(paymentId))
                             : null;
        
        // load payment variables from JSONObject
        if (payment != null) {
            return new Payment(
                paymentId,
                LocalDateTime.parse(payment.getString("capturedDateTime")),
                payment.has("capturedById") && !payment.isNull("capturedById")
                    ? User.load(payment.getInt("capturedById"))
                    : null,
                PaymentMethod.valueOf(payment.getString("paymentMethod")),
                payment.getInt("value")
            );
        } else {
            return null;
        }

    }

    public static Payment load(JSONObject object) {
        int paymentId = object.getInt("paymentid");
        LocalDateTime capturedDateTime = LocalDateTime.parse(object.getString("capturedDateTime"));
        PaymentMethod paymentMethod = PaymentMethod.valueOf(object.getString("paymentMethod"));
        int value = object.getInt("value");

        User capturedBy = null;
        if (object.has("capturedBy") && !object.isNull("capturedBy")) {
            int capturedById = object.getInt("capturedBy");
            capturedBy = User.load(capturedById);
        }

        return new Payment(paymentId, capturedDateTime, capturedBy, paymentMethod, value);
    }

    public void save() {
    	if (this.capturedBy != null) {
    		this.capturedBy.save();
    	}
    	
        // save payment variables into JSON object
        JSONObject payment = new JSONObject();
        payment.put("capturedDateTime", this.capturedDateTime.toString());
        payment.put("paymentMethod", this.paymentMethod.toString());
        payment.put("value", this.value);

        if (this.capturedBy != null) {
            payment.put("capturedById", this.capturedBy.getId());
        } else {
            payment.put("capturedById", JSONObject.NULL);
        }

        DataLoader dataLoader = new DataLoader();
        dataLoader.getJSONObject("payments").put(Integer.toString(this.paymentId), payment);
        dataLoader.saveData();
    }
    
    // Overrides for testing
    public Payment() {}
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Payment other = (Payment) obj;
        return this.paymentId == other.paymentId &&
               this.capturedDateTime.equals(other.capturedDateTime) &&
               this.capturedBy.equals(other.capturedBy) &&
               this.paymentMethod.equals(other.paymentMethod) &&
               this.value == other.value;
    }
}
