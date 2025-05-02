// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise

import aws.sdk.kotlin.services.iotfleetwise.IotFleetWiseClient
import aws.sdk.kotlin.services.iotfleetwise.model.ListSignalCatalogsRequest

// snippet-start:[iotfleetwise.kotlin.hello.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    listSignalCatalogs()
}

/**
 * Lists the AWS FleetWise Signal Catalogs associated with the current AWS account.
 */
suspend fun listSignalCatalogs() {
    val request = ListSignalCatalogsRequest {
        maxResults = 10
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.listSignalCatalogs(request)
        val summaries = response.summaries

        if (summaries.isNullOrEmpty()) {
            println("No AWS FleetWise Signal Catalogs were found.")
        } else {
            summaries.forEach { summary ->
                with(summary) {
                    println("Catalog Name: $name")
                    println("ARN: $arn")
                    println("Created: $creationTime")
                    println("Last Modified: $lastModificationTime")
                    println("---------------")
                }
            }
        }
    }
}
// snippet-end:[iotfleetwise.kotlin.hello.main]
