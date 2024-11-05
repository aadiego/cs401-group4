import org.json.JSONObject;

public class DataLoaderExample extends DataLoaderable {
	private static int nextId = 1;
	private int id;
	private String testString = null;
	private int testInt;
	private DataLoaderExample testObject;
	
	public DataLoaderExample() {
		this.id = nextId++;
		this.testString = "Default";
		this.testInt = 0;
		// Obviously don't do this in a real project as it will recusively create instances, but this is just for example purposes.
		this.testObject = new DataLoaderExample();
	}
	
	// Private constructor that needs to set all attributes, this is only used by load methods
	private DataLoaderExample(String testString, int testInt, DataLoaderExample testObject) {
		this.testString = testString;
		this.testInt = testInt;
		this.testObject = testObject;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static DataLoaderExample load(int id) {
		// Create new instance of DataLoader class
		DataLoader dataLoader = new DataLoader();
		
		// Get the parent node for the object. This should be the class name.
		// For example, in this project, "garage", "ticket", "payment", etc.
		JSONObject dataLoaderExample = dataLoader.getJSONObject("dataLoaderExample");
		
		// Get the data, if it exists.
		JSONObject data = dataLoaderExample.has(Integer.toString(id)) ? dataLoader.getJSONObject(Integer.toString(id)) : null;
		
		// If there is data, create and return the object using the private constructor or return
		// a null object.
		if (data != null) {
			return new DataLoaderExample(data.getString("testString"), data.getInt("testInt"), DataLoaderExample.load(data.getInt("testObject_id")));
		} else {
			return null;
		}
	}
	
	public static DataLoaderExample load(JSONObject object) {
		// Create and return the object using the private constructor
		return new DataLoaderExample(object.getString("testString"), object.getInt("testInt"), DataLoaderExample.load(object.getInt("testObject_id")));
	}

	@Override
	public void save() {
		// Save any dependencies first.
		this.testObject.save();
		
		// Create the JSONObject
		JSONObject dataLoaderExample = new JSONObject();
		dataLoaderExample.put("testString", this.testString);
		dataLoaderExample.put("testInt", this.testInt);
		dataLoaderExample.put("testObject_id", this.testObject.getId());
		
		// Create new instance of DataLoader class
		DataLoader dataLoader = new DataLoader();
		
		// Put the data into the parent node.
		dataLoader.getJSONObject("dataLoaderExample").put(Integer.toString(this.id), dataLoaderExample);
		
		// Call saveData to save the data to the JSON file.
		dataLoader.saveData();
	}
}

