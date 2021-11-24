// snippet-sourcedescription:[CelebrityInfo.kt demonstrates how to get information about a detected celebrity.]
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

// snippet-start:[rekognition.kotlin.celebrityInfo.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.GetCelebrityInfoRequest
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.celebrityInfo.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: 
            <id>
       
        Where:
            id - the id value of the celebrity. You can use the RecognizeCelebrities example to get the ID value. 
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val id = args[0]
    getCelebrityInfo(id)
    }

// snippet-start:[rekognition.kotlin.celebrityInfo.main]
suspend fun getCelebrityInfo(idVal: String?) {

    val request = GetCelebrityInfoRequest {
        id = idVal
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.getCelebrityInfo(request)

        // Display celebrity information.
        println("The celebrity name is ${response.name}")
        println("Further information (if available):")
        response.urls?.forEach { url ->
           println(url)
        }
    }
 }
// snippet-end:[rekognition.kotlin.celebrityInfo.main]