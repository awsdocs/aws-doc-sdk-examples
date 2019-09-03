//snippet-sourcedescription:[SetSMSAttributes.java demonstrates how to retrieve the default SMS type.]
//snippet-keyword:[Java]
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
//snippet-start:[sns.java2.SetSMSAttributes.complete]
package com.example.sns;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SetSmsAttributesRequest;
import software.amazon.awssdk.services.sns.model.SetSmsAttributesResponse;

import java.util.HashMap;


//snippet-start:[sns.java2.SetSMSAttributes.import]
//snippet-end:[sns.java2.SetSMSAttributes.import]

public class SetSMSAttributes {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "SetSMSAttributes - set your default SMS type for Amazon SNS.\n" +
                "Usage: SetSMSAttributes \n\n";

        //snippet-start:[sns.java2.SetSMSAttributes.main]
        HashMap<String, String> attributes = new HashMap<>(1);
        attributes.put("DefaultSMSType", "Transactional");

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        SetSmsAttributesRequest request = SetSmsAttributesRequest.builder()
                .attributes(attributes)
                .build();

        SetSmsAttributesResponse result = snsClient.setSMSAttributes(request);

        System.out.println("Set default Attributes to " + attributes + ". Status was " + result.sdkHttpResponse().statusCode());

        //snippet-end:[sns.java2.SetSMSAttributes.main]
    }
}
//snippet-end:[sns.java2.SetSMSAttributes.complete]

