//snippet-sourcedescription:[ListDatasetGroups.java demonstrates how to list Amazon Personalize dataset groups.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Personalize]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/21/2020]
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


public class ListDatasetGroups {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        listDSGroups(personalizeClient);
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
                System.out.println("The dataset name is : "+group.name());
                System.out.println("The dataset ARN is : "+group.datasetGroupArn());
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[personalize.java2.list_dsgroups.main]
}
