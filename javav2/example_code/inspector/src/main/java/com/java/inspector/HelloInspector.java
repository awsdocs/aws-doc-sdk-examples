// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusRequest;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusResponse;
import software.amazon.awssdk.services.inspector2.model.AccountState;
import software.amazon.awssdk.services.inspector2.model.ResourceState;
import software.amazon.awssdk.services.inspector2.model.State;
import software.amazon.awssdk.services.inspector2.model.ListFindingsRequest;
import software.amazon.awssdk.services.inspector2.model.ListFindingsResponse;
import software.amazon.awssdk.services.inspector2.model.Finding;
import software.amazon.awssdk.services.inspector2.model.ListUsageTotalsRequest;
import software.amazon.awssdk.services.inspector2.model.ListUsageTotalsResponse;
import software.amazon.awssdk.services.inspector2.model.UsageTotal;
import software.amazon.awssdk.services.inspector2.model.Inspector2Exception;
import software.amazon.awssdk.services.inspector2.paginators.ListUsageTotalsIterable;
import java.util.List;
import java.util.ArrayList;

// snippet-start:[inspector.java2.hello.main]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class HelloInspector {

    public static void main(String[] args) {
        System.out.println(" Hello Amazon Inspector!");
        Region region = Region.US_EAST_1;

        try (Inspector2Client inspectorClient = Inspector2Client.builder()
                .region(region)
                .build()) {

            System.out.println("Checking Inspector account status...");
            checkAccountStatus(inspectorClient);
            System.out.println();

            System.out.println("Checking for recent findings...");
            listRecentFindings(inspectorClient);
            System.out.println();

            System.out.println("Checking usage totals...");
            showUsageTotals(inspectorClient);
            System.out.println();

            System.out.println("Hello Inspector example completed successfully!");

        } catch (Inspector2Exception e) {
            System.err.println(" Error: " + e.getMessage());
            System.err.println(" Troubleshooting:");
            System.err.println("1. Verify AWS credentials are configured");
            System.err.println("2. Check IAM permissions for Inspector2");
            System.err.println("3. Ensure Inspector2 is enabled in your account");
            System.err.println("4. Verify you're using a supported region");
        }
    }

    /**
     * Checks the account status using the provided Inspector2Client.
     * This method sends a request to retrieve the account status and prints the details of each account's resource states.
     *
     * @param inspectorClient The Inspector2Client used to interact with the AWS Inspector service.
     */
    public static void checkAccountStatus(Inspector2Client inspectorClient) {
        try {
            BatchGetAccountStatusRequest request = BatchGetAccountStatusRequest.builder().build();
            BatchGetAccountStatusResponse response = inspectorClient.batchGetAccountStatus(request);

            List<AccountState> accounts = response.accounts();
            if (accounts == null || accounts.isEmpty()) {
                System.out.println(" No account information returned.");
                return;
            }

            for (AccountState account : accounts) {
                System.out.println(" Account: " + account.accountId());
                ResourceState resources = account.resourceState();
                if (resources == null) {
                    System.out.println("   No resource state data available.");
                    continue;
                }

                System.out.println("   Resource States:");
                printState("EC2", resources.ec2());
                printState("ECR", resources.ecr());
                printState("Lambda", resources.lambda());
                printState("Lambda Code", resources.lambdaCode());
                System.out.println();
            }

        } catch (Inspector2Exception e) {
            System.err.println(" Failed to retrieve account status: " + e.awsErrorDetails().errorMessage());
        }
    }

    public static void printState(String name, State state) {
        if (state == null) {
            System.out.println("     - " + name + ": (no data)");
            return;
        }
        String err = state.errorMessage() != null ? " (Error: " + state.errorMessage() + ")" : "";
        System.out.println("     - " + name + ": " + state.status() + err);
    }

    /**
     * Retrieves and prints the most recent findings from the Inspector2 service.
     *
     * @param inspectorClient the Inspector2Client used to interact with the AWS Inspector2 service
     */
    public static void listRecentFindings(Inspector2Client inspectorClient) {
        try {
            ListFindingsRequest request = ListFindingsRequest.builder()
                    .maxResults(10)
                    .build();

            ListFindingsResponse response = inspectorClient.listFindings(request);
            List<Finding> findings = response.findings();

            if (findings == null || findings.isEmpty()) {
                System.out.println(" No findings found.");
            } else {
                System.out.println(" Found " + findings.size() + " recent finding(s):");
                for (Finding finding : findings) {
                    System.out.println("   Title: " + finding.title());
                    System.out.println("   Severity: " + finding.severity());
                    System.out.println("   Status: " + finding.status());
                    System.out.println("   Last Observed: " + finding.lastObservedAt());
                    System.out.println();
                }
            }

        } catch (Inspector2Exception e) {
            System.err.println(" Error listing findings: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Displays the usage totals for the Inspector2 service.
     *
     * @param inspectorClient the {@code Inspector2Client} used to make the API call to
     *                         retrieve the usage totals.
     *
     * @throws Inspector2Exception if there is an error while retrieving the usage totals.
     *                             The error message is printed to the standard error output.
     */
    public static void showUsageTotals(Inspector2Client inspectorClient) {
        try {
            System.out.println("Listing usage totals using paginator...");
            ListUsageTotalsRequest request = ListUsageTotalsRequest.builder()
                    .maxResults(10)
                    .build();

            // Create paginator.
            ListUsageTotalsIterable paginator = inspectorClient.listUsageTotalsPaginator(request);
            List<UsageTotal> allTotals = new ArrayList<>();

            // Iterate through all pages.
            for (ListUsageTotalsResponse response : paginator) {
                List<UsageTotal> totals = response.totals();
                if (totals != null && !totals.isEmpty()) {
                    allTotals.addAll(totals);
                }
            }

            // Display results.
            if (allTotals.isEmpty()) {
                System.out.println(" No usage data available yet.");
                System.out.println(" Usage data appears after Inspector has been active for some time.");
            } else {
                System.out.println(" Usage Totals (Last 30 days):");
                for (UsageTotal total : allTotals) {
                    System.out.println("   Account: " + total.accountId());
                    if (total.usage() != null && !total.usage().isEmpty()) {
                        total.usage().forEach(u -> {
                            System.out.println("     - " + u.type() + ": " + u.total());
                            if (u.estimatedMonthlyCost() != null) {
                                System.out.println("       Estimated Monthly Cost: " +
                                        u.estimatedMonthlyCost() + " " + u.currency());
                            }
                        });
                    }
                    System.out.println();
                }
            }

        } catch (Inspector2Exception e) {
            System.err.println(" Error getting usage totals: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while listing usage totals: " + e.getMessage(), e);
        }
    }
}
// snippet-end:[inspector.java2.hello.main]
