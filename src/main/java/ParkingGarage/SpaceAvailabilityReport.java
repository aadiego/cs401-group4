package ParkingGarage;

import java.util.ArrayList;
import java.util.List;

public class SpaceAvailabilityReport implements ReportInterface {
    private Garage garage;
    private List<String> filters = new ArrayList<>();

    @Override
    public List<String> getFilters() {
        return filters;
    }

    @Override
    public <T> void setFilters(String name, T value) {
        if ("garage".equals(name) && value instanceof Garage) {
            garage = (Garage) value;
        } else {
            throw new IllegalArgumentException("Invalid filter/name");
        }
    }

    @Override
    public void runReport(Message message) {
        if (garage != null) {
        	message.setData("__status__", MessageStatus.SUCCESS);
        	message.setData("garageName", garage.getName());
        	message.setData("totalSpaces", garage.getTotalSpaces());
        	message.setData("occupiedSpaces", garage.getOccupiedSpaces());
        	message.setData("availableSpaces", garage.getAvailableSpaces());
        } else {
        	message.setData("__status__", MessageStatus.FAILURE);
            message.setData("message", "Invalid garage ID");
        }
    }		
}
	
