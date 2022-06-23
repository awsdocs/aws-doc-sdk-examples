//snippet-sourcedescription:[ListTextTranslationJobs.java demonstrates how to list all translation jobs.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.translate;

// snippet-start:[translate.java2._list_jobs.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.ListTextTranslationJobsRequest;
import software.amazon.awssdk.services.translate.model.ListTextTranslationJobsResponse;
import software.amazon.awssdk.services.translate.model.TextTranslationJobProperties;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._list_jobs.import]

import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListTextTranslationJobs {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getTranslationJobs(translateClient);
        translateClient.close();
    }

    // snippet-start:[translate.java2._list_jobs.main]
    public static void getTranslationJobs( TranslateClient translateClient) {
        try {
        ListTextTranslationJobsRequest textTranslationJobsRequest = ListTextTranslationJobsRequest.builder()
                .maxResults(10)
                .build();

        ListTextTranslationJobsResponse jobsResponse = translateClient.listTextTranslationJobs(textTranslationJobsRequest);
        List<TextTranslationJobProperties> props = jobsResponse.textTranslationJobPropertiesList();

        for (TextTranslationJobProperties prop: props) {
            System.out.println("The job name is: "+prop.jobName());
            System.out.println("The job id is: "+prop.jobId());
        }

    } catch (TranslateException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
 }
    // snippet-end:[translate.java2._list_jobs.main]
}