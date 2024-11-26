package ParkingGarage;
import org.json.JSONObject;

public class Garage extends DataLoaderable {
	private int id;
	private String name;
	private String address;
	private Fee currentParkingFee;
	private int occupiedSpaces;
	private int totalSpaces;
	
	// public constructor
	public Garage(String name, String address, int totalSpaces) {
		this.name = name;
		this.address = address;
		this.totalSpaces = totalSpaces;
	}
	
	// private constructor
	private Garage(int id, String name, String address, Fee currentParkingFee, int totalSpaces) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.currentParkingFee = currentParkingFee;
		this.totalSpaces = totalSpaces;
	}
	
	// Getters
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Fee getCurrentParkingFee() {
		return currentParkingFee;
	}
	
	public int getAvailableSpaces() {
		return totalSpaces - occupiedSpaces;
	}
	
	public int getOccupiedSpaces() {
		return occupiedSpaces;
	}
	
	public int getTotalSpaces() {
		return totalSpaces;
	}
	
	// Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setParkingFee(Fee fee) {
		this.currentParkingFee = fee;
	}
	
	public void setTotalSpaces(int totalSpaces) {
		this.totalSpaces = totalSpaces;
	}
	
	// update occupied spaces
	public void decrementAvailableSpaces() {
		if (occupiedSpaces < totalSpaces) {
			occupiedSpaces++;
		} else {
			System.out.println("Garage is full");
		}
	}
	
	// Increment available spaces
	public void incrementAvailableSpaces() {
		if (occupiedSpaces > 0) {
			occupiedSpaces--;
		} else {
			System.out.println("No occupied spaces to free up");
		}
	}
	
	// static method to load garage by ID
	public static Garage load(int id) {
		DataLoader dataLoader = new DataLoader();
		JSONObject garageJson = dataLoader.getJSONObject("Garage_" + id);
		return load(garageJson);
	}
	
	// static method to load garage from JSON object
	public static Garage load(JSONObject object) {
		try {
			int id = object.getInt("id");
			String name = object.getString("name");
			String address = object.getString("address");
			Fee fee = Fee.load(object.getJSONObject("currentParkingFee"));
			int totalSpaces = object.getInt("totalSpaces");
			return new Garage(id, name, address, fee, totalSpaces);
		} catch (Exception e) {
			System.err.println("Error loading Garage from JSON: " + e.getMessage());
			return null;
		}
	}
	
	// Save garage data to DataLoader
    @Override
    public void save() {
        JSONObject garageJson = new JSONObject();
        garageJson.put("id", id);
        garageJson.put("name", name);
        garageJson.put("address", address);
        garageJson.put("totalSpaces", totalSpaces);
        garageJson.put("occupiedSpaces", occupiedSpaces);
        garageJson.put("currentParkingFee", currentParkingFee);
        DataLoader dataLoader = new DataLoader();
        dataLoader.put("Garage_" + id, garageJson);
        dataLoader.saveData();
    }
}
