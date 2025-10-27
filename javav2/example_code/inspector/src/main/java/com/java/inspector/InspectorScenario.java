package com.java.inspector;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.AccountState;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusResponse;
import software.amazon.awssdk.services.inspector2.model.Counts;
import software.amazon.awssdk.services.inspector2.model.CoveredResource;
import software.amazon.awssdk.services.inspector2.model.CreateFilterResponse;
import software.amazon.awssdk.services.inspector2.model.Filter;
import software.amazon.awssdk.services.inspector2.model.FilterAction;
import software.amazon.awssdk.services.inspector2.model.FilterCriteria;
import software.amazon.awssdk.services.inspector2.model.Finding;
import software.amazon.awssdk.services.inspector2.model.ListCoverageResponse;
import software.amazon.awssdk.services.inspector2.model.ListCoverageStatisticsResponse;
import software.amazon.awssdk.services.inspector2.model.ListFiltersResponse;
import software.amazon.awssdk.services.inspector2.model.ListFindingsResponse;
import software.amazon.awssdk.services.inspector2.model.ListUsageTotalsResponse;
import software.amazon.awssdk.services.inspector2.model.Resource;
import software.amazon.awssdk.services.inspector2.model.ResourceScanType;
import software.amazon.awssdk.services.inspector2.model.ResourceState;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.AccountState;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusResponse;
import software.amazon.awssdk.services.inspector2.model.ResourceState;
import software.amazon.awssdk.services.inspector2.model.Severity;
import software.amazon.awssdk.services.inspector2.model.StringComparison;
import software.amazon.awssdk.services.inspector2.model.StringFilter;
import software.amazon.awssdk.services.inspector2.model.Usage;
import software.amazon.awssdk.services.inspector2.model.UsageTotal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InspectorScenario {


    public static void main(String[] args) {

        InspectorActions inspectorActions = new InspectorActions();

        Inspector2Client inspectorClient = Inspector2Client.builder()
                .region(Region.US_EAST_1)
                .build() ;


        System.out.println("🔍 Amazon Inspector Basics Scenario");
        System.out.println("====================================");
        System.out.println();

        // Step 1: Check current account status
        System.out.println("Step 1: Checking Inspector account status...");
        var accountStatus = inspectorActions.getAccountStatus(inspectorClient);
        displayAccountStatus(accountStatus);


        // Step 2: Enable Inspector for resource types (if not already enabled)
        System.out.println("Step 2: Ensuring Inspector is enabled...");
        List<ResourceScanType> resourceTypes = List.of(
                ResourceScanType.EC2,
                ResourceScanType.ECR,
                ResourceScanType.LAMBDA,
                ResourceScanType.LAMBDA_CODE
        );


        try {
            inspectorActions.enableInspector(inspectorClient, resourceTypes, null);
        } catch (Exception e) {
            System.out.println("ℹ️ Inspector may already be enabled: " + e.getMessage());
        }


        // Step 3: List and analyze findings
        System.out.println("Step 3: Analyzing security findings...");
        int maxResults = 10;
        var findings = inspectorActions.listFindings(inspectorClient, maxResults, null);
        displayFindings(findings);
        System.out.println();


        // Step 4: Show coverage information
        System.out.println("Step 4: Checking scan coverage...");
        maxResults = 5;
        var coverage = inspectorActions.listCoverage(inspectorClient, maxResults);
        displayCoverage(coverage);
        System.out.println();

        // Step 5: Create a findings filter (example)
        System.out.println("Step 5: Creating a findings filter...");
        createExampleFilter(inspectorActions, inspectorClient);
        System.out.println();

        // Step 6: List existing filters
        System.out.println("Step 6: Listing existing filters...");
        var filters = inspectorActions.listFilters(inspectorClient, 10);
        displayFilters(filters);
        System.out.println();

        // Step 7: Show usage totals
        System.out.println("Step 7: Checking usage and costs...");
        var usage = inspectorActions.listUsageTotals(inspectorClient, null, 10);
        displayUsage(usage);
        System.out.println();

        // Step 8: Coverage statistics
        System.out.println("Step 8: Getting coverage statistics...");
        var coverageStats = inspectorActions.listCoverageStatistics(inspectorClient);
        displayCoverageStatistics(coverageStats);
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


        System.out.println();();
        System.out.println(" This concludes the AWS Inspector Service scenario.");
    }

    /**
     * Displays coverage statistics in a formatted way.
     */
    private static void displayCoverageStatistics(ListCoverageStatisticsResponse statsResponse) {
        List<Counts> counts = statsResponse.countsByGroup();

        if (counts == null || counts.isEmpty()) {
            System.out.println("📊 No coverage statistics available");
        } else {
            System.out.println("✅ Coverage Statistics:");
            for (Counts count : counts) {
                System.out.println("   Group: " + count.groupKey());
                System.out.println("     Total Count: " + count.count());
                System.out.println();
            }
        }
    }


    public static void displayUsage(ListUsageTotalsResponse usageResponse) {
        List<UsageTotal> totals = usageResponse.totals();

        if (totals == null || totals.isEmpty()) {
            System.out.println("📊 No usage data available yet");
            System.out.println("💡 Usage data appears after Inspector has been active for some time");
        } else {
            System.out.println("✅ Usage Totals (Last 30 days):");
            for (UsageTotal total : totals) {
                System.out.println("   Account: " + total.accountId());
                List<Usage> usageList = total.usage();
                if (usageList != null) {
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
    }

    public static void displayFilters(ListFiltersResponse filtersResponse) {
        List<Filter> filters = filtersResponse.filters();

        if (filters == null || filters.isEmpty()) {
            System.out.println("📭 No filters found");
        } else {
            System.out.println("✅ Found " + filters.size() + " filter(s):");
            for (Filter filter : filters) {
                System.out.println("   - " + filter.name());
                System.out.println("     ARN: " + filter.arn());
                System.out.println("     Action: " + filter.action());
                System.out.println("     Owner: " + filter.ownerId());
                System.out.println("     Created: " + filter.createdAt());
                System.out.println();
            }
        }
    }


    public static void createExampleFilter(InspectorActions inspectorActions, Inspector2Client inspectorClient) {
        try {
            CreateFilterResponse filterRes = inspectorActions.createFilter(
                    inspectorClient,
                    "Suppress low severity findings for demo purposes"
            );

            System.out.println("✅ Created example filter: " + filterRes.arn());

        } catch (Exception e) {
            System.out.println("ℹ️ Could not create example filter: " + e.getMessage());
        }
    }
    public static void displayCoverage(ListCoverageResponse coverageResponse) {
        List<CoveredResource> coveredResources = coverageResponse.coveredResources();

        if (coveredResources == null || coveredResources.isEmpty()) {
            System.out.println("📊 No coverage information available");
        } else {
            System.out.println("✅ Coverage Information:");
            System.out.println("   Total resources covered: " + coveredResources.size());

            // Group by resource type
            Map<String, List<CoveredResource>> resourcesByType = coveredResources.stream()
                    .collect(Collectors.groupingBy(CoveredResource::resourceTypeAsString));

            for (Map.Entry<String, List<CoveredResource>> entry : resourcesByType.entrySet()) {
                System.out.println("   " + entry.getKey() + ": " + entry.getValue().size() + " resource(s)");
            }

            System.out.println();
            System.out.println("   Sample covered resources:");

            // Show top 3 resources
            for (int i = 0; i < Math.min(coveredResources.size(), 3); i++) {
                CoveredResource resource = coveredResources.get(i);
                System.out.println("     - " + resource.resourceTypeAsString() + ": " + resource.resourceId());
                System.out.println("       Scan Type: " + resource.scanTypeAsString());
                if (resource.scanStatus() != null) {
                    System.out.println("       Status: " + resource.scanStatus().statusCodeAsString());
                }
            }
        }
    }


    public static void displayAccountStatus(BatchGetAccountStatusResponse accountStatus) {
        System.out.println("✅ Inspector Account Status:");

        if (accountStatus.accounts() != null) {
            for (AccountState account : accountStatus.accounts()) {
                System.out.println("   Account ID: " + (account.accountId() != null ? account.accountId() : "Unknown"));

                // Resource state (only status is available)
                ResourceState resources = account.resourceState();
                if (resources != null) {
                    System.out.println("   Resource Status error: ");
                }

                // Overall account state
                if (account.state() != null && account.state().status() != null) {
                    System.out.println("   Overall State: " + account.state().status());
                }
            }
        }
    }


    public static void displayFindings(ListFindingsResponse findingsResponse) {
        List<Finding> findings = findingsResponse.findings();

        if (findings == null || findings.isEmpty()) {
            System.out.println("📭 No findings found");
            System.out.println("💡 This could mean:");
            System.out.println("   • Inspector hasn't completed its initial scan yet");
            System.out.println("   • Your resources don't have any vulnerabilities");
            System.out.println("   • All findings have been suppressed by filters");
        } else {
            System.out.println("✅ Found " + findings.size() + " finding(s):");

            // Group findings by severity
            Map<String, List<Finding>> findingsBySeverity = findings.stream()
                    .collect(Collectors.groupingBy(f -> f.severityAsString()));

            for (Map.Entry<String, List<Finding>> entry : findingsBySeverity.entrySet()) {
                System.out.println("   " + entry.getKey() + ": " + entry.getValue().size() + " finding(s)");
            }

            System.out.println();
            System.out.println("   Recent findings:");

            // Show top 5 findings
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
    }
}
