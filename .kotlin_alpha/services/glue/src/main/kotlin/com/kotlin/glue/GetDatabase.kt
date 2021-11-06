//snippet-sourcedescription:[GetDatabase.kt demonstrates how to get a database.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.get_database.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetDatabaseRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_database.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

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
    val glueClient= GlueClient{region ="us-east-1"}
    getSpecificDatabase(glueClient, databaseName)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_database.main]
suspend fun getSpecificDatabase(glueClient: GlueClient, databaseName: String?) {

    try {

        val databasesRequest = GetDatabaseRequest {
            name = databaseName
        }

        val response = glueClient.getDatabase(databasesRequest)
        val dbDesc = response.database?.description
        println("The database description is $dbDesc")

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_database.main]