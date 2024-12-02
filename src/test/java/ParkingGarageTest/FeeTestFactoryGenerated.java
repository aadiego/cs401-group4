package ParkingGarageTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import com.github.javafaker.*;

import ParkingGarage.*;

public class FeeTestFactoryGenerated {
	private PrintStream originalOut = System.out;
	private ByteArrayOutputStream outContext = new ByteArrayOutputStream();
	
	private static Faker faker = new Faker();
	private static int id = faker.number().numberBetween(1, 1000);
	private static FeeType type = faker.options().option(FeeType.class);
	private static int cost = faker.number().numberBetween(100, 500);
	private JSONObject feeJson;
	private Fee fee;
	
	@BeforeEach
	public void beforeEach() {
		System.setOut(new PrintStream(outContext));
		
		HashMap<String, Object> feeValues = new HashMap<String, Object>();
		feeValues.put("id", id);
		feeValues.put("type", type);
		feeValues.put("cost", cost);
		fee = Factory.FeeFactory(feeValues);
		feeJson = Factory.asJSONObject(feeValues, Arrays.asList("id"));
	}
	
	@AfterEach
	public void afterEach() {
		System.setOut(originalOut);
		
		DataLoader dataLoader = new DataLoader();
		dataLoader.getJSONObject("fees").remove(Integer.toString(id));
	}
	
	@Test
	public void testGetId() {
		assertNotNull(fee.getId());
		assertTrue(fee.getId() >= 1);
		assertEquals(id, fee.getId());
	}
	
	@Test
	public void testGetType() {
		assertNotNull(fee.getType());
		assertEquals(type, fee.getType());
	}
	
	@Test
	public void testGetCost() {
		assertNotNull(fee.getCost());
		assertTrue(fee.getCost() >= 100);
		assertEquals(cost, fee.getCost());
	}
	
	@Test
	public void testLoad() {
		fee.save();
		Fee loadedFee = Fee.load(id);
		assertEquals(loadedFee, fee);
	}
	
	@Test
	public void testSave() {
		fee.save();
		
		DataLoader dataLoader = new DataLoader();
		JSONObject feeFromDataLoader = dataLoader.getJSONObject("fees").getJSONObject(Integer.toString(id));
		assertEquals(feeFromDataLoader.getString("type"), feeJson.getString("type"));
		assertEquals(feeFromDataLoader.getInt("cost"), feeJson.getInt("cost"));
	}
	
	@Test
	public void testToString() {
		String expectedResult = "Fee{id=" + id + ", type=" + type + ", cost=" + cost + "}";
		assertEquals(expectedResult, fee.toString());
	}
}