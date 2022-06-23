// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteBranch.java demonstrates how to delete a branch.]
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

// snippet-start:[codecommit.java2.del_branch.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.DeleteBranchRequest;
// snippet-end:[codecommit.java2.del_branch.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteBranch {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <repoName> <branchName> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository.\n" +
                "    branchName - the name of the branch. \n" ;

        if (args.length != 2) {
              System.out.println(USAGE);
              System.exit(1);
         }

        String repoName = args[0];
        String branchName = args[1];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        deleteSpecificBranch(codeCommitClient, repoName, branchName);
        codeCommitClient.close();
    }

    // snippet-start:[codecommit.java2.del_branch.main]
    public static void deleteSpecificBranch(CodeCommitClient codeCommitClient, String repoName, String branchName) {

    try {
        DeleteBranchRequest branchRequest = DeleteBranchRequest.builder()
                .branchName(branchName)
                .repositoryName(repoName)
                .build();

        codeCommitClient.deleteBranch(branchRequest);
        System.out.println("The "+branchName + " branch was deleted!");

    } catch (CodeCommitException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
 }
    // snippet-end:[codecommit.java2.del_branch.main]
}