// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.mediastore

// snippet-start:[mediastore.kotlin.create_container.import]
import aws.sdk.kotlin.services.mediastore.MediaStoreClient
import aws.sdk.kotlin.services.mediastore.model.CreateContainerRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[mediastore.kotlin.create_container.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
    
        Usage: <containerName> 

        Where:
               containerName - the name of the container.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val containerName = args[0]
    println("Status is " + createMediaContainer(containerName))
}

// snippet-start:[mediastore.kotlin.create_container.main]
suspend fun createMediaContainer(containerNameVal: String?) {
    val sleepTime: Long = 10
    val request =
        CreateContainerRequest {
            containerName = containerNameVal
        }

    MediaStoreClient { region = "us-west-2" }.use { mediaStoreClient ->
        val containerResponse = mediaStoreClient.createContainer(request)
        var status = containerResponse.container?.status.toString()

        // Wait until the container is in an active state.
        while (!status.equals("Active", ignoreCase = true)) {
            status = checkContainer(containerNameVal).toString()
            println("Status - $status")
            delay(sleepTime * 1000)
        }
        println("The container ARN value is ${containerResponse.container?.arn}")
        println("The $containerNameVal is created")
    }
}
// snippet-end:[mediastore.kotlin.create_container.main]
