// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListRepositories.java demonstrates how to obtain information about all repositories.]
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

// snippet-start:[codecommit.java2.get_repos.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.ListRepositoriesResponse;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.RepositoryNameIdPair;
// snippet-end:[codecommit.java2.get_repos.import]

import java.util.List;

public class ListRepositories {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        listRepos(codeCommitClient);
    }

    // snippet-start:[codecommit.java2.get_repos.main]
    public static void listRepos(CodeCommitClient codeCommitClient) {

        try {
            // Get all repositories
            ListRepositoriesResponse repResponse = codeCommitClient.listRepositories();
            List<RepositoryNameIdPair> repoList = repResponse.repositories();

            for (RepositoryNameIdPair repo: repoList) {
                System.out.println("The repository name is "+repo.repositoryName());
            }

        } catch (CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.get_repos.main]
}
