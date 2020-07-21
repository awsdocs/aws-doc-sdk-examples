//snippet-sourcedescription:[ListTextTranslationJobs.java demonstrates how to list all translation jobs.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/20/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.translate;

// snippet-start:[translate.java2._list_jobs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.ListTextTranslationJobsRequest;
import software.amazon.awssdk.services.translate.model.ListTextTranslationJobsResponse;
import software.amazon.awssdk.services.translate.model.TextTranslationJobProperties;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._list_jobs.import]

import java.util.List;

public class ListTextTranslationJobs {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .build();

        getTranslationJobs(translateClient);
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
        // snippet-end:[translate.java2._list_jobs.main]
    }
}
