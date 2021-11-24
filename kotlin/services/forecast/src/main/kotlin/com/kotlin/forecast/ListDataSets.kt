//snippet-sourcedescription:[ListDataSets.kt demonstrates how to list Amazon Forecast data sets.]
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

// snippet-start:[forecast.kotlin.list_datasets.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.ListDatasetsRequest
// snippet-end:[forecast.kotlin.list_datasets.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
   listForecastDataSets()
 }

// snippet-start:[forecast.kotlin.list_datasets.main]
suspend fun listForecastDataSets() {

        val request = ListDatasetsRequest {
            maxResults = 10
        }

        ForecastClient { region = "us-west-2" }.use { forecast ->
            val response= forecast.listDatasets(request)
            response.datasets?.forEach { group ->
                println("The Data Set name is ${group.datasetName}")
            }
          }
}
// snippet-end:[forecast.kotlin.list_datasets.main]