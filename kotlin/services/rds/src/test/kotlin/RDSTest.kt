/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RDSTest {
    private var dbInstanceIdentifier = ""
    private var dbSnapshotIdentifier = ""
    private var dbName = ""
    private var masterUsername = ""
    private var masterUserPassword = ""
    private var newMasterUserPassword = ""

    // Set data members required for the Scenario test
    private var dbGroupNameSc = ""
    private var dbParameterGroupFamilySc = ""
    private var dbInstanceIdentifierSc = ""
    private var masterUsernameSc = ""
    private var masterUserPasswordSc = ""
    private var dbSnapshotIdentifierSc = ""
    private var dbNameSc = ""

    @BeforeAll
    fun setup() {
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
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
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertTrue(!dbInstanceIdentifier.isEmpty())
        Assertions.assertTrue(!dbSnapshotIdentifier.isEmpty())
        Assertions.assertTrue(!dbName.isEmpty())
        Assertions.assertTrue(!masterUsername.isEmpty())
        Assertions.assertTrue(!masterUserPassword.isEmpty())
        Assertions.assertTrue(!newMasterUserPassword.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createDBInstanceTest() = runBlocking {
        createDatabaseInstance(dbInstanceIdentifier, dbName, masterUsername, masterUserPassword)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun waitForInstanceReadyTest() = runBlocking {
        waitForInstanceReady(dbInstanceIdentifier)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun DescribeAccountAttributesTest() = runBlocking {
        getAccountAttributes()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun describeDBInstancesTest() = runBlocking {
        describeInstances()
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun modifyDBInstanceTest() = runBlocking {
        updateIntance(dbInstanceIdentifier, newMasterUserPassword)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun CreateDBSnapshotTest() = runBlocking {
        createSnapshot(dbInstanceIdentifier, dbSnapshotIdentifier)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun deleteDBInstanceTest() = runBlocking {
        deleteDatabaseInstance(dbInstanceIdentifier)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun scenarioTest() = runBlocking {
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
        val dbARN = createDatabaseInstance(dbGroupNameSc, dbInstanceIdentifierSc, dbNameSc, masterUsernameSc, masterUserPasswordSc)
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
}
