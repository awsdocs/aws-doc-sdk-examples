// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[MergeBranches.java demonstrates how to merge two branches.]
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

// snippet-start:[codecommit.java2.merge.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.MergeBranchesByFastForwardRequest;
import software.amazon.awssdk.services.codecommit.model.MergeBranchesByFastForwardResponse;
// snippet-end:[codecommit.java2.merge.import]

public class MergeBranches {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreatePullRequest <repoName> <targetBranch> <sourceReference> <destinationCommitId>\n\n" +
                "Where:\n" +
                "    repoName - the name of the repository,\n" +
                "    targetBranch -  the branch where the merge is applied,\n" +
                "    sourceReference  - the branch of the repository that contains the changes.\n" +
                "    destinationCommitId  - a full commit ID.\n" ;

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String repoName = args[0];
        String targetBranch = args[1];
        String sourceReference = args[2];
        String destinationCommitId = args[3];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        merge(codeCommitClient, repoName, targetBranch, sourceReference, destinationCommitId) ;

    }

    // snippet-start:[codecommit.java2.merge.main]
    public static void merge(CodeCommitClient codeCommitClient,
                      String repoName,
                      String targetBranch,
                      String sourceReference,
                      String destinationCommitId) {

        try {
            MergeBranchesByFastForwardRequest fastForwardRequest = MergeBranchesByFastForwardRequest.builder()
                .destinationCommitSpecifier(destinationCommitId)
                .targetBranch(targetBranch)
                .sourceCommitSpecifier(sourceReference)
                .repositoryName(repoName)
                .build();

            MergeBranchesByFastForwardResponse response = codeCommitClient.mergeBranchesByFastForward(fastForwardRequest);
            System.out.println("The commit id is "+response.commitId());

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.merge.main]
}
