// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.commit;

// snippet-start:[codecommit.java2.get_pull_request.main]
// snippet-start:[codecommit.java2.get_pull_request.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestRequest;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestResponse;
// snippet-end:[codecommit.java2.get_pull_request.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development
 * environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetPullRequest {
    public static void main(String[] args) {
        final String USAGE = """

                Usage:
                    <pullRequestId>\s

                Where:
                    pullRequestId - the id of the pull request.\s
                """;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String pullRequestId = args[0];
        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        getPR(codeCommitClient, pullRequestId);
        codeCommitClient.close();
    }

    public static void getPR(CodeCommitClient codeCommitClient, String pullRequestId) {
        try {
            GetPullRequestRequest pullRequestRequest = GetPullRequestRequest.builder()
                    .pullRequestId(pullRequestId)
                    .build();

            GetPullRequestResponse pullResponse = codeCommitClient.getPullRequest(pullRequestRequest);
            System.out.println("The title of the pull request is  " + pullResponse.pullRequest().title());
            System.out.println("The pull request status is " + pullResponse.pullRequest().pullRequestStatus());
            System.out.println("The pull request id is " + pullResponse.pullRequest().pullRequestId());

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[codecommit.java2.get_pull_request.main]