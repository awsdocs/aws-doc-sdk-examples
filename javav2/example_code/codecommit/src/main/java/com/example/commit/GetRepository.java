// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetRepository.java demonstrates how to obtain information about a repository.]
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

// snippet-start:[codecommit.java2.get_repo.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.GetRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.GetRepositoryResponse;
// snippet-end:[codecommit.java2.get_repo.import]

public class GetRepository {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetRepository <repoName> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository,\n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String repoName = args[0] ;

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        getRepInformation(codeCommitClient, repoName);

    }

    // snippet-start:[codecommit.java2.get_repo.main]
    public static void getRepInformation(CodeCommitClient codeCommitClient, String repoName) {

        try {
        GetRepositoryRequest repositoryRequest = GetRepositoryRequest.builder()
                .repositoryName(repoName)
                .build();


        // Get information about the repository
        GetRepositoryResponse repositoryResponse = codeCommitClient.getRepository(repositoryRequest);
        System.out.println("The ARN of the "+ repoName +" is " +repositoryResponse.repositoryMetadata().arn());

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.get_repo.main]
}
