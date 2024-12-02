package ParkingGarageTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import com.github.javafaker.*;

import ParkingGarage.*;

public class GarageTestFactoryGenerated {
	private PrintStream originalOut = System.out;
	private ByteArrayOutputStream outContext = new ByteArrayOutputStream();
	
	private static Faker faker = new Faker();
	private static int id = faker.number().numberBetween(1, 1000);
	private static String name = faker.company().name();
	private static String address = faker.address().fullAddress();
	private static Fee currentParkingFee = Factory.FeeFactory();
	private static int occupiedSpaces = faker.number().numberBetween(1, 500);
	private static int totalSpaces = occupiedSpaces + faker.number().numberBetween(1, 500);
	private JSONObject garageJson;
	private Garage garage;
	
	@BeforeEach
	public void beforeEach() {
		System.setOut(new PrintStream(outContext));
		
		HashMap<String, Object> garageValues = new HashMap<String, Object>();
		garageValues.put("id", id);
		garageValues.put("name", name);
		garageValues.put("address", address);
		garageValues.put("currentParkingFee", currentParkingFee);
		garageValues.put("occupiedSpaces", occupiedSpaces);
		garageValues.put("totalSpaces", totalSpaces);
		garage = Factory.GarageFactory(garageValues);
		garageJson = Factory.asJSONObject(garageValues, Arrays.asList("id"));
	}
	
	@AfterEach
	public void afterEach() {
		System.setOut(originalOut);
		
		DataLoader dataLoader = new DataLoader();
		dataLoader.getJSONObject("garages").remove(Integer.toString(id));
	}
	
	@Test
	public void testGetId() {
		assertNotNull(garage.getId());
		assertTrue(garage.getId() >= 1);
		assertEquals(id, garage.getId());
	}
	
	@Test
	public void testGetName() {
		assertNotNull(garage.getName());
		assertEquals(name, garage.getName());
	}
	
	@Test
	public void testGetAddress() {
		assertNotNull(garage.getAddress());
		assertEquals(address, garage.getAddress());
	}
	
	@Test
	public void testGetCurrentParkingFee() {
		assertNotNull(garage.getCurrentParkingFee());
		assertEquals(currentParkingFee, garage.getCurrentParkingFee());
	}
	
	@Test
	public void testGetAvailableSpaces() {
		assertNotNull(garage.getAvailableSpaces());
		assertTrue(garage.getAvailableSpaces() <= totalSpaces);
		assertTrue(garage.getAvailableSpaces() >= 0);
		assertEquals(totalSpaces - occupiedSpaces, garage.getAvailableSpaces());
	}
	
	@Test
	public void testGetOccupiedSpaces() {
		assertNotNull(garage.getOccupiedSpaces());
		assertTrue(garage.getOccupiedSpaces() >= 0);
		assertEquals(occupiedSpaces, garage.getOccupiedSpaces());
	}
	
	@Test
	public void testGetTotalSpaces() {
		assertNotNull(garage.getTotalSpaces());
		assertTrue(garage.getTotalSpaces() >= 0);
		assertEquals(totalSpaces, garage.getTotalSpaces());
	}
	
	@Test
	public void testSetName() {
		String newName = faker.company().name();
		garage.setName(newName);
		
		assertNotEquals(name, garage.getName());
		assertEquals(newName, garage.getName());
	}
	
	@Test
	public void testSetAddress() {
		String newAddress = faker.address().fullAddress();
		garage.setAddress(newAddress);
		
		assertNotEquals(address, garage.getAddress());
		assertEquals(newAddress, garage.getAddress());
	}
	
	@Test
	public void testSetParkingFee() {
		Fee newParkingFee = Factory.FeeFactory();
		garage.setParkingFee(newParkingFee);
		
		assertNotEquals(currentParkingFee, garage.getCurrentParkingFee());
		assertEquals(newParkingFee, garage.getCurrentParkingFee());
	}
	
	@Test
	public void testSetTotalSpaces() {
		int newTotalSpaces = occupiedSpaces + faker.number().numberBetween(501, 1000);
		garage.setTotalSpaces(newTotalSpaces);
		
		assertNotEquals(totalSpaces, garage.getTotalSpaces());
		assertEquals(newTotalSpaces, garage.getTotalSpaces());
	}
	
	@Test
	public void testDecrementAvailableSpaces_NonFullGarage() {
		int availableSpaces = garage.getAvailableSpaces();
		garage.decrementAvailableSpaces();
		assertEquals(availableSpaces - 1, garage.getAvailableSpaces());
	}
	
	@Test
	public void testDecrementAvailableSpaces_FullGarage() {
		int availableSpaces = garage.getAvailableSpaces();
		garage.setTotalSpaces(availableSpaces);
		garage.decrementAvailableSpaces();
		
		String exceptionMessage = "Garage is full" + System.lineSeparator();
		assertEquals(exceptionMessage, outContext.toString());
		outContext.reset();
	}
	
	@Test
	public void testIncrementAvailableSpaces_NonEmptyGarage() {
		int availableSpaces = garage.getAvailableSpaces();
		garage.incrementAvailableSpaces();
		assertEquals(availableSpaces + 1, garage.getAvailableSpaces());
	}
	
	@Test
	public void testIncrementAvailableSpaces_EmptyGarage() throws IllegalAccessException, NoSuchFieldException {
		Field occupiedSpacesField = garage.getClass().getDeclaredField("occupiedSpaces");
		occupiedSpacesField.setAccessible(true);
		occupiedSpacesField.set(garage, 0);
		garage.incrementAvailableSpaces();
		
		String exceptionMessage = "No occupied spaces to free up" + System.lineSeparator();
		assertEquals(exceptionMessage, outContext.toString());
		outContext.reset();
	}
	
	@Test
	public void testLoad() {
		garage.save();
		Garage loadedGarage = Garage.load(id);
		assertEquals(loadedGarage, garage);
	}
	
	@Test
	public void testSave() {
		garage.save();
		
		DataLoader dataLoader = new DataLoader();
		JSONObject userFromDataLoader = dataLoader.getJSONObject("garages").getJSONObject(Integer.toString(id));
		assertEquals(userFromDataLoader.getString("name"), garageJson.getString("name"));
		assertEquals(userFromDataLoader.getString("address"), garageJson.getString("address"));
		assertEquals(userFromDataLoader.getInt("currentParkingFeeId"), garageJson.getInt("currentParkingFeeId"));
		assertEquals(userFromDataLoader.getInt("occupiedSpaces"), garageJson.getInt("occupiedSpaces"));
		assertEquals(userFromDataLoader.getInt("totalSpaces"), garageJson.getInt("totalSpaces"));
	}
	
	@Test
	public void testToString() {
		String expectedResult = "Garage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", totalSpaces=" + totalSpaces +
                ", occupiedSpaces=" + occupiedSpaces +
                ", currentParkingFee=" + currentParkingFee.toString() +
                '}';
		
		assertEquals(expectedResult, garage.toString());
	}
}