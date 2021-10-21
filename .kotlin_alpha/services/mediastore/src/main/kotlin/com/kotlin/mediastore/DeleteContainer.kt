//snippet-sourcedescription:[DeleteContainer.kt demonstrates how to delete a given AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediastore

//snippet-start:[mediastore.kotlin.delete_container.import]
import aws.sdk.kotlin.services.mediastore.MediaStoreClient
import aws.sdk.kotlin.services.mediastore.model.DeleteContainerRequest
import aws.sdk.kotlin.services.mediastore.model.MediaStoreException
import kotlin.system.exitProcess
//snippet-end:[mediastore.kotlin.delete_container.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
        Usage: <containerName> 

        Where:
               containerName - the name of the container to delete.
    """

       if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val containerName = args[0]
    val mediastoreClient = MediaStoreClient { region = "us-east-1" }
    deleteMediaContainer(mediastoreClient, containerName)
    mediastoreClient.close()
}

//snippet-start:[mediastore.kotlin.delete_container.main]
suspend fun deleteMediaContainer(mediaStoreClient: MediaStoreClient, containerNameVal: String?) {
        try {
            val deleteContainerRequest = DeleteContainerRequest {
                containerName = containerNameVal
            }

            mediaStoreClient.deleteContainer(deleteContainerRequest)
            println("The $containerNameVal was deleted")

        } catch (e: MediaStoreException) {
            println(e.message)
            mediaStoreClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[mediastore.kotlin.delete_container.main]