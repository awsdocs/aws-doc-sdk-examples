//snippet-sourcedescription:[getEndpointURL.java demonstrates how to get an endpoint URL for an AWS Elemental MediaConvert account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Elemental MediaConvert]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediaconvert;

// snippet-start:[mediaconvert.java.getendpointurl.complete]
// snippet-start:[mediaconvert.java.getendpointurl.import]
import java.util.Iterator;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.Endpoint;
import software.amazon.awssdk.services.mediaconvert.model.MediaConvertException;
// snippet-end:[mediaconvert.java.getendpointurl.import]

// snippet-start:[mediaconvert.java.getendpointurl.main]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetEndpointURL {
    public static void main(String[] args) {

        // snippet-start:[mediaconvert.java.getendpointurl.build_mediaconvertclient]
        Region region = Region.US_WEST_2;
        MediaConvertClient mc = MediaConvertClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
        // snippet-end:[mediaconvert.java.getendpointurl.build_mediaconvertclient]

        getEndpoint(mc) ;
        mc.close();
    }

    // snippet-start:[mediaconvert.java.getendpointurl.retrieve_endpoints]
    public static void getEndpoint(MediaConvertClient mc) {

        try {
            DescribeEndpointsRequest request = DescribeEndpointsRequest.builder()
                .maxResults(20)
                .build();

            DescribeEndpointsResponse res = mc.describeEndpoints(request);
            Iterator<Endpoint> endpoints = res.endpoints().iterator();
            while (endpoints.hasNext()) {
                System.out.println(endpoints.next().url());
            }

        } catch (MediaConvertException e) {
            System.out.println(e.toString());
            System.exit(0);
        }
    }
    // snippet-end:[mediaconvert.java.getendpointurl.retrieve_endpoints]
}
// snippet-end:[mediaconvert.java.getendpointurl.main]
// snippet-end:[mediaconvert.java.getendpointurl.complete]