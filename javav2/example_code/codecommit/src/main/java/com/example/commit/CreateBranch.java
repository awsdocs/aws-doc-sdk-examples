// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateBranch.java demonstrates how to create a branch.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
// snippet-service:[AWS CodeCommit]
// snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/28/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.commit;

// snippet-start:[codecommit.java2.create_branch.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.CreateBranchRequest;
// snippet-end:[codecommit.java2.create_branch.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateBranch {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <repoName> <branchName> <commitId> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository.\n" +
                "    branchName -  the name of the branch.\n" +
                "    commitId  - The ID of the commit to point the new branch to. \n";

          if (args.length != 3) {
              System.out.println(USAGE);
              System.exit(1);
          }

        String repoName = args[0];
        String branchName = args[1];
        String commitId = args[2];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        createSpecificBranch(codeCommitClient, repoName, branchName, commitId);
        codeCommitClient.close();
    }

    // snippet-start:[codecommit.java2.create_branch.main]
    public static void createSpecificBranch(CodeCommitClient codeCommitClient,
                                            String repoName,
                                            String branchName,
                                            String commitId) {

      try {
        CreateBranchRequest branchRequest = CreateBranchRequest.builder()
                .branchName(branchName)
                .repositoryName(repoName)
                .commitId(commitId)
                .build();

            codeCommitClient.createBranch(branchRequest);
            System.out.println("Branch "+branchName + " was created");

      } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
      }
    }
    // snippet-end:[codecommit.java2.create_branch.main]
}
