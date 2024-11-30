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
		this.id = DataLoader.getNextId("garages");
		this.name = name;
		this.address = address;
		this.totalSpaces = totalSpaces;
		this.occupiedSpaces = 0;
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
	
	public void setParkingFee(Fee parkingFee) {
		this.currentParkingFee = parkingFee;
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
		try {
			DataLoader dataLoader = new DataLoader();
			JSONObject garages = dataLoader.getJSONObject("garages");
			
			if (garages.has(Integer.toString(id))) {
				JSONObject garageJson = garages.getJSONObject(Integer.toString(id));
				
				// Extract Garage Fields
				String name = garageJson.getString("name");
				String address = garageJson.getString("address");
				int totalSpaces = garageJson.getInt("totalSpaces");
				int occupiedSpaces = garageJson.getInt("occupiedSpaces");
				
				Fee parkingFee = null;
				if (garageJson.has("currentParkingFee")) {
					parkingFee = Fee.load(garageJson.getJSONObject("currentParkingFee"));
				}
				
				Garage garage = new Garage(id, name, address, parkingFee, totalSpaces);
				garage.occupiedSpaces = occupiedSpaces;
				return garage;
			} else {
				System.err.println("Garage with ID " + id + " not found.");
			}
		} catch (Exception e) {
			System.err.println("Error loading Garage with ID " + id + ": " + e.getMessage());
		}
		return null;
	}

	// static method to load garage from JSON object
	public static Garage load(JSONObject object) {
		try {
			int garageId = object.getInt("id");
			String name = object.getString("name");
			String address = object.getString("address");
			int totalSpaces = object.getInt("totalSpaces");
			int occupiedSpaces = object.getInt("occupiedSpaces");
			
			Fee parkingFee = null;
			if (object.has("currentParkingFee")) {
				parkingFee = Fee.load(object.getJSONObject("currentParkingFee"));
			}
			Garage garage = new Garage(garageId, name, address, parkingFee, totalSpaces);
			garage.occupiedSpaces = occupiedSpaces;
			return garage;
		} catch (Exception e) {
			System.err.println("Unexpected error loading Garage: " + e.getMessage());
		}
		return null;
	}

    // Save to JSON
    @Override
    public void save() {
    	try {
    		JSONObject garage = new JSONObject();
    		garage.put("id", this.id);
    		garage.put("name", this.name);
    		garage.put("address", this.address);
    		garage.put("totalSpaces", this.totalSpaces);
    		garage.put("occupiedSpaces", this.occupiedSpaces);
    		
    		if (this.currentParkingFee != null) {
    			JSONObject parkingFee = new JSONObject();
    			parkingFee.put("id", this.currentParkingFee.getId());
    			parkingFee.put("type", this.currentParkingFee.getType().name());
    			parkingFee.put("cost", this.currentParkingFee.getCost());
    			garage.put("currentParkingFee", parkingFee);
    		}
    		
    		DataLoader dataLoader = new DataLoader();
    		dataLoader.getJSONObject("garages").put(Integer.toString(this.id), garage);
    		dataLoader.saveData();
    		
    	} catch (Exception e) {
    		System.err.println("Error saving Garage with ID " + this.id + ": " + e.getMessage());
    	}
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
