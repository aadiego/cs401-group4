package ParkingGarage;

import org.json.JSONObject;

public class Fee {
	private int id;
	private FeeType type;
	private int cost;
	
	public Fee(FeeType type, int cost) {
		this.type = type;
		this.cost = cost;
	}
	
	private Fee(int id, FeeType type, int cost) {
		this.id = id;
		this.type = type;
		this.cost = cost;
	}
	
	public int getId() {
		return id;
	}
	
	public FeeType getType() {
		return type;
	}
	
	public int getCost() {
		return cost;
	}
	

	
	@Override
	public String toString() {
		return ("Fee{id=" + id + ", type=" + type + ", cost=" + cost + "}");
	}
}
