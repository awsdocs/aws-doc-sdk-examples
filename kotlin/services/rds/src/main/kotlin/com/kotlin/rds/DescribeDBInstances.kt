// snippet-sourcedescription:[DescribeDBInstances.kt demonstrates how to describe Amazon Relational Database Service (RDS) instances.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Relational Database Service]
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
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
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
