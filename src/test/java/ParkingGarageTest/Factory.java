package ParkingGarageTest;

import java.lang.reflect.Field;
import java.util.HashMap;
import com.github.javafaker.*;

import ParkingGarage.*;

public class Factory {
	private static Faker faker = new Faker();
	
	public static Fee FeeFactory() {
		int id = faker.number().numberBetween(1, 1000);
		FeeType feeType = faker.options().option(FeeType.class);
		int cost = faker.number().numberBetween(100, 500);
		
		Fee fee = new Fee(feeType, cost);
		try {
			Field idField = fee.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(fee, id);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		fee.save();
		return fee;
	}
	
	public static Fee FeeFactory(HashMap<String, Object> fee) {
		return merge(FeeFactory(), fee);
	}
	
	public static Garage GarageFactory() {
		int id = faker.number().numberBetween(1, 1000);
		String name = faker.company().name();
		String address = faker.address().fullAddress();
		Fee currentParkingFee = FeeFactory();
		int occupiedSpaces = faker.number().numberBetween(1, 500);
		int totalSpaces = faker.number().numberBetween(1, 500);
		
		Garage garage = new Garage(name, address, totalSpaces);
		try {
			Field idField = garage.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(garage, id);
			
			Field occupiedSpacesField = garage.getClass().getDeclaredField("occupiedSpaces");
			occupiedSpacesField.setAccessible(true);
			occupiedSpacesField.set(garage, occupiedSpaces);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		garage.setParkingFee(currentParkingFee);
		garage.save();
		return garage;
	}
	
	public static Garage GarageFactory(HashMap<String, Object> garage) {
		return merge(GarageFactory(), garage);
	}
	
	public static User UserFactory() {
		int userId = faker.number().numberBetween(1, 1000);
		String name = faker.name().fullName();
		String username = faker.name().username();
		String password = faker.internet().password();
		RoleType role = faker.options().option(RoleType.class);
		Garage assignedGarage = GarageFactory();
		
		User user = new User(name, username, password, role, assignedGarage);
		try {
			Field userIdField = user.getClass().getDeclaredField("userId");
			userIdField.setAccessible(true);
			userIdField.set(user, userId);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		user.save();
		return user;
	}
	
	public static User UserFactory(HashMap<String, Object> user) {
		return merge(UserFactory(), user);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T merge(T local, HashMap<String, Object> remote) {
		try {
		    Class<?> clazz = local.getClass();
		    Object merged = clazz.getDeclaredConstructor().newInstance();
		    for (Field field : clazz.getDeclaredFields()) {
		    	field.setAccessible(true);
		        Object localValue = field.get(local);
		        Object remoteValue = remote.get(field.getName());
		        if (localValue != null) {
		        	field.set(merged, (remoteValue != null) ? remoteValue : localValue);
		        }
		    }
		    return (T) merged;
		} catch (Exception ex) {
			return local;
		}
	}
}