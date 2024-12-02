package ParkingGarageTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import com.github.javafaker.*;

import ParkingGarage.*;

public class UserTest {
	private PrintStream originalOut = System.out;
	private ByteArrayOutputStream outContext = new ByteArrayOutputStream();
	
	private static Faker faker = new Faker();
	private static int userId = faker.number().numberBetween(1, 1000);
	private static String name = faker.name().fullName();
	private static String username = faker.name().username();
	private static String password = faker.internet().password();
	private static RoleType role = faker.options().option(RoleType.class);
	private static Garage assignedGarage = Factory.GarageFactory();
	private JSONObject userJson;
	private User user;
	
	@BeforeEach
	public void beforeEach() {
		System.setOut(new PrintStream(outContext));
		
		HashMap<String, Object> userValues = new HashMap<String, Object>();
		userValues.put("userId", userId);
		userValues.put("name", name);
		userValues.put("username", username);
		userValues.put("password", password);
		userValues.put("role", role);
		userValues.put("assignedGarage", assignedGarage);
		user = Factory.UserFactory(userValues);
		userJson = Factory.asJSONObject(userValues, Arrays.asList("userId"));
	}
	
	@AfterEach
	public void afterEach() {
		System.setOut(originalOut);
		
		DataLoader dataLoader = new DataLoader();
		dataLoader.getJSONObject("users").remove(Integer.toString(userId));
	}
	
	@Test
	public void testGetUserId() {
		assertNotNull(user.getId());
		assertTrue(user.getId() >= 1);
		assertEquals(userId, user.getId());
	}
	
	@Test
	public void testGetName() {
		assertNotNull(user.getName());
		assertEquals(name, user.getName());
	}
	
	@Test
	public void testGetUsername() {
		assertNotNull(user.getUsername());
		assertEquals(username, user.getUsername());
	}
	
	@Test
	public void testGetRole() {
		assertNotNull(user.getRole());
		assertEquals(role, user.getRole());
	}
	
	@Test
	public void testGetDefaultGarage() {
		assertNotNull(user.getDefaultGarage());
		assertEquals(assignedGarage, user.getDefaultGarage());
	}
	
	@Test
	public void testSetPassword() throws IllegalAccessException, NoSuchFieldException {
		String newPassword = faker.internet().password();
		user.setPassword(newPassword);
				
		Field passwordField = user.getClass().getDeclaredField("password");
		passwordField.setAccessible(true);
		String passwordFieldValue = passwordField.get(user).toString();
		
		assertNotEquals(password, passwordFieldValue);
		assertEquals(newPassword, passwordFieldValue);
	}
	
	@Test
	public void testSetRole() {
		RoleType newRole = RoleType.values()[(role.ordinal() + 1) % RoleType.values().length];
		user.setRole(newRole);
		
		assertNotEquals(role, user.getRole());
		assertEquals(newRole, user.getRole());
	}
	
	@Test
	public void testSetAssignedGarage() {
		Garage newAssignedGarage = Factory.GarageFactory();
		user.setAssignedGarage(newAssignedGarage);
		
		assertNotEquals(assignedGarage, user.getDefaultGarage());
		assertEquals(newAssignedGarage, user.getDefaultGarage());
	}
	
	@Test
	public void testAuthenticate_ValidCredentials() {
		Boolean auth = user.authenticate(username, password);
		assertTrue(auth);
	}
	
	@Test
	public void testAuthenticate_InvalidCredentails() {
		Boolean auth = user.authenticate(faker.name().username(), faker.internet().password());
		assertFalse(auth);
	}
	
	@Test
	public void testLoad() {
		user.save();
		User loadedUser = User.load(userId);
		assertEquals(loadedUser, user);
	}

	@Test
	public void testSave() {
		user.save();
		
		DataLoader dataLoader = new DataLoader();
		JSONObject userFromDataLoader = dataLoader.getJSONObject("users").getJSONObject(Integer.toString(userId));
		assertEquals(userFromDataLoader.getString("name"), userJson.getString("name"));
		assertEquals(userFromDataLoader.getString("username"), userJson.getString("username"));
		assertEquals(userFromDataLoader.getString("password"), userJson.getString("password"));
		assertEquals(userFromDataLoader.getString("role"), userJson.getString("role"));
		assertEquals(userFromDataLoader.getInt("assignedGarageId"), userJson.getInt("assignedGarageId"));
	}
}