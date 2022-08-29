//snippet-sourcedescription:[GetTemplateByName.java demonstrates how to get a template.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.EmailTemplateResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.GetEmailTemplateRequest;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetTemplateByName {
    public static void main(String[] args) {

        // Change "MyNewTemplate-1" to the name of the template to retrieve.
        String templateName = "MyNewTemplate-1";
        PinpointClient pinpoint = PinpointClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        EmailTemplateResponse templateResponse = getTemplateByName(pinpoint, templateName);
        System.out.println("Response : " + templateResponse);
        pinpoint.close();
    }

    private static EmailTemplateResponse getTemplateByName(PinpointClient client, String templateName) {

        try {
            EmailTemplateResponse response = client.getEmailTemplate(GetEmailTemplateRequest.builder()
                .templateName(templateName)
                .build()).
                    emailTemplateResponse();
            return response;

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
}