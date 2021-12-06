//snippet-sourcedescription:[CreateForecast.kt demonstrates how to create a forecast for the Amazon Forecast service.]
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

// snippet-start:[forecast.kotlin.create_forecast.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.CreateForecastRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.create_forecast.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <name> <predictorArn> 

    Where:
        name - the name of the forecast. 
        predictorArn - the ARN of the predictor to use (ie, arn:aws:forecast:us-west-2:xxxxxe33:predictor/MyPredictor). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val name = args[0]
    val predictorArn = args[1]
    val forecastArn= createNewForecast(name, predictorArn)
    println("The ARN of the new forecast is $forecastArn")
    }

// snippet-start:[forecast.kotlin.create_forecast.main]
suspend  fun createNewForecast(name: String?, predictorArnVal: String?): String? {

          val request = CreateForecastRequest {
              forecastName = name
              predictorArn = predictorArnVal
          }

          ForecastClient { region = "us-west-2" }.use { forecast ->
            val response = forecast.createForecast(request)
            return response.forecastArn
          }
  }
// snippet-end:[forecast.kotlin.create_forecast.main]