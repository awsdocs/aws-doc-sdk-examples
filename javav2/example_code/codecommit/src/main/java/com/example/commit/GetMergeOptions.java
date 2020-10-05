// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetMergeOptions.java demonstrates how to get merge options.]
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

// snippet-start:[codecommit.java2.get_merge_options.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.GetMergeOptionsRequest;
import software.amazon.awssdk.services.codecommit.model.GetMergeOptionsResponse;
// snippet-end:[codecommit.java2.get_merge_options.import]

public class GetMergeOptions {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetMergeOptions <repoName> <destinationReference> <sourceReference> \n\n" +
                "Where:\n" +
                "    repoName - the name of the repository,\n" +
                "    destinationReference -  the branch of the repository where the pull request changes are merged,\n" +
                "    sourceReference  - the branch of the repository that contains the changes for the pull request.\n" ;

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String repoName = args[0];
        String destinationReference = args[1];
        String sourceReference = args[2];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        String commitId = getMergeValues(codeCommitClient, repoName, destinationReference, sourceReference);
        System.out.println("The id value is "+commitId);

    }

    // snippet-start:[codecommit.java2.get_merge_options.main]
    public static String getMergeValues(CodeCommitClient codeCommitClient,
                                        String repoName,
                                        String destinationReference,
                                        String sourceReference) {

        try {
            GetMergeOptionsRequest optionsRequest = GetMergeOptionsRequest.builder()
                .repositoryName(repoName)
                .destinationCommitSpecifier(destinationReference)
                .sourceCommitSpecifier(sourceReference)
                .build();

            GetMergeOptionsResponse response = codeCommitClient.getMergeOptions(optionsRequest) ;
            return response.baseCommitId();

        } catch ( CodeCommitException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[codecommit.java2.get_merge_options.main]
 }
