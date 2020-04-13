//snippet-sourcedescription:[ListSegements.java demonstrates how to list segments in an application.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


package com.example.pinpoint;

//snippet-start:[pinpoint.java2.listsegments.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentsRequest;
import software.amazon.awssdk.services.pinpoint.model.GetSegmentsResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.SegmentResponse;
import java.util.List;
//snippet-end:[pinpoint.java2.listsegments.import]

public class ListSegements {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "ListSegment - list segments \n\n" +
                "Usage: ListSegments <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application that contains a segment.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String appId = args[0];
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listSegs(pinpoint, appId);

    }

    //snippet-start:[pinpoint.java2.listsegments.main]
    public static void listSegs( PinpointClient pinpoint, String appId) {

        try {
            GetSegmentsRequest request = GetSegmentsRequest.builder()
                    .applicationId(appId)
                    .build();

            GetSegmentsResponse response = pinpoint.getSegments(request);
            List<SegmentResponse> segments = response.segmentsResponse().item();

            for(SegmentResponse segment: segments) {
                System.out.println("Segement " + segment.id() + " " + segment.name() + " " + segment.lastModifiedDate());
            }
        } catch ( PinpointException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
        }
       //snippet-end:[pinpoint.java2.listsegments.main]
    }
}
