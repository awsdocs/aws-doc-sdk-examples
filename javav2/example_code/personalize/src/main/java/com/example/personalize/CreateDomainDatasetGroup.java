//snippet-sourcedescription:[CreateDomainDatasetGroup.java demonstrates how to create an Amazon Personalize
// domain dataset group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/11/2022]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_domain_dataset_group.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateDatasetGroupRequest;

import java.time.Instant;
//snippet-end:[personalize.java2.create_domain_dataset_group.import]

public class CreateDomainDatasetGroup {

    public static void main(String[] args) {

        final String USAGE = "Usage:\n" +
                "    CreateDomainDatasetGroup <name> <domain>\n\n" +
                "Where:\n" +
                "   name - The name for the new dataset group." +
                "   domain - The domain for the dataset group. Specify either ECOMMERCE or VIDEO_ON_DEMAND.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;

        String datasetGroupName = args[0];
        String domain = args[1];

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String datasetGroupArn = createDomainDatasetGroup(personalizeClient, datasetGroupName, domain);
        System.out.println("Dataset group ARN: " + datasetGroupArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_domain_dataset_group.main]
    public static String createDomainDatasetGroup(PersonalizeClient personalizeClient,
                                                  String datasetGroupName,
                                                  String domain) {

        try {
            CreateDatasetGroupRequest createDatasetGroupRequest = CreateDatasetGroupRequest.builder()
                    .name(datasetGroupName)
                    .domain(domain)
                    .build();
            return personalizeClient.createDatasetGroup(createDatasetGroupRequest).datasetGroupArn();
        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_domain_dataset_group.main]

}
