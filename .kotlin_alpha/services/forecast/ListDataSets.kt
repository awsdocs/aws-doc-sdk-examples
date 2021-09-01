//snippet-sourcedescription:[ListDataSets.kt demonstrates how to list Amazon Forecast data sets.]
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

// snippet-start:[forecast.kotlin.list_datasets.import]
import aws.sdk.kotlin.services.forecast.model.ForecastException
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.DatasetSummary
import aws.sdk.kotlin.services.forecast.model.ListDatasetsRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.list_datasets.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val forecast = ForecastClient{ region = "us-west-2"}
    listForecastDataSets(forecast)
    forecast.close()
}

// snippet-start:[forecast.kotlin.list_datasets.main]
suspend fun listForecastDataSets(forecast: ForecastClient) {
        try {
            val group = ListDatasetsRequest {
                maxResults = 10
            }

            val response= forecast.listDatasets(group)
            val groups: List<DatasetSummary>? = response.datasets

            if (groups != null) {
                for (group in groups) {
                    println("The Data Set name is ${group.datasetName}")
                }
            }

        } catch (ex: ForecastException) {
            println(ex.message)
            forecast.close()
            exitProcess(0)
        }
}
// snippet-end:[forecast.kotlin.list_datasets.main]