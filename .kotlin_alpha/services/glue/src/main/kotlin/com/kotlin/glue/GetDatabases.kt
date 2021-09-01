//snippet-sourcedescription:[GetDatabases.kt demonstrates how to get databases.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[6/4/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.get_databases.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetDatabasesRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_databases.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val glueClient= GlueClient{region ="us-east-1"}
    getAllDatabases(glueClient)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_databases.main]
suspend fun getAllDatabases(glueClient: GlueClient) {
    try {
        val databasesRequest = GetDatabasesRequest {
            maxResults = 10
        }

        val response = glueClient.getDatabases(databasesRequest)
        val databases = response.databaseList
        if (databases != null) {
            for (database in databases) {
                println("The database name is ${database.name}")
            }
        }

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_databases.main]