/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


import com.example.rds.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rds.RdsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonRDSTest {

    private static  RdsClient rdsClient ;
    private static Region region;
    private static String dbInstanceIdentifier = "" ;
    private static String dbSnapshotIdentifier = "" ;
    private static String dbName = "" ;
    private static String masterUsername = "" ;
    private static String masterUserPassword = "" ;
    private static String newMasterUserPassword = "" ;

    // Set data members required for the Scenario test
    private static String  dbGroupNameSc = "" ;
    private static String  dbParameterGroupFamilySc = "" ;
    private static String  dbInstanceIdentifierSc = "" ;
    private static String  masterUsernameSc = "" ;
    private static String  masterUserPasswordSc = "" ;
    private static String  dbSnapshotIdentifierSc = "" ;
    private static String  dbNameSc = "" ;



    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonRDSTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            dbInstanceIdentifier = prop.getProperty("dbInstanceIdentifier");
            dbSnapshotIdentifier = prop.getProperty("dbSnapshotIdentifier");
            dbName = prop.getProperty("dbName");
            masterUsername = prop.getProperty("masterUsername");
            masterUserPassword = prop.getProperty("masterUserPassword");
            newMasterUserPassword = prop.getProperty("newMasterUserPassword");
            dbGroupNameSc = prop.getProperty("dbGroupNameSc");
            dbParameterGroupFamilySc = prop.getProperty("dbParameterGroupFamilySc");
            dbInstanceIdentifierSc = prop.getProperty("dbInstanceIdentifierSc");
            masterUsernameSc = prop.getProperty("masterUsernameSc");
            masterUserPasswordSc = prop.getProperty("masterUserPasswordSc");
            dbSnapshotIdentifierSc = prop.getProperty("dbSnapshotIdentifierSc");
            dbNameSc = prop.getProperty("dbNameSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSRdsService_thenNotNull() {
        assertNotNull(rdsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDBInstance() {
        CreateDBInstance.createDatabaseInstance(rdsClient, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void waitForInstanceReady() {

        CreateDBInstance.waitForInstanceReady(rdsClient, dbInstanceIdentifier);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeAccountAttributes() {
        DescribeAccountAttributes.getAccountAttributes(rdsClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeDBInstances() {

        DescribeDBInstances.describeInstances(rdsClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ModifyDBInstance() {
        ModifyDBInstance.updateIntance(rdsClient, dbInstanceIdentifier, newMasterUserPassword);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateDBSnapshot() {
        CreateDBSnapshot.createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteDBInstance() {

        DeleteDBInstance.deleteDatabaseInstance(rdsClient, dbInstanceIdentifier);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(8)
    public void TestRDSScenario() throws InterruptedException {
        System.out.println("1. Return a list of the available DB engines");
        RDSScenario.describeDBEngines(rdsClient);

        System.out.println("2. Create a custom arameter group");
        RDSScenario.createDBParameterGroup(rdsClient, dbGroupNameSc, dbParameterGroupFamilySc);

        System.out.println("3. Get the parameter groups");
        RDSScenario.describeDbParameterGroups(rdsClient, dbGroupNameSc);

        System.out.println("4. Get the parameters in the group");
        RDSScenario.describeDbParameters(rdsClient, dbGroupNameSc, 0);

        System.out.println("5. Modify both the auto_increment_offset parameter");
        RDSScenario.modifyDBParas(rdsClient, dbGroupNameSc);

        System.out.println("6. Modify both the auto_increment_offset parameter");
        RDSScenario.describeDbParameters(rdsClient, dbGroupNameSc, -1);

        System.out.println("7. Get a list of allowed engine versions");
        RDSScenario.getAllowedEngines(rdsClient, dbParameterGroupFamilySc);

        System.out.println("8. Get a list of micro instance classes available for the selected engine") ;
        RDSScenario.getMicroInstances(rdsClient);

        System.out.println("9. Create an RDS database instance that contains a MySql database and uses the parameter group");
        String dbARN = RDSScenario.createDatabaseInstance(rdsClient, dbGroupNameSc, dbInstanceIdentifierSc, dbNameSc, masterUsernameSc, masterUserPasswordSc);

        System.out.println("10. Wait for DB instance to be ready" );
        RDSScenario.waitForInstanceReady(rdsClient, dbInstanceIdentifierSc);

        System.out.println("11. Create a snapshot of the DB instance");
        RDSScenario.createSnapshot(rdsClient, dbInstanceIdentifierSc, dbSnapshotIdentifierSc);

        System.out.println("12. Wait for DB snapshot to be ready" );
        RDSScenario.waitForSnapshotReady(rdsClient, dbInstanceIdentifierSc, dbSnapshotIdentifierSc);

        System.out.println("13. Delete the DB instance" );
        RDSScenario.deleteDatabaseInstance(rdsClient, dbInstanceIdentifierSc);

        System.out.println("14. Delete the parameter group" );
        RDSScenario.deleteParaGroup(rdsClient, dbGroupNameSc, dbARN);

        System.out.println("The Scenario has successfully completed." );
    }

 }



