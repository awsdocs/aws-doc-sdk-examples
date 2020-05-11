//snippet-sourcedescription:[DeleteDataset.java demonstrates how to delete a dataset that belongs to the Amazon Forecast service.]
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

// snippet-start:[forecast.java2.delete_forecast_dataset.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.DeleteDatasetRequest;
import software.amazon.awssdk.services.forecast.model.ForecastException;
// snippet-end:[forecast.java2.delete_forecast_dataset.import]

public class DeleteDataset {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteDataset <datasetARN> \n\n" +
                "Where:\n" +
                "    name - the Amazon Resource Name (ARN) of the dataset to delete \n\n" +
                "Example:\n" +
                "    DeleteDataset datasetARN\n";

         if (args.length < 1) {
              System.out.println(USAGE);
              System.exit(1);
           }

        String myDataSetARN = args[0];

        // Create a Forecast client
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
                .region(region)
                .build();

        deleteForecastDataSet(forecast, myDataSetARN);
    }

    // snippet-start:[forecast.java2.delete_forecast_dataset.main]
    public static void deleteForecastDataSet(ForecastClient forecast, String myDataSetARN) {

        try {

            DeleteDatasetRequest deleteRequest = DeleteDatasetRequest.builder()
                    .datasetArn(myDataSetARN)
                    .build();

            forecast.deleteDataset(deleteRequest);
            System.out.println("The dataset was deleted") ;

        } catch (ForecastException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[forecast.java2.delete_forecast_dataset.main]
    }
}
