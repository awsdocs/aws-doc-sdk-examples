// snippet-sourcedescription:[ListContainers.kt demonstrates how to list your AWS Elemental MediaStore containers.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Elemental MediaStore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediastore

// snippet-start:[mediastore.kotlin.list_containers.import]
import aws.sdk.kotlin.services.mediastore.MediaStoreClient
import aws.sdk.kotlin.services.mediastore.model.ListContainersRequest
// snippet-end:[mediastore.kotlin.list_containers.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllContainers()
}

// snippet-start:[mediastore.kotlin.list_containers.main]
suspend fun listAllContainers() {

    MediaStoreClient { region = "us-west-2" }.use { mediaStoreClient ->
        val response = mediaStoreClient.listContainers(ListContainersRequest {})
        response.containers?.forEach { container ->
            println("Container name is ${container.name}")
        }
    }
}
// snippet-end:[mediastore.kotlin.list_containers.main]
