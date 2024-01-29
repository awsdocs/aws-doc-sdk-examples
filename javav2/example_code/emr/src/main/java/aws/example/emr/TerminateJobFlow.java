// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.emr;

// snippet-start:[emr.java2.terminate_job.main]
// snippet-start:[emr.java2.terminate_job.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.TerminateJobFlowsRequest;
import software.amazon.awssdk.services.emr.model.EmrException;
// snippet-end:[emr.java2.terminate_job.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class TerminateJobFlow {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <id>

                Where:
                   id - An id of a job flow to shut down.

                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String id = args[0];
        Region region = Region.US_WEST_2;
        EmrClient emrClient = EmrClient.builder()
                .region(region)
                .build();

        terminateFlow(emrClient, id);
        emrClient.close();
    }

    public static void terminateFlow(EmrClient emrClient, String id) {
        try {
            TerminateJobFlowsRequest jobFlowsRequest = TerminateJobFlowsRequest.builder()
                    .jobFlowIds(id)
                    .build();

            emrClient.terminateJobFlows(jobFlowsRequest);
            System.out.println("You have successfully terminated " + id);

        } catch (EmrException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[emr.java2.terminate_job.main]