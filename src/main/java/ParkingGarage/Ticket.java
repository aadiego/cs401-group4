package ParkingGarage;
import java.time.LocalDateTime;
import java.time.Duration;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Ticket extends DataLoaderable {
	private int id; // Ticket id
	private Garage garage; // Garage associated with the ticket
	private Fee ticketFee; // Fee for the ticket
	private LocalDateTime entryDateTime; // Vehicle entry time
	private LocalDateTime exitDateTime; // Vehicle exit time
	private Payment payment;

	
	
	// public constructor
	public Ticket(Garage garage) {
		this.id = DataLoader.getNextId("tickets");
		this.garage = garage;
		this.entryDateTime = LocalDateTime.now();
	}
	
	// private constructor
	private Ticket(int id, Garage garage, Fee ticketFee, LocalDateTime entryDateTime, LocalDateTime exitDateTime, Payment payment) {
		this.id = id;
		this.garage = garage;
		this.ticketFee = ticketFee;
		this.entryDateTime = entryDateTime;
		this.exitDateTime = exitDateTime;
		this.payment = payment;
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
	
	public Payment getPayment() {
		return payment;
	}
	
	// setters
	public void setExitTime(LocalDateTime dateTime) {
		this.exitDateTime = dateTime;
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	
	// Static method to load a ticket by ID
	public static Ticket load(int id) {
		try {
			DataLoader dataLoader = new DataLoader();
			JSONObject tickets = dataLoader.getJSONObject("tickets");
			
			if (tickets.has(Integer.toString(id))) {
				JSONObject ticket = tickets.getJSONObject(Integer.toString(id));
				
				Garage garage = Garage.load(ticket.getInt("garageId"));
				Fee ticketFee = Fee.load(ticket.getJSONObject("ticketFee"));
				LocalDateTime entryDateTime = LocalDateTime.parse(ticket.getString("entryDateTime"));
				LocalDateTime exitDateTime = ticket.has("exitDateTime") ? LocalDateTime.parse(ticket.getString("exitDateTime")) : null;
				Payment payment = ticket.has("payment") ? Payment.load(ticket.getJSONObject("payment")) : null;
				
				return new Ticket(id, garage, ticketFee, entryDateTime, exitDateTime, payment);
					
			} else {
				System.err.println("Ticket with ID " + id + " not found.");
			}
		} catch (Exception e) {
			System.err.println("Error loading Ticket with ID " + id + ": " + e.getMessage());
		}
		return null;
	}
	
	// Static method to load ticket from JSON object
	public static Ticket load(JSONObject object) {
		try {
			int id = object.getInt("id");
			Garage garage = Garage.load(object.getInt("garageId"));
			Fee ticketFee = Fee.load(object.getJSONObject("ticketFee"));
			LocalDateTime entryDateTime = LocalDateTime.parse(object.getString("entryDateTime"));
			LocalDateTime exitDateTime = object.has("exitDateTime") ? LocalDateTime.parse(object.getString("exitDateTime")) : null;
			Payment payment = object.has("payment") ? Payment.load(object.getJSONObject("payment")) : null;
			
			return new Ticket(id, garage, ticketFee, entryDateTime, exitDateTime, payment);
		} catch (Exception e) {
			System.err.println("Error loading Ticket from JSON: " + e.getMessage());
		}
		return null;
	};
	
	// Static method to load all tickets for a specific Garage ID
    public static List<Ticket> loadTicketsForGarage(int garageId) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            DataLoader dataLoader = new DataLoader();
            JSONObject ticketsJson = dataLoader.getJSONObject("tickets");

            for (String key : ticketsJson.keySet()) {
                JSONObject ticketJson = ticketsJson.getJSONObject(key);
                if (ticketJson.getInt("garageId") == garageId) {
                    tickets.add(Ticket.load(ticketJson));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tickets for Garage ID " + garageId + ": " + e.getMessage());
        }
        return tickets;
    }
	
    // Calculate fees based on  FeeType
    public void calculateFee() {
        if (entryDateTime == null || exitDateTime == null || ticketFee == null) {
            System.err.println("Cannot calculate fee: Missing data.");
            return;
        }

        Duration duration = Duration.between(entryDateTime, exitDateTime);
        long hours = duration.toHours();
        long days = duration.toDays();
        
        if (hours < 0 || days < 0) {
            System.err.println("Error: Exit time is before entry time.");
            return;
        }
        
        if (ticketFee.getType() == FeeType.HOURLY) {
            System.out.println("Fee: $" + hours * ticketFee.getCost());
        } else if (ticketFee.getType() == FeeType.DAILY) {
            System.out.println("Fee: $" + days * ticketFee.getCost());
        }
    }
	
    // Save to JSON
    @Override
	public void save() {
		try {
			JSONObject ticket = new JSONObject();
			ticket.put("id", this.id);
			ticket.put("garageId", this.garage.getId());
			ticket.put("entryDateTime", this.entryDateTime.toString());
			ticket.put("exitDateTime", this.exitDateTime != null ? this.exitDateTime.toString() : null);
			
			if (this.ticketFee != null) {
				JSONObject fee = new JSONObject();
				fee.put("id", this.ticketFee.getId());
				fee.put("type", this.ticketFee.getType().name());
				fee.put("cost", this.ticketFee.getCost());
				ticket.put("ticketFee", fee);
				
			}
			
			if (this.payment != null) {
				ticket.put("payment", this.payment.toJSONObject());
			}
			
			DataLoader dataLoader = new DataLoader();
            dataLoader.getJSONObject("tickets").put(Integer.toString(this.id), ticket);
            dataLoader.saveData();
		} catch (Exception e) {
			System.err.println("Error saving Ticket with ID " + this.id + ": " + e.getMessage());
		}
	}
	
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", garage=" + garage.getName() +
                ", ticketFee=" + (ticketFee != null ? ticketFee.toString() : "None") +
                ", entryDateTime=" + entryDateTime +
                ", exitDateTime=" + (exitDateTime != null ? exitDateTime : "Still parked") +
                ", payment=" + (payment != null ? payment.toString() : "None") +
                '}';
    }
}
