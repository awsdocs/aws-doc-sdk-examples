///snippet-sourcedescription:[DeleteDBInstance.kt demonstrates how to delete an Amazon Relational Database Service (RDS) snapshot.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[5/28/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.delete_instance.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DeleteDbInstanceRequest
import aws.sdk.kotlin.services.rds.model.RdsException
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.delete_instance.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <dbInstanceIdentifier> 
        Where:
            dbInstanceIdentifier - the database instance identifier to delete. 
           
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val dbInstanceIdentifier = args[0]
    val rdsClient = RdsClient{region="us-west-2"}
    deleteDatabaseInstance(rdsClient, dbInstanceIdentifier)
    rdsClient.close()
}

// snippet-start:[rds.kotlin.delete_instance.main]
suspend fun deleteDatabaseInstance(rdsClient: RdsClient, dbInstanceIdentifierVal: String?) {
    try {
        val deleteDbInstanceRequest = DeleteDbInstanceRequest {
            dbInstanceIdentifier = dbInstanceIdentifierVal
            deleteAutomatedBackups = true
            skipFinalSnapshot = true
        }

        val response = rdsClient.deleteDbInstance(deleteDbInstanceRequest)
        print("The status of the database is ${response.dbInstance?.dbInstanceStatus}")

    } catch (e: RdsException) {
        println(e.message)
        rdsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rds.kotlin.delete_instance.main]