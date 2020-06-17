//snippet-sourcedescription:[CreateForecast.java demonstrates how to create a forecast for the Amazon Forecast service.]
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

// snippet-start:[forecast.java2.create_forecast.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.CreateForecastRequest;
import software.amazon.awssdk.services.forecast.model.CreateForecastResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
// snippet-end:[forecast.java2.create_forecast.import]

public class CreateForecast {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateForecast <name><predictorArn> \n\n" +
                "Where:\n" +
                "    name - the name of the forecast \n\n" +
                "    predictorArn - the Amazon Resource Name (ARN) of the predictor to use \n\n" +
                "Example:\n" +
                "    CreateForecast MyForecast arn:aws:forecast:us-west-2:81454e33:predictor/MyPredictor\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        String predictorArn = args[1];

        // Create a Forecast client
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
                .region(region)
                .build();

        String forecastArn = createNewForecast(forecast, name, predictorArn) ;
        System.out.println("The ARN of the new forecast is "+forecastArn);
    }

    // snippet-start:[forecast.java2.create_forecast.main]
    public static String createNewForecast(ForecastClient forecast, String name, String predictorArn) {

       try {
            CreateForecastRequest forecastRequest = CreateForecastRequest.builder()
                .forecastName(name)
                .predictorArn(predictorArn)
                .build() ;

            CreateForecastResponse response = forecast.createForecast(forecastRequest);
          return response.forecastArn();

       } catch (ForecastException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
       return "";
    }
    // snippet-end:[forecast.java2.create_forecast.main]
}
