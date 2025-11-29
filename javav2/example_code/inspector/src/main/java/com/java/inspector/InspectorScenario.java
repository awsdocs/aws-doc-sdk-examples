// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import java.util.Scanner;

// snippet-start:[inspector.java2_scenario.main]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InspectorScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        InspectorActions inspectorActions = new InspectorActions();

        Inspector2Client inspectorClient = Inspector2Client.builder()
                .region(Region.US_EAST_1)
                .build() ;

        System.out.println("🔍 Amazon Inspector Basics Scenario");
        System.out.println(DASHES);
        System.out.println();

        System.out.println("""
            Amazon Inspector is a security assessment service provided 
            by Amazon Web Services (AWS) that helps improve the security 
            and compliance of applications deployed on AWS. 
            It automatically assesses applications for vulnerabilities 
            or deviations from best practices. By leveraging Amazon 
            Inspector, users can gain insights into the overall 
            security state of their application and identify potential 
            security risks. 
            
            This service operates by conducting both network and 
            host-based assessments, allowing it to detect a wide 
            range of security issues, including those related to 
            operating systems, network configurations, and application 
            dependencies.                   
            """);

        waitForInputToContinue(scanner);

        // Step 1: Check current account status.
        System.out.println(DASHES);
        System.out.println("Step 1: Checking Inspector account status...");

        try {
            inspectorActions.getAccountStatus(inspectorClient);

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        waitForInputToContinue(scanner);

        // Step 2: Enable Inspector for resource types (if not already enabled).
        System.out.println(DASHES);
        System.out.println("Step 2: Ensuring Inspector is enabled...");

        try {
            inspectorActions.enableInspector(inspectorClient, null);

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        waitForInputToContinue(scanner);

        // Step 3: List and analyze findings.
        System.out.println(DASHES);
        System.out.println("Step 3: Analyzing security findings...");
        int maxResults = 10;

        try {
            inspectorActions.listFindings(inspectorClient, maxResults, null);

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        System.out.println();
        waitForInputToContinue(scanner);

        // Step 4: Show coverage information.
        System.out.println(DASHES);
        System.out.println("Step 4: Checking scan coverage...");
        maxResults = 5;

        try {
            inspectorActions.listCoverage(inspectorClient, maxResults);

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        System.out.println();
        waitForInputToContinue(scanner);

        // Step 5: Create a findings filter (example)
        System.out.println(DASHES);
        System.out.println("Step 5: Creating a findings filter...");
        try {
            inspectorActions.createFilter(
                    inspectorClient,
                    "Suppress low severity findings for demo purposes"
            );

            System.out.println("Created example filter");

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        System.out.println();
        waitForInputToContinue(scanner);

        // Step 6: List existing filters
        System.out.println(DASHES);
        System.out.println("Step 6: Listing existing filters...");

        try {
            inspectorActions.listFilters(inspectorClient, 10);

        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        System.out.println();
        waitForInputToContinue(scanner);

        // Step 7: Show usage totals
        System.out.println(DASHES);
        System.out.println("Step 7: Checking usage and costs...");

        try {
            inspectorActions.listUsageTotals(inspectorClient, null, 10);
        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }

        System.out.println();
        waitForInputToContinue(scanner);

        // Step 8: Coverage statistics
        System.out.println(DASHES);
        System.out.println("Step 8: Getting coverage statistics...");

        try{
            inspectorActions.listCoverageStatistics(inspectorClient);
        } catch (Exception e) {
            System.err.println(" Could not create example filter: " + e.getMessage());
        }
        System.out.println();

        System.out.println("🎉 Inspector Basics scenario completed successfully!");
        System.out.println();
        System.out.println("📚 What you learned:");
        System.out.println("   ✓ How to check Inspector account status and configuration");
        System.out.println("   ✓ How to enable Inspector for different resource types");
        System.out.println("   ✓ How to list and analyze security findings");
        System.out.println("   ✓ How to monitor scan coverage across your resources");
        System.out.println("   ✓ How to create filters to suppress findings");
        System.out.println("   ✓ How to track usage and costs");
        System.out.println();
        System.out.println("🔗 Next steps:");
        System.out.println("   • Set up EventBridge rules to respond to findings");
        System.out.println("   • Create custom filters for your environment");
        System.out.println("   • Generate detailed findings reports");
        System.out.println("   • Integrate with AWS Security Hub");
        waitForInputToContinue(scanner);

        System.out.println();
        System.out.println(" This concludes the AWS Inspector Service scenario.");
    }


    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[inspector.java2_scenario.main]