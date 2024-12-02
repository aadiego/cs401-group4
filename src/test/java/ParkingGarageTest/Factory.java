package ParkingGarageTest;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.github.javafaker.*;
import org.json.*;

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
		int totalSpaces = occupiedSpaces + faker.number().numberBetween(1, 500);
		
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
		return garage;
	}
	
	public static Garage GarageFactory(HashMap<String, Object> garage) {
		return merge(GarageFactory(), garage);
	}
	
	public static Payment PaymentFactory() {
		int paymentId = faker.number().numberBetween(1, 1000);

		Instant instant = faker.date().between(new Date(1730444400), new Date()).toInstant();
		LocalDateTime capturedDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		
		User capturedBy = UserFactory();
		PaymentMethod paymentMethod = faker.options().option(PaymentMethod.class);
		int value = faker.number().numberBetween(100, 500);
		
		Payment payment = new Payment(capturedBy, paymentMethod, value);
		try {
			Field paymentIdField = payment.getClass().getDeclaredField("userId");
			paymentIdField.setAccessible(true);
			paymentIdField.set(payment, paymentId);
			
			Field capturedDateTimeField = payment.getClass().getDeclaredField("capturedDateTime");
			capturedDateTimeField.setAccessible(true);
			capturedDateTimeField.set(payment, capturedDateTime);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return payment;
	}
	
	public static Payment PaymentFactory(HashMap<String, Object> payment) {
		return merge(PaymentFactory(), payment);
	}
	
	public static Ticket TicketFactory() {
		int id = faker.number().numberBetween(1, 1000);
		Garage garage = GarageFactory();
		Fee ticketFee = garage.getCurrentParkingFee();
		
		Instant instant = faker.date().between(new Date(1730444400), new Date()).toInstant();
		LocalDateTime entryDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		
		LocalDateTime exitDateTime = null;
		Payment payment = null;
		Boolean hasExited = faker.random().nextInt(1, 100) <= 25; // 25% chance the ticket has an exitDateTime and payment.
		if (hasExited) {
			int timeDiffMins = faker.number().numberBetween(60, 180);
			exitDateTime = entryDateTime.plusMinutes(timeDiffMins);
			
			int cost = ticketFee.getType() == FeeType.DAILY ? ticketFee.getCost() : ticketFee.getCost() * (int) Math.ceil(timeDiffMins / 60);

			HashMap<String, Object> paymentValues = new HashMap<String, Object>();
			paymentValues.put("capturedDateTime", exitDateTime);
			paymentValues.put("cost", cost);
			payment = PaymentFactory(paymentValues);
		}
		
		Ticket ticket = new Ticket(garage);
		try {
			Field idField = ticket.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(ticket, id);
			
			Field entryDateTimeField = ticket.getClass().getDeclaredField("entryDateTime");
			entryDateTimeField.setAccessible(true);
			entryDateTimeField.set(ticket, entryDateTime);
			
			Field exitDateTimeField = ticket.getClass().getDeclaredField("exitDateTime");
			exitDateTimeField.setAccessible(true);
			exitDateTimeField.set(ticket, exitDateTime);
			
			Field paymentField = ticket.getClass().getDeclaredField("payment");
			paymentField.setAccessible(true);
			paymentField.set(ticket, payment);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return ticket;
	}
	
	public static Ticket TicketFactory(HashMap<String, Object> ticket) {
		return merge(TicketFactory(), ticket) ;
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
	
	public static JSONObject asJSONObject(HashMap<String, Object> hashMap, List<String> excludeKeys) {
		JSONObject jsonObject = new JSONObject();
		hashMap.forEach((key, value) -> {
			if(!excludeKeys.contains(key)) {
				if ((value instanceof String) || (value instanceof Integer)) {
					jsonObject.put(key, value);
				} else if (value instanceof Fee) {
					jsonObject.put(key.concat("Id"), ((Fee) value).getId());
				} else if (value instanceof Garage) {
					jsonObject.put(key.concat("Id"), ((Garage) value).getId());
				} else if (value instanceof Payment) {
					jsonObject.put(key.concat("Id"), ((Payment) value).getId());
				} else if (value instanceof Ticket) {
					jsonObject.put(key.concat("Id"), ((Ticket) value).getId());
				} else if (value instanceof User) {
					jsonObject.put(key.concat("Id"), ((User) value).getId());
				} else {
					jsonObject.put(key, value.toString());
				}
			}
		});
		return jsonObject;
	}
}