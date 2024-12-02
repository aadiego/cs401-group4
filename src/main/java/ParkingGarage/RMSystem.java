package ParkingGarage;

public class RMSystem {

    // Generate a Space Availability Report
    public void generateSpaceAvailabilityReport(Garage garage) {
        SpaceAvailabilityReport report = new SpaceAvailabilityReport();
        report.setFilters("garage", garage);  
        report.runReport();  
    }

    // Generate a Revenue Report
    public void generateRevenueReport(String garageID, String startDate, String endDate) {
        RevenueReport report = new RevenueReport();
        report.setFilters("garageId", garageID);  
        report.setFilters("startDate", startDate);  
        report.setFilters("endDate", endDate);  
        report.runReport();  
    }
}


