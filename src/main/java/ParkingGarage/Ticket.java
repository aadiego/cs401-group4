package ParkingGarage;
import java.time.LocalDateTime;
import org.json.JSONObject;

public class Ticket extends DataLoaderable {
	private int id;
	private Garage garage;
	private Fee ticketFee;
	private LocalDateTime entryDateTime;
	private LocalDateTime exitDateTime;
	// private Payment payment; 
	
	
	public Ticket(Garage garage) {
		this.garage = garage;
		this.entryDateTime = LocalDateTime.now();
	}
	
	private Ticket(int id, Garage garage, Fee ticketFee, LocalDateTime entryDateTime, LocalDateTime exitDateTime) {
		this.id = id;
		this.garage = garage;
		this.ticketFee = ticketFee;
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;
		// this.payment = payment;
	}
	
	// Getters
	public int getId() {
		return id;
	}
	
	public Garage getGarage() {
		return garage;
	}
	
	public LocalDateTime getEntryDateTime() {
		return entryDateTime;
	}
	
	public LocalDateTime getExitDateTime() {
		return exitDateTime;
	}
	
	public Fee getFee() {
		return ticketFee;
	}
	
	//public Payment getPayment() {
	//	return payment;
	//}
	
	// setters
	public void setExitTime(LocalDateTime dateTime) {
		this.exitDateTime = dateTime;
	}
	
	//public void setPayment(Payment payment) {
		
	//}
	
	
	// Static method to load a ticket by ID
	public static Ticket load(int id) {
		DataLoader dataLoader = new DataLoader();
		JSONObject ticketJson = dataLoader.getJSONObject("Ticket_" + id);
		return load(ticketJson);
	}
	
	// Static method to load ticket from JSON object
	public static Ticket load(JSONObject object) {
		try {
			int id = object.getInt("id");
			Garage garage = Garage.load(object.getInt("garage"));
			LocalDateTime entryDateTime = LocalDateTime.parse(object.getString("entryDateTime"));
			LocalDateTime exitDateTime = object.has("exitDateTime") ? LocalDateTime.parse(object.getString("exitDateTime")) : null;
			Fee fee = object.has("fee") ? Fee.load(object.getJSONObject("fee")) : null;
			return new Ticket(id, garage, fee, entryDateTime, exitDateTime);
			
		} catch (Exception e) {
			System.err.println("Error loading Ticket from JSON: " + e.getMessage());
			return null;
		}
	}
}
