// snippet-sourcedescription:[GetDatabase.kt demonstrates how to get a database.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.get_database.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetDatabaseRequest
import kotlin.system.exitProcess
// snippet-end:[glue.kotlin.get_database.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        
        Usage:
            <dbName>

        Where:
            dbName - the database name. 
              
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val databaseName = args[0]
    getSpecificDatabase(databaseName)
}

// snippet-start:[glue.kotlin.get_database.main]
suspend fun getSpecificDatabase(databaseName: String?) {

    val request = GetDatabaseRequest {
        name = databaseName
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getDatabase(request)
        val dbDesc = response.database?.description
        println("The database description is $dbDesc")
    }
}
// snippet-end:[glue.kotlin.get_database.main]
