//snippet-sourcedescription:[ListForecasts.kt demonstrates how to list forecasts for the Amazon Forecast service.]
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

// snippet-start:[forecast.kotlin.list_forecasts.import]
import aws.sdk.kotlin.services.forecast.model.ForecastException
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.ListForecastsRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.list_forecasts.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val forecast = ForecastClient{ region = "us-west-2"}
    listAllForeCasts(forecast)
    forecast.close()
}

// snippet-start:[forecast.kotlin.list_forecasts.main]
suspend fun listAllForeCasts(forecast: ForecastClient) {
        try {

            val request = ListForecastsRequest{
                maxResults = 10
            }

            val response = forecast.listForecasts(request)
            response.forecasts?.forEach { forecast ->
                println("The name of the forecast is ${forecast.forecastName}")
                println("The ARN of the forecast is ${forecast.forecastArn}")
            }

        } catch (ex: ForecastException) {
            println(ex.message)
            forecast.close()
            exitProcess(0)
        }
 }
// snippet-end:[forecast.kotlin.list_forecasts.main]