// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.delete_forecast.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.DeleteForecastRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.delete_forecast.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <forecastArn>

    Where:
       forecastArn - The ARN that belongs to the forecast to delete. 
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val forecastArn = args[0]
    delForecast(forecastArn)
}

// snippet-start:[forecast.kotlin.delete_forecast.main]
suspend fun delForecast(forecastArnVal: String) {
    val request =
        DeleteForecastRequest {
            forecastArn = forecastArnVal
        }

    ForecastClient { region = "us-west-2" }.use { forecast ->
        forecast.deleteForecast(request)
        println("$forecastArnVal was successfully deleted")
    }
}
// snippet-end:[forecast.kotlin.delete_forecast.main]
