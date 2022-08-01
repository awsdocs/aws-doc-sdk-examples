// snippet-sourcedescription:[DeleteDBInstance.kt demonstrates how to delete an Amazon Relational Database Service (RDS) snapshot.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.delete_instance.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DeleteDbInstanceRequest
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.delete_instance.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <dbInstanceIdentifier> 
        Where:
            dbInstanceIdentifier - The database instance identifier to delete. 
           
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val dbInstanceIdentifier = args[0]
    deleteDatabaseInstance(dbInstanceIdentifier)
}

// snippet-start:[rds.kotlin.delete_instance.main]
suspend fun deleteDatabaseInstance(dbInstanceIdentifierVal: String?) {

    val deleteDbInstanceRequest = DeleteDbInstanceRequest {
        dbInstanceIdentifier = dbInstanceIdentifierVal
        deleteAutomatedBackups = true
        skipFinalSnapshot = true
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.deleteDbInstance(deleteDbInstanceRequest)
        print("The status of the database is ${response.dbInstance?.dbInstanceStatus}")
    }
}
// snippet-end:[rds.kotlin.delete_instance.main]
