//snippet-sourcedescription:[RDSScenario.java demonstrates how to perform multiple operations by using an  Amazon Relational Database Service (RDS) service client.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rds;

// snippet-start:[rds.java2.scenario.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceResponse;
import software.amazon.awssdk.services.rds.model.CreateDbParameterGroupResponse;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotRequest;
import software.amazon.awssdk.services.rds.model.CreateDbSnapshotResponse;
import software.amazon.awssdk.services.rds.model.DBEngineVersion;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DBParameterGroup;
import software.amazon.awssdk.services.rds.model.DBSnapshot;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbEngineVersionsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbEngineVersionsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbParameterGroupsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbParametersResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbSnapshotsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbSnapshotsResponse;
import software.amazon.awssdk.services.rds.model.DescribeOrderableDbInstanceOptionsResponse;
import software.amazon.awssdk.services.rds.model.Filter;
import software.amazon.awssdk.services.rds.model.ModifyDbParameterGroupResponse;
import software.amazon.awssdk.services.rds.model.OrderableDBInstanceOption;
import software.amazon.awssdk.services.rds.model.Parameter;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.rds.model.CreateDbParameterGroupRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbParameterGroupsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbParametersRequest;
import software.amazon.awssdk.services.rds.model.ModifyDbParameterGroupRequest;
import software.amazon.awssdk.services.rds.model.DescribeOrderableDbInstanceOptionsRequest;
import software.amazon.awssdk.services.rds.model.DeleteDbParameterGroupRequest;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[rds.java2.scenario.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java example performs these tasks:
 *
 * 1. Returns a list of the available DB engines by invoking the DescribeDbEngineVersions method.
 * 2. Select an engine family and create a custom DB parameter group by invoking the createDBParameterGroup method.
 * 3. Get the parameter groups by invoking the DescribeDbParameterGroups methods.
 * 4. Get parameters in the group by invoking the DescribeDbParameters method.
 * 5. Modify both the auto_increment_offset parameters by invoking the modifyDbParameterGroup method.
 * 6. Get and display the updated parameters.
 * 7. Get a list of allowed engine versions by invoking the describeDbEngineVersions method.
 * 8. Get a list of micro instance classes available for the selected engine
 * 9. Create an RDS database instance that contains a MySql database and uses the parameter group
 * 10. Wait for DB instance to be ready and print out the connection endpoint value.
 * 11. Create a snapshot of the DB instance.
 * 12. Wait for DB snapshot to be ready.
 * 13. Delete the DB instance. rds.DeleteDbInstance.
 * 14. Delete the parameter group.
 */
public class RDSScenario {

    public static long sleepTime = 20;
    public static void main(String[] args) throws InterruptedException {

        final String usage = "\n" +
            "Usage:\n" +
            "    <dbGroupName> <dbParameterGroupFamily> <dbInstanceIdentifier> <dbName> <masterUsername> <masterUserPassword> <dbSnapshotIdentifier>\n\n" +
            "Where:\n" +
            "    dbGroupName - The database group name. \n"+
            "    dbParameterGroupFamily - The database parameter group name.\n"+
            "    dbInstanceIdentifier - The database instance identifier \n"+
            "    dbName -  The database name. \n"+
            "    masterUsername - The master user name. \n"+
            "    masterUserPassword - The password that corresponds to the master user name. \n"+
            "    dbSnapshotIdentifier - The snapshot identifier. \n" ;

        if (args.length != 7) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbGroupName = args[0];
        String dbParameterGroupFamily = args[1];
        String dbInstanceIdentifier = args[2];
        String dbName = args[3];
        String masterUsername = args[4];
        String masterUserPassword = args[5];
        String dbSnapshotIdentifier = args[6];

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
        System.out.println("1. Return a list of the available DB engines");
        describeDBEngines(rdsClient);

        System.out.println("2. Create a custom parameter group");
        createDBParameterGroup(rdsClient, dbGroupName, dbParameterGroupFamily);

        System.out.println("3. Get the parameter groups");
        describeDbParameterGroups(rdsClient, dbGroupName);

        System.out.println("4. Get the parameters in the group");
        describeDbParameters(rdsClient, dbGroupName, 0);

        System.out.println("5. Modify the auto_increment_offset parameter");
        modifyDBParas(rdsClient, dbGroupName);

        System.out.println("6. Display the updated value");
        describeDbParameters(rdsClient, dbGroupName, -1);

        System.out.println("7. Get a list of allowed engine versions");
        getAllowedEngines(rdsClient, dbParameterGroupFamily);

        System.out.println("8. Get a list of micro instance classes available for the selected engine") ;
        getMicroInstances(rdsClient);

        System.out.println("9. Create an RDS database instance that contains a MySql database and uses the parameter group");
        String dbARN = createDatabaseInstance(rdsClient, dbGroupName, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword);
        System.out.println("The ARN of the new database is "+dbARN);


        System.out.println("10. Wait for DB instance to be ready" );
        waitForInstanceReady(rdsClient, dbInstanceIdentifier);

        System.out.println("11. Create a snapshot of the DB instance");
        createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier);

        System.out.println("12. Wait for DB snapshot to be ready" );
        waitForSnapshotReady(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier);

        System.out.println("13. Delete the DB instance" );
        deleteDatabaseInstance(rdsClient, dbInstanceIdentifier);

        System.out.println("14. Delete the parameter group");
        deleteParaGroup(rdsClient, dbGroupName, dbARN);

        System.out.println("The Scenario has successfully completed." );
        rdsClient.close();
    }

    // snippet-start:[rds.java2.scenario.del_paragroup.main]
    // Delete the parameter group after database has been deleted.
    // An exception is thrown if you attempt to delete the para group while database exists.
    public static void deleteParaGroup( RdsClient rdsClient, String dbGroupName, String dbARN) throws InterruptedException {
        try {

            boolean isDataDel  = false;
            boolean didFind;
            String instanceARN ;

            // Make sure that the database has been deleted.
            while (!isDataDel) {
                DescribeDbInstancesResponse response = rdsClient.describeDBInstances();
                List<DBInstance> instanceList = response.dbInstances();
                int listSize = instanceList.size();
                isDataDel = false ; // reset this value
                didFind = false; // reset this value
                int index = 1;
                for (DBInstance instance: instanceList) {
                    instanceARN = instance.dbInstanceArn();
                    if (instanceARN.compareTo(dbARN) == 0) {
                        System.out.println(dbARN + " still exists");
                        didFind = true ;
                    }
                    if ((index == listSize) && (!didFind)) {
                        // Went through the entire list and did not find the database ARN.
                        isDataDel = true;
                    }
                    index ++;
                }
            }

            // Delete the para group.
            DeleteDbParameterGroupRequest parameterGroupRequest = DeleteDbParameterGroupRequest.builder()
                .dbParameterGroupName(dbGroupName)
                .build();

            rdsClient.deleteDBParameterGroup(parameterGroupRequest);
            System.out.println(dbGroupName +" was deleted.");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.del_paragroup.main]

    // snippet-start:[rds.java2.scenario.del_db.main]
    // Delete the DB instance.
    public static void deleteDatabaseInstance( RdsClient rdsClient, String dbInstanceIdentifier) {
        try {
            DeleteDbInstanceRequest deleteDbInstanceRequest = DeleteDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .deleteAutomatedBackups(true)
                .skipFinalSnapshot(true)
                .build();

            DeleteDbInstanceResponse response = rdsClient.deleteDBInstance(deleteDbInstanceRequest);
            System.out.print("The status of the database is " + response.dbInstance().dbInstanceStatus());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.del_db.main]

    // snippet-start:[rds.java2.scenario.wait_db.main]
    // Waits until the snapshot instance is available.
    public static void waitForSnapshotReady(RdsClient rdsClient, String dbInstanceIdentifier, String dbSnapshotIdentifier) {
        try {
            boolean snapshotReady = false;
            String snapshotReadyStr;
            System.out.println("Waiting for the snapshot to become available.");

            DescribeDbSnapshotsRequest snapshotsRequest = DescribeDbSnapshotsRequest.builder()
                .dbSnapshotIdentifier(dbSnapshotIdentifier)
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .build();

            while (!snapshotReady) {
                DescribeDbSnapshotsResponse response = rdsClient.describeDBSnapshots(snapshotsRequest);
                List<DBSnapshot> snapshotList = response.dbSnapshots();
                for (DBSnapshot snapshot : snapshotList) {
                    snapshotReadyStr = snapshot.status();
                    if (snapshotReadyStr.contains("available")) {
                        snapshotReady = true;
                    } else {
                        System.out.print(".");
                        Thread.sleep(sleepTime * 1000);
                    }
                }
            }

            System.out.println("The Snapshot is available!");
        } catch (RdsException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.wait_db.main]

    // snippet-start:[rds.java2.scenario.create_snapshot.main]
    // Create an Amazon RDS snapshot.
    public static void createSnapshot(RdsClient rdsClient, String dbInstanceIdentifier, String dbSnapshotIdentifier) {
        try {
            CreateDbSnapshotRequest snapshotRequest = CreateDbSnapshotRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .dbSnapshotIdentifier(dbSnapshotIdentifier)
                .build();

            CreateDbSnapshotResponse response = rdsClient.createDBSnapshot(snapshotRequest);
            System.out.print("The Snapshot id is " + response.dbSnapshot().dbiResourceId());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.create_snapshot.main]

    // snippet-start:[rds.java2.scenario.wait_db_ready.main]
    // Waits until the database instance is available.
    public static void waitForInstanceReady(RdsClient rdsClient, String dbInstanceIdentifier) {
        boolean instanceReady = false;
        String instanceReadyStr;
        System.out.println("Waiting for instance to become available.");

        try {
            DescribeDbInstancesRequest instanceRequest = DescribeDbInstancesRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .build();

            String endpoint="";
            while (!instanceReady) {
                DescribeDbInstancesResponse response = rdsClient.describeDBInstances(instanceRequest);
                List<DBInstance> instanceList = response.dbInstances();
                for (DBInstance instance : instanceList) {
                    instanceReadyStr = instance.dbInstanceStatus();
                    if (instanceReadyStr.contains("available")) {
                        endpoint = instance.endpoint().address();
                        instanceReady = true;
                    } else {
                        System.out.print(".");
                        Thread.sleep(sleepTime * 1000);
                    }
                }
            }
            System.out.println("Database instance is available! The connection endpoint is "+ endpoint);

        } catch (RdsException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.wait_db_ready.main]

    // snippet-start:[rds.java2.scenario.create_db.main]
    // Create a database instance and return the ARN of the database.
    public static String createDatabaseInstance(RdsClient rdsClient,
                                              String dbGroupName,
                                              String dbInstanceIdentifier,
                                              String dbName,
                                              String masterUsername,
                                              String masterUserPassword) {

        try {
            CreateDbInstanceRequest instanceRequest = CreateDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .allocatedStorage(100)
                .dbName(dbName)
                .dbParameterGroupName(dbGroupName)
                .engine("mysql")
                .dbInstanceClass("db.m4.large")
                .engineVersion("8.0")
                .storageType("standard")
                .masterUsername(masterUsername)
                .masterUserPassword(masterUserPassword)
                .build();

            CreateDbInstanceResponse response = rdsClient.createDBInstance(instanceRequest);
            System.out.print("The status is " + response.dbInstance().dbInstanceStatus());
            return  response.dbInstance().dbInstanceArn();

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }

        return "";
    }
    // snippet-end:[rds.java2.scenario.create_db.main]

    // snippet-start:[rds.java2.scenario.get_instances.main]
    // Get a list of micro instances.
    public static void getMicroInstances(RdsClient rdsClient) {
        try {
            DescribeOrderableDbInstanceOptionsRequest dbInstanceOptionsRequest = DescribeOrderableDbInstanceOptionsRequest.builder()
                .engine("mysql")
                .build();

            DescribeOrderableDbInstanceOptionsResponse response = rdsClient.describeOrderableDBInstanceOptions(dbInstanceOptionsRequest);
            List<OrderableDBInstanceOption> orderableDBInstances = response.orderableDBInstanceOptions();
            for (OrderableDBInstanceOption dbInstanceOption: orderableDBInstances) {
                System.out.println("The engine version is " +dbInstanceOption.engineVersion());
                System.out.println("The engine description is " +dbInstanceOption.engine());
            }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.get_instances.main]

    // snippet-start:[rds.java2.scenario.get_engines.main]
    // Get a list of allowed engine versions.
    public static void getAllowedEngines(RdsClient rdsClient, String dbParameterGroupFamily) {
        try {
            DescribeDbEngineVersionsRequest versionsRequest = DescribeDbEngineVersionsRequest.builder()
                .dbParameterGroupFamily(dbParameterGroupFamily)
                .engine("mysql")
                .build();

           DescribeDbEngineVersionsResponse response = rdsClient.describeDBEngineVersions(versionsRequest);
           List<DBEngineVersion> dbEngines = response.dbEngineVersions();
           for (DBEngineVersion dbEngine: dbEngines) {
               System.out.println("The engine version is " +dbEngine.engineVersion());
               System.out.println("The engine description is " +dbEngine.dbEngineDescription());
           }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.get_engines.main]

    // snippet-start:[rds.java2.scenario.mod_paras.main]
    // Modify auto_increment_offset and auto_increment_increment parameters.
    public static void modifyDBParas(RdsClient rdsClient, String dbGroupName) {
        try {
            Parameter parameter1 = Parameter.builder()
                .parameterName("auto_increment_offset")
                .applyMethod("immediate")
                .parameterValue("5")
                .build();

            List<Parameter> paraList = new ArrayList<>();
            paraList.add(parameter1);

            ModifyDbParameterGroupRequest groupRequest = ModifyDbParameterGroupRequest.builder()
                .dbParameterGroupName(dbGroupName)
                .parameters(paraList)
                .build();

            ModifyDbParameterGroupResponse response = rdsClient.modifyDBParameterGroup(groupRequest);
            System.out.println("The parameter group "+ response.dbParameterGroupName() +" was successfully modified");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.mod_paras.main]

   // snippet-start:[rds.java2.scenario.get_paras.main]
   // Retrieve parameters in the group.
   public static void describeDbParameters(RdsClient rdsClient, String dbGroupName, int flag) {
       try {
           DescribeDbParametersRequest dbParameterGroupsRequest;
           if (flag == 0) {
               dbParameterGroupsRequest = DescribeDbParametersRequest.builder()
                   .dbParameterGroupName(dbGroupName)
                   .build();
           } else {
               dbParameterGroupsRequest = DescribeDbParametersRequest.builder()
                   .dbParameterGroupName(dbGroupName)
                   .source("user")
                   .build();
           }

           DescribeDbParametersResponse response = rdsClient.describeDBParameters(dbParameterGroupsRequest);
           List<Parameter> dbParameters = response.parameters();
           String paraName;
           for (Parameter para: dbParameters) {
               // Only print out information about either auto_increment_offset or auto_increment_increment.
               paraName = para.parameterName();
               if ( (paraName.compareTo("auto_increment_offset") ==0) || (paraName.compareTo("auto_increment_increment ") ==0)) {
                   System.out.println("*** The parameter name is  " + paraName);
                   System.out.println("*** The parameter value is  " + para.parameterValue());
                   System.out.println("*** The parameter data type is " + para.dataType());
                   System.out.println("*** The parameter description is " + para.description());
                   System.out.println("*** The parameter allowed values  is " + para.allowedValues());
               }
           }

       } catch (RdsException e) {
           System.out.println(e.getLocalizedMessage());
           System.exit(1);
       }
   }
   // snippet-end:[rds.java2.scenario.get_paras.main]

    // snippet-start:[rds.java2.scenario.desc_para_groups.main]
    public static void describeDbParameterGroups(RdsClient rdsClient, String dbGroupName) {
        try {
            DescribeDbParameterGroupsRequest groupsRequest = DescribeDbParameterGroupsRequest.builder()
                .dbParameterGroupName(dbGroupName)
                .maxRecords(20)
                .build();

            DescribeDbParameterGroupsResponse response = rdsClient.describeDBParameterGroups(groupsRequest);
            List<DBParameterGroup> groups = response.dbParameterGroups();
            for (DBParameterGroup group: groups) {
                System.out.println("The group name is "+group.dbParameterGroupName());
                System.out.println("The group description is "+group.description());
            }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.desc_para_groups.main]

    // snippet-start:[rds.java2.scenario.create_para_group.main]
    public static void createDBParameterGroup(RdsClient rdsClient, String dbGroupName, String dbParameterGroupFamily) {
        try {
            CreateDbParameterGroupRequest groupRequest = CreateDbParameterGroupRequest.builder()
                .dbParameterGroupName(dbGroupName)
                .dbParameterGroupFamily(dbParameterGroupFamily)
                .description("Created by using the AWS SDK for Java")
                .build();

            CreateDbParameterGroupResponse response = rdsClient.createDBParameterGroup(groupRequest);
            System.out.println("The group name is "+ response.dbParameterGroup().dbParameterGroupName());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.create_para_group.main]


    // snippet-start:[rds.java2.scenario_desc_engine.main]
    public static void describeDBEngines( RdsClient rdsClient) {
        try {
            DescribeDbEngineVersionsRequest engineVersionsRequest = DescribeDbEngineVersionsRequest.builder()
                .defaultOnly(true)
                .engine("mysql")
                .maxRecords(20)
                .build();

            DescribeDbEngineVersionsResponse response = rdsClient.describeDBEngineVersions(engineVersionsRequest);
            List<DBEngineVersion> engines = response.dbEngineVersions();

            // Get all DBEngineVersion objects.
            for (DBEngineVersion engineOb: engines) {
                System.out.println("The name of the DB parameter group family for the database engine is "+engineOb.dbParameterGroupFamily());
                System.out.println("The name of the database engine "+engineOb.engine());
                System.out.println("The version number of the database engine "+engineOb.engineVersion());
            }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario_desc_engine.main]
}
