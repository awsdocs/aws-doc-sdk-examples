// snippet-sourcedescription:[CreateDataSet.kt demonstrates how to create a data set for the Amazon Forecast service.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Forecast]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.forecast

// snippet-start:[forecast.kotlin.create_forecast_dataset.import]
import aws.sdk.kotlin.services.forecast.ForecastClient
import aws.sdk.kotlin.services.forecast.model.AttributeType
import aws.sdk.kotlin.services.forecast.model.CreateDatasetRequest
import aws.sdk.kotlin.services.forecast.model.DatasetType
import aws.sdk.kotlin.services.forecast.model.Domain
import aws.sdk.kotlin.services.forecast.model.Schema
import aws.sdk.kotlin.services.forecast.model.SchemaAttribute
import kotlin.system.exitProcess
// snippet-end:[forecast.kotlin.create_forecast_dataset.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <name>  

    Where:
        name - The name of the data set. 
           """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val name = args[0]
    val myDataSetARN = createForecastDataSet(name)
    println("The ARN of the new data set is $myDataSetARN")
}

// snippet-start:[forecast.kotlin.create_forecast_dataset.main]
suspend fun createForecastDataSet(name: String?): String? {

    val schemaOb = Schema {
        attributes = getSchema()
    }

    val request = CreateDatasetRequest {
        datasetName = name
        domain = Domain.fromValue("CUSTOM")
        datasetType = DatasetType.fromValue("RELATED_TIME_SERIES")
        dataFrequency = "D"
        schema = schemaOb
    }

    ForecastClient { region = "us-west-2" }.use { forecast ->
        val response = forecast.createDataset(request)
        return response.datasetArn
    }
}

// Create a SchemaAttribute list required to create a data set.
private fun getSchema(): MutableList<SchemaAttribute> {

    val schemaList = mutableListOf<SchemaAttribute>()

    val att1 = SchemaAttribute {
        attributeName = "item_id"
        attributeType = AttributeType.fromValue("string")
    }

    val att2 = SchemaAttribute {
        attributeName = "timestamp"
        attributeType = AttributeType.fromValue("timestamp")
    }

    val att3 = SchemaAttribute {
        attributeName = "target_value"
        attributeType = AttributeType.fromValue("float")
    }

    // Push the SchemaAttribute objects to the List.
    schemaList.add(att1)
    schemaList.add(att2)
    schemaList.add(att3)

    return schemaList
}
// snippet-end:[forecast.kotlin.create_forecast_dataset.main]
