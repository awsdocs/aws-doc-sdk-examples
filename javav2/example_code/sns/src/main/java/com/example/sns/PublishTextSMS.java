//snippet-sourcedescription:[PublishTextSMS.java demonstrates how to send an Amazon Simple Notification Service (Amazon SNS) text message.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.PublishTextSMS.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.PublishTextSMS.import]

public class PublishTextSMS {

    /**
     * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
     *
     * For information, see this documentation topic:
     *
     * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
     */
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: " +
                "PublishTextSMS <message> <phoneNumber>\n\n" +
                "Where:\n" +
                "  message - the message text to send.\n\n" +
                "  phoneNumber - the mobile phone number to which a message is sent (for example, +1XXX5550100). \n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String message = args[0];
        String phoneNumber = args[1];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        pubTextSMS(snsClient, message, phoneNumber);
        snsClient.close();
    }

    //snippet-start:[sns.java2.PublishTextSMS.main]
    public static void pubTextSMS(SnsClient snsClient, String message, String phoneNumber) {
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

