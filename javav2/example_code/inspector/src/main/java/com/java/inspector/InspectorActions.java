// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.*;
import software.amazon.awssdk.services.inspector2.paginators.ListCoverageIterable;
import software.amazon.awssdk.services.inspector2.paginators.ListCoverageStatisticsIterable;
import software.amazon.awssdk.services.inspector2.paginators.ListFiltersIterable;
import software.amazon.awssdk.services.inspector2.paginators.ListUsageTotalsIterable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// snippet-start:[inspector.java2_actions.main]
public class InspectorActions {

    // snippet-start:[inspector.java2.enable.main]
    /**
     * Enables AWS Inspector for the provided account(s) and default resource types.
     *
     * @param inspectorClient The Inspector2 client.
     * @param accountIds      Optional list of AWS account IDs.
     */
    public void enableInspector(Inspector2Client inspectorClient, List<String> accountIds) {

        // Default resource types to enable.
        List<ResourceScanType> resourceTypes = List.of(
                ResourceScanType.EC2,
                ResourceScanType.ECR,
                ResourceScanType.LAMBDA,
                ResourceScanType.LAMBDA_CODE
        );

        EnableRequest.Builder requestBuilder = EnableRequest.builder()
                .resourceTypes(resourceTypes);

        if (accountIds != null && !accountIds.isEmpty()) {
            requestBuilder.accountIds(accountIds);
        }

        EnableRequest request = requestBuilder.build();
        try {
            EnableResponse response = inspectorClient.enable(request);

            if (response.accounts() != null && !response.accounts().isEmpty()) {
                System.out.println("Inspector enable operation results:");
                for (Account account : response.accounts()) {
                    String accountId = account.accountId() != null ? account.accountId() : "Unknown";
                    String status = account.status() != null ? account.statusAsString() : "Unknown";
                    System.out.println("   • Account: " + accountId + " → Status: " + status);
                }
            } else {
                System.out.println("  Inspector may already be enabled for all target accounts.");
            }

        } catch (ValidationException ve) {
            System.out.println("  Inspector may already be enabled for this account: " + ve.getMessage());

        } catch (Inspector2Exception e) {
            System.err.println("AWS Inspector2 service error: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable Inspector: " + e.getMessage(), e);
        }
    }
    // snippet-end:[inspector.java2.enable.main]

    // snippet-start:[inspector.java2.list_coverage.stats.main]
    /**
     * Retrieves and prints the coverage statistics using a paginator.
     *
     * @param inspectorClient the Inspector2Client used to retrieve the coverage statistics
     */
    public void listCoverageStatistics(Inspector2Client inspectorClient) {
        try {
            System.out.println("Listing coverage statistics using paginator...");
            ListCoverageStatisticsRequest request = ListCoverageStatisticsRequest.builder()
                    .build();

            // Create paginator.
            ListCoverageStatisticsIterable paginator = inspectorClient.listCoverageStatisticsPaginator(request);
            List<Counts> allCounts = new ArrayList<>();

            // Iterate through all pages.
            for (ListCoverageStatisticsResponse response : paginator) {
                List<Counts> counts = response.countsByGroup();
                if (counts != null && !counts.isEmpty()) {
                    allCounts.addAll(counts);
                }
            }

            // Display results.
            if (allCounts.isEmpty()) {
                System.out.println("No coverage statistics available");
            } else {
                System.out.println("Coverage Statistics:");
                for (Counts count : allCounts) {
                    System.out.println("   Group: " + count.groupKey());
                    System.out.println("     Total Count: " + count.count());
                    System.out.println();
                }
            }

        } catch (ValidationException ve) {
            System.out.println(" Validation error: " + ve.getMessage());
            System.out.println(" This likely means there are no coverage statistics available at this time.");

        } catch (Inspector2Exception e) {
            System.err.println(" AWS Inspector2 service error: " + e.awsErrorDetails().errorMessage());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list coverage statistics: " + e.getMessage(), e);
        }
    }
    // snippet-end:[inspector.java2.list_coverage.stats.main]

    // snippet-start:[inspector.java2.list_usage_totals.main]
    /**
     * Retrieves and prints the usage totals for the specified accounts using a paginator.
     *
     * @param inspectorClient the Inspector2Client used to make the API call
     * @param accountIds a list of account IDs for which to retrieve usage totals. If null or empty, all accounts are considered.
     * @param maxResults the maximum number of results to return
     */
    public void listUsageTotals(
            Inspector2Client inspectorClient,
            List<String> accountIds,
            int maxResults
    ) {
        try {
            System.out.println("Listing usage totals using paginator...");
            ListUsageTotalsRequest.Builder requestBuilder = ListUsageTotalsRequest.builder()
                    .maxResults(maxResults);

            if (accountIds != null && !accountIds.isEmpty()) {
                requestBuilder.accountIds(accountIds);
            }

            ListUsageTotalsRequest request = requestBuilder.build();

            // Create paginator.
            ListUsageTotalsIterable paginator = inspectorClient.listUsageTotalsPaginator(request);

            List<UsageTotal> allTotals = new ArrayList<>();
            for (ListUsageTotalsResponse response : paginator) {
                List<UsageTotal> totals = response.totals();
                if (totals != null && !totals.isEmpty()) {
                    allTotals.addAll(totals);
                }
            }

            // Display results.
            if (allTotals.isEmpty()) {
                System.out.println("No usage data available yet");
                System.out.println("Usage data appears after Inspector has been active for some time");
            } else {
                System.out.println("Usage Totals (Last 30 days):");
                for (UsageTotal total : allTotals) {
                    System.out.println("   Account: " + total.accountId());
                    List<Usage> usageList = total.usage();

                    if (usageList != null && !usageList.isEmpty()) {
                        for (Usage usage : usageList) {
                            System.out.println("     - " + usage.type() + ": " + usage.total());
                            if (usage.estimatedMonthlyCost() != null) {
                                System.out.println("       Estimated Monthly Cost: " +
                                        usage.estimatedMonthlyCost() + " " + usage.currency());
                            }
                        }
                    }
                    System.out.println();
                }
            }

        } catch (ValidationException ve) {
            System.out.println(" Validation error: " + ve.getMessage());
            System.out.println(" This likely means there is no usage data available for the provided accounts.");

        } catch (Inspector2Exception e) {
            System.err.println(" AWS Inspector2 service error: " + e.awsErrorDetails().errorMessage());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list usage totals: " + e.getMessage(), e);
        }
    }
    // snippet-end:[inspector.java2.list_usage_totals.main]

    // snippet-start:[inspector.java2.get_account_status.main]
    /**
     * Retrieves the account status using the Inspector2Client.
     *
     * @param inspectorClient the Inspector2Client used to send the request and retrieve the account status
     */
    public void getAccountStatus(Inspector2Client inspectorClient) {
        BatchGetAccountStatusRequest request = BatchGetAccountStatusRequest.builder()
                .accountIds(Collections.emptyList()) // current account
                .build();

        BatchGetAccountStatusResponse response = inspectorClient.batchGetAccountStatus(request);
        if (response.accounts() != null) {
            for (AccountState account : response.accounts()) {
                String state = (account.state() != null && account.state().status() != null)
                        ? String.valueOf(account.state().status())
                        : "Unknown";
                System.out.println("Account: " + account.accountId() + ", State: " + state);
            }
        }

        System.out.println("Inspector Account Status:");
        if (response.accounts() != null) {
            for (AccountState account : response.accounts()) {
                System.out.println("   Account ID: " + (account.accountId() != null ? account.accountId() : "Unknown"));

                // Resource state (only status is available)
                ResourceState resources = account.resourceState();
                if (resources != null) {
                    System.out.println("   Resource Status error: ");
                }

                // Overall account state.
                if (account.state() != null && account.state().status() != null) {
                    System.out.println("   Overall State: " + account.state().status());
                }
            }
        }
    }
    // snippet-end:[inspector.java2.get_account_status.main]

    // snippet-start:[inspector.java2.list_filters.main]
    /**
     * Retrieves a list of filters using a paginator.
     *
     * @param inspector2Client An instance of {@code Inspector2Client} used to interact with AWS Inspector2.
     * @param maxResults The maximum number of filters to return. If null, the default maximum results will be used.
     *
     * @throws Inspector2Exception If an error occurs specific to AWS Inspector2, such as invalid parameters or service issues.
     */
    public void listFilters(Inspector2Client inspector2Client, Integer maxResults) {
        try {
            System.out.println("Listing filters using paginator...");
            ListFiltersRequest.Builder requestBuilder = ListFiltersRequest.builder();
            if (maxResults != null) {
                requestBuilder.maxResults(maxResults);
            }

            // Create paginator.
            ListFiltersIterable paginator = inspector2Client.listFiltersPaginator(requestBuilder.build());
            int totalCount = 0;

            // Iterate over pages.
            for (var response : paginator) {
                List<Filter> filters = response.filters();
                if (filters == null || filters.isEmpty()) {
                    continue;
                }

                for (Filter filter : filters) {
                    totalCount++;
                    System.out.println("   - " + filter.name());
                    System.out.println("     ARN: " + filter.arn());
                    System.out.println("     Action: " + filter.action());
                    System.out.println("     Owner: " + filter.ownerId());
                    System.out.println("     Created: " + filter.createdAt());
                    System.out.println();
                }
            }

            if (totalCount == 0) {
                System.out.println(" No filters found.");
            } else {
                System.out.println(" Found " + totalCount + " filter(s) in total.");
            }

        } catch (Inspector2Exception e) {
            System.err.println("Failed to list filters: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw e;
        }
    }
    // snippet-end:[inspector.java2.list_filters.main]

    // snippet-start:[inspector.java2.create.filter.main]
    /**
     * Creates a new filter in AWS Inspector2 to suppress findings of low severity.
     *
     * @param inspector2Client An instance of {@code Inspector2Client} used to interact with AWS Inspector2.
     * @param description A descriptive string that explains the purpose of the filter.
     *
     *
     * @throws Inspector2Exception If an error occurs specific to AWS Inspector2, such as invalid parameters or service issues.
     * @throws Exception If an unexpected error occurs during the filter creation process.
     *
     */
      public void createFilter(Inspector2Client inspector2Client, String description) {
            String filterName = "suppress-low-severity-" + System.currentTimeMillis();
            System.out.println("Creating filter: " + filterName);

            try {
                // Define a filter to match LOW severity findings.
                StringFilter severityFilter = StringFilter.builder()
                        .value(Severity.LOW.toString())
                        .comparison(StringComparison.EQUALS)
                        .build();

                // Create filter criteria using the severity filter.
                FilterCriteria filterCriteria = FilterCriteria.builder()
                        .severity(Collections.singletonList(severityFilter))
                        .build();

                // Build the filter creation request.
                CreateFilterRequest createRequest = CreateFilterRequest.builder()
                        .name(filterName)
                        .filterCriteria(filterCriteria)
                        .action(FilterAction.SUPPRESS)
                        .description(description)
                        .build();

                // Execute the request.
                CreateFilterResponse response = inspector2Client.createFilter(createRequest);
                System.out.println("Successfully created filter with ARN: " + response.arn());

            } catch (Inspector2Exception e) {
                System.err.println("Failed to create filter: " + e.awsErrorDetails().errorMessage());
                throw e;
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                throw e;
            }
        }
    // snippet-end:[inspector.java2.create.filter.main]

    // snippet-start:[inspector.java2.list_findings.main]
    /**
     * Lists findings from AWS Inspector2 .
     *
     * @param inspectorClient The Inspector2 client.
     * @param maxResults      Maximum number of results to retrieve.
     * @param filterCriteria  Optional filter criteria (can be null).
     */
    public void listFindings(
            Inspector2Client inspectorClient,
            int maxResults,
            FilterCriteria filterCriteria
    ) {
        // Build the request
        ListFindingsRequest.Builder requestBuilder = ListFindingsRequest.builder()
                .maxResults(maxResults);

        if (filterCriteria != null) {
            requestBuilder.filterCriteria(filterCriteria);
        }

        ListFindingsRequest request = requestBuilder.build();
        try {
            ListFindingsResponse findingsResponse = inspectorClient.listFindings(request);
            List<Finding> findings = findingsResponse.findings();

            if (findings == null || findings.isEmpty()) {
                System.out.println(" No findings found");
                System.out.println(" This could mean:");
                System.out.println("   • Inspector hasn't completed its initial scan yet");
                System.out.println("   • Your resources don't have any vulnerabilities");
                System.out.println("   • All findings have been suppressed by filters");
            } else {
                System.out.println(" Found " + findings.size() + " finding(s):");
                Map<String, List<Finding>> findingsBySeverity = findings.stream()
                        .collect(Collectors.groupingBy(f -> f.severityAsString()));

                for (Map.Entry<String, List<Finding>> entry : findingsBySeverity.entrySet()) {
                    System.out.println("   " + entry.getKey() + ": " + entry.getValue().size() + " finding(s)");
                }

                System.out.println();
                System.out.println("   Recent findings:");
                for (int i = 0; i < Math.min(findings.size(), 5); i++) {
                    Finding finding = findings.get(i);
                    System.out.println("   " + (i + 1) + ". " + finding.title());
                    System.out.println("      Type: " + finding.type());
                    System.out.println("      Severity: " + finding.severityAsString());
                    System.out.println("      Status: " + finding.statusAsString());

                    if (finding.resources() != null && !finding.resources().isEmpty()) {
                        Resource resource = finding.resources().get(0);
                        System.out.println("      Resource: " + resource.typeAsString() + " - " + resource.id());
                    }

                    if (finding.inspectorScore() != null) {
                        System.out.println("      Inspector Score: " + finding.inspectorScore());
                    }
                    System.out.println();
                }
            }

        } catch (ValidationException ve) {
            System.out.println(" Validation error: " + ve.getMessage());

        } catch (Inspector2Exception e) {
            System.err.println("AWS Inspector2 service error: " + e.awsErrorDetails().errorMessage());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list findings: " + e.getMessage(), e);
        }
    }
    // snippet-end:[inspector.java2.list_findings.main]

    // snippet-start:[inspector.java2.list_coverage.main]
    /**
     * Lists AWS Inspector2 coverage details for scanned resources using a paginator.
     *
     * @param inspectorClient The Inspector2 client.
     * @param maxResults      Maximum number of resources to return.
     */
    public void listCoverage(Inspector2Client inspectorClient, int maxResults) {
        try {
            System.out.println("Listing coverage information using paginator...");
            ListCoverageRequest request = ListCoverageRequest.builder()
                    .maxResults(maxResults)
                    .build();

            // Create paginator.
            ListCoverageIterable paginator = inspectorClient.listCoveragePaginator(request);
            List<CoveredResource> allCoveredResources = new ArrayList<>();

            // Iterate through all pages.
            for (ListCoverageResponse response : paginator) {
                List<CoveredResource> coveredResources = response.coveredResources();
                if (coveredResources != null && !coveredResources.isEmpty()) {
                    allCoveredResources.addAll(coveredResources);
                }
            }

            if (allCoveredResources.isEmpty()) {
                System.out.println(" No coverage information available.");
                System.out.println(" This likely means Inspector hasn't yet scanned your resources or no supported resource types are present.");
                return;
            }

            System.out.println(" Coverage Information:");
            System.out.println("   Total resources covered: " + allCoveredResources.size());

            // Group by resource type.
            Map<String, List<CoveredResource>> resourcesByType = allCoveredResources.stream()
                    .collect(Collectors.groupingBy(CoveredResource::resourceTypeAsString));

            for (Map.Entry<String, List<CoveredResource>> entry : resourcesByType.entrySet()) {
                System.out.println("   " + entry.getKey() + ": " + entry.getValue().size() + " resource(s)");
            }

            System.out.println();
            System.out.println("   Sample covered resources:");

            // Display up to 3 sample resources.
            for (int i = 0; i < Math.min(allCoveredResources.size(), 3); i++) {
                CoveredResource resource = allCoveredResources.get(i);
                System.out.println("     - " + resource.resourceTypeAsString() + ": " + resource.resourceId());
                System.out.println("       Scan Type: " + resource.scanTypeAsString());

                if (resource.scanStatus() != null) {
                    System.out.println("       Status: " + resource.scanStatus().statusCodeAsString());
                }

                if (resource.accountId() != null) {
                    System.out.println("       Account ID: " + resource.accountId());
                }
                System.out.println();
            }

        } catch (ValidationException ve) {
            System.out.println(" Validation error: " + ve.getMessage());
            System.out.println(" This likely means no resources are currently covered by Inspector2.");

        } catch (Inspector2Exception e) {
            System.err.println(" AWS Inspector2 service error: " + e.awsErrorDetails().errorMessage());
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list coverage: " + e.getMessage(), e);
        }
    }
    // snippet-end:[inspector.java2.list_coverage.main]
}
// snippet-end:[inspector.java2_actions.main]