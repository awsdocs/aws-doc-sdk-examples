//snippet-sourcedescription:[DescribeContainer.kt demonstrates how to describe a given AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.mediastore

//snippet-start:[mediastore.kotlin.describe_container.import]
import aws.sdk.kotlin.services.mediastore.MediaStoreClient
import aws.sdk.kotlin.services.mediastore.model.DescribeContainerRequest
import kotlin.system.exitProcess
//snippet-end:[mediastore.kotlin.describe_container.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
        Usage: 
            <containerName> 

        Where:
            containerName - the name of the container.
       """

      if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val containerName = args[0]
    println("Status is ${checkContainer(containerName)}")
    }

//snippet-start:[mediastore.kotlin.describe_container.main]
suspend fun checkContainer(containerNameVal: String?): String? {

            val request = DescribeContainerRequest {
                containerName = containerNameVal
            }

            MediaStoreClient { region = "us-west-2" }.use { mediaStoreClient ->
              val containerResponse = mediaStoreClient.describeContainer(request)
              println("The container name is ${containerResponse.container?.name}")
              println("The container ARN is ${containerResponse.container?.arn}")
              return containerResponse.container?.status.toString()
            }
  }
//snippet-end:[mediastore.kotlin.describe_container.main]
