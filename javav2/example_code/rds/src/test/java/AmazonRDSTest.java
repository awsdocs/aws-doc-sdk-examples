/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.rds.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rds.RdsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // Set data members required for the Scenario test.
    private static String  dbGroupNameSc = "" ;
    private static String  dbParameterGroupFamilySc = "" ;
    private static String  dbInstanceIdentifierSc = "" ;
    private static String  masterUsernameSc = "" ;
    private static String  masterUserPasswordSc = "" ;
    private static String  dbSnapshotIdentifierSc = "" ;
    private static String  dbNameSc = "" ;

    @BeforeAll
    public static void setUp() throws IOException {
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

            prop.load(input);
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
        assertDoesNotThrow(() ->CreateDBInstance.createDatabaseInstance(rdsClient, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void waitForInstanceReady() {
        assertDoesNotThrow(() ->CreateDBInstance.waitForInstanceReady(rdsClient, dbInstanceIdentifier));
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeAccountAttributes() {
        assertDoesNotThrow(() ->DescribeAccountAttributes.getAccountAttributes(rdsClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeDBInstances() {
        assertDoesNotThrow(() ->DescribeDBInstances.describeInstances(rdsClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ModifyDBInstance() {
        assertDoesNotThrow(() ->ModifyDBInstance.updateIntance(rdsClient, dbInstanceIdentifier, newMasterUserPassword));
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateDBSnapshot() {
        assertDoesNotThrow(() ->CreateDBSnapshot.createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier));
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteDBInstance() {
        assertDoesNotThrow(() ->DeleteDBInstance.deleteDatabaseInstance(rdsClient, dbInstanceIdentifier));
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void TestRDSScenario() throws InterruptedException {
        assertDoesNotThrow(() ->RDSScenario.describeDBEngines(rdsClient));
        assertDoesNotThrow(() ->RDSScenario.createDBParameterGroup(rdsClient, dbGroupNameSc, dbParameterGroupFamilySc));
        assertDoesNotThrow(() ->RDSScenario.describeDbParameterGroups(rdsClient, dbGroupNameSc));
        assertDoesNotThrow(() ->RDSScenario.describeDbParameters(rdsClient, dbGroupNameSc, 0));
        assertDoesNotThrow(() ->RDSScenario.modifyDBParas(rdsClient, dbGroupNameSc));
        assertDoesNotThrow(() ->RDSScenario.describeDbParameters(rdsClient, dbGroupNameSc, -1));
        assertDoesNotThrow(() ->RDSScenario.getAllowedEngines(rdsClient, dbParameterGroupFamilySc));
        assertDoesNotThrow(() ->RDSScenario.getMicroInstances(rdsClient));
        String dbARN = RDSScenario.createDatabaseInstance(rdsClient, dbGroupNameSc, dbInstanceIdentifierSc, dbNameSc, masterUsernameSc, masterUserPasswordSc);
        assertFalse(dbARN.isEmpty());
        assertDoesNotThrow(() ->RDSScenario.waitForInstanceReady(rdsClient, dbInstanceIdentifierSc));
        assertDoesNotThrow(() ->RDSScenario.createSnapshot(rdsClient, dbInstanceIdentifierSc, dbSnapshotIdentifierSc));
        assertDoesNotThrow(() ->RDSScenario.waitForSnapshotReady(rdsClient, dbInstanceIdentifierSc, dbSnapshotIdentifierSc));
        assertDoesNotThrow(() ->RDSScenario.deleteDatabaseInstance(rdsClient, dbInstanceIdentifierSc));
        assertDoesNotThrow(() ->RDSScenario.deleteParaGroup(rdsClient, dbGroupNameSc, dbARN));
        System.out.println("Test 9 passed");
    }
}



