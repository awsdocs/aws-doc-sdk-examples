//snippet-sourcedescription:[GetSMSAtrributes.java demonstrates how to etrieve the default SMS type.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
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
//snippet-start:[sns.java2.GetSMSAtrributes.complete]
package com.example.sns;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.GetSmsAttributesRequest;
import software.amazon.awssdk.services.sns.model.GetSmsAttributesResponse;


//snippet-start:[sns.java2.GetSMSAtrributes.import]
//snippet-end:[sns.java2.GetSMSAtrributes.import]

public class GetSMSAtrributes {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "GetSMSAtrributes - retrieve your default SMS type for Amazon SNS.\n" +
                "Usage: GetSMSAtrributes \n\n";

        //snippet-start:[sns.java2.GetSMSAtrributes.main]
        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        GetSmsAttributesRequest request = GetSmsAttributesRequest.builder()
                .attributes("DefaultSMSType")
                .build();

        GetSmsAttributesResponse result = snsClient.getSMSAttributes();

        System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nAttributes: \n\n" + result.attributes());
        //snippet-end:[sns.java2.GetSMSAtrributes.main]
    }
}
//snippet-end:[sns.java2.GetSMSAtrributes.complete]

