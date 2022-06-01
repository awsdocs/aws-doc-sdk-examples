//snippet-sourcedescription:[DescribeEnvironment.kt demonstrates how to describe an AWS Elastic Beanstalk environment.]
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

//snippet-start:[eb.kotlin.describe_env.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.DescribeEnvironmentsRequest
import kotlin.system.exitProcess
//snippet-end:[eb.kotlin.describe_env.import]

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
    describeEnv(appName)
}

//snippet-start:[eb.kotlin.describe_env.main]
suspend fun describeEnv(appName: String) {

        val request = DescribeEnvironmentsRequest {
            environmentNames = listOf(appName)
        }

       ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
         val res = beanstalkClient.describeEnvironments(request)
         res.environments?.forEach { env ->
             System.out.println("The environment name is ${env.environmentName}")
             System.out.println("The environment ARN is  ${env.environmentArn}")
         }
       }
   }
//snippet-end:[eb.kotlin.describe_env.main]