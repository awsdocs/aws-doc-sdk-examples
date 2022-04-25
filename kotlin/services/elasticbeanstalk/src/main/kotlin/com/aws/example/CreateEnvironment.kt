//snippet-sourcedescription:[CreateEnvironment.kt demonstrates how to create an AWS Elastic Beanstalk environment.]
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

//snippet-start:[eb.kotlin.create_env.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.ConfigurationOptionSetting
import aws.sdk.kotlin.services.elasticbeanstalk.model.CreateEnvironmentRequest
import kotlin.system.exitProcess
//snippet-end:[eb.kotlin.create_env.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <envName> <appName>

        Where:
            envName - The name of the AWS Elastic Beanstalk environment. 
            appName - The name of the AWS Elastic Beanstalk application.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
    }

    val envName = args[0]
    val appName = args[0]
    val envArn = createEBEnvironment( envName, appName)
    println("The environment ARN is $envArn")
}

//snippet-start:[eb.kotlin.create_env.main]
suspend fun createEBEnvironment( envName: String?, appName: String?): String {

        val setting1 = ConfigurationOptionSetting {
            namespace = "aws:autoscaling:launchconfiguration"
            optionName = "IamInstanceProfile"
            value = "aws-elasticbeanstalk-ec2-role"
        }

        val applicationRequest = CreateEnvironmentRequest {
            description = "An AWS Elastic Beanstalk environment created using the AWS SDK for Kotlin"
            environmentName = envName
            solutionStackName = "64bit Amazon Linux 2 v3.2.12 running Corretto 11"
            applicationName = appName
            optionSettings = listOf(setting1)
        }

        var envArn: String
        ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
            val applicationResponse = beanstalkClient.createEnvironment(applicationRequest)
            envArn = applicationResponse.environmentArn.toString()
        }
        return envArn
}
//snippet-end:[eb.kotlin.create_env.main]