package ParkingGarage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class ClientHandlerFacade {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private User userContext;
	
	public ClientHandlerFacade(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	
	public void handleLogin(Message message) throws IOException {
		String username = (String) message.getData("username");
		String password = (String) message.getData("password");
		
		try {
			User user = User.authenticate(username, password);
			message.setData("__status__", MessageStatus.SUCCESS);
			message.setData("userId", user.getId());
			message.setData("role", user.getRole());
			message.setData("defaultGarage", user.getDefaultGarage());
			userContext = user;
		} catch (Exception ex) {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", ex.getMessage());
		}
		out.writeObject(message);
		out.flush();
	}
	
	public void handleCreateUser(Message message) throws IOException {
		if (userContext != null && userContext.getRole() == RoleType.ADMIN) {
			String name = (String) message.getData("name");
			String username = (String) message.getData("username");
			String password = (String) message.getData("password");
			RoleType role = (RoleType) message.getData("role");
			Garage assignedGarage = Garage.load((int) message.getData("assignedGarageId"));
			
			try {
				User newUser = new User(name, username, password, role, assignedGarage);
				message.setData("__status__", MessageStatus.SUCCESS);
				message.setData("userId", newUser.getId());
			} catch (Exception ex) {
				message.setData("__status__", MessageStatus.FAILURE);
				message.setData("message", ex.getMessage());
			}
		} else {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", "Unauthorized");
		}
		out.writeObject(message);
		out.flush();
		
	}

	public void handleEnterGarage(Message message) throws IOException {
		try {
			Garage garage = Garage.load((int) message.getData("garageId"));
			Ticket ticket = new Ticket(garage);
			ticket.save();
			
			message.setData("__status__", MessageStatus.SUCCESS);
			message.setData("ticketId", ticket.getId());
			message.setData("feeType", ticket.getFee().getType());
			message.setData("feeCost", ticket.getFee().getCost());
			message.setData("entryDateTime", ticket.getEntryDateTime());
		} catch (Exception ex) {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", ex.getMessage());
		}
		out.writeObject(message);
		out.flush();
	}
	
	public void handleExitGarage(Message message) throws IOException {
		try {
			Ticket ticket = Ticket.load((int) message.getData("ticketId"));
			
			if (ticket == null) {
				throw new Exception("Ticket not found");
			}
			
			Payment payment = Payment.load((int) message.getData("paymentId"));
			
			if (payment == null) {
				throw new Exception("Payment not found");
			}
			
			LocalDateTime now = LocalDateTime.now();
			ticket.setExitTime(now);
			ticket.setPayment(payment);
			message.setData("__status__", MessageStatus.SUCCESS);
			message.setData("exitDateTime", now);
		} catch (Exception ex) {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", ex.getMessage());
		}
		out.writeObject(message);
		out.flush();
	}
	
	public void handleCheckTicket(Message message) throws IOException {
		try {
			Ticket ticket = Ticket.load((int) message.getData("ticketId"));
			
			if (ticket == null) {
				throw new Exception("Ticket not found");
			}
		
			message.setData("__status__", MessageStatus.SUCCESS);
			message.setData("feeType", ticket.getFee().getType());
			message.setData("feeCost", ticket.getFee().getCost());
			message.setData("entryDateTime", ticket.getEntryDateTime());
			message.setData("calculatedFee", ticket.calculateFee());
		} catch (Exception ex) {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", ex.getMessage());
		}
		out.writeObject(message);
		out.flush();
	}
	
	public void handlePayment(Message message) throws IOException {
		PaymentMethod paymentMethod = (PaymentMethod) message.getData("paymentMethod");
		int value = (int) message.getData("value");
		
		try {
			Payment payment;
			if (userContext == null) {
				payment = new Payment(paymentMethod, value);
			} else {
				payment = new Payment(userContext, paymentMethod, value);
				message.setData("capturedByName", payment.getCapturedBy().getName());
			}
			message.setData("__status__", MessageStatus.SUCCESS);
			message.setData("paymentId", payment.getId());
			message.setData("capturedDateTime", payment.getCapturedDateTime());
		} catch (Exception ex) {
			message.setData("__status__", MessageStatus.FAILURE);
			message.setData("message", ex.getMessage());
		}
		out.writeObject(message);
		out.flush();
	}

	public void handleReport(Message message) {
		
		
	}

	public void handleLogout(Message message) throws IOException {
		userContext = null;
		message.setData("__status__", MessageStatus.SUCCESS);
		out.writeObject(message);
		out.flush();
	}

	public void handleQuit(Message message) throws IOException {
		socket.close();
		socket = null;
		in = null;
		out = null;
	}
 }