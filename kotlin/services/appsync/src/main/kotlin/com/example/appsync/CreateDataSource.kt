// snippet-sourcedescription:[CreateDataSource.kt demonstrates how to create an AWS AppSync data source that uses Amazon DynamoDB.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS AppSync]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync

// snippet-start:[appsync.kotlin.create_ds.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.CreateDataSourceRequest
import aws.sdk.kotlin.services.appsync.model.DataSourceType
import aws.sdk.kotlin.services.appsync.model.DynamodbDataSourceConfig
import kotlin.system.exitProcess
// snippet-end:[appsync.kotlin.create_ds.import]

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <apiId> <dsName> <dsRole> <tableName>

        Where:
            apiId - The Id of the API. (You can get this value from the AWS Management Console.)
            dsName - The name of the data source. 
            dsRole - The AWS Identity and Access Management (IAM) service role for the data source. 
            tableName - The name of the Amazon DynamoDB table used as the data source.
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    val dsName = args[1]
    val dsRole = args[2]
    val tableName = args[3]
    val dsARN = createDS(dsName, dsRole, apiId, tableName)
    println("The ARN of the data source is $dsARN")
}

// snippet-start:[appsync.kotlin.create_ds.main]
suspend fun createDS(dsName: String, dsRole: String, apiVal: String, tableNameVal: String): String? {

    val config = DynamodbDataSourceConfig {
        awsRegion = "us-east-1"
        tableName = tableNameVal
        versioned = true
    }

    val request = CreateDataSourceRequest {
        description = "Created using the AWS SDK for Kotlin"
        apiId = apiVal
        name = dsName
        serviceRoleArn = dsRole
        dynamodbConfig = config
        type = DataSourceType.AmazonDynamodb
    }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        val response = appClient.createDataSource(request)
        return response.dataSource?.dataSourceArn
    }
}
// snippet-end:[appsync.kotlin.create_ds.main]
