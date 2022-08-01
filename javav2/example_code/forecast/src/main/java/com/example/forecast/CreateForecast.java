//snippet-sourcedescription:[CreateForecast.java demonstrates how to create a forecast for the Amazon Forecast service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Forecast]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.forecast;

// snippet-start:[forecast.java2.create_forecast.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.forecast.model.CreateForecastRequest;
import software.amazon.awssdk.services.forecast.model.CreateForecastResponse;
import software.amazon.awssdk.services.forecast.model.ForecastException;
// snippet-end:[forecast.java2.create_forecast.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateForecast {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <name> <predictorArn> \n\n" +
            "Where:\n" +
            "    name - The name of the forecast. \n\n" +
            "    predictorArn - The arn of the predictor to use. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String name = args[0];
        String predictorArn = args[1];
        Region region = Region.US_WEST_2;
        ForecastClient forecast = ForecastClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String forecastArn = createNewForecast(forecast, name, predictorArn) ;
        System.out.println("The ARN of the new forecast is "+forecastArn);
        forecast.close();
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
