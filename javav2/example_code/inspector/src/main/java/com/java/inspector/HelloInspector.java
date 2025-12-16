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
}
// snippet-end:[inspector.java2.hello.main]