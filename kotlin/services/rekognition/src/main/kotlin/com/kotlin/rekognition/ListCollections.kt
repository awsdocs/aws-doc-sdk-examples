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
// snippet-end:[rekognition.kotlin.list_collections.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    listAllCollections()
}

// snippet-start:[rekognition.kotlin.list_collections.main]
suspend fun listAllCollections() {

        val request = ListCollectionsRequest {
            maxResults = 10
        }

        RekognitionClient { region = "us-east-1" }.use { rekClient ->
          val response = rekClient.listCollections(request)
          response.collectionIds?.forEach { resultId ->
            println(resultId)
          }
        }
}
// snippet-end:[rekognition.kotlin.list_collections.main]