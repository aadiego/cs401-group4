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
    public void runReport() {
        if (garage != null) {
            System.out.println("Garage Name: " + garage.getName());
            System.out.println("Total Spaces: " + garage.getTotalSpaces());
            System.out.println("Occupied Spaces: " + garage.getOccupiedSpaces());
            System.out.println("Available Spaces: " + garage.getAvailableSpaces());
        } else {
            System.out.println("Garage is null");
        }
    }		
}
	
