//snippet-sourcedescription:[getEndpointURL.java demonstrates how to get mediaconvert account endpoint URL.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/30/2020]
//snippet-sourceauthor:[smacdon - AWS]
// snippet-start:[mediaconvert.java.getendpointurl.complete]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mediaconvert;

// snippet-start:[mediaconvert.java.getendpointurl.import]
import java.util.Iterator;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.Endpoint;
// snippet-end:[mediaconvert.java.getendpointurl.import]

/**
 * Get MediaConvert service endpoint URL
 */

// snippet-start:[mediaconvert.java.getendpointurl.main]
public class GetEndpointURL {
    public static void main(String[] args) {
        try {
            // snippet-start:[mediaconvert.java.getendpointurl.build_mediaconvertclient]
            MediaConvertClient mc = MediaConvertClient.builder().build();
            // snippet-end:[mediaconvert.java.getendpointurl.build_mediaconvertclient]

            getEndpoint(mc) ;

        } catch (SdkException e) {
            System.out.println(e.toString());
        }

    }
    // snippet-start:[mediaconvert.java.getendpointurl.retrieve_endpoints]
    public static void getEndpoint(MediaConvertClient mc) {

            DescribeEndpointsResponse res = mc
                    .describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

            Iterator<Endpoint> endpoints = res.endpoints().iterator();
            while (endpoints.hasNext()) {
                System.out.println(endpoints.next().url());
            }
            // snippet-end:[mediaconvert.java.getendpointurl.retrieve_endpoints]
     }
}
// snippet-end:[mediaconvert.java.getendpointurl.main]
// snippet-end:[mediaconvert.java.getendpointurl.complete]
