//snippet-sourcedescription:[ListDatasetGroups.java demonstrates how to list Amazon Personalize data set groups.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.list_dsgroups.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.ListDatasetGroupsRequest;
import software.amazon.awssdk.services.personalize.model.ListDatasetGroupsResponse;
import software.amazon.awssdk.services.personalize.model.DatasetGroupSummary;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import java.util.List;
//snippet-end:[personalize.java2.list_dsgroups.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDatasetGroups {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
            .region(region)
            .build();

        listDSGroups(personalizeClient);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.list_dsgroups.main]
    public static void listDSGroups( PersonalizeClient personalizeClient ) {

        try {
            ListDatasetGroupsRequest groupsRequest = ListDatasetGroupsRequest.builder()
                .maxResults(15)
                .build();

            ListDatasetGroupsResponse groupsResponse = personalizeClient.listDatasetGroups(groupsRequest);
            List<DatasetGroupSummary> groups = groupsResponse.datasetGroups();
            for (DatasetGroupSummary group: groups) {
                System.out.println("The DataSet name is : "+group.name());
                System.out.println("The DataSet ARN is : "+group.datasetGroupArn());
            }

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.list_dsgroups.main]
}
