package ParkingGarageTest;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class JUnitTestRunner {
    public static void main(String[] args) {
        // Build a discovery request for the specified test classes
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectClass(FeeTest.class),
                        selectClass(FeeTestFactoryGenerated.class),
                        selectClass(GarageTest.class),
                        selectClass(GarageTestFactoryGenerated.class),
                        selectClass(PaymentTest.class),
                        selectClass(TicketTest.class),
                        selectClass(TicketTestFactoryGenerated.class),
                        selectClass(UserTest.class)
                )
                .build();

        // Create a test launcher
        Launcher launcher = LauncherFactory.create();

        // Add a listener to collect the test summary
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        // Run the tests
        launcher.execute(request);

        // Print the summary
        TestExecutionSummary summary = listener.getSummary();
        System.out.println("Test Results:");
        System.out.println("Tests found: " + summary.getTestsFoundCount());
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        System.out.println("Tests aborted: " + summary.getTestsAbortedCount());

        // Print failures and their error messages
        for(Failure failure : summary.getFailures()) {
        	System.out.println("Test failed: " +
        			failure.getTestIdentifier().getDisplayName() +
                    " - " + failure.getException().getMessage());
        }
    }
}