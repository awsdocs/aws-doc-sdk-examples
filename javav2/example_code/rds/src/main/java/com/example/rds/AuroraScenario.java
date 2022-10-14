//snippet-sourcedescription:[AuroraScenario.java demonstrates how to perform multiple operations on Aurora Clusters by using an Amazon Relational Database Service (RDS) service client.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rds;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbClusterParameterGroupRequest;
import software.amazon.awssdk.services.rds.model.CreateDbClusterParameterGroupResponse;
import software.amazon.awssdk.services.rds.model.CreateDbClusterResponse;
import software.amazon.awssdk.services.rds.model.CreateDbClusterSnapshotRequest;
import software.amazon.awssdk.services.rds.model.CreateDbClusterSnapshotResponse;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceResponse;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBClusterParameterGroup;
import software.amazon.awssdk.services.rds.model.DBClusterSnapshot;
import software.amazon.awssdk.services.rds.model.DBEngineVersion;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DeleteDbClusterParameterGroupRequest;
import software.amazon.awssdk.services.rds.model.DeleteDbClusterRequest;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParameterGroupsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParameterGroupsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParametersRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParametersResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterSnapshotsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterSnapshotsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbClustersRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbClustersResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbEngineVersionsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.DescribeOrderableDbInstanceOptionsResponse;
import software.amazon.awssdk.services.rds.model.ModifyDbClusterParameterGroupRequest;
import software.amazon.awssdk.services.rds.model.ModifyDbClusterParameterGroupResponse;
import software.amazon.awssdk.services.rds.model.OrderableDBInstanceOption;
import software.amazon.awssdk.services.rds.model.Parameter;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.rds.model.DescribeDbEngineVersionsRequest;
import software.amazon.awssdk.services.rds.model.CreateDbClusterRequest;
import software.amazon.awssdk.services.rds.model.DescribeOrderableDbInstanceOptionsRequest;
import java.util.ArrayList;
import java.util.List;

// snippet-start:[rds.java2.scenario.aurora.main]
/**
 * Before running this Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java example performs the following tasks:
 *
 * 1. Gets available engine families for Amazon Aurora MySQL-Compatible Edition by calling the DescribeDbEngineVersions(Engine='aurora-mysql') method.
 * 2. Selects an engine family and creates a custom DB cluster parameter group by invoking the describeDBClusterParameters method.
 * 3. Gets the parameter groups by invoking the describeDBClusterParameterGroups method.
 * 4. Gets parameters in the group by invoking the describeDBClusterParameters method.
 * 5. Modifies the auto_increment_offset parameter by invoking the modifyDbClusterParameterGroupRequest method.
 * 6. Gets and displays the updated parameters.
 * 7. Gets a list of allowed engine versions by invoking the describeDbEngineVersions method.
 * 8. Creates an Aurora DB cluster database cluster that contains a MySQL database.
 * 9.  Waits for DB instance to be ready.
 * 10. Gets a list of instance classes available for the selected engine.
 * 11. Creates a database instance in the cluster.
 * 12. Waits for DB instance to be ready.
 * 13. Creates a snapshot.
 * 14. Waits for DB snapshot to be ready.
 * 15. Deletes the DB cluster.
 * 16. Deletes the DB cluster group.
 */

public class AuroraScenario {
    public static long sleepTime = 20;
    public static void main(String[] args) throws InterruptedException {

        final String usage = "\n" +
            "Usage:\n" +
            "    <dbClusterGroupName> <dbParameterGroupFamily> <dbInstanceClusterIdentifier> <dbInstanceIdentifier> <dbName> <dbSnapshotIdentifier> <username> <userPassword>" +
            "Where:\n" +
            "    dbClusterGroupName - The name of the DB cluster parameter group. \n"+
            "    dbParameterGroupFamily - The DB cluster parameter group family name (for example, aurora-mysql5.7). \n"+
            "    dbInstanceClusterIdentifier - The instance cluster identifier value.\n"+
            "    dbInstanceIdentifier - The database instance identifier.\n"+
            "    dbName  The database name.\n"+
            "    dbSnapshotIdentifier - The snapshot identifier.\n"+
            "    username - The database user name.\n" +
            "    userPassword - The database user name password.\n" ;

        if (args.length != 8) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbClusterGroupName = args[0];
        String dbParameterGroupFamily = args[1];
        String dbInstanceClusterIdentifier = args[2];
        String dbInstanceIdentifier = args[3];
        String dbName = args[4];
        String dbSnapshotIdentifier = args[5];
        String username = args[6];
        String userPassword = args[7];

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println("1. Return a list of the available DB engines");
        describeDBEngines(rdsClient);

        System.out.println("2. Create a custom parameter group");
        createDBClusterParameterGroup(rdsClient, dbClusterGroupName, dbParameterGroupFamily);

        System.out.println("3. Get the parameter group");
        describeDbClusterParameterGroups(rdsClient, dbClusterGroupName);

        System.out.println("4. Get the parameters in the group");
        describeDbClusterParameters(rdsClient, dbClusterGroupName, 0);

        System.out.println("5. Modify the auto_increment_offset parameter");
        modifyDBClusterParas(rdsClient, dbClusterGroupName);

        System.out.println("6. Display the updated parameter value");
        describeDbClusterParameters(rdsClient, dbClusterGroupName, -1);

        System.out.println("7. Get a list of allowed engine versions");
        getAllowedEngines(rdsClient, dbParameterGroupFamily);

        System.out.println("8. Create an Aurora DB cluster database");
        String arnClusterVal = createDBCluster(rdsClient, dbClusterGroupName, dbName, dbInstanceClusterIdentifier, username, userPassword) ;
        System.out.println("The ARN of the cluster is "+arnClusterVal);

        System.out.println("9. Wait for DB instance to be ready" );
        waitForInstanceReady(rdsClient, dbInstanceClusterIdentifier);

        System.out.println("10. Get a list of instance classes available for the selected engine");
        String instanceClass = getListInstanceClasses(rdsClient);

        System.out.println("11. Create a database instance in the cluster.");
        String clusterDBARN = createDBInstanceCluster(rdsClient, dbInstanceIdentifier, dbInstanceClusterIdentifier, instanceClass);
        System.out.println("The ARN of the database is "+clusterDBARN);

        System.out.println("12. Wait for DB instance to be ready" );
        waitDBInstanceReady(rdsClient, dbInstanceIdentifier);

        System.out.println("13. Create a snapshot");
        createDBClusterSnapshot(rdsClient, dbInstanceClusterIdentifier, dbSnapshotIdentifier);

        System.out.println("14. Wait for DB snapshot to be ready" );
        waitForSnapshotReady(rdsClient, dbSnapshotIdentifier, dbInstanceClusterIdentifier);

        System.out.println("14. Delete the DB instance" );
        deleteDatabaseInstance(rdsClient, dbInstanceIdentifier);

        System.out.println("15. Delete the DB cluster");
        deleteCluster(rdsClient, dbInstanceClusterIdentifier);

        System.out.println("16. Delete the DB cluster group");
        deleteDBClusterGroup(rdsClient, dbClusterGroupName, clusterDBARN);

        System.out.println("The Scenario has successfully completed." );
        rdsClient.close();
    }

    // snippet-start:[rds.java2.scenario.cluster.del_paragroup.main]
    public static void deleteDBClusterGroup( RdsClient rdsClient, String dbClusterGroupName, String clusterDBARN) throws InterruptedException {
        try {
            boolean isDataDel = false;
            boolean didFind;
            String instanceARN ;

            // Make sure that the database has been deleted.
            while (!isDataDel) {
                DescribeDbInstancesResponse response = rdsClient.describeDBInstances();
                List<DBInstance> instanceList = response.dbInstances();
                int listSize = instanceList.size();
                isDataDel = false ;
                didFind = false;
                int index = 1;
                for (DBInstance instance: instanceList) {
                    instanceARN = instance.dbInstanceArn();
                    if (instanceARN.compareTo(clusterDBARN) == 0) {
                        System.out.println(clusterDBARN + " still exists");
                        didFind = true ;
                    }
                    if ((index == listSize) && (!didFind)) {
                        // Went through the entire list and did not find the database ARN.
                        isDataDel = true;
                    }
                    Thread.sleep(sleepTime * 1000);
                    index ++;
                }
            }

            DeleteDbClusterParameterGroupRequest clusterParameterGroupRequest = DeleteDbClusterParameterGroupRequest.builder()
                .dbClusterParameterGroupName(dbClusterGroupName)
                .build();

            rdsClient.deleteDBClusterParameterGroup(clusterParameterGroupRequest);
            System.out.println(dbClusterGroupName +" was deleted.");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.del_paragroup.main]

    // snippet-start:[rds.java2.scenario.cluster.del.main]
    public static void deleteCluster(RdsClient rdsClient, String dbInstanceClusterIdentifier) {
        try {
            DeleteDbClusterRequest deleteDbClusterRequest = DeleteDbClusterRequest.builder()
                .dbClusterIdentifier(dbInstanceClusterIdentifier)
                .skipFinalSnapshot(true)
                .build();

            rdsClient.deleteDBCluster(deleteDbClusterRequest);
            System.out.println(dbInstanceClusterIdentifier +" was deleted!");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.del.main]

    // snippet-start:[rds.java2.scenario.cluster.del.instance.main]
    public static void deleteDatabaseInstance( RdsClient rdsClient, String dbInstanceIdentifier) {
        try {
            DeleteDbInstanceRequest deleteDbInstanceRequest = DeleteDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .deleteAutomatedBackups(true)
                .skipFinalSnapshot(true)
                .build();

            DeleteDbInstanceResponse response = rdsClient.deleteDBInstance(deleteDbInstanceRequest);
            System.out.println("The status of the database is " + response.dbInstance().dbInstanceStatus());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.del.instance.main]


    // snippet-start:[rds.java2.scenario.cluster.wait.snapshot.main]
    public static void waitForSnapshotReady(RdsClient rdsClient, String dbSnapshotIdentifier, String dbInstanceClusterIdentifier) {
        try {
            boolean snapshotReady = false;
            String snapshotReadyStr;
            System.out.println("Waiting for the snapshot to become available.");

            DescribeDbClusterSnapshotsRequest snapshotsRequest = DescribeDbClusterSnapshotsRequest.builder()
                .dbClusterSnapshotIdentifier(dbSnapshotIdentifier)
                .dbClusterIdentifier(dbInstanceClusterIdentifier)
                .build();

            while (!snapshotReady) {
                DescribeDbClusterSnapshotsResponse response = rdsClient.describeDBClusterSnapshots (snapshotsRequest);
                List<DBClusterSnapshot> snapshotList = response.dbClusterSnapshots();
                for (DBClusterSnapshot snapshot : snapshotList) {
                    snapshotReadyStr = snapshot.status();
                    if (snapshotReadyStr.contains("available")) {
                        snapshotReady = true;
                    } else {
                        System.out.println(".");
                        Thread.sleep(sleepTime * 5000);
                    }
                }
            }

            System.out.println("The Snapshot is available!");

        } catch (RdsException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
   // snippet-end:[rds.java2.scenario.cluster.wait.snapshot.main]

    // snippet-start:[rds.java2.scenario.cluster.create.snapshot.main]
    public static void createDBClusterSnapshot(RdsClient rdsClient, String dbInstanceClusterIdentifier, String dbSnapshotIdentifier) {
        try {
            CreateDbClusterSnapshotRequest snapshotRequest = CreateDbClusterSnapshotRequest.builder()
                .dbClusterIdentifier(dbInstanceClusterIdentifier)
                .dbClusterSnapshotIdentifier(dbSnapshotIdentifier)
                .build();

            CreateDbClusterSnapshotResponse response = rdsClient.createDBClusterSnapshot(snapshotRequest);
            System.out.println("The Snapshot ARN is " + response.dbClusterSnapshot().dbClusterSnapshotArn());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.create.snapshot.main]

    // snippet-start:[rds.java2.scenario.cluster.wait.db.main]
    public static void waitDBInstanceReady(RdsClient rdsClient, String dbInstanceIdentifier) {
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
    // snippet-end:[rds.java2.scenario.cluster.wait.db.main]


    // snippet-start:[rds.java2.scenario.cluster.create.instance.main]
    public static String createDBInstanceCluster(RdsClient rdsClient,
                                                 String dbInstanceIdentifier,
                                                 String dbInstanceClusterIdentifier,
                                                 String instanceClass){

        try {
            CreateDbInstanceRequest instanceRequest = CreateDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .dbClusterIdentifier(dbInstanceClusterIdentifier)
                .engine("aurora-mysql")
                .dbInstanceClass(instanceClass)
                .build() ;

            CreateDbInstanceResponse response = rdsClient.createDBInstance(instanceRequest);
            System.out.print("The status is " + response.dbInstance().dbInstanceStatus());
            return response.dbInstance().dbInstanceArn();

        } catch (RdsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[rds.java2.scenario.cluster.create.instance.main]

    // snippet-start:[rds.java2.scenario.cluster.list.instances.main]
    public static String getListInstanceClasses(RdsClient rdsClient) {
        try{
            DescribeOrderableDbInstanceOptionsRequest optionsRequest = DescribeOrderableDbInstanceOptionsRequest.builder()
                .engine("aurora-mysql")
                .maxRecords(20)
                .build();

            DescribeOrderableDbInstanceOptionsResponse response = rdsClient.describeOrderableDBInstanceOptions(optionsRequest);
            List<OrderableDBInstanceOption> instanceOptions = response.orderableDBInstanceOptions();
            String instanceClass = "";
            for (OrderableDBInstanceOption instanceOption: instanceOptions) {
                instanceClass = instanceOption.dbInstanceClass();
                System.out.println("The instance class is " +instanceOption.dbInstanceClass());
                System.out.println("The engine version is " +instanceOption.engineVersion());
            }
            return instanceClass;

        } catch (RdsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[rds.java2.scenario.cluster.list.instances.main]

    // snippet-start:[rds.java2.scenario.cluster.wait.instances.main]
    // Waits until the database instance is available.
    public static void waitForInstanceReady(RdsClient rdsClient, String dbClusterIdentifier) {
        boolean instanceReady = false;
        String instanceReadyStr;
        System.out.println("Waiting for instance to become available.");

        try {
            DescribeDbClustersRequest instanceRequest = DescribeDbClustersRequest.builder()
                .dbClusterIdentifier(dbClusterIdentifier)
                .build();

            while (!instanceReady) {
                DescribeDbClustersResponse response = rdsClient.describeDBClusters(instanceRequest);
                List<DBCluster> clusterList = response.dbClusters();
                for (DBCluster cluster : clusterList) {
                    instanceReadyStr = cluster.status();
                    if (instanceReadyStr.contains("available")) {
                        instanceReady = true;
                    } else {
                        System.out.print(".");
                        Thread.sleep(sleepTime * 1000);
                    }
                }
            }
            System.out.println("Database cluster is available!");

        } catch (RdsException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.wait.instances.main]


    // snippet-start:[rds.java2.scenario.cluster.create.main]
    public static String createDBCluster(RdsClient rdsClient, String dbParameterGroupFamily, String dbName, String dbClusterIdentifier, String userName, String password) {
        try {
            CreateDbClusterRequest clusterRequest = CreateDbClusterRequest.builder()
                .databaseName(dbName)
                .dbClusterIdentifier(dbClusterIdentifier)
                .dbClusterParameterGroupName(dbParameterGroupFamily)
                .engine("aurora-mysql")
                .masterUsername(userName)
                .masterUserPassword(password)
                .build();

            CreateDbClusterResponse response = rdsClient.createDBCluster(clusterRequest);
            return response.dbCluster().dbClusterArn();

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[rds.java2.scenario.cluster.create.main]


    // snippet-start:[rds.java2.scenario.cluster.getengs.main]
    // Get a list of allowed engine versions.
    public static void getAllowedEngines(RdsClient rdsClient, String dbParameterGroupFamily) {
        try {
            DescribeDbEngineVersionsRequest versionsRequest = DescribeDbEngineVersionsRequest.builder()
                .dbParameterGroupFamily(dbParameterGroupFamily)
                .engine("aurora-mysql")
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
    // snippet-end:[rds.java2.scenario.cluster.getengs.main]

    // snippet-start:[rds.java2.scenario.cluster.modclus.params.main]
    // Modify the auto_increment_offset parameter.
    public static void modifyDBClusterParas(RdsClient rdsClient, String dClusterGroupName) {
        try {
            Parameter parameter1 = Parameter.builder()
                .parameterName("auto_increment_offset")
                .applyMethod("immediate")
                .parameterValue("5")
                .build();

            List<Parameter> paraList = new ArrayList<>();
            paraList.add(parameter1);
            ModifyDbClusterParameterGroupRequest groupRequest = ModifyDbClusterParameterGroupRequest.builder()
                .dbClusterParameterGroupName(dClusterGroupName)
                .parameters(paraList)
                .build();

            ModifyDbClusterParameterGroupResponse response = rdsClient.modifyDBClusterParameterGroup(groupRequest);
            System.out.println("The parameter group "+ response.dbClusterParameterGroupName() +" was successfully modified");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.modclus.params.main]

    // snippet-start:[rds.java2.scenario.cluster.des.params.main]
    public static void describeDbClusterParameters(RdsClient rdsClient, String dbCLusterGroupName, int flag) {
        try {
            DescribeDbClusterParametersRequest dbParameterGroupsRequest;
            if (flag == 0) {
                dbParameterGroupsRequest = DescribeDbClusterParametersRequest.builder()
                    .dbClusterParameterGroupName(dbCLusterGroupName)
                    .build();
            } else {
                dbParameterGroupsRequest = DescribeDbClusterParametersRequest.builder()
                    .dbClusterParameterGroupName(dbCLusterGroupName)
                    .source("user")
                    .build();
            }

            DescribeDbClusterParametersResponse response = rdsClient.describeDBClusterParameters(dbParameterGroupsRequest);
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
    // snippet-end:[rds.java2.scenario.cluster.des.params.main]

    // snippet-start:[rds.java2.scenario.cluster.des.param.groups.main]
    public static void describeDbClusterParameterGroups(RdsClient rdsClient, String dbClusterGroupName) {
        try {
            DescribeDbClusterParameterGroupsRequest groupsRequest = DescribeDbClusterParameterGroupsRequest.builder()
                .dbClusterParameterGroupName(dbClusterGroupName)
                .maxRecords(20)
                .build();

            DescribeDbClusterParameterGroupsResponse response = rdsClient.describeDBClusterParameterGroups(groupsRequest);
            List<DBClusterParameterGroup> groups = response.dbClusterParameterGroups();
            for (DBClusterParameterGroup group: groups) {
                System.out.println("The group name is "+group.dbClusterParameterGroupName());
                System.out.println("The group ARN is "+group.dbClusterParameterGroupArn());
            }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.des.param.groups.main]

    // snippet-start:[rds.java2.scenario.cluster.create.param.group.main]
    public static void createDBClusterParameterGroup(RdsClient rdsClient, String dbClusterGroupName, String dbParameterGroupFamily) {
        try {
            CreateDbClusterParameterGroupRequest groupRequest = CreateDbClusterParameterGroupRequest.builder()
                .dbClusterParameterGroupName(dbClusterGroupName)
                .dbParameterGroupFamily(dbParameterGroupFamily)
                .description("Created by using the AWS SDK for Java")
                .build();

            CreateDbClusterParameterGroupResponse response = rdsClient.createDBClusterParameterGroup(groupRequest);
            System.out.println("The group name is "+ response.dbClusterParameterGroup().dbClusterParameterGroupName());

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.scenario.cluster.create.param.group.main]

    // snippet-start:[rds.java2.scenario.cluster.describe.engines.main]
    public static void describeDBEngines( RdsClient rdsClient) {
        try {
            DescribeDbEngineVersionsRequest engineVersionsRequest = DescribeDbEngineVersionsRequest.builder()
                .engine("aurora-mysql")
                .defaultOnly(true)
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
    // snippet-end:[rds.java2.scenario.cluster.describe.engines.main]
}
// snippet-end:[rds.java2.scenario.aurora.main]