package ParkingGarage;
import org.json.JSONObject;

public class User {
    // class attributes
    private int userId; // do we want a userID as well as a ticket ID?
    private String name;
    private String username;
    private String password;
    private RoleType role;
    private Garage assignedGarage;

    // constructor
    public User(String name, String username, String password, RoleType role, Garage assignedGarage) {
        this.userId = Integer.MIN_VALUE;  // TODO: Update to use auto incrementing number function
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedGarage = assignedGarage;
    }
    
    private User(int userId, String name, String username, String password, RoleType role, Garage assignedGarage) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedGarage = assignedGarage;
    }

    // Getter and Setter
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public RoleType getRole() {
        return role;
    }

    public Garage getDefaultGarage() {
        return assignedGarage;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public void setAssignedGarage(Garage garage) {
        this.assignedGarage = garage;
    }

    // authentication
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // load method
    public static User load(int userId) {
    	DataLoader dataLoader = new DataLoader();
    	JSONObject users = dataLoader.getJSONObject("users");
    	
    	JSONObject user = users.has(Integer.toString(userId))
    					? users.getJSONObject(Integer.toString(userId))
    					: null;
    	
    	if(users != null) {
    		return new User(userId,
    				user.getString("name"),
    				user.getString("username"),
    				user.getString("passwprd"),
    				RoleType.valueOf(user.getString("role")),
    				Garage.load(user.getJSONObject("assignedGarageId")));
    	} else {
    		return null;
    	}
    }

    public static User load(JSONObject object) {
        // parse JSON object to create user instance
        int userId = object.getInt("userId");
        String name = object.getString("name");
        String username = object.getString("username");
        String password = object.getString("password");
        RoleType role = RoleType.valueOf(object.getString("role"));
        Garage assignedGarage = Garage.load(object.getJSONObject("assignedGarageId"));

        return new User(userId, name, username, password, role, assignedGarage);
    }

    // save method
    public void save() {
    	JSONObject user = new JSONObject();
    	user.put("name", this.name);
    	user.put("username", this.username);
    	user.put("password", this.password);
        user.put("role", this.role.toString());
        user.put("assignedGarageId", this.assignedGarage.getId());
        
        DataLoader dataLoader = new DataLoader();
        dataLoader.getJSONObject("users").put(Integer.toString(this.userId), user);
        dataLoader.saveData();
    }


}
