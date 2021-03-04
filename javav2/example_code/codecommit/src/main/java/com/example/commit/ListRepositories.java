// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListRepositories.java demonstrates how to obtain information about all repositories.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
// snippet-service:[AWS CodeCommit]
// snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2020]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
        codeCommitClient.close();
    }

    // snippet-start:[codecommit.java2.get_repos.main]
    public static void listRepos(CodeCommitClient codeCommitClient) {

        try {
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
