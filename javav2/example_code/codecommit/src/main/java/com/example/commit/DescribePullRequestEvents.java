// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribePullRequestEvents.java demonstrates how to obtain information about pull request events.]
// snippet-service:[AWS CodeCommit]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CodeCommit]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-30]
// snippet-sourceauthor:[AWS - scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.example.commit;

// snippet-start:[codecommit.java2.describe_pr_events.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.DescribePullRequestEventsRequest;
import software.amazon.awssdk.services.codecommit.model.DescribePullRequestEventsResponse;
import software.amazon.awssdk.services.codecommit.model.PullRequestEvent;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import java.util.List;
// snippet-end:[codecommit.java2.describe_pr_events.import]

public class DescribePullRequestEvents {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribePullRequestEvents <prId> \n\n" +
                "Where:\n" +
                "    prId - the id of the pull request \n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
       String prId = args[0];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        describePREvents(codeCommitClient, prId);
    }

    // snippet-start:[codecommit.java2.describe_pr_events.main]
    public static void describePREvents(CodeCommitClient codeCommitClient, String prId) {

        try {
            DescribePullRequestEventsRequest eventsRequest = DescribePullRequestEventsRequest.builder()
                    .pullRequestId(prId)
                    .build();

            DescribePullRequestEventsResponse eventsResponse = codeCommitClient.describePullRequestEvents(eventsRequest);
            List<PullRequestEvent> events = eventsResponse.pullRequestEvents();
            for (PullRequestEvent event : events) {
                System.out.println("The event name is: "+event.pullRequestEventType().toString());
            }

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.describe_pr_events.main]
}

