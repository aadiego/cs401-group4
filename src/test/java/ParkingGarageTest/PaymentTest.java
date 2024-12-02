package ParkingGarageTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import com.github.javafaker.*;

import ParkingGarage.*;

public class PaymentTest {
	private PrintStream originalOut = System.out;
	private ByteArrayOutputStream outContext = new ByteArrayOutputStream();
	
	private static Faker faker = new Faker();
	private static int paymentId = faker.number().numberBetween(1, 1000);
	private static LocalDateTime capturedDateTime = LocalDateTime.ofInstant(
			faker.date().between(new Date(1730444400), new Date()).toInstant(),
			ZoneId.systemDefault());
    private static User capturedBy = Factory.UserFactory();
    private static PaymentMethod paymentMethod = faker.options().option(PaymentMethod.class);
    private static int value = faker.number().numberBetween(100, 500);
    private JSONObject paymentJson;
    private Payment payment;
	
	@BeforeEach
	public void beforeEach() {
		System.setOut(new PrintStream(outContext));
		
		HashMap<String, Object> paymentValues = new HashMap<String, Object>();
		paymentValues.put("paymentId", paymentId);
		paymentValues.put("capturedDateTime", capturedDateTime);
		paymentValues.put("capturedBy", capturedBy);
		paymentValues.put("paymentMethod", paymentMethod);
		paymentValues.put("value", value);
		payment = Factory.PaymentFactory(paymentValues);
		paymentJson = Factory.asJSONObject(paymentValues, Arrays.asList("paymentId"));
	}
	
	@AfterEach
	public void afterEach() {
		System.setOut(originalOut);
		
		DataLoader dataLoader = new DataLoader();
		dataLoader.getJSONObject("payments").remove(Integer.toString(paymentId));
	}
	
	@Test
	public void testGetId() {
		assertNotNull(payment.getId());
		assertTrue(payment.getId() >= 1);
		assertEquals(paymentId, payment.getId());
	}
	
	@Test
	public void testGetCapturedDateTime() {
		assertNotNull(payment.getCapturedDateTime());
		assertEquals(capturedDateTime, payment.getCapturedDateTime());
	}
	
	@Test
	public void testGetCapturedBy() {
		assertNotNull(payment.getCapturedBy());
		assertEquals(capturedBy, payment.getCapturedBy());
	}
	
	@Test
	public void testGetPaymentMethod() {
		assertNotNull(payment.getPaymentMethod());
		assertEquals(paymentMethod, payment.getPaymentMethod());
	}
	
	@Test
	public void testGetValue() {
		assertNotNull(payment.getValue());
		assertTrue(payment.getValue() >= 100);
		assertEquals(value, payment.getValue());
	}
	
	@Test
	public void testLoad() {
		payment.save();
		Payment loadedPayment = Payment.load(paymentId);
		assertEquals(loadedPayment, payment);
	}
	
	@Test
	public void testSave() {
		payment.save();
		
		DataLoader dataLoader = new DataLoader();
		JSONObject feeFromDataLoader = dataLoader.getJSONObject("payments").getJSONObject(Integer.toString(paymentId));
		assertEquals(feeFromDataLoader.getString("capturedDateTime"), paymentJson.getString("capturedDateTime"));
		assertEquals(feeFromDataLoader.getInt("capturedById"), paymentJson.getInt("capturedById"));
		assertEquals(feeFromDataLoader.getString("paymentMethod"), paymentJson.getString("paymentMethod"));
		assertEquals(feeFromDataLoader.getInt("value"), paymentJson.getInt("value"));
	}
}