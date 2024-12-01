package ParkingGarageTest;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ParkingGarage.Fee;
import ParkingGarage.FeeType;
import ParkingGarage.Garage;

class GarageTest {

	@Test
    void testSave() {
        // Create a Garage object
        Garage garage = new Garage("Main Garage", "123 Main St", 50);
        garage.save(); // Save the garage

        // Load the Garage object back and verify
        Garage loadedGarage = Garage.load(garage.getId());
        assertNotNull(loadedGarage, "Loaded Garage should not be null");
        assertEquals(garage.getName(), loadedGarage.getName(), "Garage name should match");
        assertEquals(garage.getAddress(), loadedGarage.getAddress(), "Garage address should match");
        assertEquals(garage.getTotalSpaces(), loadedGarage.getTotalSpaces(), "Garage total spaces should match");
    }

	@Test
    void testGarage() {
        // Test valid Garage creation
        Garage garage = new Garage("Test Garage", "456 Test Blvd", 100);
        assertEquals("Test Garage", garage.getName(), "Garage name should match");
        assertEquals("456 Test Blvd", garage.getAddress(), "Garage address should match");
        assertEquals(100, garage.getTotalSpaces(), "Garage total spaces should match");

        // Test that initial occupied spaces are zero
        assertEquals(0, garage.getOccupiedSpaces(), "Occupied spaces should initially be zero");
    }

	@Test
    void testGetId() {
        Garage garage = new Garage("Garage A", "789 Garage Ave", 30);
        assertTrue(garage.getId() > 0, "Garage ID should be greater than 0");
    }

	@Test
    void testGetName() {
        Garage garage = new Garage("Garage B", "123 Garage St", 20);
        assertEquals("Garage B", garage.getName(), "Garage name should match");
    }
	@Test
    void testGetAddress() {
        Garage garage = new Garage("Garage C", "456 Garage Rd", 40);
        assertEquals("456 Garage Rd", garage.getAddress(), "Garage address should match");
    }

	@Test
    void testGetCurrentParkingFee() {
        Garage garage = new Garage("Garage D", "789 Garage Dr", 50);
        Fee fee = new Fee(FeeType.HOURLY, 10);
        garage.setParkingFee(fee);
        assertEquals(fee, garage.getCurrentParkingFee(), "Current parking fee should match the set fee");
    }

	@Test
    void testGetAvailableSpaces() {
        Garage garage = new Garage("Garage E", "123 Garage Ln", 100);
        assertEquals(100, garage.getAvailableSpaces(), "Available spaces should initially equal total spaces");
        garage.decrementAvailableSpaces();
        assertEquals(99, garage.getAvailableSpaces(), "Available spaces should decrement correctly");
    }
	@Test
    void testGetOccupiedSpaces() {
        Garage garage = new Garage("Garage F", "456 Garage Blvd", 80);
        assertEquals(0, garage.getOccupiedSpaces(), "Occupied spaces should initially be zero");
        garage.decrementAvailableSpaces();
        assertEquals(1, garage.getOccupiedSpaces(), "Occupied spaces should increment correctly");
    }

	@Test
    void testGetTotalSpaces() {
        Garage garage = new Garage("Garage G", "789 Garage Ct", 60);
        assertEquals(60, garage.getTotalSpaces(), "Total spaces should match the set value");
    }

	@Test
    void testSetName() {
        Garage garage = new Garage("Old Name", "123 Old St", 40);
        garage.setName("New Name");
        assertEquals("New Name", garage.getName(), "Garage name should update correctly");
    }

	@Test
    void testSetAddress() {
        Garage garage = new Garage("Garage H", "Old Address", 70);
        garage.setAddress("New Address");
        assertEquals("New Address", garage.getAddress(), "Garage address should update correctly");
    }

	@Test
    void testSetParkingFee() {
        Garage garage = new Garage("Garage I", "123 Garage St", 30);
        Fee fee = new Fee(FeeType.DAILY, 15);
        garage.setParkingFee(fee);
        assertEquals(fee, garage.getCurrentParkingFee(), "Parking fee should update correctly");
    }

	@Test
    void testSetTotalSpaces() {
        Garage garage = new Garage("Garage J", "456 Garage Blvd", 50);
        garage.setTotalSpaces(60);
        assertEquals(60, garage.getTotalSpaces(), "Total spaces should update correctly");
    }

	@Test
    void testDecrementAvailableSpaces() {
        Garage garage = new Garage("Garage K", "789 Garage Ave", 20);
        garage.decrementAvailableSpaces();
        assertEquals(19, garage.getAvailableSpaces(), "Available spaces should decrement correctly");
        assertEquals(1, garage.getOccupiedSpaces(), "Occupied spaces should increment correctly");
    }

	@Test
    void testIncrementAvailableSpaces() {
        Garage garage = new Garage("Garage L", "123 Garage Blvd", 15);
        garage.decrementAvailableSpaces();
        garage.incrementAvailableSpaces();
        assertEquals(15, garage.getAvailableSpaces(), "Available spaces should increment correctly");
        assertEquals(0, garage.getOccupiedSpaces(), "Occupied spaces should decrement correctly");
    }

	@Test
    void testLoadInt() {
        // Save and load a Garage by ID
        Garage garage = new Garage("Garage M", "456 Garage Dr", 40);
        garage.save();

        Garage loadedGarage = Garage.load(garage.getId());
        assertNotNull(loadedGarage, "Loaded Garage should not be null");
        assertEquals(garage.getName(), loadedGarage.getName(), "Garage name should match");
    }

	@Test
    void testLoadJSONObject() {
        // Save Garage to JSON and load it back
        Garage garage = new Garage("Garage N", "789 Garage Ln", 50);
        JSONObject json = new JSONObject();
        json.put("id", garage.getId());
        json.put("name", garage.getName());
        json.put("address", garage.getAddress());
        json.put("totalSpaces", garage.getTotalSpaces());
        json.put("occupiedSpaces", garage.getOccupiedSpaces());

        Garage loadedGarage = Garage.load(json);
        assertNotNull(loadedGarage, "Loaded Garage should not be null");
        assertEquals(garage.getName(), loadedGarage.getName(), "Garage name should match");
    }
	@Test
	void testToString() {
	    Garage garage = new Garage("Garage O", "123 Garage Ct", 25);
	    String garageString = garage.toString();
	    assertTrue(garageString.contains("id=" + garage.getId()), "String should contain garage ID");
	    assertTrue(garageString.contains("name='Garage O'"), "String should contain garage name");
	    assertTrue(garageString.contains("address='123 Garage Ct'"), "String should contain garage address");
	    assertTrue(garageString.contains("totalSpaces=25"), "String should contain total spaces");
	    assertTrue(garageString.contains("occupiedSpaces=0"), "String should contain occupied spaces");
	}

}
