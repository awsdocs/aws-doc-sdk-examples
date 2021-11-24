//snippet-sourcedescription:[DeleteApp.kt demonstrates how to delete an Amazon Pinpoint application.]
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

//snippet-start:[pinpoint.kotlin.deleteapp.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.DeleteAppRequest
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.deleteapp.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appId> 

    Where:
         appId - the Id of the application to delete.
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    deletePinApp(appId)
}

//snippet-start:[pinpoint.kotlin.deleteapp.main]
 suspend fun deletePinApp(appId: String?) {

             PinpointClient { region = "us-west-2" }.use { pinpoint ->
               val result = pinpoint.deleteApp(DeleteAppRequest {
                 applicationId = appId
               })
               val appName= result.applicationResponse?.name
               println("Application $appName has been deleted.")
             }
  }
//snippet-end:[pinpoint.kotlin.deleteapp.main]