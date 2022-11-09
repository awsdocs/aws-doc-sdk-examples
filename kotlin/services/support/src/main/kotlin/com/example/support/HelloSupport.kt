// snippet-sourcedescription:[HelloSupport.kt demonstrates how to create a Service Client and perform a single operation.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Support]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.support

import aws.sdk.kotlin.services.support.SupportClient
import aws.sdk.kotlin.services.support.model.DescribeServicesRequest

// snippet-start:[support.kotlin.hello.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

In addition, you must have the AWS Business Support Plan to use the AWS Support Java API. For more information, see:

https://aws.amazon.com/premiumsupport/plans/

This Kotlin example performs the following task:

1. Gets and displays available services.
 */

suspend fun main() {
    displayAllServices()
}

// Return a List that contains a Service name and Category name.
suspend fun displayAllServices() {
    val servicesRequest = DescribeServicesRequest {
        language = "en"
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeServices(servicesRequest)
        println("Get the first 10 services")
        var index = 1

        response.services?.forEach { service ->
            if (index == 11) {
                return@forEach
            }

            println("The Service name is: " + service.name)

            // Get the categories for this service.
            service.categories?.forEach { cat ->
                println("The category name is: " + cat.name)
                index++
            }
        }
    }
}

// snippet-end:[support.kotlin.hello.main]
