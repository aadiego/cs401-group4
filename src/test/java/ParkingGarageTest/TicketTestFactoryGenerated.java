package ParkingGarageTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import com.github.javafaker.*;

import ParkingGarage.*;

public class TicketTestFactoryGenerated {
	private PrintStream originalOut = System.out;
	private ByteArrayOutputStream outContext = new ByteArrayOutputStream();
	
	private static Faker faker = new Faker();
	private static int id = faker.number().numberBetween(1, 1000);
	private static Garage garage = Factory.GarageFactory();
	private static Fee ticketFee = garage.getCurrentParkingFee();
	private static LocalDateTime entryDateTime = LocalDateTime.ofInstant(
			faker.date().between(new Date(1730444400), new Date()).toInstant(),
			ZoneId.systemDefault());
	private static int timeDiffMins = faker.number().numberBetween(60, 180);
	private static LocalDateTime exitDateTime = entryDateTime.plusMinutes(timeDiffMins);
	private static Payment payment;
	private JSONObject ticketJson;
	private Ticket ticket;

	@BeforeEach
	public void beforeEach() {	
		System.setOut(new PrintStream(outContext));
		
		HashMap<String, Object> paymentValues = new HashMap<String, Object>();
		paymentValues.put("capturedDateTime", exitDateTime);
		paymentValues.put("cost", ticketFee.getType() == FeeType.DAILY
				? ticketFee.getCost()
				: ticketFee.getCost() * (int) Math.ceil(timeDiffMins / 60));
		
		payment = Factory.PaymentFactory(paymentValues);
		
		HashMap<String, Object> ticketValues = new HashMap<String, Object>();
		ticketValues.put("id", id);
		ticketValues.put("garage", garage);
		ticketValues.put("ticketFee", ticketFee);
		ticketValues.put("entryDateTime", entryDateTime);
		ticketValues.put("exitDateTime", exitDateTime);
		ticketValues.put("payment", payment);
		ticket = Factory.TicketFactory(ticketValues);
		ticketJson = Factory.asJSONObject(ticketValues, Arrays.asList("id"));
	}
	
	@AfterEach
	public void afterEach() {
		System.setOut(originalOut);
		
		DataLoader dataLoader = new DataLoader();
		dataLoader.getJSONObject("tickets").remove(Integer.toString(id));
	}
	
	@Test
	public void testGetId() {
		assertNotNull(ticket.getId());
		assertTrue(ticket.getId() >= 1);
		assertEquals(id, ticket.getId());
	}
	
	@Test
	public void testGetGarage() {
		assertNotNull(ticket.getGarage());
		assertEquals(garage, ticket.getGarage());
	}
	
	@Test
	public void testGetEntryDateTime() {
		assertNotNull(ticket.getEntryDateTime());
		assertEquals(entryDateTime, ticket.getEntryDateTime());
	}
	
	@Test
	public void testGetExitDateTime() {
		assertNotNull(ticket.getExitDateTime());
		assertEquals(exitDateTime, ticket.getExitDateTime());
	}
	
	@Test
	public void testGetFee() {
		assertNotNull(ticket.getFee());
		assertEquals(ticketFee, ticket.getFee());
	}
	
	@Test
	public void testGetPayment() {
		assertNotNull(ticket.getPayment());
		assertEquals(payment, ticket.getPayment());
	}
	
	@Test
	public void testSetExitTime() {
		LocalDateTime now = LocalDateTime.now();
		ticket.setExitTime(now);
		assertNotEquals(exitDateTime, ticket.getExitDateTime());
		assertEquals(now, ticket.getExitDateTime());
	}
	
	@Test
	public void testSetPayment() {
		Payment newPayment = Factory.PaymentFactory();
		ticket.setPayment(newPayment);
		assertNotEquals(payment, ticket.getPayment());
		assertEquals(newPayment, ticket.getPayment());
	}
	
	@Test
	public void testLoad() {
		ticket.save();
		Ticket loadedTicket = Ticket.load(id);
		assertEquals(loadedTicket, ticket);
	}
	
	@Test
	public void testLoadTicketsForGarage() {
		Garage garage = Factory.GarageFactory();
		HashMap<String, Object> ticketValues = new HashMap<String, Object>();
		ticketValues.put("garage", garage);
		
		List<Ticket> tickets = new ArrayList<>();
		for(int index = 0; index < 10; index++) {
			Ticket thisTicket = Factory.TicketFactory(ticketValues);
			thisTicket.save();
			tickets.add(thisTicket);
		}
		
		List<Ticket> loadedTickets = Ticket.loadTicketsForGarage(garage.getId());
		assertEquals(loadedTickets.size(), tickets.size());
		
		tickets.forEach(ticket -> {
			DataLoader dataLoader = new DataLoader();
			dataLoader.getJSONObject("tickets").remove(Integer.toString(ticket.getId()));
			
		});
	}
	
	@Test
	public void testCalculateFee() {
		int cost = ticketFee.getType() == FeeType.DAILY
				? ticketFee.getCost()
				: ticketFee.getCost() * (int) Math.ceil(timeDiffMins / 60);
		String expectedResult = "Fee: $" + (cost / 100) + System.lineSeparator();
		assertEquals(expectedResult, outContext.toString());
		outContext.reset();
	}
	
	@Test
	public void testSave() {
		ticket.save();
		
		DataLoader dataLoader = new DataLoader();
		JSONObject ticketFromDataLoader = dataLoader.getJSONObject("tickets").getJSONObject(Integer.toString(id));
		assertEquals(ticketFromDataLoader.getInt("garageId"), ticketJson.getInt("garageId"));
		assertEquals(ticketFromDataLoader.getInt("ticketFeeId"), ticketJson.getInt("ticketFeeId"));
		assertEquals(ticketFromDataLoader.getString("entryDateTime"), ticketJson.getString("entryDateTime"));
		assertEquals(ticketFromDataLoader.getString("exitDateTime"), ticketJson.getString("exitDateTime"));
		assertEquals(ticketFromDataLoader.getInt("paymentId"), ticketJson.getInt("paymentId"));
	}
}