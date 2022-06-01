//snippet-sourcedescription:[DescribeForecast.java demonstrates how to describe a forecast for the Amazon Forecast service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Forecast]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.forecast;

// snippet-start:[forecast.java2.describe_forecast.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.DescribeForecastRequest;
import software.amazon.awssdk.services.forecast.model.DescribeForecastResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
// snippet-end:[forecast.java2.describe_forecast.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeForecast {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <forecastarn> \n\n" +
                "Where:\n" +
                "    forecastarn - The arn of the forecast (for example, \"arn:aws:forecast:us-west-2:xxxxx322:forecast/my_forecast)\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String forecastarn = args[0];
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describe(forecast, forecastarn);
        forecast.close();
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
