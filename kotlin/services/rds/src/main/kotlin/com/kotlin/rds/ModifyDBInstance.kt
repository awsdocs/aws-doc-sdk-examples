// snippet-sourcedescription:[ModifyDBInstance.kt demonstrates how to modify an Amazon RDS instance.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.modify_instance.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.ModifyDbInstanceRequest
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.modify_instance.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <dbInstanceIdentifier> <masterUserPassword> 

        Where:
            dbInstanceIdentifier - The database instance identifier. 
            masterUserPassword - The password that corresponds to the master user name. 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val dbInstanceIdentifier = args[0]
    val masterUserPassword = args[1]
    updateIntance(dbInstanceIdentifier, masterUserPassword)
}

// snippet-start:[rds.kotlin.modify_instance.main]
suspend fun updateIntance(dbInstanceIdentifierVal: String?, masterUserPasswordVal: String?) {

    val request = ModifyDbInstanceRequest {
        dbInstanceIdentifier = dbInstanceIdentifierVal
        publiclyAccessible = true
        masterUserPassword = masterUserPasswordVal
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val instanceResponse = rdsClient.modifyDbInstance(request)
        println("The ARN of the modified database is ${instanceResponse.dbInstance?.dbInstanceArn}")
    }
}
// snippet-end:[rds.kotlin.modify_instance.main]
