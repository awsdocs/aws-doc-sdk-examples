//snippet-sourcedescription:[DescribeForecast.kt demonstrates how to describe a forecast for the Amazon Forecast service.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Forecast]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.describe_forecast.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.DescribeForecastRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.describe_forecast.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <forecastArn>

    Where:
       forecastArn - the ARN that belongs to the forecast to describe. 
      """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val forecastArn = args[0]
    describe(forecastArn)
   }

// snippet-start:[forecast.kotlin.describe_forecast.main]
suspend fun describe(forecastarn: String?) {

            val request = DescribeForecastRequest {
                forecastArn = forecastarn
            }

            ForecastClient { region = "us-west-2" }.use { forecast ->
              val response = forecast.describeForecast(request)
              println("The name of the forecast is ${response.forecastName}")
            }
 }
// snippet-end:[forecast.kotlin.describe_forecast.main]
