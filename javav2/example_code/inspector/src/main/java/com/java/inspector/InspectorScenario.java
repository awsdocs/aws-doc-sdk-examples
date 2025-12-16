// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

// snippet-start:[inspector.java2_scenario.main]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InspectorScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final Logger logger = LoggerFactory.getLogger(InspectorScenario.class);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        InspectorActions inspectorActions = new InspectorActions();
        logger.info("Amazon Inspector Basics Scenario");

        logger.info("""
                    Amazon Inspector is a security assessment service provided by Amazon Web Services (AWS) that helps
                    improve the security and compliance of applications deployed on AWS. It automatically assesses
                    applications for vulnerabilities or deviations from best practices. By leveraging Amazon Inspector,
                    users can gain insights into the overall security state of their application and identify potential
                    security risks.
                    
                    This service operates by conducting both network and host-based assessments, allowing it to detect a
                    wide range of security issues, including those related to operating systems, network configurations,
                    and application dependencies.
                    """);

        waitForInputToContinue();

        try {
            runScenario(inspectorActions);

            logger.info("");
            logger.info("Scenario completed successfully!");
            logger.info("");
            logger.info("What you learned:");
            logger.info("  - How to check Inspector account status");
            logger.info("  - How to enable Inspector");
            logger.info("  - How to list and analyze findings");
            logger.info("  - How to check coverage information");
            logger.info("  - How to create and manage filters");
            logger.info("  - How to track usage and costs");
            logger.info("  - How to clean up resources");
            logger.info("");

        } catch (Exception ex) {
            logger.error("Scenario failed due to unexpected error: {}", ex.getMessage(), ex);

        } finally {
            scanner.close();
            logger.info("Exiting...");
        }
    }

    /**
     * Runs the Inspector scenario in a step-by-step sequence.
     *
     * All InspectorActions methods are asynchronous and return CompletableFutures.
     * Each step ends with .join(). Any async exception thrown during .join() will bubble up
     *
     */
    public static void runScenario(InspectorActions actions) {

        // Step 1
        logger.info(DASHES);
        logger.info("Step 1: Checking Inspector account status...");
        String status = actions.getAccountStatusAsync().join();
        logger.info(status);
        waitForInputToContinue();

        // Step 2
        logger.info(DASHES);
        logger.info("Step 2: Enabling Inspector...");
        String message = actions.enableInspectorAsync(null).join();
        logger.info(message);
        waitForInputToContinue();

        // Step 3
        logger.info(DASHES);
        logger.info("Step 3: Listing findings...");
        String allFindings = actions.listLowSeverityFindingsAsync().join();

        if (!allFindings.equals("No findings found.")) {
            // Split by newline and get the last ARN
            String[] arns = allFindings.split("\\r?\\n");
            String lastArn = arns[arns.length - 1];

            // Looks up details
            logger.info("Look up details on: {}" , lastArn);
            waitForInputToContinue();
            String details = actions.getFindingDetailsAsync(lastArn).join() ;
            logger.info(details);
        } else {
            System.out.println("No findings found.");
        }

        waitForInputToContinue();

        // Step 4
        logger.info(DASHES);
        logger.info("Step 4: Listing coverage...");
        String coverage = actions.listCoverageAsync(5).join();
        logger.info(coverage);
        waitForInputToContinue();

        // Step 5
        logger.info(DASHES);
        logger.info("Step 5: Creating filter...");
        String filterName = "suppress-low-" + System.currentTimeMillis();
        String filterArn = actions
                .createLowSeverityFilterAsync(filterName, "Suppress low severity findings")
                .join();
        logger.info("Created filter: {}", filterArn);
        waitForInputToContinue();

        // Step 6
        logger.info(DASHES);
        logger.info("Step 6: Listing filters...");
        String filters = actions.listFiltersAsync(10).join();
        logger.info(filters);
        waitForInputToContinue();

        // Step 7
        logger.info(DASHES);
        logger.info("Step 7: Usage totals...");
        String usage = actions.listUsageTotalsAsync(null, 10).join();
        logger.info(usage);
        waitForInputToContinue();

        // Step 8
        logger.info(DASHES);
        logger.info("Step 8: Coverage statistics...");
        String stats = actions.listCoverageStatisticsAsync().join();
        logger.info(stats);
        waitForInputToContinue();

        // Step 9
        logger.info(DASHES);
        logger.info("Step 9: Delete filter?");
        logger.info("Filter ARN: {}", filterArn);
        logger.info("Delete the filter and disable Inspector? (y/n)");

        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            actions.deleteFilterAsync(filterArn).join();
            logger.info("Filter deleted.");
            logger.info("Disable Inspector .");
            String disableMsg = actions.disableInspectorAsync(null).join();
            logger.info(disableMsg);
        }

        waitForInputToContinue();
    }

    // Utility Method
    private static void waitForInputToContinue() {
        while (true) {
            logger.info("");
            logger.info("Enter 'c' to continue:");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("c")) break;
            logger.info("Invalid input, try again.");
        }
    }
}
// snippet-end:[inspector.java2_scenario.main]