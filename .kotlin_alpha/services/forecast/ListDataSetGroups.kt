//snippet-sourcedescription:[ListDataSetGroups.kt demonstrates how to list data set groups for the Amazon Forecast service.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Forecast]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[04/21/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.list_forecast_datasetgroups.import]
import aws.sdk.kotlin.services.forecast.model.ForecastException
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.ListDatasetGroupsRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.list_forecast_datasetgroups.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val forecast = ForecastClient{ region = "us-west-2"}
    listDataGroups(forecast)
    forecast.close()
}

// snippet-start:[forecast.kotlin.list_forecast_datasetgroups.main]
suspend fun listDataGroups(forecast: ForecastClient) {
        try {
            val group = ListDatasetGroupsRequest {
                maxResults = 10
            }

            val response = forecast.listDatasetGroups(group)
            val groups= response.datasetGroups

            if (groups != null) {
                for (group in groups) {
                    println("The data set group name is ${group.datasetGroupName}")
                }
            }

        } catch (ex: ForecastException) {
            println(ex.message)
            forecast.close()
            exitProcess(0)
        }
}
// snippet-end:[forecast.kotlin.list_forecast_datasetgroups.main]