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
		this.ticketFee = garage.getCurrentParkingFee();
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
	
    public void setFee(Fee ticketFee) {
        this.ticketFee = ticketFee;
    }

	
	// Static method to load a ticket by ID
	public static Ticket load(int id) {
		try {
			DataLoader dataLoader = new DataLoader();
			JSONObject tickets = dataLoader.getJSONObject("tickets");
			
			if (tickets.has(Integer.toString(id))) {
				JSONObject ticket = tickets.getJSONObject(Integer.toString(id));
				
				Garage garage = Garage.load(ticket.getInt("garageId"));
				Fee ticketFee = Fee.load(ticket.getInt("ticketFeeId"));
				LocalDateTime entryDateTime = LocalDateTime.parse(ticket.getString("entryDateTime"));
				LocalDateTime exitDateTime = ticket.has("exitDateTime") ? LocalDateTime.parse(ticket.getString("exitDateTime")) : null;
				Payment payment = ticket.has("paymentId") ? Payment.load(ticket.getInt("paymentId")) : null;
				
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
			Fee ticketFee = Fee.load(object.getInt("ticketFeeId"));
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
            	if (key != "autoIncrement" ) {
	                JSONObject ticketJson = ticketsJson.getJSONObject(key);
	                if (ticketJson.getInt("garageId") == garageId) {
	                    tickets.add(Ticket.load(ticketJson));
	                }
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

        if (ticketFee.getType() == FeeType.HOURLY) {
            ticketFee = new Fee(FeeType.HOURLY, (int) (hours * ticketFee.getCost()));
        } else if (ticketFee.getType() == FeeType.DAILY) {
            ticketFee = new Fee(FeeType.DAILY, (int) (days * ticketFee.getCost()));
        }
    }

	
    // Save to JSON
    @Override
	public void save() {
    	this.garage.save();
    	this.ticketFee.save();
    	
    	if (this.payment != null) {
    		this.payment.save();
    	}
    	
		try {
			JSONObject ticket = new JSONObject();
			ticket.put("id", this.id);
			ticket.put("garageId", this.garage.getId());
			ticket.put("entryDateTime", this.entryDateTime.toString());
			ticket.put("exitDateTime", this.exitDateTime != null ? this.exitDateTime.toString() : null);
			ticket.put("ticketFeeId", this.ticketFee.getId());
			
			if (this.payment != null) {
				ticket.put("paymentId", this.payment.getId());
			}
			
			DataLoader dataLoader = new DataLoader();
            dataLoader.getJSONObject("tickets").put(Integer.toString(this.id), ticket);
            dataLoader.saveData();

        } catch (Exception e) {
            System.err.println("Error saving Ticket with ID " + this.id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

	
    @Override
    public String toString() {
    	
        return "Ticket{" +
                "id=" + id +
                ", garage=" + (garage != null ? garage.getName() : "None") +
                ", ticketFee=" + (ticketFee != null ? ticketFee.toString() : "None") +
                ", entryDateTime=" + (entryDateTime != null ? entryDateTime : "None") +
                ", exitDateTime=" + (exitDateTime != null ? exitDateTime : "Still parked") +
                ", payment=" + (payment != null ? payment.toString() : "None") +
                '}';
    }
    
    // Overrides for testing
    public Ticket() {}
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Ticket other = (Ticket) obj;
        Boolean optionalValid = true;
        if (this.exitDateTime != null) {
        	optionalValid = this.exitDateTime.equals(other.exitDateTime);
        }
        if (this.payment != null) {
        	optionalValid = optionalValid && this.payment.equals(other.payment);
        }

        return this.id == other.id &&
               this.garage.equals(other.garage) &&
               this.ticketFee.equals(other.ticketFee) &&
               this.entryDateTime.equals(other.entryDateTime) &&
               optionalValid;
    }
}
