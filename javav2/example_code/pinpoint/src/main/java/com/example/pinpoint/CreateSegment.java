//snippet-sourcedescription:[CreateSegment.java demonstrates how to create a segment for a campaign in Pinpoint.]
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
//snippet-start:[pinpoint.java2.CreateSegment.complete]


package com.example.pinpoint;

//snippet-start:[pinpoint.java2.CreateSegment.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;

import java.util.HashMap;
import java.util.Map;
//snippet-end:[pinpoint.java2.CreateSegment.import]


public class CreateSegment {


    public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateSegment - create a segment \n\n" +
                "Usage: CreateSegment <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID the application to create a segment for.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.CreateSegment.main]
        String appId = args[0];

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        SegmentResponse result = createSegment(pinpoint, appId);
        System.out.println("Segment " + result.name() + " created.");
        System.out.println(result.segmentType());
        //snippet-end:[pinpoint.java2.CreateSegment.main]

    }


    //snippet-start:[pinpoint.java2.CreateSegment.helper]
    private static SegmentResponse createSegment(PinpointClient client, String appId) {
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

        return createSegmentResult.segmentResponse();
    }
    //snippet-end:[pinpoint.java2.CreateSegment.helper]
}
//snippet-end:[pinpoint.java2.CreateSegment.complete]