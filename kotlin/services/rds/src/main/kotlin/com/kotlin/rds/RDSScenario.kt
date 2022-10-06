// snippet-sourcedescription:[RDSScenario.kt demonstrates how to perform multiple operations by using an Amazon Relational Database Service (RDS) service client.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.ApplyMethod
import aws.sdk.kotlin.services.rds.model.CreateDbInstanceRequest
import aws.sdk.kotlin.services.rds.model.CreateDbParameterGroupRequest
import aws.sdk.kotlin.services.rds.model.CreateDbSnapshotRequest
import aws.sdk.kotlin.services.rds.model.DbEngineVersion
import aws.sdk.kotlin.services.rds.model.DbSnapshot
import aws.sdk.kotlin.services.rds.model.DeleteDbInstanceRequest
import aws.sdk.kotlin.services.rds.model.DeleteDbParameterGroupRequest
import aws.sdk.kotlin.services.rds.model.DescribeDbEngineVersionsRequest
import aws.sdk.kotlin.services.rds.model.DescribeDbInstancesRequest
import aws.sdk.kotlin.services.rds.model.DescribeDbParameterGroupsRequest
import aws.sdk.kotlin.services.rds.model.DescribeDbParametersRequest
import aws.sdk.kotlin.services.rds.model.DescribeDbSnapshotsRequest
import aws.sdk.kotlin.services.rds.model.DescribeOrderableDbInstanceOptionsRequest
import aws.sdk.kotlin.services.rds.model.ModifyDbParameterGroupRequest
import aws.sdk.kotlin.services.rds.model.Parameter
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

// snippet-start:[rds.kotlin.scenario.main]
/**
 Before running this Java V2 code example, set up your development environment, including your credentials.

 For more information, see the following documentation topic:

 https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html

 This example performs these tasks:

 1. Returns a list of the available DB engines by invoking the DescribeDbEngineVersions method.
 2. Select an engine family and create a custom DB parameter group by invoking the createDBParameterGroup method.
 3. Get the parameter groups by invoking the DescribeDbParameterGroups methods.
 4. Get parameters in the group by invoking the DescribeDbParameters method.
 5. Modify both the auto_increment_offset and auto_increment_increment parameters by invoking the modifyDbParameterGroup method.
 6. Get and display the updated parameters.
 7. Get a list of allowed engine versions by invoking the describeDbEngineVersions method.
 8. Get a list of micro instance classes available for the selected engine
 9. Create an RDS database instance that contains a MySql database and uses the parameter group
 10. Wait for DB instance to be ready and print out the connection endpoint value.
 11. Create a snapshot of the DB instance.
 12. Wait for DB snapshot to be ready.
 13. Delete the DB instance. rds.DeleteDbInstance.
 14. Delete the parameter group.
 */

var sleepTime: Long = 20
suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <dbGroupName> <dbParameterGroupFamily> <dbInstanceIdentifier> <dbName> <masterUsername> <masterUserPassword> <dbSnapshotIdentifier>

        Where:
            dbGroupName - The database group name. 
            dbParameterGroupFamily - The database parameter group name.
            dbInstanceIdentifier - The database instance identifier 
            dbName -  The database name. 
            masterUsername - The master user name. 
            masterUserPassword - The password that corresponds to the master user name. 
            dbSnapshotIdentifier - The snapshot identifier. 
    """

    if (args.size != 7) {
        println(usage)
        exitProcess(1)
    }

    val dbGroupName = args[0]
    val dbParameterGroupFamily = args[1]
    val dbInstanceIdentifier = args[2]
    val dbName = args[3]
    val masterUsername = args[4]
    val masterUserPassword = args[5]
    val dbSnapshotIdentifier = args[6]

    println("1. Return a list of the available DB engines")
    describeDBEngines()

    println("2. Create a custom parameter group")
    createDBParameterGroup(dbGroupName, dbParameterGroupFamily)

    println("3. Get the parameter groups")
    describeDbParameterGroups(dbGroupName)

    println("4. Get the parameters in the group")
    describeDbParameters(dbGroupName, 0)

    println("5. Modify the auto_increment_offset parameter")
    modifyDBParas(dbGroupName)

    println("6. Display the updated value")
    describeDbParameters(dbGroupName, -1)

    println("7. Get a list of allowed engine versions")
    getAllowedEngines(dbParameterGroupFamily)

    println("8. Get a list of micro instance classes available for the selected engine")
    getMicroInstances()

    println("9. Create an RDS database instance that contains a MySql database and uses the parameter group")
    val dbARN = createDatabaseInstance(dbGroupName, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword)
    println("The ARN of the new database is $dbARN")

    println("10. Wait for DB instance to be ready")
    waitForDbInstanceReady(dbInstanceIdentifier)

    println("11. Create a snapshot of the DB instance")
    createDbSnapshot(dbInstanceIdentifier, dbSnapshotIdentifier)

    println("12. Wait for DB snapshot to be ready")
    waitForSnapshotReady(dbInstanceIdentifier, dbSnapshotIdentifier)

    println("13. Delete the DB instance")
    deleteDbInstance(dbInstanceIdentifier)

    println("14. Delete the parameter group")
    if (dbARN != null) {
        deleteParaGroup(dbGroupName, dbARN)
    }

    println("The Scenario has successfully completed.")
}

// snippet-start:[rds.kotlin.scenario.del_paragroup.main]
suspend fun deleteParaGroup(dbGroupName: String, dbARN: String) {
    var isDataDel = false
    var didFind: Boolean
    var instanceARN: String

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        // Make sure that the database has been deleted.
        while (!isDataDel) {
            val response = rdsClient.describeDbInstances()
            val instanceList = response.dbInstances
            val listSize = instanceList?.size
            isDataDel = false // reset this value
            didFind = false // reset this value
            var index = 1
            if (instanceList != null) {
                for (instance in instanceList) {
                    instanceARN = instance.dbInstanceArn.toString()
                    if (instanceARN.compareTo(dbARN) == 0) {
                        println("$dbARN still exists")
                        didFind = true
                    }
                    if (index == listSize && !didFind) {
                        // Went through the entire list and did not find the database name.
                        isDataDel = true
                    }
                    index++
                }
            }
        }

        // Delete the para group.
        val parameterGroupRequest = DeleteDbParameterGroupRequest {
            dbParameterGroupName = dbGroupName
        }
        rdsClient.deleteDbParameterGroup(parameterGroupRequest)
        println("$dbGroupName was deleted.")
    }
}
// snippet-end:[rds.kotlin.scenario.del_paragroup.main]

// snippet-start:[rds.kotlin.scenario.del_db.main]
suspend fun deleteDbInstance(dbInstanceIdentifierVal: String) {
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
// snippet-end:[rds.kotlin.scenario.del_db.main]

// snippet-start:[rds.kotlin.scenario.wait_db.main]
// Waits until the snapshot instance is available.
suspend fun waitForSnapshotReady(dbInstanceIdentifierVal: String?, dbSnapshotIdentifierVal: String?) {
    var snapshotReady = false
    var snapshotReadyStr: String
    println("Waiting for the snapshot to become available.")

    val snapshotsRequest = DescribeDbSnapshotsRequest {
        dbSnapshotIdentifier = dbSnapshotIdentifierVal
        dbInstanceIdentifier = dbInstanceIdentifierVal
    }

    while (!snapshotReady) {
        RdsClient { region = "us-west-2" }.use { rdsClient ->
            val response = rdsClient.describeDbSnapshots(snapshotsRequest)
            val snapshotList: List<DbSnapshot>? = response.dbSnapshots
            if (snapshotList != null) {
                for (snapshot in snapshotList) {
                    snapshotReadyStr = snapshot.status.toString()
                    if (snapshotReadyStr.contains("available")) {
                        snapshotReady = true
                    } else {
                        print(".")
                        delay(sleepTime * 1000)
                    }
                }
            }
        }
    }
    println("The Snapshot is available!")
}
// snippet-end:[rds.kotlin.scenario.wait_db.main]

// snippet-start:[rds.kotlin.scenario.create_snapshot.main]
// Create an Amazon RDS snapshot.
suspend fun createDbSnapshot(dbInstanceIdentifierVal: String?, dbSnapshotIdentifierVal: String?) {
    val snapshotRequest = CreateDbSnapshotRequest {
        dbInstanceIdentifier = dbInstanceIdentifierVal
        dbSnapshotIdentifier = dbSnapshotIdentifierVal
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.createDbSnapshot(snapshotRequest)
        print("The Snapshot id is ${response.dbSnapshot?.dbiResourceId}")
    }
}
// snippet-end:[rds.kotlin.scenario.create_snapshot.main]

// snippet-start:[rds.kotlin.scenario.wait_db_ready.main]
// Waits until the database instance is available.
suspend fun waitForDbInstanceReady(dbInstanceIdentifierVal: String?) {
    var instanceReady = false
    var instanceReadyStr: String
    println("Waiting for instance to become available.")

    val instanceRequest = DescribeDbInstancesRequest {
        dbInstanceIdentifier = dbInstanceIdentifierVal
    }
    var endpoint = ""
    while (!instanceReady) {
        RdsClient { region = "us-west-2" }.use { rdsClient ->
            val response = rdsClient.describeDbInstances(instanceRequest)
            val instanceList = response.dbInstances
            if (instanceList != null) {
                for (instance in instanceList) {
                    instanceReadyStr = instance.dbInstanceStatus.toString()
                    if (instanceReadyStr.contains("available")) {
                        endpoint = instance.endpoint?.address.toString()
                        instanceReady = true
                    } else {
                        print(".")
                        delay(sleepTime * 1000)
                    }
                }
            }
        }
    }
    println("Database instance is available! The connection endpoint is $endpoint")
}
// snippet-end:[rds.kotlin.scenario.wait_db_ready.main]

// snippet-start:[rds.kotlin.scenario.create_db.main]
// Create a database instance and return the ARN of the database.
suspend fun createDatabaseInstance(dbGroupNameVal: String?, dbInstanceIdentifierVal: String?, dbNameVal: String?, masterUsernameVal: String?, masterUserPasswordVal: String?): String? {
    val instanceRequest = CreateDbInstanceRequest {
        dbInstanceIdentifier = dbInstanceIdentifierVal
        allocatedStorage = 100
        dbName = dbNameVal
        dbParameterGroupName = dbGroupNameVal
        engine = "mysql"
        dbInstanceClass = "db.m4.large"
        engineVersion = "8.0"
        storageType = "standard"
        masterUsername = masterUsernameVal
        masterUserPassword = masterUserPasswordVal
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.createDbInstance(instanceRequest)
        print("The status is ${response.dbInstance?.dbInstanceStatus}")
        return response.dbInstance?.dbInstanceArn
    }
}
// snippet-end:[rds.kotlin.scenario.create_db.main]

// snippet-start:[rds.kotlin.scenario.get_instances.main]
// Get a list of micro instances.
suspend fun getMicroInstances() {
    val dbInstanceOptionsRequest = DescribeOrderableDbInstanceOptionsRequest {
        engine = "mysql"
    }
    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeOrderableDbInstanceOptions(dbInstanceOptionsRequest)
        val orderableDBInstances = response.orderableDbInstanceOptions
        if (orderableDBInstances != null) {
            for (dbInstanceOption in orderableDBInstances) {
                println("The engine version is ${dbInstanceOption.engineVersion}")
                println("The engine description is ${dbInstanceOption.engine}")
            }
        }
    }
}
// snippet-end:[rds.kotlin.scenario.get_instances.main]

// snippet-start:[rds.kotlin.scenario.get_engines.main]
// Get a list of allowed engine versions.
suspend fun getAllowedEngines(dbParameterGroupFamilyVal: String?) {
    val versionsRequest = DescribeDbEngineVersionsRequest {
        dbParameterGroupFamily = dbParameterGroupFamilyVal
        engine = "mysql"
    }
    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeDbEngineVersions(versionsRequest)
        val dbEngines: List<DbEngineVersion>? = response.dbEngineVersions
        if (dbEngines != null) {
            for (dbEngine in dbEngines) {
                println("The engine version is ${dbEngine.engineVersion}")
                println("The engine description is ${dbEngine.dbEngineDescription}")
            }
        }
    }
}
// snippet-end:[rds.kotlin.scenario.get_engines.main]

// snippet-start:[rds.kotlin.scenario.mod_paras.main]
// Modify the auto_increment_offset parameter.
suspend fun modifyDBParas(dbGroupName: String) {
    val parameter1 = Parameter {
        parameterName = "auto_increment_offset"
        applyMethod = ApplyMethod.Immediate
        parameterValue = "5"
    }

    val paraList: ArrayList<Parameter> = ArrayList()
    paraList.add(parameter1)
    val groupRequest = ModifyDbParameterGroupRequest {
        dbParameterGroupName = dbGroupName
        parameters = paraList
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.modifyDbParameterGroup(groupRequest)
        println("The parameter group ${response.dbParameterGroupName} was successfully modified")
    }
}
// snippet-end:[rds.kotlin.scenario.mod_paras.main]

// snippet-start:[rds.kotlin.scenario.get_paras.main]
// Retrieve parameters in the group.
suspend fun describeDbParameters(dbGroupName: String?, flag: Int) {
    val dbParameterGroupsRequest: DescribeDbParametersRequest
    dbParameterGroupsRequest = if (flag == 0) {
        DescribeDbParametersRequest {
            dbParameterGroupName = dbGroupName
        }
    } else {
        DescribeDbParametersRequest {
            dbParameterGroupName = dbGroupName
            source = "user"
        }
    }
    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeDbParameters(dbParameterGroupsRequest)
        val dbParameters: List<Parameter>? = response.parameters
        var paraName: String
        if (dbParameters != null) {
            for (para in dbParameters) {
                // Only print out information about either auto_increment_offset or auto_increment_increment.
                paraName = para.parameterName.toString()
                if (paraName.compareTo("auto_increment_offset") == 0 || paraName.compareTo("auto_increment_increment ") == 0) {
                    println("*** The parameter name is  $paraName")
                    System.out.println("*** The parameter value is  ${para.parameterValue}")
                    System.out.println("*** The parameter data type is ${para.dataType}")
                    System.out.println("*** The parameter description is ${para.description}")
                    System.out.println("*** The parameter allowed values  is ${para.allowedValues}")
                }
            }
        }
    }
}
// snippet-end:[rds.kotlin.scenario.get_paras.main]

// snippet-start:[rds.kotlin.scenario.desc_para_groups.main]
suspend fun describeDbParameterGroups(dbGroupName: String?) {
    val groupsRequest = DescribeDbParameterGroupsRequest {
        dbParameterGroupName = dbGroupName
        maxRecords = 20
    }
    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeDbParameterGroups(groupsRequest)
        val groups = response.dbParameterGroups
        if (groups != null) {
            for (group in groups) {
                println("The group name is ${group.dbParameterGroupName}")
                println("The group description is ${group.description}")
            }
        }
    }
}
// snippet-end:[rds.kotlin.scenario.desc_para_groups.main]

// snippet-start:[rds.kotlin.scenario.create_para_group.main]
// Create a parameter group
suspend fun createDBParameterGroup(dbGroupName: String?, dbParameterGroupFamilyVal: String?) {
    val groupRequest = CreateDbParameterGroupRequest {
        dbParameterGroupName = dbGroupName
        dbParameterGroupFamily = dbParameterGroupFamilyVal
        description = "Created by using the AWS SDK for Kotlin"
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.createDbParameterGroup(groupRequest)
        println("The group name is ${response.dbParameterGroup?.dbParameterGroupName}")
    }
}
// snippet-end:[rds.kotlin.scenario.create_para_group.main]

// snippet-start:[rds.kotlin.scenario_desc_engine.main]
// Returns a list of the available DB engines.
suspend fun describeDBEngines() {
    val engineVersionsRequest = DescribeDbEngineVersionsRequest {
        defaultOnly = true
        engine = "mysql"
        maxRecords = 20
    }

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeDbEngineVersions(engineVersionsRequest)
        val engines: List<DbEngineVersion>? = response.dbEngineVersions

        // Get all DbEngineVersion objects.
        if (engines != null) {
            for (engineOb in engines) {
                println("The name of the DB parameter group family for the database engine is ${engineOb.dbParameterGroupFamily}.")
                println("The name of the database engine ${engineOb.engine}.")
                println("The version number of the database engine ${engineOb.engineVersion}")
            }
        }
    }
}
// snippet-end:[rds.kotlin.scenario_desc_engine.main]
// snippet-end:[rds.kotlin.scenario.main]
