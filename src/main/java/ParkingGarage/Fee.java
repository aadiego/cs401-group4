package ParkingGarage;

import org.json.JSONObject;

public class Fee extends DataLoaderable {
	private int id; // fee id
	private FeeType type; // type of fee (FeeType)
	private int cost; // cost of the fee
	
	// public constructor
	public Fee(FeeType type, int cost) {
	    if (cost < 0) {
	        throw new IllegalArgumentException("Cost cannot be negative.");
	    }
	    this.id = DataLoader.getNextId("fees"); // Automatically assigns a unique ID.
	    this.type = type;
	    this.cost = cost;
	}
	
	// private constructor for dataLoader
	private Fee(int id, FeeType type, int cost) {
		this.id = id;
		this.type = type;
		this.cost = cost;
	}
	
	
	// Getters
	public int getId() {
		return id;
	}
	
	public FeeType getType() {
		return type;
	}
	
	public int getCost() {
		return cost;
	}
	
	// Static method to load a fee by ID
	public static Fee load(int id) {
		try {
			DataLoader dataLoader = new DataLoader();
			JSONObject fees = dataLoader.getJSONObject("fees");
			
			if(fees.has(Integer.toString(id))) {
				JSONObject fee = fees.getJSONObject(Integer.toString(id));
				return new Fee(id, FeeType.valueOf(fee.getString("type")), fee.getInt("cost"));
			} else {
				System.err.println("Fee with ID " + id + "not found.");
			}
		} catch (Exception e) {
			System.err.println("Error loading Fee with ID " + id + ": " + e.getMessage());
		}
		return null; // Return null if loading fails
	}
	
	// Static method to load a fee from a JSON object
	public static Fee load(JSONObject object) {
		try {
			int feeId = object.getInt("id");
			FeeType type = FeeType.valueOf(object.getString("type").toUpperCase());
			int cost = object.getInt("cost");			
			return new Fee(feeId, type, cost);
		} catch (Exception e) {
			System.err.println("Unexpected error loading Fee: " + e.getMessage());
		}
		return null;
	}
	
	// Save to JSON
	@Override
	public void save() {
		try {
			JSONObject fee = new JSONObject();
			fee.put("type", this.type.name());
			fee.put("cost", this.cost);
			
			DataLoader dataLoader = new DataLoader();
			dataLoader.getJSONObject("fees").put(Integer.toString(this.id), fee);
			dataLoader.saveData();
		} catch (Exception e) {
			System.err.println("Error saving Fee with ID " + this.id + ": " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return ("Fee{id=" + id + ", type=" + type + ", cost=" + cost + "}");
	}
	
    // Overrides for testing
    public Fee() {}
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Fee other = (Fee) obj;
        return this.id == other.id &&
               this.type.equals(other.type) &&
               this.cost == other.cost;
    }
}
