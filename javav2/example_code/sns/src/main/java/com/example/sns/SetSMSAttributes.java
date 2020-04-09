//snippet-sourcedescription:[SetSMSAttributes.java demonstrates how to set attributes for Amazon SNS.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/6/2020]
//snippet-sourceauthor:[scmacdon AWS]
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

package com.example.sns;

//snippet-start:[sns.java2.SetSMSAttributes.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SetSmsAttributesRequest;
import software.amazon.awssdk.services.sns.model.SetSmsAttributesResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import java.util.HashMap;
//snippet-end:[sns.java2.SetSMSAttributes.import]

public class SetSMSAttributes {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "SetSMSAttributes - set your default SMS type for Amazon SNS.\n" +
                "Usage: SetSMSAttributes \n\n";


        HashMap<String, String> attributes = new HashMap<>(1);
        attributes.put("DefaultSMSType", "Transactional");
        attributes.put("UsageReportS3Bucket", "janbucket77" );

        SnsClient snsClient = SnsClient.builder()
                    .region(Region.US_WEST_2)
                    .build();

        setSNSAttributes(snsClient, attributes);
        }

    //snippet-start:[sns.java2.SetSMSAttributes.main]
   public static void setSNSAttributes( SnsClient snsClient, HashMap<String, String> attributes) {

        try {
            SetSmsAttributesRequest request = SetSmsAttributesRequest.builder()
                .attributes(attributes)
                .build();

            SetSmsAttributesResponse result = snsClient.setSMSAttributes(request);
            System.out.println("Set default Attributes to " + attributes + ". Status was " + result.sdkHttpResponse().statusCode());

    } catch (SnsException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
        }
        //snippet-end:[sns.java2.SetSMSAttributes.main]
    }
}
