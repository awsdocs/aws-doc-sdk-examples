//snippet-sourcedescription:[GetJob.java demonstrates how to get information about a specific MediaConvert job.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/30/2020]
//snippet-sourceauthor:[smacdon - AWS ]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mediaconvert;

// snippet-start:[mediaconvert.java.get_job.import]
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.model.GetJobRequest;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.GetJobResponse;
import java.net.URI;
// snippet-end:[mediaconvert.java.get_job.import]

public class GetJob {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "GetJob <jobId> \n\n" +
                "Where:\n" +
                "  jobId - the job id value.\n\n" ;

        if (args.length < 1) {
              System.out.println(USAGE);
              System.exit(1);
          }

        String jobId = args[0];

        Region region = Region.US_WEST_2;
        MediaConvertClient mc = MediaConvertClient.builder()
                .region(region)
                .build();

        getSpecificJob(mc, jobId);
    }

    // snippet-start:[mediaconvert.java.get_job.main]
    public static void getSpecificJob(MediaConvertClient mc, String jobId) {

        try {
        DescribeEndpointsResponse res = mc.describeEndpoints(DescribeEndpointsRequest.builder()
                        .maxResults(20)
                        .build());

        if (res.endpoints().size() <= 0) {
            System.out.println("Cannot find MediaConvert service endpoint URL!");
            System.exit(1);
        }
        String endpointURL = res.endpoints().get(0).url();

        MediaConvertClient emc = MediaConvertClient.builder()
                .region(Region.US_WEST_2)
                .endpointOverride(URI.create(endpointURL))
                .build();

        GetJobRequest jobRequest = GetJobRequest.builder()
                .id(jobId)
                .build();

        GetJobResponse response = emc.getJob(jobRequest);
        System.out.println("The ARN of the job is "+response.job().arn());

    } catch (SdkException e) {
        System.out.println(e.toString());
    }
    // snippet-end:[mediaconvert.java.get_job.main]
   }
}
