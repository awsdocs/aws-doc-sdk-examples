// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import software.amazon.awssdk.services.inspector2.paginators.ListFindingsIterable;
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
    private static final Logger logger = LoggerFactory.getLogger(HelloInspector.class);
    public static void main(String[] args) {
        logger.info(" Hello Amazon Inspector!");
        try (Inspector2Client inspectorClient = Inspector2Client.builder()
                .build()) {

            logger.info("Checking Inspector account status...");
            checkAccountStatus(inspectorClient);
            logger.info("");

            logger.info("Checking for recent findings...");
            listRecentFindings(inspectorClient);
            logger.info("");

            logger.info("Checking usage totals...");
            showUsageTotals(inspectorClient);
            logger.info("");

            logger.info("The Hello Inspector example completed successfully.");

        } catch (Inspector2Exception e) {
            logger.info(" Error: {}" , e.getMessage());
            logger.info(" Troubleshooting:");
            logger.info("1. Verify AWS credentials are configured");
            logger.info("2. Check IAM permissions for Inspector2");
            logger.info("3. Ensure Inspector2 is enabled in your account");
            logger.info("4. Verify you're using a supported region");
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
                logger.info(" No account information returned.");
                return;
            }

            for (AccountState account : accounts) {
                logger.info(" Account: " + account.accountId());
                ResourceState resources = account.resourceState();
                if (resources == null) {
                    System.out.println("   No resource state data available.");
                    continue;
                }

                logger.info("   Resource States:");
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
            logger.info("     - {} : (no data)", name);
            return;
        }
        String err = state.errorMessage() != null ? " (Error: " + state.errorMessage() + ")" : "";
        logger.info("     - {}: {}{}", name, state.status(), err);
    }

    /**
     * Lists recent findings from AWS Inspector2 using the synchronous client with a paginator.
     *
     * @param inspectorClient an instance of {@link Inspector2Client} used to call AWS Inspector2
     * @throws Inspector2Exception if there is an error communicating with the Inspector2 service
     */
    /**
     * Lists up to 10 recent findings from AWS Inspector2 using the synchronous client.
     *
     * <p>This method retrieves findings in pages and logs details for each finding,
     * including title, severity, status, and last observed time. Only the first
     * 10 findings are logged, even if more exist.
     *
     * @param inspectorClient an instance of {@link Inspector2Client} used to call AWS Inspector2
     * @throws Inspector2Exception if there is an error communicating with the Inspector2 service
     */
    public static void listRecentFindings(Inspector2Client inspectorClient) {
        final int MAX_FINDINGS = 10;
        int totalLogged = 0;

        try {
            // Build initial request with page size
            ListFindingsRequest request = ListFindingsRequest.builder()
                    .maxResults(MAX_FINDINGS)
                    .build();

            // Paginator returns an iterable over responses
            ListFindingsIterable responses = inspectorClient.listFindingsPaginator(request);

            for (ListFindingsResponse response : responses) {
                List<Finding> findings = response.findings();
                if (findings == null || findings.isEmpty()) {
                    continue;
                }

                for (Finding finding : findings) {
                    if (totalLogged >= MAX_FINDINGS) {
                        break;
                    }

                    logger.info("   Title: {}", finding.title());
                    logger.info("   Severity: {}", finding.severity());
                    logger.info("   Status: {}", finding.status());
                    logger.info("   Last Observed: {}", finding.lastObservedAt());
                    logger.info("");

                    totalLogged++;
                }

                if (totalLogged >= MAX_FINDINGS) {
                    break;
                }
            }

            if (totalLogged == 0) {
                logger.info(" No findings found.");
            } else {
                logger.info(" Displayed {} recent finding(s).", totalLogged);
            }

        } catch (Inspector2Exception e) {
            logger.info(" Error listing findings: {}", e.awsErrorDetails().errorMessage());
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
            logger.info("Listing usage totals using paginator...");
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
                logger.info(" No usage data available yet.");
                logger.info(" Usage data appears after Inspector has been active for some time.");
            } else {
                logger.info(" Usage Totals (Last 30 days):");
                for (UsageTotal total : allTotals) {
                    logger.info("   Account: {}" , total.accountId());
                    if (total.usage() != null && !total.usage().isEmpty()) {
                        total.usage().forEach(u -> {
                            logger.info("     - {}: {}", u.type(), u.total());

                            if (u.estimatedMonthlyCost() != null) {
                                logger.info("       Estimated Monthly Cost: {} {}", u.estimatedMonthlyCost(), u.currency());
                            }
                        });
                    }
                    logger.info("");
                }
            }

        } catch (Inspector2Exception e) {
            logger.info(" Error getting usage totals: {}" , e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while listing usage totals: " + e.getMessage(), e);
        }
    }
}
// snippet-end:[inspector.java2.hello.main]