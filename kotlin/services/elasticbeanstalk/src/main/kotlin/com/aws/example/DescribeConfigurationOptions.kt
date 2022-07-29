// snippet-sourcedescription:[DescribeConfigurationOptions.kt demonstrates how to describe configuration options.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS Elastic Beanstalk]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example

// snippet-start:[eb.kotlin.describe_config.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest
import aws.sdk.kotlin.services.elasticbeanstalk.model.OptionSpecification
import kotlin.system.exitProcess
// snippet-end:[eb.kotlin.describe_config.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <envName> 

        Where:
            envName - The name of the AWS Elastic Beanstalk environment. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val envName = args[0]
    getOptions(envName)
}

// snippet-start:[eb.kotlin.describe_config.main]
suspend fun getOptions(envName: String) {

    val spec = OptionSpecification {
        namespace = "aws:ec2:instances"
    }

    val request = DescribeConfigurationOptionsRequest {
        environmentName = envName
        options = listOf(spec)
    }

    ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
        val res = beanstalkClient.describeConfigurationOptions(request)
        res.options?.forEach { option ->

            println("The namespace is ${option.namespace}")
            val optionName = option.name
            println("The name is $optionName")
            if (optionName != null) {
                if (optionName.compareTo("InstanceTypes") == 0) {

                    val valueOptions = option.valueOptions
                    valueOptions?.forEach { value ->
                        println("The value is $value")
                    }
                }
            }
        }
    }
}
// snippet-end:[eb.kotlin.describe_config.main]
