// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.java.inspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.inspector2.Inspector2Client;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusRequest;
import software.amazon.awssdk.services.inspector2.model.BatchGetAccountStatusResponse;
import software.amazon.awssdk.services.inspector2.model.AccountState;
import software.amazon.awssdk.services.inspector2.model.ListMembersRequest;
import software.amazon.awssdk.services.inspector2.model.ListMembersResponse;
import software.amazon.awssdk.services.inspector2.model.Member;
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
        logger.info("Hello Amazon Inspector!");

        try (Inspector2Client inspectorClient = Inspector2Client.builder().build()) {

            logger.info("Listing member accounts for this Inspector administrator account...");
            listMembers(inspectorClient);

            logger.info("The Hello Inspector example completed successfully.");

        } catch (Inspector2Exception e) {
            logger.error("Error: {}", e.getMessage());
            logger.info("Troubleshooting:");
            logger.info("1. Verify AWS credentials are configured");
            logger.info("2. Check IAM permissions for Inspector2");
            logger.info("3. Ensure Inspector2 is enabled in your account");
            logger.info("4. Verify you're using a supported region");
        }
    }

    /**
     * Lists all member accounts associated with the current Inspector administrator account.
     *
     * @param inspectorClient The Inspector2Client used to interact with AWS Inspector.
     */
    public static void listMembers(Inspector2Client inspectorClient) {
        try {
            ListMembersRequest request = ListMembersRequest.builder()
                    .maxResults(50) // optional: limit results
                    .build();

            ListMembersResponse response = inspectorClient.listMembers(request);
            List<Member> members = response.members();

            if (members == null || members.isEmpty()) {
                logger.info("No member accounts found for this Inspector administrator account.");
                return;
            }

            logger.info("Found {} member account(s):", members.size());
            for (Member member : members) {
                logger.info(" - Account ID: {}, Status: {}",
                        member.accountId(),
                        member.relationshipStatusAsString());
            }

        } catch (Inspector2Exception e) {
            logger.error("Failed to list members: {}", e.awsErrorDetails().errorMessage());
        }
    }
}
// snippet-end:[inspector.java2.hello.main]