//snippet-sourcedescription:[PublishTextSMS.java demonstrates how to send a text message.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
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
//snippet-start:[sns.java2.PublishTextSMS.complete]
package com.example.sns;

//snippet-start:[sns.java2.PublishTextSMS.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.PublishTextSMS.import]

public class PublishTextSMS {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "PublishTextSMS - send an SMS message\n" +
                "Usage: PublishTextSMS <message> <phoneNumber>\n\n" +
                "Where:\n" +
                "  message - message text to send.\n\n" +
                "  phoneNumber - phone number to look up. Example: +1XXX5550100\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[sns.java2.PublishTextSMS.main]
        String message = args[0];
        String phoneNumber = args[1];

        SnsClient snsClient = SnsClient.builder().region(Region.US_WEST_2).build();

        try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

            PublishResponse result = snsClient.publish(request);

            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {

        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
        }

        //snippet-end:[sns.java2.PublishTextSMS.main]
    }
}
//snippet-end:[sns.java2.PublishTextSMS.complete]
