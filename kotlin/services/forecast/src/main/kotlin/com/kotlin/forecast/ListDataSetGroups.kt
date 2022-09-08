// snippet-sourcedescription:[ListDataSetGroups.kt demonstrates how to list data set groups for the Amazon Forecast service.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Forecast]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.list_forecast_datasetgroups.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.ListDatasetGroupsRequest
// snippet-end:[forecast.kotlin.list_forecast_datasetgroups.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listDataGroups()
}

// snippet-start:[forecast.kotlin.list_forecast_datasetgroups.main]
suspend fun listDataGroups() {

    val request = ListDatasetGroupsRequest {
        maxResults = 10
    }

    ForecastClient { region = "us-west-2" }.use { forecast ->
        val response = forecast.listDatasetGroups(request)
        response.datasetGroups?.forEach { group ->
            println("The data set group name is ${group.datasetGroupName}")
        }
    }
}
// snippet-end:[forecast.kotlin.list_forecast_datasetgroups.main]
