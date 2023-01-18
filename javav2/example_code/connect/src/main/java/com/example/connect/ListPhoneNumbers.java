//snippet-sourcedescription:[ListPhoneNumbers.java demonstrates how to list Amazon Connect instance phone numbers.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.ListPhoneNumbersSummary;
import software.amazon.awssdk.services.connect.model.ListPhoneNumbersV2Request;
import software.amazon.awssdk.services.connect.model.ListPhoneNumbersV2Response;
import software.amazon.awssdk.services.connect.model.PhoneNumberType;
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListPhoneNumbers {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <targetArn>\n\n" +
            "Where:\n" +
            "   targetArn - The ARN of the Amazon Connect instance.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String targetArn = args[0];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        getPhoneNumbers(connectClient, targetArn);
    }

    // snippet-start:[connect.java2.list.phone.numbers.main]
    public static void getPhoneNumbers( ConnectClient connectClient, String targetArn ) {
        try{
            ListPhoneNumbersV2Request numbersV2Request = ListPhoneNumbersV2Request.builder()
                .maxResults(10)
                .phoneNumberTypes(PhoneNumberType.TOLL_FREE)
                .targetArn(targetArn)
                .build();

            ListPhoneNumbersV2Response response = connectClient.listPhoneNumbersV2(numbersV2Request);
            List<ListPhoneNumbersSummary> numbers = response.listPhoneNumbersSummaryList();
            for (ListPhoneNumbersSummary num: numbers) {
                System.out.println("Phone number is "+num.phoneNumber());
                System.out.println("Country code  is "+num.phoneNumberCountryCode().toString());
            }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.list.phone.numbers.main]
}
