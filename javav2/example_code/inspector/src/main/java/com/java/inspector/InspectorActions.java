package com.java.inspector;

import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.*;

import java.util.Collections;
import java.util.List;

public class InspectorActions {

    // ---------------------- ENABLE INSPECTOR ----------------------
    public EnableResponse enableInspector(
            Inspector2Client inspectorClient,
            List<ResourceScanType> resourceTypes,
            List<String> accountIds // can be null
    ) {
        EnableRequest.Builder requestBuilder = EnableRequest.builder()
                .resourceTypes(resourceTypes);

        if (accountIds != null && !accountIds.isEmpty()) {
            requestBuilder.accountIds(accountIds);
        }

        EnableRequest request = requestBuilder.build();

        try {
            EnableResponse response = inspectorClient.enable(request);
            System.out.println("✅ Inspector enable request completed");

            if (response.accounts() != null) {
                for (Account account : response.accounts()) {
                    String accountId = account.accountId() != null ? account.accountId() : "Unknown";
                    String status = account.status() != null ? account.statusAsString() : "Unknown";
                    System.out.println("Account: " + accountId + ", Status: " + status);
                }
            }

            return response;

        } catch (Exception e) {
            System.err.println("❌ Failed to enable Inspector: " + e.getMessage());
            throw e;
        }
    }

    // ---------------------- COVERAGE STATISTICS ----------------------
    public static ListCoverageStatisticsResponse listCoverageStatistics(Inspector2Client inspectorClient) {
        ListCoverageStatisticsRequest request = ListCoverageStatisticsRequest.builder()
                .build();

        return inspectorClient.listCoverageStatistics(request);
    }

    // ---------------------- LIST USAGE TOTALS ----------------------
    public ListUsageTotalsResponse listUsageTotals(
            Inspector2Client inspectorClient,
            List<String> accountIds,
            int maxResults
    ) {
        ListUsageTotalsRequest.Builder requestBuilder = ListUsageTotalsRequest.builder()
                .maxResults(maxResults);

        if (accountIds != null && !accountIds.isEmpty()) {
            requestBuilder.accountIds(accountIds);
        }

        ListUsageTotalsRequest request = requestBuilder.build();
        return inspectorClient.listUsageTotals(request);
    }

    // ---------------------- ACCOUNT STATUS ----------------------
    public BatchGetAccountStatusResponse getAccountStatus(Inspector2Client inspectorClient) {
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

        return response;
    }

    // ---------------------- LIST FILTERS ----------------------
    public static ListFiltersResponse listFilters(
            Inspector2Client inspector2Client,
            Integer maxResults
    ) {
        try {
            System.out.println("Listing filters");

            ListFiltersRequest.Builder requestBuilder = ListFiltersRequest.builder();
            if (maxResults != null) {
                requestBuilder.maxResults(maxResults);
            }

            ListFiltersResponse response = inspector2Client.listFilters(requestBuilder.build());

            int count = response.filters() != null ? response.filters().size() : 0;
            System.out.println("Successfully listed filters: " + count);
            return response;

        } catch (Inspector2Exception e) {
            System.out.println("Failed to list filters: " + e.getMessage());
            throw e;
        }
    }

    // ---------------------- CREATE FILTER ----------------------
    public static CreateFilterResponse createFilter(
            Inspector2Client inspector2Client,
            String description
    ) {
        try {
            String filterName = "suppress-low-severity-" + System.currentTimeMillis();
            System.out.println("Creating filter: " + filterName);

            // Build severity filter
            StringFilter severityFilter = StringFilter.builder()
                    .value(Severity.LOW.toString())
                    .comparison(StringComparison.EQUALS)
                    .build();

            FilterCriteria filterCriteria = FilterCriteria.builder()
                    .severity(List.of(severityFilter))
                    .build();

            CreateFilterRequest createRequest = CreateFilterRequest.builder()
                    .name(filterName)
                    .filterCriteria(filterCriteria)
                    .action(FilterAction.SUPPRESS)
                    .description(description)
                    .build();

            CreateFilterResponse response = inspector2Client.createFilter(createRequest);
            System.out.println("✅ Successfully created filter with ARN: " + response.arn());
            return response;

        } catch (Inspector2Exception e) {
            System.err.println("❌ Failed to create filter: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            throw e;
        }
    }

    // ---------------------- LIST FINDINGS ----------------------
    public ListFindingsResponse listFindings(
            Inspector2Client inspectorClient,
            int maxResults,
            FilterCriteria filterCriteria // can be null
    ) {
        ListFindingsRequest.Builder requestBuilder = ListFindingsRequest.builder()
                .maxResults(maxResults);

        if (filterCriteria != null) {
            requestBuilder.filterCriteria(filterCriteria);
        }

        ListFindingsRequest request = requestBuilder.build();
        ListFindingsResponse response = inspectorClient.listFindings(request);

        List<Finding> findings = response.findings();
        int count = findings != null ? findings.size() : 0;
        System.out.println("✅ Listed " + count + " findings");

        return response;
    }

    // ---------------------- LIST COVERAGE ----------------------
    public ListCoverageResponse listCoverage(Inspector2Client inspectorClient, int maxResults) {
        ListCoverageRequest request = ListCoverageRequest.builder()
                .maxResults(maxResults)
                .build();

        return inspectorClient.listCoverage(request);
    }
}
