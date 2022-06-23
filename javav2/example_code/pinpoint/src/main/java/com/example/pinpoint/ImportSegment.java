//snippet-sourcedescription:[ImportSegment.java demonstrates how to how to import a segment into Amazon Pinpoint.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.pinpoint;

//snippet-start:[pinpoint.java2.importsegment.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.CreateImportJobRequest;
import software.amazon.awssdk.services.pinpoint.model.ImportJobResponse;
import software.amazon.awssdk.services.pinpoint.model.ImportJobRequest;
import software.amazon.awssdk.services.pinpoint.model.Format;
import software.amazon.awssdk.services.pinpoint.model.CreateImportJobResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
//snippet-end:[pinpoint.java2.importsegment.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ImportSegment {
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "  <appId> <bucket> <key> <roleArn> \n\n" +
                "Where:\n" +
                "  appId - The application ID to create a segment for.\n\n" +
                "  bucket - The name of the Amazon S3 bucket that contains the segment definitons.\n\n" +
                "  key - The key of the S3 object. " +
                "  roleArn - ARN of the role that allows Amazon Pinpoint to access S3. You need to set trust management for this to work. See https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String appId = args[0];
        String bucket = args[1];
        String key = args[2];
        String roleArn = args[3];

        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ImportJobResponse response = createImportSegment(pinpoint, appId, bucket, key, roleArn);
        System.out.println("Import job for " + bucket + " submitted.");
        System.out.println("See application " + response.applicationId() + " for import job status.");
        System.out.println("See application " + response.jobStatus() + " for import job status.");
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.importsegment.main]
    public static ImportJobResponse createImportSegment(PinpointClient client,
                                                        String appId,
                                                        String bucket,
                                                        String key,
                                                        String roleArn) {

        try {
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

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    //snippet-end:[pinpoint.java2.importsegment.main]
}