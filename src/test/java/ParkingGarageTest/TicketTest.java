package ParkingGarageTest;

import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ParkingGarage.DataLoader;
import ParkingGarage.Fee;
import ParkingGarage.FeeType;
import ParkingGarage.Garage;
import ParkingGarage.Payment;
import ParkingGarage.PaymentMethod;
import ParkingGarage.Ticket;

import java.time.LocalDateTime;
import java.util.List;

class TicketTest {

	@Test
	void testSave() {
	    Garage garage = new Garage("Test Garage", "123 Test St", 50);
	    Fee fee = new Fee(FeeType.HOURLY, 15);
	    Payment payment = new Payment(PaymentMethod.CREDIT, 45);

	    Ticket ticket = new Ticket(garage);
	    ticket.setFee(fee);
	    ticket.setPayment(payment);
	    ticket.save();

	    // Load and validate the saved ticket
	    DataLoader dataLoader = new DataLoader();
	    JSONObject savedTicket = dataLoader.getJSONObject("tickets").getJSONObject(Integer.toString(ticket.getId()));

	    assertNotNull(savedTicket, "Saved ticket should not be null");
	    assertEquals(ticket.getId(), savedTicket.getInt("id"), "Ticket ID should match");
	    assertEquals(garage.getId(), savedTicket.getInt("garageId"), "Garage ID should match");
	    assertEquals(ticket.getEntryDateTime().toString(), savedTicket.getString("entryDateTime"), "Entry date-time should match");

	    // Validate payment ID in ticket
	    assertEquals(payment.getId(), savedTicket.getInt("paymentId"), "Payment ID should match");
	}


	@Test
    void testTicketConstructor() {
        // Test Ticket creation with a Garage
        Garage garage = new Garage("Garage B", "456 Garage Blvd", 100);
        Ticket ticket = new Ticket(garage);
        assertNotNull(ticket.getGarage(), "Garage should not be null");
        assertEquals(garage, ticket.getGarage(), "Garage should match");
        assertNotNull(ticket.getEntryDateTime(), "Entry date-time should not be null");
    }

	@Test
    void testGetId() {
        Garage garage = new Garage("Garage C", "789 Garage Ln", 30);
        Ticket ticket = new Ticket(garage);
        assertTrue(ticket.getId() > 0, "Ticket ID should be greater than 0");
    }

	@Test
    void testGetGarage() {
        Garage garage = new Garage("Garage D", "123 Garage Rd", 40);
        Ticket ticket = new Ticket(garage);
        assertEquals(garage, ticket.getGarage(), "Garage should match");
    }

	@Test
    void testGetEntryDateTime() {
        Garage garage = new Garage("Garage E", "456 Garage Dr", 20);
        Ticket ticket = new Ticket(garage);
        assertNotNull(ticket.getEntryDateTime(), "Entry date-time should not be null");
    }

	@Test
    void testGetExitDateTime() {
        Garage garage = new Garage("Garage F", "789 Garage Ct", 25);
        Ticket ticket = new Ticket(garage);
        assertNull(ticket.getExitDateTime(), "Exit date-time should initially be null");

        // Set exit time and verify
        LocalDateTime exitTime = LocalDateTime.now();
        ticket.setExitTime(exitTime);
        assertEquals(exitTime, ticket.getExitDateTime(), "Exit time should match the set value");
    }

	@Test
    void testGetFee() {
        Garage garage = new Garage("Garage G", "123 Garage Blvd", 50);
        Ticket ticket = new Ticket(garage);
        Fee fee = new Fee(FeeType.HOURLY, 10);
        ticket.setFee(fee);
        assertEquals(fee, ticket.getFee(), "Fee should match the set value");
    }

	@Test
    void testGetPayment() {
        Garage garage = new Garage("Garage H", "456 Garage Ln", 100);
        Ticket ticket = new Ticket(garage);
        Payment payment = new Payment(PaymentMethod.CASH, 50);
        ticket.setPayment(payment);
        assertEquals(payment, ticket.getPayment(), "Payment should match the set value");
    }

	@Test
    void testSetExitTime() {
        Garage garage = new Garage("Garage I", "789 Garage Rd", 75);
        Ticket ticket = new Ticket(garage);

        LocalDateTime exitTime = LocalDateTime.now();
        ticket.setExitTime(exitTime);
        assertEquals(exitTime, ticket.getExitDateTime(), "Exit time should match the set value");
    }

	@Test
    void testSetPayment() {
        Garage garage = new Garage("Garage J", "123 Garage Ct", 60);
        Ticket ticket = new Ticket(garage);

        Payment payment = new Payment(PaymentMethod.CREDIT, 100);
        ticket.setPayment(payment);
        assertEquals(payment, ticket.getPayment(), "Payment should match the set value");
    }

	@Test
    void testLoadById() {
        // Setup: Create and save a Garage and a Ticket
        Garage garage = new Garage("Test Garage", "123 Test St", 50);
        garage.save();

        Fee fee = new Fee(FeeType.HOURLY, 10);
        fee.save();

        Ticket ticket = new Ticket(garage);
        ticket.setFee(fee);
        ticket.setExitTime(ticket.getEntryDateTime().plusHours(2)); // Set exit time
        ticket.save(); // Save the ticket

        // Test: Load the Ticket by ID
        Ticket loadedTicket = Ticket.load(ticket.getId());

        // Assertions
        assertNotNull(loadedTicket, "Loaded Ticket should not be null");
        assertEquals(ticket.getId(), loadedTicket.getId(), "Ticket ID should match");
        assertEquals(ticket.getGarage().getId(), loadedTicket.getGarage().getId(), "Garage ID should match");
        assertEquals(ticket.getEntryDateTime(), loadedTicket.getEntryDateTime(), "Entry date-time should match");
        assertEquals(ticket.getExitDateTime(), loadedTicket.getExitDateTime(), "Exit date-time should match");
        assertNotNull(loadedTicket.getFee(), "Fee should not be null");
        assertEquals(ticket.getFee().getCost(), loadedTicket.getFee().getCost(), "Fee cost should match");
    }


	@Test
    void testLoadFromJson() {
        // Setup: Create a Garage and a Fee
        Garage garage = new Garage("Test Garage", "123 Test Blvd", 30);
        garage.save();

        Fee fee = new Fee(FeeType.DAILY, 20);
        fee.save();

        // Create a Ticket and its JSON representation
        Ticket ticket = new Ticket(garage);
        ticket.setFee(fee);
        ticket.setExitTime(ticket.getEntryDateTime().plusDays(1));
        JSONObject ticketJson = new JSONObject();
        ticketJson.put("id", ticket.getId());
        ticketJson.put("garageId", garage.getId());
        ticketJson.put("entryDateTime", ticket.getEntryDateTime().toString());
        ticketJson.put("exitDateTime", ticket.getExitDateTime().toString());
        ticketJson.put("ticketFee", new JSONObject()
                .put("id", fee.getId())
                .put("type", fee.getType().name())
                .put("cost", fee.getCost()));

        // Test: Load the Ticket from the JSON object
        Ticket loadedTicket = Ticket.load(ticketJson);

        // Assertions
        assertNotNull(loadedTicket, "Loaded Ticket should not be null");
        assertEquals(ticket.getId(), loadedTicket.getId(), "Ticket ID should match");
        assertEquals(ticket.getGarage().getId(), loadedTicket.getGarage().getId(), "Garage ID should match");
        assertEquals(ticket.getEntryDateTime(), loadedTicket.getEntryDateTime(), "Entry date-time should match");
        assertEquals(ticket.getExitDateTime(), loadedTicket.getExitDateTime(), "Exit date-time should match");
        assertNotNull(loadedTicket.getFee(), "Fee should not be null");
        assertEquals(ticket.getFee().getType(), loadedTicket.getFee().getType(), "Fee type should match");
        assertEquals(ticket.getFee().getCost(), loadedTicket.getFee().getCost(), "Fee cost should match");
    }


	@Test
	void testLoadTicketsForGarage() {
	    // Create a Garage
	    Garage garage = new Garage("Garage M", "123 Garage St", 100);

	    // Create and save two Tickets for the Garage
	    Ticket ticket1 = new Ticket(garage);
	    Ticket ticket2 = new Ticket(garage);
	    ticket1.save();
	    ticket2.save();

	    // Load tickets for the Garage
	    List<Ticket> tickets = Ticket.loadTicketsForGarage(garage.getId());
	    
	    // Assertions
	    assertNotNull(tickets, "Loaded Tickets list should not be null");
	    assertEquals(2, tickets.size(), "There should be 2 tickets for the Garage");
	}

	@Test
    void testCalculateFee() {
        Garage garage = new Garage("Garage N", "456 Garage Blvd", 100);
        Ticket ticket = new Ticket(garage);

        Fee fee = new Fee(FeeType.HOURLY, 10);
        ticket.setFee(fee);
        ticket.setExitTime(ticket.getEntryDateTime().plusHours(3));
        ticket.calculateFee();

        assertEquals(30, ticket.getFee().getCost(), "Calculated fee should be 3 * 10 = 30");
    }

	@Test
	void testToString() {
	    Garage garage = new Garage("Main Garage", "123 Main St", 50);
	    Fee fee = new Fee(FeeType.HOURLY, 10);
	    Ticket ticket = new Ticket(garage);
	    ticket.setFee(fee);

	    String expected = "Ticket{" +
	                      "id=" + ticket.getId() +
	                      ", garage=Main Garage" +
	                      ", ticketFee=" + fee.toString() +
	                      ", entryDateTime=" + ticket.getEntryDateTime() +
	                      ", exitDateTime=Still parked" +
	                      ", payment=None" +
	                      "}";

	    assertEquals(expected, ticket.toString(), "Ticket toString() output should match the expected format");
	}


}
