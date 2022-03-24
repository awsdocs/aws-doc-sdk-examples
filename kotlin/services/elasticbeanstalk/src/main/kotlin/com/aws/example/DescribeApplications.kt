//snippet-sourcedescription:[DescribeApplication.kt demonstrates how to describe an AWS Elastic Beanstalk application.]
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

//snippet-start:[eb.kotlin.describe_app.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.DescribeApplicationsRequest
import aws.sdk.kotlin.services.elasticbeanstalk.model.DescribeEnvironmentsRequest
//snippet-end:[eb.kotlin.describe_app.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
   describeApps()
}

//snippet-start:[eb.kotlin.describe_app.main]
suspend fun describeApps() {

    ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
        val response = beanstalkClient.describeApplications(DescribeApplicationsRequest {})
        response.applications?.forEach { app ->
            println("The application name is ${app.applicationName}")

            val desRequest = DescribeEnvironmentsRequest {
                applicationName = app.applicationName
            }

            ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
                val res = beanstalkClient.describeEnvironments(desRequest)
                res.environments?.forEach { desc ->

                    println("The environment ARN is ${desc.environmentArn}")
                }
            }
        }
    }
}
//snippet-end:[eb.kotlin.describe_app.main]
