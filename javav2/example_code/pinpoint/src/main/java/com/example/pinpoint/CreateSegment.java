//snippet-sourcedescription:[CreateSegment.java demonstrates how to create a segment for a campaign in Amazon Pinpoint.]
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

//snippet-start:[pinpoint.java2.createsegment.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.AttributeDimension;
import software.amazon.awssdk.services.pinpoint.model.SegmentResponse;
import software.amazon.awssdk.services.pinpoint.model.AttributeType;
import software.amazon.awssdk.services.pinpoint.model.RecencyDimension;
import software.amazon.awssdk.services.pinpoint.model.SegmentBehaviors;
import software.amazon.awssdk.services.pinpoint.model.SegmentDemographics;
import software.amazon.awssdk.services.pinpoint.model.SegmentLocation;
import software.amazon.awssdk.services.pinpoint.model.SegmentDimensions;
import software.amazon.awssdk.services.pinpoint.model.WriteSegmentRequest;
import software.amazon.awssdk.services.pinpoint.model.CreateSegmentRequest;
import software.amazon.awssdk.services.pinpoint.model.CreateSegmentResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import java.util.HashMap;
import java.util.Map;
//snippet-end:[pinpoint.java2.createsegment.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateSegment {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "  <appId>\n\n" +
                "Where:\n" +
                "  appId - The application ID to create a segment for.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String appId = args[0];
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        SegmentResponse result = createSegment(pinpoint, appId);
        System.out.println("Segment " + result.name() + " created.");
        System.out.println(result.segmentType());
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.createsegment.main]
    public static SegmentResponse createSegment(PinpointClient client, String appId) {

        try {
            Map<String, AttributeDimension> segmentAttributes = new HashMap<>();
            segmentAttributes.put("Team", AttributeDimension.builder()
                    .attributeType(AttributeType.INCLUSIVE)
                    .values("Lakers")
                    .build());

            RecencyDimension recencyDimension = RecencyDimension.builder()
                    .duration("DAY_30")
                    .recencyType("ACTIVE")
                    .build();

            SegmentBehaviors segmentBehaviors = SegmentBehaviors.builder()
                    .recency(recencyDimension)
                    .build();

            SegmentDemographics segmentDemographics = SegmentDemographics
                    .builder()
                    .build();

            SegmentLocation segmentLocation = SegmentLocation
                    .builder()
                    .build();

            SegmentDimensions dimensions = SegmentDimensions
                    .builder()
                    .attributes(segmentAttributes)
                    .behavior(segmentBehaviors)
                    .demographic(segmentDemographics)
                    .location(segmentLocation)
                    .build();

            WriteSegmentRequest writeSegmentRequest = WriteSegmentRequest.builder()
                    .name("MySegment")
                    .dimensions(dimensions)
                    .build();

            CreateSegmentRequest createSegmentRequest = CreateSegmentRequest.builder()
                    .applicationId(appId)
                    .writeSegmentRequest(writeSegmentRequest)
                    .build();

            CreateSegmentResponse createSegmentResult = client.createSegment(createSegmentRequest);
            System.out.println("Segment ID: " + createSegmentResult.segmentResponse().id());
            System.out.println("Done");
            return createSegmentResult.segmentResponse();

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    //snippet-end:[pinpoint.java2.createsegment.main]
}