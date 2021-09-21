//snippet-sourcedescription:[CreateDBSnapshot.kt demonstrates how to create an Amazon Relational Database Service (RDS) snapshot.]
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

// snippet-start:[rds.kotlin.create_snap.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.CreateDbSnapshotRequest
import aws.sdk.kotlin.services.rds.model.RdsException
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.create_snap.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <dbInstanceIdentifier> <dbSnapshotIdentifier>

        Where:
            dbInstanceIdentifier - the database instance identifier. 
            dbSnapshotIdentifier - the dbSnapshotIdentifier identifier. 
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val dbInstanceIdentifier = args[0]
    val dbSnapshotIdentifier = args[1]
    val rdsClient = RdsClient{region="us-west-2"}
    createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier)
    rdsClient.close()
}

// snippet-start:[rds.kotlin.create_snap.main]
suspend fun createSnapshot(rdsClient: RdsClient, dbInstanceIdentifierVal: String?, dbSnapshotIdentifierVal: String?) {
    try {
        val snapshotRequest = CreateDbSnapshotRequest {
            dbInstanceIdentifier = dbInstanceIdentifierVal
            dbSnapshotIdentifier = dbSnapshotIdentifierVal
        }

        val response = rdsClient.createDbSnapshot(snapshotRequest)
        print("The Snapshot id is ${response.dbSnapshot?.dbiResourceId}")

    } catch (e: RdsException) {
        println(e.message)
        rdsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rds.kotlin.create_snap.main]