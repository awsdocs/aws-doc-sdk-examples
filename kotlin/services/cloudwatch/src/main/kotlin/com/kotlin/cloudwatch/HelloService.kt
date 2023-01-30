// snippet-sourcedescription:[HelloService.kt demonstrates how to list Amazon CloudWatch metrics using a paginator.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.cloudwatch

import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.ListMetricsRequest
import aws.sdk.kotlin.services.cloudwatch.paginators.listMetricsPaginated
import kotlinx.coroutines.flow.transform
import kotlin.system.exitProcess

// snippet-start:[cloudwatch.kotlin.hello.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
           <namespace> 
        Where:
           namespace - The namespace to filter against (for example, AWS/EC2). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val namespace = args[0]
    listAllMets(namespace)
}

suspend fun listAllMets(namespaceVal: String?) {
    val request = ListMetricsRequest {
        namespace = namespaceVal
    }

    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
        cwClient.listMetricsPaginated(request)
            .transform { it.metrics?.forEach { obj -> emit(obj) } }
            .collect { obj ->
                println("Name is ${obj.metricName}")
                println("Namespace is ${obj.namespace}")
            }
    }
}
// snippet-end:[cloudwatch.kotlin.hello.main]
