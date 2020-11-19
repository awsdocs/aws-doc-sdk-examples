//snippet-sourcedescription:[getEndpointURL.java demonstrates how to get mediaconvert account endpoint URL.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[smacdon - AWS ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediaconvert;

// snippet-start:[mediaconvert.java.getendpointurl.complete]
// snippet-start:[mediaconvert.java.getendpointurl.import]
import java.util.Iterator;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.Endpoint;
import software.amazon.awssdk.services.mediaconvert.model.MediaConvertException;
// snippet-end:[mediaconvert.java.getendpointurl.import]

// snippet-start:[mediaconvert.java.getendpointurl.main]
public class GetEndpointURL {
    public static void main(String[] args) {

            // snippet-start:[mediaconvert.java.getendpointurl.build_mediaconvertclient]
            Region region = Region.US_WEST_2;
            MediaConvertClient mc = MediaConvertClient.builder()
                    .region(region)
                    .build();
            // snippet-end:[mediaconvert.java.getendpointurl.build_mediaconvertclient]
            getEndpoint(mc) ;
            mc.close();
    }

    // snippet-start:[mediaconvert.java.getendpointurl.retrieve_endpoints]
    public static void getEndpoint(MediaConvertClient mc) {

        try {
            DescribeEndpointsResponse res = mc
                    .describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

            Iterator<Endpoint> endpoints = res.endpoints().iterator();
            while (endpoints.hasNext()) {
                System.out.println(endpoints.next().url());
            }

        } catch (MediaConvertException e) {
            System.out.println(e.toString());
            System.exit(0);
        }
            // snippet-end:[mediaconvert.java.getendpointurl.retrieve_endpoints]
     }
}
// snippet-end:[mediaconvert.java.getendpointurl.main]
// snippet-end:[mediaconvert.java.getendpointurl.complete]
