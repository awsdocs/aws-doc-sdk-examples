# Redshift Scenario Specification

## Overview
This SDK getting started scenario demonstrates how to interact with Amazon Redshift using the AWS SDK. It demonstrates various tasks such as creating a Redshift cluster, verifying its readiness, creating a table, populating the table with data, executing SQL queries, and finally cleaning up resources. Its purpose is to demonstrate how to get up and running with Amazon Redshift and the AWS SDK.

## Resources and User Input
The only required resource for this SDK getting started scenario is the Movie.json file. This can be located in the `aws-doc-sdk-examples\resources\sample_files` folder.

The following user input is required for this SDK getting started scenario:
- The user name and password. 
- A cluster id value.
- A value between 50 and 200 that represents the number of records to add to the table.
- The year to use to query records from the database.
- Whether or not to delete the Amazon Redshift cluster.

## Hello Redshift
This program is intended for users not familiar with the Redshift SDK to easily get up an running. The logic is to show use of `redshiftClient.describeClustersPaginator()`.

### Program execution
The following shows the output of the program in the console. 

``` java 
 Cluster identifier: redshift-cluster-1 status = available
 Cluster identifier: redshift-cluster-wf status = available
```

## Scenario Program Flow
The SDK getting started scenario executes the following steps:
1. Prompts the user for a user name and password. 
2. Prompts the user for a cluster Id value or use the default value.
3. Creates a Redshift cluster using `redshiftClient.createCluster()`.
4. Waits until the Redshift cluster is available for use using `redshiftClient.describeClusters`.
5. Lists all databases using the `redshiftDataClient.listDatabasesPaginator()`.   
6. Creates a table named "Movies" with fields ID, title, and year using `redshiftDataClient.executeStatement()`.
7. Inserts a specified number of records into the "Movies" table by reading the Movies JSON file. Then it uses `redshiftDataClient.executeStatement()` to insert movie data.
8. Prompts the user for a movie release year.
9. Runs a SQL query, using `redshiftDataClient.executeStatement()`, to retrieve movies released in the specified year. The result set is displayed in the program.
10. Modifies the Redshift cluster using `redshiftClient.modifyCluster()`. 
11. Prompts the user for confirmation to delete the Redshift cluster.
12. If confirmed, deletes the specified Redshift cluster using `redshiftClient.deleteCluster()`.

### Program execution
The following shows the output of the program in the console. 

``` java
--------------------------------------------------------------------------------
Welcome to the Amazon Redshift SDK Getting Started scenario.
This Java program demonstrates how to interact with Amazon Redshift by using the AWS SDK for Java (v2). 
Amazon Redshift is a fully managed, petabyte-scale data warehouse service hosted in the cloud.

The program's primary functionalities include cluster creation, verification of cluster readiness, 
list databases, table creation, data population within the table, and execution of SQL statements.
Furthermore, it demonstrates the process of querying data from the Movie table. 

Upon completion of the program, all AWS resources are cleaned up.

Lets get started...
Please enter your user name (default is awsuser)

--------------------------------------------------------------------------------
Please enter your user password (default is AwsUser1000)

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
A Redshift cluster refers to the collection of computing resources and storage that work together to process and analyze large volumes of data.
Enter a cluster id value (default is redshift-cluster-movies): 

Created cluster redshift-cluster-movies
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Wait until redshift-cluster-movies is available.
Press Enter to continue...
Waiting for cluster to become available. This may take a few mins.
Elapsed Time: 00:02 - Waiting for cluster... 
Elapsed Time: 00:07 - Waiting for cluster... 
Elapsed Time: 00:12 - Waiting for cluster... 
Elapsed Time: 00:18 - Waiting for cluster... 
Elapsed Time: 00:23 - Waiting for cluster... 
Elapsed Time: 00:28 - Waiting for cluster... 
Elapsed Time: 00:33 - Waiting for cluster... 
Elapsed Time: 00:39 - Waiting for cluster... 
Elapsed Time: 00:44 - Waiting for cluster... 
Elapsed Time: 00:49 - Waiting for cluster... 
Cluster is available! Total Elapsed Time: 00:54
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
 When you created redshift-cluster-movies, the dev database is created by default and used in this scenario. 

 To create a custom database, you need to have a CREATEDB privilege. 
 For more information, see the documentation here: https://docs.aws.amazon.com/redshift/latest/dg/r_CREATE_DATABASE.html.

Press Enter to continue...
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
List databases in redshift-cluster-movies
Press Enter to continue...
The database name is : awsdatacatalog
The database name is : dev
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now you will create a table named Movies.
Press Enter to continue...
Table created: Movies
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Populate the Movies table using the Movies.json file.
Specify the number of records you would like to add to the Movies Table.
Please enter a value between 50 and 200.
Enter a value: 50
Inserted: Rush (2013)
Inserted: Prisoners (2013)
Inserted: The Hunger Games: Catching Fire (2013)
Inserted: Thor: The Dark World (2013)
Inserted: This Is the End (2013)
Inserted: Despicable Me 2 (2013)
50 records were added to the Movies table. 
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Query the Movies table by year. Enter a value between 2012-2014.
Enter a year: 2013
The identifier of the statement is 9af7e953-4b23-4cc6-a7d0-d6f6ab30988c
...PICKED
...STARTED
...STARTED
...STARTED
...FINISHED
The statement is finished!
The Movie title field is Rush
The Movie title field is Prisoners
The Movie title field is The Hunger Games: Catching Fire
The Movie title field is Thor: The Dark World

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now you will modify the Redshift cluster.
Press Enter to continue...

The modified cluster was successfully modified and has wed:07:30-wed:08:00 as the maintenance window
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Would you like to delete the Amazon Redshift cluster? (y/n)
The redshift-cluster-movies was deleted
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the Amazon Redshift SDK Getting Started scenario.
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `deleteCluster`              | redshift_metadata.yaml       | redshift_DeleteCluster                  |
| `addrecord`                  | redshift_metadata.yaml       | redshift_Insert                         |
| `describeStatement`          | redshift_metadata.yaml       | redshift_DescribeStatement              |
| `modifyCluster `             | redshift_metadata.yaml       | redshift_ModifyCluster                  |
| `querymoviesd`               | redshift_metadata.yaml       | redshift_Query                          |
| `getStatementResult`         | redshift_metadata.yaml       | redshift_GetStatementResult             |
| `describeClusters`           | redshift_metadata.yaml       | redshift_DescribeClusters               |
| `createTable `               | redshift_metadata.yaml       | redshift_CreateTable                    |
| `createCluster `             | redshift_metadata.yaml       | redshift_CreateCluster                  |
| `describeClustersPaginator ` | redshift_metadata.yaml       | redshift_Hello                          |
| `scenario`                   | redshift_metadata.yaml       | redshift_Scenario                        |

