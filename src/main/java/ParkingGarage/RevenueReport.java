package ParkingGarage;

import java.util.ArrayList;
import java.util.List;

public class RevenueReport implements ReportInterface {
    private String garageId;
    private String startDate;
    private String endDate;
    private List<String> filters = new ArrayList<>();

    @Override
    public List<String> getFilters() {
        return filters;
    }

    @Override
    public <T> void setFilters(String name, T value) {
        if ("garageId".equals(name)) {
            garageId = (String) value;
            filters.add("Garage ID: " + value);
        } else if ("startDate".equals(name)) {
            startDate = (String) value;
            filters.add("Start Date: " + value);
        } else if ("endDate".equals(name)) {
            endDate = (String) value;
            filters.add("End Date: " + value);
        }
    }

    @Override
    public void runReport(Message message) {
        System.out.println("Generating Revenue Report...");
        System.out.println("Garage ID: " + garageId);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        // Optionally print the filters applied
        System.out.println("\nFilters applied: ");
        for (String filter : filters) {
            System.out.println(filter);
        }
    }
}
