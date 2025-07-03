// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.scenerio

import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeSubnetsRequest
import aws.sdk.kotlin.services.ec2.model.DescribeVpcsRequest
import aws.sdk.kotlin.services.ec2.model.Filter
import aws.sdk.kotlin.services.neptune.NeptuneClient
import aws.sdk.kotlin.services.neptune.model.CreateDbClusterRequest
import aws.sdk.kotlin.services.neptune.model.CreateDbInstanceRequest
import aws.sdk.kotlin.services.neptune.model.CreateDbSubnetGroupRequest
import aws.sdk.kotlin.services.neptune.model.DeleteDbClusterRequest
import aws.sdk.kotlin.services.neptune.model.DeleteDbInstanceRequest
import aws.sdk.kotlin.services.neptune.model.DeleteDbSubnetGroupRequest
import aws.sdk.kotlin.services.neptune.model.DescribeDbClustersRequest
import aws.sdk.kotlin.services.neptune.model.DescribeDbInstancesRequest
import aws.sdk.kotlin.services.neptune.model.NeptuneException
import aws.sdk.kotlin.services.neptune.model.StartDbClusterRequest
import aws.sdk.kotlin.services.neptune.model.StopDbClusterRequest
import aws.sdk.kotlin.services.neptune.model.DbSubnetGroupQuotaExceededFault
import aws.sdk.kotlin.services.neptune.model.DbClusterNotFoundFault
import aws.sdk.kotlin.services.neptune.model.DbInstanceNotFoundFault
import aws.sdk.kotlin.services.neptune.model.DbSubnetGroupNotFoundFault
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.Scanner

// snippet-start:[neptune.kotlin.scenario.main]
val DASHES = String(CharArray(80)).replace("\u0000", "-")
var scanner = Scanner(System.`in`)

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    val subnetGroupName = "neptuneSubnetGroup"
    val clusterName = "neptuneCluster"
    val dbInstanceId = "neptuneDB"
    val client = NeptuneClient.fromEnvironment { region = "us-east-1" }

    println(
        """
        Amazon Neptune is a fully managed AWS graph database optimized for complex, connected datasets. 
        It supports property graphs (Gremlin, openCypher) 
        and RDF graphs (SPARQL), making it ideal for knowledge 
        graphs, fraud detection, social networks, 
        recommendations, and network management.

        Neptune handles provisioning, patching, backups, a
        nd replication, offering high availability by default.
    
        Developers can build relationship-aware apps using the 
        AWS SDK for Kotlin and automate infrastructure with 
        NeptuneClient.
                    
        Let's get started...
                    
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    runScenario(client, subnetGroupName, dbInstanceId, clusterName)
    println(
        """
        Thank you for checking out the Amazon Neptune Service Use demo. We hope you
        learned something new, or got some inspiration for your own apps today.
        For more AWS code examples, have a look at:
        https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
        """.trimIndent(),
    )
}

suspend fun runScenario(
    client: NeptuneClient,
    subnetGroupName: String,
    dbInstanceId: String,
    clusterName: String,
) {
    try {
        println(DASHES)
        println("1. Create a Neptune DB Subnet Group")
        println("The Neptune DB subnet group is used when launching a Neptune cluster")
        waitForInputToContinue(scanner)
        createSubnetGroup(client, subnetGroupName)
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("2. Create a Neptune Cluster")
        println("A Neptune Cluster allows you to store and query highly connected datasets with low latency.")
        waitForInputToContinue(scanner)
        val dbClusterId = createDbCluster(client, clusterName)
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("3. Create a Neptune DB Instance")
        println("In this step, we add a new database instance to the Neptune cluster")
        waitForInputToContinue(scanner)
        createDbInstance(client, dbInstanceId, dbClusterId)
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("4. Check the status of the Neptune DB Instance")
        println(
            """
            In this step, we will wait until the DB instance 
            becomes available. This may take around 10 minutes.
            """.trimIndent(),
        )
        waitForInputToContinue(scanner)
        checkInstanceStatus(client, dbInstanceId, "available")
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("5. Show Neptune Cluster details")
        waitForInputToContinue(scanner)
        describeDBClusters(client, dbClusterId)
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("6. Stop the Amazon Neptune cluster")
        println(
            """
            Once stopped, this step polls the status 
            until the cluster is in a stopped state.
            """.trimIndent(),
        )
        waitForInputToContinue(scanner)
        stopDBCluster(client, dbClusterId)
        waitForClusterStatus(client, dbClusterId, "stopped")
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("7. Start the Amazon Neptune cluster")
        println(
            """
            Once started, this step polls the clusters 
            status until it's in an available state.
            We will also poll the instance status.
            """.trimIndent(),
        )
        waitForInputToContinue(scanner)
        startDBCluster(client, dbClusterId)
        waitForClusterStatus(client, dbClusterId, "available")
        checkInstanceStatus(client, dbInstanceId, "available")
        waitForInputToContinue(scanner)
        println(DASHES)

        println(DASHES)
        println("8. Delete the Neptune Assets")
        println("Would you like to delete the Neptune Assets? (y/n)")
        val delAns = scanner.nextLine().trim()
        if (delAns.equals("y", ignoreCase = true)) {
            println("You selected to delete the Neptune assets.")
            deleteDbInstance(client, dbInstanceId)
            waitUntilInstanceDeleted(client, dbInstanceId)
            deleteDBCluster(client, dbClusterId)
            deleteDBSubnetGroup(client, subnetGroupName)
            println("Neptune resources deleted successfully.")
        } else {
            println("You selected not to delete Neptune assets.")
        }
    } catch (e: Exception) {
        println("An error occurred during the scenario: ${e.message}")
        e.printStackTrace()
    }
}

// snippet-start:[neptune.kotlin.delete.subnet.group.main]
/**
 * Deletes a subnet group.
 *
 * @param subnetGroupName the identifier of the subnet group to delete
 * @return a {@link CompletableFuture} that completes when the cluster has been deleted
 */
suspend fun deleteDBSubnetGroup(neptuneClient: NeptuneClient, subnetGroupName: String) {
    val request = DeleteDbSubnetGroupRequest {
        dbSubnetGroupName = subnetGroupName
    }

    try {
        neptuneClient.deleteDbSubnetGroup(request)
        println("Deleting Subnet Group: $subnetGroupName")
    } catch (e: DbSubnetGroupNotFoundFault) {
        println("The subnet group was not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception occurred: ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.delete.subnet.group.main]

// snippet-start:[neptune.kotlin.delete.cluster.main]
/**
 * Deletes a DB instance.
 *
 * @param clusterId the identifier of the cluster to delete
 */
suspend fun deleteDBCluster(neptuneClient: NeptuneClient, clusterId: String) {
    val request = DeleteDbClusterRequest {
        dbClusterIdentifier = clusterId
        skipFinalSnapshot = true
    }

    try {
        neptuneClient.deleteDbCluster(request)
        println("️ Deleting DB Cluster: $clusterId")
    } catch (e: DbClusterNotFoundFault) {
        println("\nResource not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception occurred : ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.delete.cluster.main]

suspend fun waitUntilInstanceDeleted(
    neptuneClient: NeptuneClient,
    instanceId: String,
    timeout: Duration = Duration.ofMinutes(20),
    pollInterval: Duration = Duration.ofSeconds(10),
): Boolean {
    println("Waiting for instance '$instanceId' to be deleted...")

    val startTime = System.currentTimeMillis()

    while (true) {
        try {
            val request = DescribeDbInstancesRequest {
                dbInstanceIdentifier = instanceId
            }

            val response = neptuneClient.describeDbInstances(request)
            val status = response.dbInstances?.firstOrNull()?.dbInstanceStatus ?: "Unknown"
            val elapsed = (System.currentTimeMillis() - startTime) / 1000
            print("\r  Waiting: Instance $instanceId status: ${status.padEnd(10)} (${elapsed}s elapsed)")
            System.out.flush()
        } catch (e: NeptuneException) {
            val errorCode = e.sdkErrorMetadata.errorCode
            return if (errorCode == "DBInstanceNotFound") {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                println("\nInstance '$instanceId' deleted after ${elapsed}s.")
                true
            } else {
                println("\nError polling DB instance '$instanceId': ${errorCode ?: "Unknown"} — ${e.message}")
                false
            }
        } catch (e: Exception) {
            println("\nUnexpected error while polling DB instance '$instanceId': ${e.message}")
            return false
        }

        val elapsedMs = System.currentTimeMillis() - startTime
        if (elapsedMs > timeout.toMillis()) {
            println("\nTimeout: Instance '$instanceId' was not deleted after ${timeout.toMinutes()} minutes.")
            return false
        }

        delay(pollInterval.toMillis())
    }
}

// snippet-start:[neptune.kotlin.delete.instance.main]
/**
 * Deletes the specified Amazon Neptune DB instance.
 *
 * @param neptuneClient The Neptune client used to perform the deletion.
 * @param instanceId The identifier of the DB instance to be deleted.
 *
 * @throws DbInstanceNotFoundFault if the specified DB instance does not exist.
 * @throws NeptuneException if any other error occurs during the deletion process.
 */
suspend fun deleteDbInstance(neptuneClient: NeptuneClient, instanceId: String) {
    val request = DeleteDbInstanceRequest {
        dbInstanceIdentifier = instanceId
        skipFinalSnapshot = true
    }

    try {
        neptuneClient.deleteDbInstance(request)
        println("Deleting DB Instance: $instanceId")
    } catch (e: DbInstanceNotFoundFault) {
        println("\nThe DB instance was not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception occurred : ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.delete.instance.main]

/**
 * Waits for a Neptune cluster to reach a desired status.
 *
 * @param clusterId      the ID of the Neptune cluster to monitor
 * @param desiredStatus  the desired status of the Neptune cluster
 * @param timeout        the maximum time to wait for the cluster to reach the desired status
 * @param pollInterval   the interval at which to check the cluster's status
 *
 */
suspend fun waitForClusterStatus(
    neptuneClient: NeptuneClient,
    clusterId: String,
    desiredStatus: String,
    timeout: Duration = Duration.ofMinutes(20),
    pollInterval: Duration = Duration.ofSeconds(10),
) {
    println("Waiting for cluster $clusterId to reach status '$desiredStatus'...")
    val startTime = System.currentTimeMillis()
    while (true) {
        val request = DescribeDbClustersRequest {
            dbClusterIdentifier = clusterId
        }

        val response = neptuneClient.describeDbClusters(request)
        val currentStatus = response.dbClusters?.firstOrNull()?.status ?: "Unknown"
        val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
        println("Elapsed: ${formatElapsedTime(elapsedSeconds.toInt()).padEnd(20)} Cluster status: ${currentStatus.padEnd(20)}")
        System.out.flush()
        if (currentStatus.equals(desiredStatus, ignoreCase = true)) {
            println("Neptune cluster reached desired status $desiredStatus after ${formatElapsedTime(elapsedSeconds.toInt())}")
            return
        }

        if (System.currentTimeMillis() - startTime > timeout.toMillis()) {
            throw RuntimeException("Timed out waiting for Neptune cluster to reach status: $desiredStatus")
        }
        delay(pollInterval.toMillis())
    }
}

// snippet-start:[neptune.kotlin.start.cluster.main]
/**
 * Starts the specified Amazon Neptune DB cluster.
 *
 * @param neptuneClient The Neptune client used to make the request.
 * @param clusterIdentifier The unique identifier of the DB cluster to be started.
 *
 * @throws DbClusterNotFoundFault if the specified DB cluster does not exist.
 * @throws NeptuneException if any other service-level error occurs.
 */
suspend fun startDBCluster(neptuneClient: NeptuneClient, clusterIdentifier: String) {
    val request = StartDbClusterRequest {
        dbClusterIdentifier = clusterIdentifier
    }

    try {
        neptuneClient.startDbCluster(request)
        println("DB Cluster started : $clusterIdentifier")
    } catch (e: DbClusterNotFoundFault) {
        println("\nResource not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception occurred : ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.start.cluster.main]

// snippet-start:[neptune.kotlin.stop.cluster.main]
/**
 * Stops an Amazon Neptune DB cluster.
 *
 * @param neptuneClient The Neptune client used to make the request.
 * @param clusterIdentifier The unique identifier of the DB cluster to be started.
 *
 * @throws DbClusterNotFoundFault if the specified DB cluster does not exist.
 * @throws NeptuneException if any other service-level error occurs.
 */
suspend fun stopDBCluster(neptuneClient: NeptuneClient, clusterIdentifier: String) {
    val request = StopDbClusterRequest {
        dbClusterIdentifier = clusterIdentifier
    }

    try {
        neptuneClient.stopDbCluster(request)
        println("DB Cluster stopped: $clusterIdentifier")
    } catch (e: DbClusterNotFoundFault) {
        println("\nThe Neptune DB cluster was not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("A Neptune exception occurred : ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.stop.cluster.main]

// snippet-start:[neptune.kotlin.describe.cluster.main]
/**
* Describes the specified Amazon Neptune DB cluster and prints detailed cluster information.
*
* @param neptuneClient The Neptune client used to make the describe request.
* @param clusterId The identifier of the Neptune DB cluster to describe.
*
* @throws DbClusterNotFoundFault if the specified DB cluster does not exist.
* @throws NeptuneException if any other error occurs while describing the cluster.
*/
suspend fun describeDBClusters(neptuneClient: NeptuneClient, clusterId: String) {
    val request = DescribeDbClustersRequest {
        dbClusterIdentifier = clusterId
    }

    try {
        val response = neptuneClient.describeDbClusters(request)
        response.dbClusters?.forEach { cluster ->
            println("Cluster Identifier: ${cluster.dbClusterIdentifier}")
            println("Status: ${cluster.status}")
            println("Engine: ${cluster.engine}")
            println("Engine Version: ${cluster.engineVersion}")
            println("Endpoint: ${cluster.endpoint}")
            println("Reader Endpoint: ${cluster.readerEndpoint}")
            println("Availability Zones: ${cluster.availabilityZones}")
            println("Subnet Group: ${cluster.dbSubnetGroup}")
            println("VPC Security Groups:")
            cluster.vpcSecurityGroups?.forEach { vpcGroup ->
                println("  - ${vpcGroup.vpcSecurityGroupId}")
            }
            println("Storage Encrypted: ${cluster.storageEncrypted}")
            println("IAM DB Auth Enabled: ${cluster.iamDatabaseAuthenticationEnabled}")
            println("Backup Retention Period: ${cluster.backupRetentionPeriod} days")
            println("Preferred Backup Window: ${cluster.preferredBackupWindow}")
            println("Preferred Maintenance Window: ${cluster.preferredMaintenanceWindow}")
            println("------")
        }
    } catch (e: DbClusterNotFoundFault) {
        println("\nThe Neptune DB cluster was not found: ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception occurred : ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.describe.cluster.main]

// snippet-start:[neptune.kotlin.describe.dbinstance.main]
/**
 * Polls the status of an Amazon Neptune DB instance until it reaches the desired status or times out.
 *
 * @param neptuneClient The Neptune client used to query instance status.
 * @param instanceId The identifier of the DB instance to monitor.
 * @param desiredStatus The status to wait for (e.g., "available").
 * @param pollInterval The interval between status checks. Default is 10 seconds.
 *
 * @throws DbInstanceNotFoundFault if the DB instance is not found and should not be retried.
 * @throws NeptuneException if a service-level error occurs.
 */
suspend fun checkInstanceStatus(
    neptuneClient: NeptuneClient,
    instanceId: String,
    desiredStatus: String,
    pollInterval: Duration = Duration.ofSeconds(10),
) {
    val startTime = System.currentTimeMillis()
    println("Checking status for instance '$instanceId'...")

    try {
        while (true) {
            val request = DescribeDbInstancesRequest {
                dbInstanceIdentifier = instanceId
            }

            val currentStatus = try {
                val response = neptuneClient.describeDbInstances(request)
                response.dbInstances?.firstOrNull()?.dbInstanceStatus ?: "Unknown"
            } catch (e: DbInstanceNotFoundFault) {
                println("\nInstance '$instanceId' not found. Retrying...")
                delay(pollInterval.toMillis())
                continue // retry loop
            }

            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            print("\rElapsed: ${formatElapsedTime(elapsedSeconds.toInt()).padEnd(20)}  Status: ${currentStatus.padEnd(20)}")
            System.out.flush()

            if (desiredStatus.equals(currentStatus, ignoreCase = true)) {
                println("\nInstance reached desired status '$desiredStatus' after ${formatElapsedTime(elapsedSeconds.toInt())}.")
                break
            }

            delay(pollInterval.toMillis())
        }
    } catch (e: NeptuneException) {
        println("\nNeptune exception while checking instance status: ${e.message}")
        throw e
    } catch (e: Exception) {
        println("\nUnexpected exception while checking instance status: ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.describe.dbinstance.main]

private fun formatElapsedTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

// snippet-start:[neptune.kotlin.create.dbinstance.main]

/**
* Creates a new Amazon Neptune DB instance and returns its identifier.
*
* @param neptuneClient The Neptune client used to send the create request.
* @param dbInstanceId The identifier to assign to the new DB instance.
* @param dbClusterId The identifier of the Neptune DB cluster to associate the instance with.
* @return The identifier of the created Neptune DB instance.
*
* @throws DbSubnetGroupQuotaExceededFault if the subnet group quota has been exceeded.
* @throws NeptuneException if any other error occurs during the creation of the instance.
*/
suspend fun createDbInstance(neptuneClient: NeptuneClient, dbInstanceId: String, dbClusterId: String): String? {
    val request = CreateDbInstanceRequest {
        dbInstanceIdentifier = dbInstanceId
        dbInstanceClass = "db.r5.large"
        engine = "neptune"
        dbClusterIdentifier = dbClusterId
    }

    try {
        val response = neptuneClient.createDbInstance(request)
        val instanceId = response.dbInstance?.dbInstanceIdentifier
        println("Created Neptune DB Instance: $instanceId")
        return instanceId
    } catch (e: DbSubnetGroupQuotaExceededFault) {
        println("Quota exceeded when creating '$dbInstanceId': ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception when creating '$dbInstanceId': ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.create.dbinstance.main]

// snippet-start:[neptune.kotlin.create.cluster.main]
/**
 * Creates a new Amazon Neptune DB cluster and returns its identifier.
 *
 * @param neptuneClient The Neptune client used to perform the cluster creation.
 * @param dbName The unique identifier to assign to the new DB cluster.
 * @return The identifier of the created Neptune DB cluster.
 *
 * @throws DbSubnetGroupQuotaExceededFault if the subnet group quota is exceeded.
 * @throws NeptuneException if a service-level error occurs during cluster creation.
 * @throws RuntimeException if the cluster is created but no cluster ID is returned.
 */
suspend fun createDbCluster(neptuneClient: NeptuneClient, dbName: String): String {
    val request = CreateDbClusterRequest {
        dbClusterIdentifier = dbName
        engine = "neptune"
        deletionProtection = false
        backupRetentionPeriod = 1
    }

    try {
        val response = neptuneClient.createDbCluster(request)
        val clusterId = response.dbCluster?.dbClusterIdentifier
            ?: throw RuntimeException("Cluster creation succeeded but no ID returned.")

        println("DB Cluster created: $clusterId")
        return clusterId
    } catch (e: DbSubnetGroupQuotaExceededFault) {
        println("Quota exceeded when creating '$dbName': ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception when creating '$dbName': ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.create.cluster.main]

// snippet-start:[neptune.kotlin.create.subnet.main]
/**
 * Creates an Amazon Neptune DB subnet group using the default VPC and its associated subnets.
 *
 * @param neptuneClient The Neptune client used to send the create request.
 * @param groupName The name to assign to the new subnet group.
 *
 * @throws DbSubnetGroupQuotaExceededFault if the subnet group quota has been exceeded.
 * @throws NeptuneException if a service-level error occurs during subnet group creation.
 */
suspend fun createSubnetGroup(neptuneClient: NeptuneClient, groupName: String) {
    val vpcId = getDefaultVpcId()
    val subnetList = getSubnetIds(vpcId)

    val request = CreateDbSubnetGroupRequest {
        dbSubnetGroupName = groupName
        dbSubnetGroupDescription = "Subnet group for Neptune cluster"
        subnetIds = subnetList
    }

    try {
        val response = neptuneClient.createDbSubnetGroup(request)
        val name = response.dbSubnetGroup?.dbSubnetGroupName
        println("Subnet group created: $name")
    } catch (e: DbSubnetGroupQuotaExceededFault) {
        println("Quota exceeded when creating subnet group '$groupName': ${e.message}")
        throw e
    } catch (e: NeptuneException) {
        println("Neptune exception when creating subnet group '$groupName': ${e.message}")
        throw e
    }
}
// snippet-end:[neptune.kotlin.create.subnet.main]

suspend fun getDefaultVpcId(): String {
    Ec2Client.fromEnvironment { region = "us-east-1" }.use { ec2Client ->
        val request = DescribeVpcsRequest {
            filters = listOf(
                Filter {
                    name = "isDefault"
                    values = listOf("true")
                }
            )
        }

        val response = ec2Client.describeVpcs(request)
        val defaultVpcId = response.vpcs?.firstOrNull()?.vpcId
            ?: throw RuntimeException("No default VPC found in this region.")

        println("Default VPC ID: $defaultVpcId")
        return defaultVpcId
    }
}

suspend fun getSubnetIds(vpcId: String): List<String> {
    Ec2Client.fromEnvironment { region = "us-east-1" }.use { ec2Client ->
        val request = DescribeSubnetsRequest {
            filters = listOf(
                Filter {
                    name = "vpc-id"
                    values = listOf(vpcId)
                }
            )
        }
        val response = ec2Client.describeSubnets(request)
        return response.subnets?.mapNotNull { it.subnetId } ?: emptyList()
    }
}

private fun waitForInputToContinue(scanner: Scanner) {
    while (true) {
        println("")
        println("Enter 'c' followed by <ENTER> to continue:")
        val input = scanner.nextLine()

        if (input.trim { it <= ' ' }.equals("c", ignoreCase = true)) {
            println("Continuing with the program...")
            println("")
            break
        } else {
            println("Invalid input. Please try again.")
        }
    }
}
// snippet-end:[neptune.kotlin.scenario.main]
