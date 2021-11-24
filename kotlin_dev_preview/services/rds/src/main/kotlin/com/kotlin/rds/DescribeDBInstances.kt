//snippet-sourcedescription:[DescribeDBInstances.kt demonstrates how to describe Amazon Relational Database Service (RDS) instances.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.describe_instances.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DescribeDbInstancesRequest
// snippet-end:[rds.kotlin.describe_instances.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    describeInstances()
}

// snippet-start:[rds.kotlin.describe_instances.main]
suspend fun describeInstances() {

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeDbInstances(DescribeDbInstancesRequest {})
        response.dbInstances?.forEach { instance ->
               println("Instance Identifier is ${instance.dbInstanceIdentifier}")
               println("The Engine is ${instance.engine}")
               println("Connection endpoint is ${instance.endpoint?.address}")
         }
    }
}
// snippet-end:[rds.kotlin.describe_instances.main]