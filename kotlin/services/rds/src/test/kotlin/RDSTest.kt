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
import java.util.Random
import java.util.UUID

/**
 * To run these integration tests, you need to either set the required values
 * in the config.properties file or in AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RDSTest {
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

// Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.

        /*
                val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
                val prop = Properties()

                // Load the properties file.
                prop.load(input)
                dbInstanceIdentifier = prop.getProperty("dbInstanceIdentifier")
                dbSnapshotIdentifier = prop.getProperty("dbSnapshotIdentifier")
                dbName = prop.getProperty("dbName")
                masterUsername = prop.getProperty("masterUsername")
                masterUserPassword = prop.getProperty("masterUserPassword")
                newMasterUserPassword = prop.getProperty("newMasterUserPassword")
                dbGroupNameSc = prop.getProperty("dbGroupNameSc")
                dbParameterGroupFamilySc = prop.getProperty("dbParameterGroupFamilySc")
                dbInstanceIdentifierSc = prop.getProperty("dbInstanceIdentifierSc")
                masterUsernameSc = prop.getProperty("masterUsernameSc")
                masterUserPasswordSc = prop.getProperty("masterUserPasswordSc")
                dbSnapshotIdentifierSc = prop.getProperty("dbSnapshotIdentifierSc")
                dbNameSc = prop.getProperty("dbNameSc")
         */
        }

    @Test
    @Order(2)
    fun createDBInstanceTest() =
        runBlocking {
            createDatabaseInstance(dbInstanceIdentifier, dbName, masterUsername, masterUserPassword)
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun waitForInstanceReadyTest() =
        runBlocking {
            waitForInstanceReady(dbInstanceIdentifier)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun describeAccountAttributesTest() =
        runBlocking {
            getAccountAttributes()
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun describeDBInstancesTest() =
        runBlocking {
            describeInstances()
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun modifyDBInstanceTest() =
        runBlocking {
            updateIntance(dbInstanceIdentifier, newMasterUserPassword)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun createDBSnapshotTest() =
        runBlocking {
            createSnapshot(dbInstanceIdentifier, dbSnapshotIdentifier)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun deleteDBInstanceTest() =
        runBlocking {
            deleteDatabaseInstance(dbInstanceIdentifier)
            println("Test 8 passed")
        }

    @Test
    @Order(9)
    fun scenarioTest() =
        runBlocking {
            println("1. Return a list of the available DB engines")
            describeDBEngines()

            println("2. Create a custom parameter group")
            createDBParameterGroup(dbGroupNameSc, dbParameterGroupFamilySc)

            println("3. Get the parameter groups")
            describeDbParameterGroups(dbGroupNameSc)

            println("4. Get the parameters in the group")
            describeDbParameters(dbGroupNameSc, 0)

            println("5. Modify the auto_increment_offset parameter")
            modifyDBParas(dbGroupNameSc)

            println("6. Display the updated value")
            describeDbParameters(dbGroupNameSc, -1)

            println("7. Get a list of allowed engine versions")
            getAllowedEngines(dbParameterGroupFamilySc)

            println("8. Get a list of micro instance classes available for the selected engine")
            getMicroInstances()

            println("9. Create an RDS database instance that contains a MySql database and uses the parameter group")
            val dbARN =
                createDatabaseInstance(
                    dbGroupNameSc,
                    dbInstanceIdentifierSc,
                    dbNameSc,
                    masterUsername,
                    masterUserPassword
                )
            println("The ARN of the new database is $dbARN")

            println("10. Wait for DB instance to be ready")
            waitForDbInstanceReady(dbInstanceIdentifierSc)

            println("11. Create a snapshot of the DB instance")
            createDbSnapshot(dbInstanceIdentifierSc, dbSnapshotIdentifierSc)

            println("12. Wait for DB snapshot to be ready")
            waitForSnapshotReady(dbInstanceIdentifierSc, dbSnapshotIdentifierSc)

            println("13. Delete the DB instance")
            deleteDbInstance(dbInstanceIdentifierSc)

            println("14. Delete the parameter group")
            if (dbARN != null) {
                deleteParaGroup(dbGroupNameSc, dbARN)
            }
            println("The Scenario has successfully completed.")
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
