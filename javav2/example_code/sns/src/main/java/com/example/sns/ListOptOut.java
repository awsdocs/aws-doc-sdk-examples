//snippet-sourcedescription:[ListOptOut.java demonstrates how to list the phone numbers for which the users have selected to no longer receive future text messages.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[sns.java2.ListOptOut.complete]
package com.example.sns;

//snippet-start:[sns.java2.ListOptOut.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListPhoneNumbersOptedOutRequest;
import software.amazon.awssdk.services.sns.model.ListPhoneNumbersOptedOutResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.ListOptOut.import]

public class ListOptOut {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "ListOptOut - list phone numbers that opted out of receiving SMS messages\n" +
                "Usage: ListOptOut \n\n";

        //snippet-start:[sns.java2.ListOptOut.main]
        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        try {

            ListPhoneNumbersOptedOutRequest request = ListPhoneNumbersOptedOutRequest.builder().build();
            ListPhoneNumbersOptedOutResponse result = snsClient.listPhoneNumbersOptedOut(request);
            System.out.println("Status was " + result.sdkHttpResponse().statusCode() + "\n\nPhone Numbers: \n\n" + result.phoneNumbers());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
       //snippet-end:[sns.java2.ListOptOut.main]
    }
}
//snippet-end:[sns.java2.ListOptOut.complete]
