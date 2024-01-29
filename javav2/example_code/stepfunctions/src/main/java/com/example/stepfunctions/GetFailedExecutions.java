// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.stepfunctions;

// snippet-start:[stepfunctions.java2.get_failed_exes.main]
// snippet-start:[stepfunctions.java2.get_failed_exes.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.ListExecutionsRequest;
import software.amazon.awssdk.services.sfn.model.ListExecutionsResponse;
import software.amazon.awssdk.services.sfn.model.ExecutionListItem;
import software.amazon.awssdk.services.sfn.model.SfnException;
import java.util.List;
// snippet-end:[stepfunctions.java2.get_failed_exes.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetFailedExecutions {
    public static void main(String[] args) {

        final String usage = """

                Usage:
                    <stateMachineARN>

                Where:
                    stateMachineARN - The ARN of the state machine.
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String stateMachineARN = args[0];
        Region region = Region.US_WEST_2;
        SfnClient sfnClient = SfnClient.builder()
                .region(region)
                .build();

        getFailedExes(sfnClient, stateMachineARN);
        sfnClient.close();
    }

    public static void getFailedExes(SfnClient sfnClient, String stateMachineARN) {
        try {
            ListExecutionsRequest executionsRequest = ListExecutionsRequest.builder()
                    .maxResults(10)
                    .stateMachineArn(stateMachineARN)
                    .build();

            ListExecutionsResponse response = sfnClient.listExecutions(executionsRequest);
            List<ExecutionListItem> items = response.executions();
            for (ExecutionListItem item : items) {
                System.out.println("The Amazon Resource Name (ARN) of the failed execution is " + item.executionArn());
            }

        } catch (SfnException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[stepfunctions.java2.get_failed_exes.main]
}
