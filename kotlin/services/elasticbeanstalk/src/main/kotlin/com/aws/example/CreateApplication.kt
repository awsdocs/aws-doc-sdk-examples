//snippet-sourcedescription:[CreateApplication.kt demonstrates how to create an AWS Elastic Beanstalk application.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elastic Beanstalk ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.aws.example

//snippet-start:[eb.kotlin.create_app.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.CreateApplicationRequest
import kotlin.system.exitProcess
//snippet-end:[eb.kotlin.create_app.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <appName> 

        Where:
            appName - The name of the AWS Elastic Beanstalk application. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val appName = args[0]
    val appArn = createApp(appName)
    println("The application ARN is $appArn")
}

//snippet-start:[eb.kotlin.create_app.main]
suspend fun createApp( appName: String?): String {

        val applicationRequest = CreateApplicationRequest {
            description = "An AWS Elastic Beanstalk app created using the AWS SDK for Kotlin"
            applicationName = appName
        }

        var tableArn: String
        ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
            val applicationResponse = beanstalkClient.createApplication(applicationRequest)
            tableArn = applicationResponse.application?.applicationArn.toString()
        }
        return tableArn
}
//snippet-end:[eb.kotlin.create_app.main]