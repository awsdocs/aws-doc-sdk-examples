// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[pinpoint.java.pinpoint_create_segment.complete]

import com.amazonaws.services.pinpoint.AmazonPinpointClient;
import com.amazonaws.services.pinpoint.model.AttributeDimension;
import com.amazonaws.services.pinpoint.model.AttributeType;
import com.amazonaws.services.pinpoint.model.CreateSegmentRequest;
import com.amazonaws.services.pinpoint.model.CreateSegmentResult;
import com.amazonaws.services.pinpoint.model.RecencyDimension;
import com.amazonaws.services.pinpoint.model.SegmentBehaviors;
import com.amazonaws.services.pinpoint.model.SegmentDemographics;
import com.amazonaws.services.pinpoint.model.SegmentDimensions;
import com.amazonaws.services.pinpoint.model.SegmentLocation;
import com.amazonaws.services.pinpoint.model.SegmentResponse;
import com.amazonaws.services.pinpoint.model.WriteSegmentRequest;

import java.util.HashMap;
import java.util.Map;

public class PinpointSegmentSample {

        public SegmentResponse createSegment(AmazonPinpointClient client, String appId) {
                Map<String, AttributeDimension> segmentAttributes = new HashMap<>();
                segmentAttributes.put("Team", new AttributeDimension().withAttributeType(AttributeType.INCLUSIVE)
                                .withValues("Lakers"));

                SegmentBehaviors segmentBehaviors = new SegmentBehaviors();
                SegmentDemographics segmentDemographics = new SegmentDemographics();
                SegmentLocation segmentLocation = new SegmentLocation();

                RecencyDimension recencyDimension = new RecencyDimension();
                recencyDimension.withDuration("DAY_30").withRecencyType("ACTIVE");
                segmentBehaviors.setRecency(recencyDimension);

                SegmentDimensions dimensions = new SegmentDimensions()
                                .withAttributes(segmentAttributes)
                                .withBehavior(segmentBehaviors)
                                .withDemographic(segmentDemographics)
                                .withLocation(segmentLocation);

                WriteSegmentRequest writeSegmentRequest = new WriteSegmentRequest()
                                .withName("MySegment").withDimensions(dimensions);

                CreateSegmentRequest createSegmentRequest = new CreateSegmentRequest()
                                .withApplicationId(appId).withWriteSegmentRequest(writeSegmentRequest);

                CreateSegmentResult createSegmentResult = client.createSegment(createSegmentRequest);

                System.out.println("Segment ID: " + createSegmentResult.getSegmentResponse().getId());

                return createSegmentResult.getSegmentResponse();
        }

}

// snippet-end:[pinpoint.java.pinpoint_create_segment.complete]
