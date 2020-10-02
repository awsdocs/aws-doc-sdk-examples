// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetPullRequest.java demonstrates how to obtain information about a pull request.]
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

// snippet-start:[codecommit.java2.get_pull_request.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestRequest;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestResponse;
// snippet-end:[codecommit.java2.get_pull_request.import]

public class GetPullRequest {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetPullRequest <pullRequestId> \n\n" +
                "Where:\n" +
                "    pullRequestId - the id of the pull request \n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String pullRequestId = args[0];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        getPR(codeCommitClient, pullRequestId) ;
    }

    // snippet-start:[codecommit.java2.get_pull_request.main]
    public static void getPR(CodeCommitClient codeCommitClient, String pullRequestId ) {

        try {

            GetPullRequestRequest pullRequestRequest = GetPullRequestRequest.builder()
                    .pullRequestId(pullRequestId)
                    .build();

            GetPullRequestResponse pullResponse = codeCommitClient.getPullRequest(pullRequestRequest);
            System.out.println("The title of the pull request is  "+pullResponse.pullRequest().title());
            System.out.println("The pull request status is "+pullResponse.pullRequest().pullRequestStatus());
            System.out.println("The pull request id is "+pullResponse.pullRequest().pullRequestId());

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.get_pull_request.main]
}


