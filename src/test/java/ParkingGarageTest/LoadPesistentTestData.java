package ParkingGarageTest;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import ParkingGarage.*;

public class LoadPesistentTestData {
  @Test
  public void loadPersistentData() {
    Garage garageA = Factory.GarageFactory();
    Garage garageB = Factory.GarageFactory();

    HashMap<String, Object> ticketValuesA = new HashMap<String, Object>();
    ticketValuesA.put("garage", garageA);

    HashMap<String, Object> ticketValuesB = new HashMap<String, Object>();
    ticketValuesB.put("garage", garageB);

    Ticket ticketA = Factory.TicketFactory(ticketValuesA);
    Ticket ticketB = Factory.TicketFactory(ticketValuesA);
    Ticket ticketC = Factory.TicketFactory(ticketValuesA);
    Ticket ticketD = Factory.TicketFactory(ticketValuesA);
    Ticket ticketE = Factory.TicketFactory(ticketValuesA);
    Ticket ticketF = Factory.TicketFactory(ticketValuesA);
    Ticket ticketG = Factory.TicketFactory(ticketValuesA);
    Ticket ticketH = Factory.TicketFactory(ticketValuesA);
    Ticket ticketI = Factory.TicketFactory(ticketValuesB);
    Ticket ticketJ = Factory.TicketFactory(ticketValuesB);
    Ticket ticketK = Factory.TicketFactory(ticketValuesB);
    Ticket ticketL = Factory.TicketFactory(ticketValuesB);
    Ticket ticketM = Factory.TicketFactory(ticketValuesB);
    Ticket ticketN = Factory.TicketFactory(ticketValuesB);
    Ticket ticketO = Factory.TicketFactory(ticketValuesB);
  }
}
