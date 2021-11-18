// snippet-sourcedescription:[ListCollections.kt demonstrates how to list the available Amazon Rekognition collections.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11-05-2021]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.list_collections.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.ListCollectionsRequest
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.list_collections.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    val rekClient = RekognitionClient{ region = "us-east-1"}
    listAllCollections(rekClient)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.list_collections.main]
suspend fun listAllCollections(rekClient: RekognitionClient) {
    try {
        val listCollectionsRequest = ListCollectionsRequest {
            maxResults = 10
        }
        val response = rekClient.listCollections(listCollectionsRequest)
        response.collectionIds?.forEach { resultId ->
                println(resultId)
        }

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.list_collections.main]