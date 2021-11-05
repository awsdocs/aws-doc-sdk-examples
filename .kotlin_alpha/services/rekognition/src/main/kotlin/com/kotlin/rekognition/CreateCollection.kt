// snippet-sourcedescription:[CreateCollection.kt demonstrates how to create an Amazon Rekognition collection.]
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

// snippet-start:[rekognition.kotlin.create_collection.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.CreateCollectionRequest
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.create_collection.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: <collectionName> 

        Where:
            collectionName - the name of the collection. 
       
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val collectionName = args[0]
    val rekClient = RekognitionClient{ region = "us-east-1"}
    createMyCollection(rekClient, collectionName)
    rekClient.close()

}

// snippet-start:[rekognition.kotlin.create_collection.main]
 suspend fun createMyCollection(rekClient: RekognitionClient, collectionIdVal: String?) {
        try {

            val collectionRequest = CreateCollectionRequest {
                collectionId = collectionIdVal
            }

            val collectionResponse = rekClient.createCollection(collectionRequest)
            println("Collection ARN is ${collectionResponse.collectionArn}")
            println("Status code is ${collectionResponse.statusCode.toString()}" )

        } catch (e: RekognitionException) {
            println(e.message)
            rekClient.close()
            exitProcess(0)
        }
    }
// snippet-end:[rekognition.kotlin.create_collection.main]