//snippet-sourcedescription:[GetSegmentById.java demonstrates how to get a segment using its id value.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.segment_id.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentRequest;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
//snippet-end:[pinpoint.java2.segment_id.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetSegmentById {

    public static void main(String[] args) {

        final String usage = "\n" +
            " Usage:" +
            "   <appId> <segmentId>\n\n" +
            " Where:\n" +
            "   appId - The id of the application.\n\n"+
            "   segmentId - The id of the segment.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String appId = args[0];
        String segmentId = args[1];
        PinpointClient pinpoint = PinpointClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println("Name of the segment is " + getSegmentById(pinpoint, appId, segmentId) );
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.segment_id.main]
    private static String getSegmentById(PinpointClient client, String applicationId, String segmentId) {

        try {
            GetSegmentRequest request = GetSegmentRequest.builder()
                .applicationId(applicationId)
                .segmentId(segmentId)
                .build();

            GetSegmentResponse segmentResponse = client.getSegment(request);
            return segmentResponse.segmentResponse().name();

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    //snippet-end:[pinpoint.java2.segment_id.main]
}
