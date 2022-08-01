// snippet-sourcedescription:[DeleteApplication.kt demonstrates how to delete an AWS Elastic Beanstalk application.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS Elastic Beanstalk]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example

// snippet-start:[eb.kotlin.delete_app.import]
import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import aws.sdk.kotlin.services.elasticbeanstalk.model.DeleteApplicationRequest
import kotlin.system.exitProcess
// snippet-end:[eb.kotlin.delete_app.import]

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
    deleteApp(appName)
}

// snippet-start:[eb.kotlin.delete_app.main]
suspend fun deleteApp(appName: String?) {

    val applicationRequest = DeleteApplicationRequest {
        applicationName = appName
        terminateEnvByForce = true
    }

    ElasticBeanstalkClient { region = "us-east-1" }.use { beanstalkClient ->
        beanstalkClient.deleteApplication(applicationRequest)
        println("The Elastic Beanstalk application was successfully deleted!")
    }
}
// snippet-end:[eb.kotlin.delete_app.main]
