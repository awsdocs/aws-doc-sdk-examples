// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.rds.createDBParameterGroup
import com.kotlin.rds.createDatabaseInstance
import com.kotlin.rds.createDbSnapshot
import com.kotlin.rds.createSnapshot
import com.kotlin.rds.deleteDatabaseInstance
import com.kotlin.rds.deleteDbInstance
import com.kotlin.rds.deleteParaGroup
import com.kotlin.rds.describeDBEngines
import com.kotlin.rds.describeDbParameterGroups
import com.kotlin.rds.describeDbParameters
import com.kotlin.rds.describeInstances
import com.kotlin.rds.getAccountAttributes
import com.kotlin.rds.getAllowedEngines
import com.kotlin.rds.getMicroInstances
import com.kotlin.rds.modifyDBParas
import com.kotlin.rds.updateIntance
import com.kotlin.rds.waitForDbInstanceReady
import com.kotlin.rds.waitForInstanceReady
import com.kotlin.rds.waitForSnapshotReady
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Random
import java.util.UUID

/**
 * To run these integration tests, you need to either set the required values
 * in the config.properties file or in AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RDSTest {
    private val logger: Logger = LoggerFactory.getLogger(RDSTest::class.java)
    private var dbInstanceIdentifier = ""
    private var dbSnapshotIdentifier = ""
    private var dbName = ""
    private var masterUsername = ""
    private var masterUserPassword = ""
    private var newMasterUserPassword = ""
    private var dbGroupNameSc = ""
    private var dbParameterGroupFamilySc = ""
    private var dbInstanceIdentifierSc = ""
    private var dbSnapshotIdentifierSc = ""
    private var dbNameSc = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            val rand = Random()
            val randomNum = rand.nextInt(10000 - 1 + 1) + 1

            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            dbInstanceIdentifier = values.dbInstanceIdentifier + UUID.randomUUID()
            dbSnapshotIdentifier = values.dbSnapshotIdentifier + UUID.randomUUID()
            dbName = values.dbName + randomNum
            masterUsername = values.masterUsername.toString()
            masterUserPassword = values.masterUserPasswordSc.toString()
            newMasterUserPassword = values.newMasterUserPassword.toString()
            dbGroupNameSc = values.dbGroupNameSc + UUID.randomUUID()
            dbParameterGroupFamilySc = values.dbParameterGroupFamilySc.toString()
            dbInstanceIdentifierSc = values.dbInstanceIdentifierSc + UUID.randomUUID()
            dbSnapshotIdentifierSc = values.dbSnapshotIdentifierSc + UUID.randomUUID()
            dbNameSc = values.dbNameSc + randomNum
        }

    @Test
    @Order(1)
    fun createDBInstanceTest() =
        runBlocking {
            createDatabaseInstance(dbInstanceIdentifier, dbName, masterUsername, masterUserPassword)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun waitForInstanceReadyTest() =
        runBlocking {
            waitForInstanceReady(dbInstanceIdentifier)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun describeAccountAttributesTest() =
        runBlocking {
            getAccountAttributes()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun describeDBInstancesTest() =
        runBlocking {
            describeInstances()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun modifyDBInstanceTest() =
        runBlocking {
            updateIntance(dbInstanceIdentifier, newMasterUserPassword)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun createDBSnapshotTest() =
        runBlocking {
            createSnapshot(dbInstanceIdentifier, dbSnapshotIdentifier)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteDBInstanceTest() =
        runBlocking {
            deleteDatabaseInstance(dbInstanceIdentifier)
            logger.info("Test 7 passed")
        }

    @Test
    @Order(8)
    fun scenarioTest() =
        runBlocking {
            describeDBEngines()
            createDBParameterGroup(dbGroupNameSc, dbParameterGroupFamilySc)
            describeDbParameterGroups(dbGroupNameSc)
            describeDbParameters(dbGroupNameSc, 0)
            modifyDBParas(dbGroupNameSc)
            describeDbParameters(dbGroupNameSc, -1)
            getAllowedEngines(dbParameterGroupFamilySc)
            getMicroInstances()
            val dbARN =
                createDatabaseInstance(
                    dbGroupNameSc,
                    dbInstanceIdentifierSc,
                    dbNameSc,
                    masterUsername,
                    masterUserPassword,
                )
            waitForDbInstanceReady(dbInstanceIdentifierSc)
            createDbSnapshot(dbInstanceIdentifierSc, dbSnapshotIdentifierSc)
            waitForSnapshotReady(dbInstanceIdentifierSc, dbSnapshotIdentifierSc)
            deleteDbInstance(dbInstanceIdentifierSc)
            if (dbARN != null) {
                deleteParaGroup(dbGroupNameSc, dbARN)
            }
            logger.info("Test 8 passed.")
        }

    suspend fun getSecretValues(): String? {
        val secretName = "test/rds"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }

        SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
            val valueResponse = secretsClient.getSecretValue(valueRequest)
            return valueResponse.secretString
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/rds (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val dbInstanceIdentifier: String? = null
        val dbSnapshotIdentifier: String? = null
        val dbName: String? = null
        val masterUsername: String? = null
        val masterUserPassword: String? = null
        val newMasterUserPassword: String? = null
        val dbGroupNameSc: String? = null
        val dbParameterGroupFamilySc: String? = null
        val dbInstanceIdentifierSc: String? = null
        val dbNameSc: String? = null
        val masterUsernameSc: String? = null
        val masterUserPasswordSc: String? = null
        val dbSnapshotIdentifierSc: String? = null
        val dbClusterGroupName: String? = null
        val dbParameterGroupFamily: String? = null
        val dbInstanceClusterIdentifier: String? = null
        val secretName: String? = null
    }
}
