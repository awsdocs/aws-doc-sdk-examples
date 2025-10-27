// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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

import java.util.List;

// snippet-start:[inspector.java2.hello.main]
public class HelloInspector {

    public static void main(String[] args) {
        System.out.println("🔍 Hello Amazon Inspector!");
        Region region = Region.US_EAST_1;

        try (Inspector2Client inspectorClient = Inspector2Client.builder()
                .region(region)
                .build()) {

            System.out.println("🔍 Checking Inspector account status...");
            checkAccountStatus(inspectorClient);
            System.out.println();

            System.out.println("📋 Checking for recent findings...");
            listRecentFindings(inspectorClient);
            System.out.println();

            System.out.println("💰 Checking usage totals...");
            showUsageTotals(inspectorClient);
            System.out.println();

            System.out.println("🎉 Hello Inspector example completed successfully!");

        } catch (Inspector2Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("🔧 Troubleshooting:");
            System.err.println("1. Verify AWS credentials are configured");
            System.err.println("2. Check IAM permissions for Inspector2");
            System.err.println("3. Ensure Inspector2 is enabled in your account");
            System.err.println("4. Verify you're using a supported region");
        }
    }

    // ✅ Fixed version using BatchGetAccountStatus
    private static void checkAccountStatus(Inspector2Client inspectorClient) {
        try {
            BatchGetAccountStatusRequest request = BatchGetAccountStatusRequest.builder().build();
            BatchGetAccountStatusResponse response = inspectorClient.batchGetAccountStatus(request);

            List<AccountState> accounts = response.accounts();
            if (accounts == null || accounts.isEmpty()) {
                System.out.println("❌ No account information returned.");
                return;
            }

            for (AccountState account : accounts) {
                System.out.println("✅ Account: " + account.accountId());
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
            System.err.println("❌ Failed to retrieve account status: " + e.awsErrorDetails().errorMessage());
        }
    }

    private static void printState(String name, State state) {
        if (state == null) {
            System.out.println("     - " + name + ": (no data)");
            return;
        }
        String err = state.errorMessage() != null ? " (Error: " + state.errorMessage() + ")" : "";
        System.out.println("     - " + name + ": " + state.status() + err);
    }

    private static void listRecentFindings(Inspector2Client inspectorClient) {
        try {
            ListFindingsRequest request = ListFindingsRequest.builder()
                    .maxResults(10)
                    .build();

            ListFindingsResponse response = inspectorClient.listFindings(request);
            List<Finding> findings = response.findings();

            if (findings == null || findings.isEmpty()) {
                System.out.println("📭 No findings found.");
            } else {
                System.out.println("✅ Found " + findings.size() + " recent finding(s):");
                for (Finding finding : findings) {
                    System.out.println("   Title: " + finding.title());
                    System.out.println("   Severity: " + finding.severity());
                    System.out.println("   Status: " + finding.status());
                    System.out.println("   Last Observed: " + finding.lastObservedAt());
                    System.out.println();
                }
            }

        } catch (Inspector2Exception e) {
            System.err.println("❌ Error listing findings: " + e.awsErrorDetails().errorMessage());
        }
    }

    private static void showUsageTotals(Inspector2Client inspectorClient) {
        try {
            ListUsageTotalsRequest request = ListUsageTotalsRequest.builder()
                    .maxResults(10)
                    .build();

            ListUsageTotalsResponse response = inspectorClient.listUsageTotals(request);
            List<UsageTotal> totals = response.totals();

            if (totals == null || totals.isEmpty()) {
                System.out.println("📊 No usage data available yet.");
            } else {
                System.out.println("✅ Usage Totals (Last 30 days):");
                for (UsageTotal total : totals) {
                    System.out.println("   Account: " + total.accountId());
                    total.usage().forEach(u ->
                            System.out.println("     - " + u.type() + ": " + u.total() +
                                    " (Est. Monthly: " + u.estimatedMonthlyCost() + " " + u.currency() + ")"));
                    System.out.println();
                }
            }

        } catch (Inspector2Exception e) {
            System.err.println("❌ Error getting usage totals: " + e.awsErrorDetails().errorMessage());
        }
    }
}
// snippet-end:[inspector.java2.hello.main]
