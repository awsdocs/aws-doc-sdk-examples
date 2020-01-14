//snippet-sourcedescription:[ListSegements.java demonstrates how to list segements in an application .]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
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
//snippet-start:[pinpoint.java2.ListSegments.complete]

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.ListSegments.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentsRequest;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentsResponse;
import software.amazon.awssdk.services.pinpoint.model.SegmentResponse;

import java.util.List;
//snippet-end:[pinpoint.java2.ListSegments.import]

public class ListSegements {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "ListSegment - list segments \n\n" +
                "Usage: ListSegments <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID the application to that contains a segment.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[pinpoint.java2.ListSegments.main]
        String appId = args[0];

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        GetSegmentsRequest request = GetSegmentsRequest.builder().applicationId(appId).build();
        GetSegmentsResponse response = pinpoint.getSegments(request);
        List<SegmentResponse> segments = response.segmentsResponse().item();

        for(SegmentResponse segment: segments) {
            System.out.println("Segement " + segment.id() + " " + segment.name() + " " + segment.lastModifiedDate());
        }
        //snippet-end:[pinpoint.java2.ListSegments.main]
    }
}
//snippet-end:[pinpoint.java2.ListSegments.complete]