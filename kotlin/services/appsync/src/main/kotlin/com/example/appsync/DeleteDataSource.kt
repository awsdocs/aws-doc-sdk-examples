// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.appsync

// snippet-start:[appsync.kotlin.del_ds.import]
import aws.sdk.kotlin.services.appsync.AppSyncClient
import aws.sdk.kotlin.services.appsync.model.DeleteDataSourceRequest
import kotlin.system.exitProcess
// snippet-end:[appsync.kotlin.del_ds.import]

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
            keyId - The Id of the key to delete.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
    }

    val apiId = args[0]
    val dsName = args[1]
    deleteDS(apiId, dsName)
}

// snippet-start:[appsync.kotlin.del_ds.main]
suspend fun deleteDS(
    apiIdVal: String?,
    dsName: String?,
) {
    val request =
        DeleteDataSourceRequest {
            apiId = apiIdVal
            name = dsName
        }

    AppSyncClient { region = "us-east-1" }.use { appClient ->
        appClient.deleteDataSource(request)
        println("The data source was deleted.")
    }
}
// snippet-end:[appsync.kotlin.del_ds.main]
