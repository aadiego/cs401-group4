package ParkingGarage;
import org.json.JSONObject;

public class Garage extends DataLoaderable {
	private int id; // unique identifier
	private String name; // Name of the garage
	private String address; // Address of the garage
	private Fee currentParkingFee; // Current parking fee (of type fee).
	private int occupiedSpaces; // Number of occupied spaces
	private int totalSpaces; // Total number of parking spaces
	
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
	
	// Increment the occupiedSpaces by 1.
	public void decrementAvailableSpaces() {
		if (occupiedSpaces < totalSpaces) {
			occupiedSpaces++;
		} else {
			System.out.println("Garage is full");
		}
	}
	
	// Decrement the occupiedSpaces by 1
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
    
    @Override
    public String toString() {
        return "Garage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", totalSpaces=" + totalSpaces +
                ", occupiedSpaces=" + occupiedSpaces +
                ", currentParkingFee=" + (currentParkingFee != null ? currentParkingFee.toString() : "None") +
                '}';
    }
}
