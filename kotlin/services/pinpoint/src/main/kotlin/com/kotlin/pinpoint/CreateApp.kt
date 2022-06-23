//snippet-sourcedescription:[CreateApp.kt demonstrates how to create an Amazon Pinpoint application.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.createapp.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.CreateAppRequest
import aws.sdk.kotlin.services.pinpoint.model.CreateApplicationRequest
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.createapp.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appName> 

    Where:
         appName - the name of the application to create.
      """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val appName = args[0]
    val appId = createApplication(appName)
    println("The app Id is: $appId")
 }

//snippet-start:[pinpoint.kotlin.createapp.main]
 suspend fun createApplication(applicationName: String?):String? {

            val createApplicationRequestOb = CreateApplicationRequest{
                name = applicationName
            }

            PinpointClient { region = "us-west-2" }.use { pinpoint ->
             val result = pinpoint.createApp(CreateAppRequest {
                createApplicationRequest = createApplicationRequestOb
             })
             return result.applicationResponse?.id
        }
    }
//snippet-end:[pinpoint.kotlin.createapp.main]