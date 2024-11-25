package ParkingGarage;
import org.json.JSONObject;

public class Garage {
	private int id;
	private String name;
	private String address;
	private Fee currentParkingFee;
	private int occupiedSpaces;
	private int totalSpaces;
	
	public Garage(String name, String address, int totalSpaces) {
		this.name = name;
		this.address = address;
		this.totalSpaces = totalSpaces;
	}
	
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
	
	public void setParkingFee(Fee parkingFee) {
		this.currentParkingFee = parkingFee;
	}
	
	public void setTotalSpaces(int totalSpaces) {
		this.totalSpaces = totalSpaces;
	}
	
	// Decrement available spaces
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
	
	// Load garage details from an ID
	public void load(int id) {
		
	}
	
	// Load garage from JSON
	public JSONObject save() {
		
	}
}
