//snippet-sourcedescription:[ListDataSets.java demonstrates how to list Amazon Forecast datasets.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Forecast]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[5/4/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.forecast;
// snippet-start:[forecast.java2.list_datasets.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.DatasetSummary;
import software.amazon.awssdk.services.forecast.model.ListDatasetsRequest;
import software.amazon.awssdk.services.forecast.model.ListDatasetsResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[forecast.java2.list_datasets.import]

public class ListDataSets {

    public static void main(String[] args) {
        // Create a Forecast client
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
                .region(region)
                .build();

        listForecastDataSets(forecast);
    }

    // snippet-start:[forecast.java2.list_datasets.import]
    public static void listForecastDataSets(ForecastClient forecast) {

       try {
           ListDatasetsRequest group = ListDatasetsRequest.builder()
                .maxResults(10)
                .build();

           ListDatasetsResponse response = forecast.listDatasets(group);
           List<DatasetSummary> groups = response.datasets();
           Iterator<DatasetSummary> groupsIterator = groups.iterator();

           while(groupsIterator.hasNext()) {

            DatasetSummary myGroup = groupsIterator.next();
            System.out.println("The dataset name is " +myGroup.datasetName()) ;
        }
       } catch (ForecastException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
      // snippet-end:[forecast.java2.list_datasets.import]
    }
}
