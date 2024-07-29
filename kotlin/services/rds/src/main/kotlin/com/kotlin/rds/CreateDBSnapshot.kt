// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.rds

// snippet-start:[rds.kotlin.create_snap.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.CreateDbSnapshotRequest
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.create_snap.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <dbInstanceIdentifier> <dbSnapshotIdentifier>

        Where:
            dbInstanceIdentifier - The database instance identifier. 
            dbSnapshotIdentifier - The dbSnapshotIdentifier identifier. 
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val dbInstanceIdentifier = args[0]
    val dbSnapshotIdentifier = args[1]
    createSnapshot(dbInstanceIdentifier, dbSnapshotIdentifier)
}

// snippet-start:[rds.kotlin.create_snap.main]
suspend fun createSnapshot(
    dbInstanceIdentifierVal: String?,
    dbSnapshotIdentifierVal: String?,
) {
    val snapshotRequest =
        CreateDbSnapshotRequest {
            dbInstanceIdentifier = dbInstanceIdentifierVal
            dbSnapshotIdentifier = dbSnapshotIdentifierVal
        }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.createDbSnapshot(snapshotRequest)
        print("The Snapshot id is ${response.dbSnapshot?.dbiResourceId}")
    }
}
// snippet-end:[rds.kotlin.create_snap.main]
