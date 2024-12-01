package PGMSReportPKG;

import java.util.List;

public interface ReportInterface {
	public List<String> getFilters();
	
	public <T> void setFilters(String name, T value);
	
	public void runReport();
	
	

}