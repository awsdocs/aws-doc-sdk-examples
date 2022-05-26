// snippet-sourcedescription:[DeleteApiKey.kt demonstrates how to delete a unique key.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS AppSync]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/25/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.appsync

// snippet-start:[appsync.kotlin.get_ds.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.GetDataSourceRequest
import kotlin.system.exitProcess
// snippet-end:[appsync.kotlin.get_ds.import]

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
            <apiId> <keyId>
        Where:
            apiId - The Id of the API. (You can get this value from the AWS Management Console.)
            dsName - The name of the data source.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    val dsName = args[0]
    getDS(apiId, dsName)
}

// snippet-start:[appsync.kotlin.get_ds.main]
suspend fun getDS(apiIdVal: String?, dsName: String?) {

    val request = GetDataSourceRequest {
        apiId = apiIdVal
        name = dsName
    }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        val response = appClient.getDataSource(request)
        println("The DataSource ARN is ${response.dataSource?.dataSourceArn}")
    }
}
// snippet-end:[appsync.kotlin.get_ds.main]
