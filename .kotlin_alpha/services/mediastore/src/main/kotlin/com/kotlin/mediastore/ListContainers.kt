//snippet-sourcedescription:[DeleteContainer.kt demonstrates how to delete a given AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediastore

//snippet-start:[mediastore.kotlin.list_containers.import]
import aws.sdk.kotlin.services.mediastore.MediaStoreClient
import aws.sdk.kotlin.services.mediastore.model.ListContainersRequest
import aws.sdk.kotlin.services.mediastore.model.MediaStoreException
import kotlin.system.exitProcess
//snippet-end:[mediastore.kotlin.list_containers.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    val mediastoreClient = MediaStoreClient { region = "us-east-1" }
    listAllContainers(mediastoreClient)
    mediastoreClient.close()
}

//snippet-start:[mediastore.kotlin.list_containers.main]
suspend fun listAllContainers(mediaStoreClient: MediaStoreClient) {
        try {
            val containersResponse = mediaStoreClient.listContainers(ListContainersRequest{})
            val containers = containersResponse.containers
            if (containers != null) {
                for (container in containers) {
                    println("Container name is ${container.name}")
                }
            }
        } catch (e: MediaStoreException) {
            println(e.message)
            mediaStoreClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[mediastore.kotlin.list_containers.main]