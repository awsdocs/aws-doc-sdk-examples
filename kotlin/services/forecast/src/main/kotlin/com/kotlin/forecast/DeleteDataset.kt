// snippet-sourcedescription:[DeleteDataset.kt demonstrates how to delete a data set that belongs to the Amazon Forecast service.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Forecast]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.delete_forecast_dataset.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.DeleteDatasetRequest
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.delete_forecast_dataset.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <dataSetARN>  

    Where:
        dataSetARN - The ARN of the data set to delete. 
       """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val dataSetARN = args[0]
    deleteForecastDataSet(dataSetARN)
}

// snippet-start:[forecast.kotlin.delete_forecast_dataset.main]
suspend fun deleteForecastDataSet(myDataSetARN: String?) {

    val request = DeleteDatasetRequest {
        datasetArn = myDataSetARN
    }

    ForecastClient { region = "us-west-2" }.use { forecast ->
        forecast.deleteDataset(request)
        println("$myDataSetARN data set was deleted")
    }
}
// snippet-end:[forecast.kotlin.delete_forecast_dataset.main]
