//snippet-sourcedescription:[ModifyDBInstance.kt demonstrates how to modify an Amazon RDS instance.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.modify_instance.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.ModifyDbInstanceRequest
import aws.sdk.kotlin.services.rds.model.RdsException
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.modify_instance.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <dbInstanceIdentifier> <masterUserPassword> 

        Where:
            dbInstanceIdentifier - the database instance identifier. 
            masterUserPassword - the password that corresponds to the master user name. 
    """

     if (args.size != 2) {
          System.out.println(usage)
          exitProcess(0)
      }

    val dbInstanceIdentifier = args[0]
    val masterUserPassword = args[1]
    val rdsClient = RdsClient{region="us-west-2"}
    updateIntance(rdsClient, dbInstanceIdentifier, masterUserPassword)
    rdsClient.close()
}

// snippet-start:[rds.kotlin.modify_instance.main]
suspend  fun updateIntance(rdsClient: RdsClient, dbInstanceIdentifierVal: String?, masterUserPasswordVal: String?) {
    try {

        val modifyDbInstanceRequest = ModifyDbInstanceRequest {
            dbInstanceIdentifier = dbInstanceIdentifierVal
            publiclyAccessible = true
            masterUserPassword = masterUserPasswordVal
        }

        val instanceResponse = rdsClient.modifyDbInstance(modifyDbInstanceRequest)
        println("The ARN of the modified database is ${instanceResponse.dbInstance?.dbInstanceArn}")

    } catch (e: RdsException) {
        println(e.message)
        rdsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rds.kotlin.modify_instance.main]