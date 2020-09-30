// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateBranch.java demonstrates how to create a branch.]
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

// snippet-start:[codecommit.java2.create_branch.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.CreateBranchRequest;
// snippet-end:[codecommit.java2.create_branch.import]

public class CreateBranch {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateBranch <repoName> <branchName> <commitId> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository,\n" +
                "    branchName -  the name of the branch,\n" +
                "    commitId  - The ID of the commit to point the new branch to \n";

          if (args.length < 3) {
              System.out.println(USAGE);
              System.exit(1);
          }

        /* Read the name from command args*/
        String repoName = args[0];
        String branchName = args[1];
        String commitId = args[2];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        createSpecificBranch(codeCommitClient, repoName, branchName, commitId);
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
