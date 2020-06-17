//snippet-sourcedescription:[DescribeForecast.java demonstrates how to describe a forecast for the Amazon Forecast service.]
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

// snippet-start:[forecast.java2.describe_forecast.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.DescribeForecastRequest;
import software.amazon.awssdk.services.forecast.model.DescribeForecastResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
// snippet-end:[forecast.java2.describe_forecast.import]

public class DescribeForecast {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeForecast <forecastarn> \n\n" +
                "Where:\n" +
                "    forecastarn - the Amazon Resource Name (ARN) of the forecast (i.e., \"arn:aws:forecast:us-west-2:81333322:forecast/my_forecast)\n\n" +
                "Example:\n" +
                "    DescribeForecast arn:aws:forecast:us-west-2:81333322:forecast/my_forecast\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String forecastarn = args[0];

        // Create a Forecast client
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
                .region(region)
                .build();

        describe(forecast, forecastarn);
    }

    // snippet-start:[forecast.java2.describe_forecast.main]
    public static void describe(ForecastClient forecast, String forecastarn) {

    try {
        DescribeForecastRequest request = DescribeForecastRequest.builder()
                .forecastArn(forecastarn)
                .build();

        DescribeForecastResponse response = forecast.describeForecast(request);
        System.out.println("The name of the forecast is " +response.forecastName());

    } catch (ForecastException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
   }
    // snippet-end:[forecast.java2.describe_forecast.main]
}
