// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreatePullRequest.java demonstrates how to create a pull request.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CodeCommit]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.commit;

// snippet-start:[codecommit.java2.create_pr.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.CreatePullRequestRequest;
import software.amazon.awssdk.services.codecommit.model.Target;
import software.amazon.awssdk.services.codecommit.model.CreatePullRequestResponse;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[codecommit.java2.create_pr.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreatePullRequest {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <repoName> <destinationReference> <sourceReference> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository.\n" +
                "    destinationReference -  the branch of the repository where the pull request changes are merged.\n" +
                "    sourceReference - the branch of the repository that contains the changes for the pull request.\n" ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String repoName = args[0];
        String destinationReference = args[1];
        String sourceReference = args[2];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        String prId = createPR(codeCommitClient, repoName, destinationReference, sourceReference);
        System.out.println("The pull request id is "+prId);
        codeCommitClient.close();
    }

    // snippet-start:[codecommit.java2.create_pr.main]
    public static String createPR(CodeCommitClient codeCommitClient,
                                  String repoName,
                                  String destinationReference,
                                  String sourceReference) {

        try {
             // Create a Target object that contains the destination and source
             Target target = Target.builder()
                .repositoryName(repoName)
                .destinationReference(destinationReference)
                .sourceReference(sourceReference)
                .build();

             List<Target> myList = new ArrayList<>();
             myList.add(target);

            CreatePullRequestRequest pullRequestRequest = CreatePullRequestRequest.builder()
                .description("A Pull request created by the Java API")
                .title("Example Pull Request")
                .targets(myList)
                .build();

            CreatePullRequestResponse requestResponse = codeCommitClient.createPullRequest(pullRequestRequest);
            return requestResponse.pullRequest().pullRequestId();

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[codecommit.java2.create_pr.main]
}
