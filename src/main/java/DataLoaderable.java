import org.json.JSONObject;

public abstract class DataLoaderable {
	public static <T> T load(int id) throws NotImplementedException {
		throw new NotImplementedException();
	}
	
	public static <T> T load(JSONObject object) throws NotImplementedException {
		throw new NotImplementedException();
	}
	
	public void save() throws NotImplementedException {
		throw new NotImplementedException();
	}
}