package ParkingGarageTest;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ParkingGarage.Fee;
import ParkingGarage.FeeType;

class FeeTest {

	@Test
    void testSave() {
        // Create a Fee object
        Fee fee = new Fee(FeeType.HOURLY, 15);
        fee.save(); // Save the fee

        // Load the Fee object back and verify
        Fee loadedFee = Fee.load(fee.getId());
        assertNotNull(loadedFee, "Loaded Fee should not be null");
        assertEquals(fee.getType(), loadedFee.getType(), "Fee type should match");
        assertEquals(fee.getCost(), loadedFee.getCost(), "Fee cost should match");
    }

	@Test
    void testFee() {
        // Test valid Fee creation
        Fee fee = new Fee(FeeType.DAILY, 25);
        assertEquals(FeeType.DAILY, fee.getType(), "Fee type should be DAILY");
        assertEquals(25, fee.getCost(), "Fee cost should be 25");

        // Test invalid Fee creation (negative cost)
        assertThrows(IllegalArgumentException.class, () -> {
            new Fee(FeeType.HOURLY, -10);
        }, "Fee with negative cost should throw IllegalArgumentException");
    }

	@Test
    void testGetId() {
        Fee fee = new Fee(FeeType.HOURLY, 20);
        assertTrue(fee.getId() > 0, "Fee ID should be greater than 0");
    }

	@Test
    void testGetType() {
        Fee fee = new Fee(FeeType.DAILY, 30);
        assertEquals(FeeType.DAILY, fee.getType(), "Fee type should be DAILY");
    }

	@Test
    void testGetCost() {
        Fee fee = new Fee(FeeType.HOURLY, 40);
        assertEquals(40, fee.getCost(), "Fee cost should be 40");
    }

	@Test
    void testLoadInt() {
        // Create and save a Fee
        Fee fee = new Fee(FeeType.HOURLY, 50);
        fee.save();

        // Load Fee by ID
        Fee loadedFee = Fee.load(fee.getId());
        assertNotNull(loadedFee, "Loaded Fee should not be null");
        assertEquals(fee.getType(), loadedFee.getType(), "Fee type should match");
        assertEquals(fee.getCost(), loadedFee.getCost(), "Fee cost should match");
    }

	@Test
    void testLoadJSONObject() {
        // Create a Fee and save it
        Fee fee = new Fee(FeeType.DAILY, 60);
        JSONObject feeJson = new JSONObject();
        feeJson.put("id", fee.getId());
        feeJson.put("type", fee.getType().name());
        feeJson.put("cost", fee.getCost());

        // Load Fee from JSON
        Fee loadedFee = Fee.load(feeJson);
        assertNotNull(loadedFee, "Loaded Fee should not be null");
        assertEquals(fee.getType(), loadedFee.getType(), "Fee type should match");
        assertEquals(fee.getCost(), loadedFee.getCost(), "Fee cost should match");
    }

	@Test
    void testToString() {
        Fee fee = new Fee(FeeType.HOURLY, 70);
        String feeString = fee.toString();
        assertTrue(feeString.contains("id=" + fee.getId()), "Fee string should contain ID");
        assertTrue(feeString.contains("type=" + fee.getType().name()), "Fee string should contain type");
        assertTrue(feeString.contains("cost=" + fee.getCost()), "Fee string should contain cost");
    }

}
