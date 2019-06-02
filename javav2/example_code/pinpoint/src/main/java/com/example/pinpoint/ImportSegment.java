//snippet-sourcedescription:[ImportSegment.java demonstrates how to how to import a segment into Pinpoint.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-01]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
//snippet-start:[pinpoint.java2.ImportSegment.complete]
package com.example.pinpoint;

//snippet-start:[pinpoint.java2.ImportSegment.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
//snippet-end:[pinpoint.java2.ImportSegment.import]


public class ImportSegment {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "ImportSegment - import a segment \n\n" +
                "Usage: ImportSegment <appId> <bucket> <key> <roleArn> \n\n" +
                "Where:\n" +
                "  appId - the ID the application to create a segment for.\n\n" +
                "  bucket - name of the s3 bucket that contains the segment definitons.\n\n" +
                "  key - key of the s3 object " +
                "  roleArn - ARN of the role that allows pinpoint to access S3";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.DeleteApp.main]
        String appId = args[0];
        String bucket = args[1];
        String key = args[2];
        String roleArn = args[3];

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        ImportJobResponse response = createImportSegment(pinpoint, appId, bucket, key, roleArn);
        System.out.println("Import job for " + bucket + " submitted.");
        System.out.println("See application " + response.applicationId() + " for import job status.");
        //snippet-end:[pinpoint.java2.DeleteApp.main]
    }

    //snippet-start:[pinpoint.java2.DeleteApp.helper]
    public static ImportJobResponse createImportSegment(PinpointClient client,
                                                        String appId,
                                                        String bucket,
                                                        String key,
                                                        String roleArn) {

        // Create the job.
        ImportJobRequest importRequest = ImportJobRequest.builder()
                .defineSegment(true)
                .registerEndpoints(true)
                .roleArn(roleArn)
                .format(Format.JSON)
                .s3Url("s3://" + bucket + "/" + key)
                .build();

        CreateImportJobRequest jobRequest = CreateImportJobRequest.builder()
                .importJobRequest(importRequest)
                .applicationId(appId)
                .build();

        CreateImportJobResponse jobResponse = client.createImportJob(jobRequest);

        return jobResponse.importJobResponse();
    }
    //snippet-start:[pinpoint.java2.DeleteApp.helper]
}
//snippet-end:[pinpoint.java2.ImportSegment.complete]