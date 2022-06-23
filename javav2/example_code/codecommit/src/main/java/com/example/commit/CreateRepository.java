// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateRepository.java demonstrates how to create a new repository.]
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

// snippet-start:[codecommit.java2.create_repo.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryResponse;
// snippet-end:[codecommit.java2.create_repo.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateRepository {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <repoName> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository. \n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String repoName = args[0];
        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        createRepo(codeCommitClient, repoName);
        codeCommitClient.close();
    }

    // snippet-start:[codecommit.java2.create_repo.main]
    public static void createRepo(CodeCommitClient codeCommitClient, String repoName) {

        try {
            CreateRepositoryRequest repositoryRequest = CreateRepositoryRequest.builder()
                .repositoryDescription("Created by the CodeCommit Java API")
                .repositoryName(repoName)
                .build();

            CreateRepositoryResponse repositoryResponse = codeCommitClient.createRepository(repositoryRequest);
            System.out.println("The ARN of the new repository is "+repositoryResponse.repositoryMetadata().arn());

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.create_repo.main]
}
