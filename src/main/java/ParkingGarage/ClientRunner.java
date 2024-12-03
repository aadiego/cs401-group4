package ParkingGarage;

import java.util.*;

public class ClientRunner {
	public static void main(String[] args) {
		Map<String, Object> parsedArgs = parseArgs(args);
		String host = (String) parsedArgs.get("host");
		int port = (int) parsedArgs.get("port");
		
		switch((String) parsedArgs.get("guimode")) {
			case "customer":
				CustomerGUI.run(host, port);
				break;
			case "employee":
				EmployeeGUI.run(host, port);
				break;
			case "cmdline":
				new CommandLineClient(host, port);
				break;
		}
	}

	public static Map<String, Object> parseArgs(String[] args) {
		Map<String, Object> parsedArgs = new HashMap<String, Object>();

		// Default values
		parsedArgs.put("host", "localhost");
		parsedArgs.put("port", 12345);
		parsedArgs.put("guimode", "employee");

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "--host":
					parsedArgs.put("host", args[i + 1]);
					i++;
					break;
				case "-p":
				case "--port":
					parsedArgs.put("port", Integer.parseInt(args[i + 1]));
					i++;
					break;
				case "-g":
				case "--guimode":
					String guimode = args[i + 1];
					if (guimode != "customer" || guimode != "employee" || guimode != "cmdline") {
						System.out.println("Invalid argument value: " + guimode);
						System.out.println("Usage: java ClientRunner [-h|--host <hostname>] [-p|--port <port>] [-g|--guimode customer|employee|cmdline]");
						System.exit(1);
					}
					
					parsedArgs.put("guimode", args[i + 1]);
					i++;
					break;
				default:
					System.out.println("Invalid argument: " + args[i]);
					System.out.println("Usage: java ClientRunner [-h|--host <hostname>] [-p|--port <port>] [-g|--guimode customer|employee|cmdline]");
					System.exit(1);
			}
		}
		return parsedArgs;
	}
}