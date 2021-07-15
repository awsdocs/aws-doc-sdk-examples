//snippet-sourcedescription:[ListDataSetGroups.java demonstrates how to list data set groups for the Amazon Forecast service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Forecast]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.forecast;

// snippet-start:[forecast.java2.list_forecast_datasetgroups.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.DatasetGroupSummary;
import software.amazon.awssdk.services.forecast.model.ListDatasetGroupsRequest;
import software.amazon.awssdk.services.forecast.model.ListDatasetGroupsResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[forecast.java2.list_forecast_datasetgroups.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDataSetGroups {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
            .region(region)
            .build();

        listDataGroups(forecast);
        forecast.close();
}

// snippet-start:[forecast.java2.list_forecast_datasetgroups.main]
public static void listDataGroups(ForecastClient forecast) {

    try {
        ListDatasetGroupsRequest group = ListDatasetGroupsRequest.builder()
            .maxResults(10)
            .build();

        ListDatasetGroupsResponse response = forecast.listDatasetGroups(group);

        List<DatasetGroupSummary> groups = response.datasetGroups();
        Iterator<DatasetGroupSummary> groupsIterator = groups.iterator();

        while(groupsIterator.hasNext()) {

            DatasetGroupSummary myGroup = groupsIterator.next();
            System.out.println("The Data Set name is " +myGroup.datasetGroupName()) ;
        }
    } catch (ForecastException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
    // snippet-end:[forecast.java2.list_forecast_datasetgroups.main]
  }
}
