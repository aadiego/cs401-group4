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
		this.type = type;
		this.cost = cost;
	}
	
	// private constructor
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
			JSONObject feeJson = dataLoader.getJSONObject("Fee_" + id);
			return load(feeJson);
		} catch (Exception e) {
			System.err.println("Failed to load Fee with ID: " + id);
			e.printStackTrace();
			return null;
		}

	}
	
	// Static method to load a fee from a JSON object
	public static Fee load(JSONObject object) {
		int id = object.getInt("id");
		FeeType type = FeeType.valueOf(object.getString("type"));
		int  cost = object.getInt("cost");
		return new Fee(id, type, cost);
	}
	
	// Save fee data to DataLoader
	@Override
	public void save() {
		JSONObject feeJson = new JSONObject();
		feeJson.put("id", id);
		feeJson.put("type", type.name());
		feeJson.put("cost", cost);
		DataLoader dataLoader = new DataLoader();
		dataLoader.put("Fee_" + id, feeJson);
		dataLoader.saveData();
	}
	
	@Override
	public String toString() {
		return ("Fee{id=" + id + ", type=" + type + ", cost=" + cost + "}");
	}
}
