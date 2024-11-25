package ParkingGarage;
import java.time.LocalDateTime;
import org.json.JSONObject;

public class Ticket {
	private int id;
	private Garage garage;
	private Fee ticketFee;
	private LocalDateTime entryDateTime;
	private LocalDateTime exitDateTime;
	private Payment payment;
	
	
	public Ticket(Garage garage) {
		this.garage = garage;
		this.entryDateTime = LocalDateTime.now();
	}
	
	private Ticket(int id, Garage garage, Fee ticketFee, LocalDateTime entryDateTime, LocalDateTime exitDateTime, Payment payment) {
		this.id = id;
		this.garage = garage;
		this.ticketFee = ticketFee;
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;
		this.payment = payment;
	}
}
