package ParkingGarage;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.json.*;

public class DataLoader {
	// Attributes
	private static JSONObject data = null;
	private static String sourceName = "application_data.json";
	private static boolean modified = false;

	// Constructor
	public DataLoader() {
		if (data == null) {
			loadData();
		}
	}
	
	public static int getNextId(String name) {
		DataLoader dataLoader = new DataLoader();
		
		int nextId;
		if(data.has(name)) {
			nextId = data
					.getJSONObject(name)
					.increment("autoIncrement")
					.getInt("autoIncrement");
		} else {
			nextId = 1;
			JSONObject object = new JSONObject();
			object.put("autoIncrement", nextId);
			data.put(name, object);
		}
		
		dataLoader.saveData();
		return nextId;
	}

	// Load data from file
	private void loadData() {
		// Open the file and read the JSON Object data from the file
		File file = new File(sourceName);

		// Check if the file exists and is a file
		if (file.exists() && file.isFile()) {
			try {
				// Create a scanner to read the file line-by-line.
				Scanner scanner = new Scanner(file);
				String rawJsonString = "";

				while (scanner.hasNextLine()) {
					// Grab the next line of data from the file
					rawJsonString += scanner.nextLine();
				}
				
				if (rawJsonString.equals("")) {
					rawJsonString = "{}";
				}

				// Close the scanner, set the modified flag to false, and return the raw JSON
				// string
				scanner.close();
				modified = false;
				data = new JSONObject(rawJsonString);
				return;
			} catch (JSONException e) {
				System.out.println("Error parsing JSON data from file: " + sourceName);
			} catch (Exception e) {
				System.out.println("File not found: " + sourceName);
			}
		} else {
			// Else create a new file
			try {
				file.createNewFile();
			} catch (Exception e) {
				System.out.println("Error creating file: " + sourceName);
			}
		}
		modified = false;
		data = new JSONObject();
	}

	// Save data to file
	public void saveData() {
		// Write the JSON Object data to the file
		if (modified) {
			try {
				// Create a new file writer
				FileWriter fileWriter = new FileWriter(sourceName);
				data.write(fileWriter, 2, 0);
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("Error saving data to file: " + sourceName);
			}
		}
	}

	// Data getters
	public JSONObject getJSONObject(String key) {
		try {
			return data.getJSONObject(key);
		} catch (JSONException e) {
			put(key, new JSONObject());
			return data.getJSONObject(key);
		}
	}

	public JSONArray getJSONArray(String key) {
		try {
			return data.getJSONArray(key);
		} catch (JSONException e) {
			put(key, new JSONArray());
			return data.getJSONArray(key);
		}
	}

	// Data setters
	public <T> JSONObject put(String key, T value) {
		modified = true;
		return data.put(key, value);
	}
}
