# Amazon Neptune Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with Amazon Neptune using an AWS SDK. 
It demonstrates various tasks such as creating a Neptune DB Subnet Group, creating a Neptune Cluster, creating a Neptune DB Instance, and so on. 

Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with Amazon Neptune and an AWS SDK.

## Resources
This Basics scenario does not require any additional AWS resources. 

## Hello Amazon Neptune
This program is intended for users not familiar with Amazon Neptune to easily get up and running. The program invokes `describeDBClustersPaginator`to iterate through subnet groups. '

Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. 

## Basics Scenario Program Flow
The Amazon Neptune Basics scenario executes the following operations.

1. **Create a Neptune DB Subnet Group**:
   - Description: Creates a Neptune DB Subnet Group by invoking `createDBSubnetGroup`.
   - Exception Handling: Check to see if a `ServiceQuotaExceededException` is thrown. 
     If so, display the message and end the program.

2. **Create a Neptune Cluster**:
   - Description: Creates a Neptune Cluster by invoking `createDBCluster`.
   - Exception Handling: Check to see if a `ServiceQuotaExceededException` is thrown. If it is thrown, if so, display the message and end the program.

3. **Create a Neptune DB Instance**:
   - Description: Creates a Neptune DB Instance by invoking `createDBInstance`. 
   - Exception Handling: Check to see if an `ServiceQuotaExceededException` is thrown. If so, display the message and end the program.

4. **Check the status of the Neptune DB Instance**:
   - Description: Check the status of the DB instance by invoking `describeDBInstances`. Poll the instance until it reaches an `availbale`state. 
   - Exception Handling: This operatioin handles a `ResourceNotFoundException`. If thrown,  display the message and end the program. 

5. **Show Neptune Cluster details**:
   - Description: Shows the details of the cluster by invoking `describeDBClusters`.
   - Exception Handling: Check to see if a `DBClusterNotFound` is thrown. If so, display the message and end the program.

6. **Stop the Cluster**:
   - Description: Stop the cluster by invoking `stopDBCluster`. Poll the cluster until it reaches a `stopped`state. 
   - Exception Handling: Check to see if a `DBClusterNotFound` is thrown. If so, display the message and end the program.

7. **Start the cluster**:
   - Description: Start the cluster by invoking `startBCluster`. Poll the cluster until it reaches an `available`state.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.   


8.  **Delete the Neptune Assets**:
   - Description: Delete the various resources.
   - Exception Handling: Each call can throw a different exception. The `delete_db_instance` method will handle `DBInstanceNotFoundFault`. The  `delete_db_cluster` method will handle `DBClusterNotFoundFault`and the ` delete_db_subnet_group` method will handle `DBSubnetGroupNotFoundFault`.

### Program execution
The following shows the output of the Amazon Neptune Basics scenario. 

```
   Amazon Neptune is a fully managed graph
 database service by AWS, designed specifically
 for handling complex relationships and connected
 datasets at scale. It supports two popular graph models:
 property graphs (via openCypher and Gremlin) and RDF
 graphs (via SPARQL). This makes Neptune ideal for
 use cases such as knowledge graphs, fraud detection,
 social networking, recommendation engines, and
 network management, where relationships between
 entities are central to the data.

Being fully managed, Neptune handles database
provisioning, patching, backups, and replication,
while also offering high availability and durability
within AWS's infrastructure.

For developers, programming with Neptune allows
for building intelligent, relationship-aware
applications that go beyond traditional tabular
databases. Developers can use the AWS SDK for Java
V2 to automate infrastructure operations
(via NeptuneClient).

Let's get started...


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
1. Create a Neptune DB Subnet Group
The Neptune DB subnet group is used when launching a Neptune cluster

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Subnet group created: neptunesubnetgroup56

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Create a Neptune Cluster
A Neptune Cluster allows you to store and query highly connected datasets with low latency.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

DB Cluster created: neptunecluster56

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Create a Neptune DB Instance
In this step, we add a new database instance to the Neptune cluster

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Created Neptune DB Instance: neptunedb56

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Check the status of the Neptune DB Instance
In this step, we will wait until the DB instance
becomes available. This may take around 10 minutes.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

 Neptune instance reached desired status 'available' after 10 mins, 29 secs.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5.Show Neptune Cluster details

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Cluster Identifier: neptunecluster56
Status: available
Engine: neptune
Engine Version: 1.4.5.0
Endpoint: neptunecluster56.cluster-csf1if1wwrox.us-east-1.neptune.amazonaws.com
Reader Endpoint: neptunecluster56.cluster-ro-csf1if1wwrox.us-east-1.neptune.amazonaws.com
Availability Zones: [us-east-1f, us-east-1c, us-east-1a]
Subnet Group: default
VPC Security Groups:
  - sg-dd2e43f0
Storage Encrypted: false
IAM DB Auth Enabled: false
Backup Retention Period: 1 days
Preferred Backup Window: 04:54-05:24
Preferred Maintenance Window: sat:03:37-sat:04:07
------

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Stop the Amazon Neptune cluster
Once stopped, this step polls the status
until the cluster is in a stopped state.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

DB Cluster Stopped
 Waiting for cluster 'neptunecluster56' to reach status 'stopped'...
 Neptune cluster reached desired status 'stopped' after 12 mins, 10 secs.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Start the Amazon Neptune cluster
Once started, this step polls the clusters
status until it's in an available state.
We will also poll the instance status.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

 DB Cluster starting...
 Waiting for cluster 'neptunecluster56' to reach status 'available'...
 Neptune cluster reached desired status 'available' after 10 mins, 28 secs.
 Neptune instance reached desired status 'available' after 0 secs.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Delete the Neptune Assets
Would you like to delete the Neptune Assets? (y/n)
y
You selected to delete the Neptune assets.
 Deleting DB Instance: neptuneDB56
 Instance neptuneDB56 deleted after 750s
 Deleting DB Cluster: neptuneCluster56
 Deleting Subnet Group: neptuneSubnetGroup56
 Neptune resources deleted successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Thank you for checking out the Amazon Neptune Service Use demo. We hope you
learned something new, or got some inspiration for your own apps today.
For more AWS code examples, have a look at:
https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html

--------------------------------------------------------------------------------
```

## SOS Tags

The following table describes the metadata used in this Basics Scenario. The metadata file is `neptune_metadata.yaml`.


| action                 | metadata key                         |
|------------------------|------------------------------------- |
|`createDBSubnetGroup`   | neptune_CreateDBSubnetGroup          |
|`createDBCluster`       | neptune_CreateDBCluster              |
|`createDBInstance`      | neptune_CreateDBInstance             |
|`describeDBInstances`   | neptune_DescribeDBInstances          |
|`describeDBClusters`    | neptune_DescribeDBClusters           |
| `stopDBCluster`        | neptune_StopDBCluster                |
|`startDBCluster`        | neptune_StartDBCluster               |
|`deleteDBInstance`      | neptune_DeleteDBInstance             |
|`deleteDBCluster`       | neptune_DeleteDBCluster              |
|`deleteDBSubnetGroup`   | neptune_DeleteDBSubnetGroup          |
|`scenario`              | neptune_Scenario                     |
|`hello`                 | neptune_Hello                        |

### Additional SOS Tags
We will add additional code examples to the AWS Code Library. These code examples were created by the SME. These APIs cannot be used in the main scenario because you must run them from within the same VPC as the cluster. There is no console access. However, we will still add them to the AWS Code Library.

This table decribes the SOS tags for NeptunedataClient and NeptuneGraphClient.

| action                        | metadata key                         |
|-------------------------------|------------------------------------- |
|`executeGremlinProfileQuery`   | neptune_ExecuteGremlinProfileQuery   |
|`executeGremlinQuery`          | neptune_ExecuteGremlinQuery          |
|`executeOpenCypherExplainQuery`| neptune_ExecuteOpenCypherExplainQuery                          |
|`createGraph  `                | neptune_CreateGraph:                 |
|`executeQuery`                 | neptune_ExecuteQuery                 |

