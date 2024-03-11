# Redshift Scenario Java Specification

## Overview
This Java program serves as an getting started MVP for interacting with Amazon Redshift using the AWS SDK for Java (v2). It demonstrates various tasks such as creating a Redshift cluster, verifying its readiness, establishing a database, creating a table, populating data, executing SQL queries, and finally, cleaning up resources. Its purpose is to demonstrate how to get up and running with Amazon Redshift and the AWS SDK for Java V2.

## Resources and User Input
The only required resource for this Java program is the Movie.json file. This can be located in the `aws-doc-sdk-examples\resources\sample_files` folder.

User input is also required for this program. The user is asked to input the following values:
- A cluster id value.
- A database name (default is dev if the user does not enter a value).
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
The program executes the following steps:
1. Prompts the user for a unique cluster ID or use the default value.
2. Creates a Redshift cluster with the specified or default cluster ID using `redshiftClient.createCluster()`.
3. Waits until the Redshift cluster is available for use using `redshiftClient.describeClusters`.
4. Prompts the user for a database name or use the default value.
5. Creates a Redshift database within the specified cluster using `redshiftDataClient.executeStatement()`.
6. Creates a table named "Movies" with fields ID, title, and year using `redshiftDataClient.executeStatement()`.
7. Inserts a specified number of records into the "Movies" table by reading the Movies JSON file. Then it uses `redshiftDataClient.executeStatement()` to insert movie data.
8. Prompts the user for a movie release year.
9. Runs a SQL query, using `redshiftDataClient.executeStatement()`, to retrieve movies released in the specified year. The result set is displayed in the program.
10. Lists all databases using the `redshiftDataClient.listDatabasesPaginator()`.  
11. Modifies the Redshift cluster using `redshiftClient.modifyCluster()`. 
12. Prompts the user for confirmation to delete the Redshift cluster.
13. If confirmed, deletes the specified Redshift cluster using `redshiftClient.deleteCluster()`.

### Program execution
The following shows the output of the program in the console. 

``` java

--------------------------------------------------------------------------------
Welcome to the Amazon Redshift example MVP scenario.
This Java program demonstrates how to interact with Amazon Redshift by using the AWS SDK for Java (v2). 
Amazon Redshift is a fully managed, petabyte-scale data warehouse service hosted in the cloud.

The program's primary functionalities include cluster creation, verification of cluster readiness, 
database establishment, table creation, data population within the table, and execution of SQL statements.
Furthermore, it demonstrates the process of querying data from the Movie table. 

Upon completion of the program, all AWS resources are cleaned up.

Press Enter to continue...
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
A Redshift cluster refers to the collection of computing resources and storage that work together to process and analyze large volumes of data.
Enter a cluster id value (default is redshift-cluster-200): 

Created cluster redshift-cluster-200
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Wait until the Redshift cluster is available.
Press Enter to continue...
Waiting for cluster to become available. This may take a few mins.
Elapsed Time: 00:00 - Waiting for cluster... 
Elapsed Time: 00:05 - Waiting for cluster... 
Elapsed Time: 00:10 - Waiting for cluster... 
Elapsed Time: 00:16 - Waiting for cluster... 
Elapsed Time: 00:21 - Waiting for cluster... 
Elapsed Time: 00:26 - Waiting for cluster... 
Elapsed Time: 00:31 - Waiting for cluster... 
Elapsed Time: 00:37 - Waiting for cluster... 
Elapsed Time: 00:42 - Waiting for cluster... 
Elapsed Time: 00:47 - Waiting for cluster... 
Elapsed Time: 00:52 - Waiting for cluster... 
Elapsed Time: 00:58 - Waiting for cluster... 
Elapsed Time: 01:03 - Waiting for cluster... 
Elapsed Time: 01:08 - Waiting for cluster... 
Elapsed Time: 01:14 - Waiting for cluster... 
Cluster is available! Total Elapsed Time: 01:19
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Create a Redshift database
Enter a database name (default is dev): 

Database created: dev
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now you will create a table named Movie.
Press Enter to continue...
Table created: Movies
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
Inserted: Insidious: Chapter 2 (2013)
Inserted: World War Z (2013)
Inserted: X-Men: Days of Future Past (2014)
Inserted: Transformers: Age of Extinction (2014)
Inserted: Now You See Me (2013)
Inserted: Gravity (2013)
Inserted: We're the Millers (2013)
Inserted: Riddick (2013)
Inserted: The Family (2013)
Inserted: Star Trek Into Darkness (2013)
Inserted: After Earth (2013)
Inserted: The Great Gatsby (2013)
Inserted: Divergent (2014)
Inserted: We Are What We Are (2013)
Inserted: Iron Man 3 (2013)
Inserted: The Amazing Spider-Man 2 (2014)
Inserted: Curse of Chucky (2013)
Inserted: The Conjuring (2013)
Inserted: Oldboy (2013)
Inserted: Escape Plan (2013)
Inserted: Elysium (2013)
Inserted: Cloudy with a Chance of Meatballs 2 (2013)
Inserted: RoboCop (2014)
Inserted: Carrie (2013)
Inserted: The Mortal Instruments: City of Bones (2013)
Inserted: Captain America: The Winter Soldier (2014)
Inserted: Need for Speed (2014)
Inserted: Runner Runner (2013)
Inserted: I Spit on Your Grave 2 (2013)
Inserted: Battle of the Year (2013)
Inserted: Behind the Candelabra (2013)
Inserted: No se Aceptan Devoluciones (2013)
Inserted: The Bling Ring (2013)
Inserted: Furious 6 (2013)
Inserted: Machete Kills (2013)
Inserted: The World's End (2013)
Inserted: Pitch Perfect (2012)
Inserted: Epic (2013)
Inserted: The Avengers (2012)
Inserted: Metallica Through the Never (2013)
Inserted: Oblivion (2013)
Inserted: Dom Hemingway (2013)
Inserted: The Hangover Part III (2013)
Inserted: Despicable Me 2 (2013)
50 records were added to the Movies table. 
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Query the Movies table by year. Enter a value between 2010-2014.
Enter a year: 2012
The identifier of the statement is cf006da9-48b9-4ff6-8274-83916353e67d
...PICKED
...FINISHED
The statement is finished!
The Movie title field is Pitch Perfect
The Movie title field is The Avengers
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now you will list your databases.
Press Enter to continue...
The database name is : awsdatacatalog
The database name is : dev
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Would you like to delete the Amazon Redshift cluster? (y/n)
y
You selected to delete redshift-cluster-200
Press Enter to continue...
The status is deleting
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the Amazon Redshift example mvp scenario.
--------------------------------------------------------------------------------
```

## Metadata


| action / scenario            | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `deleteCluster`              | redshift_metadata.yaml       | redshift.java2.delete_cluster.main      |
| `addrecord`                  | redshift_metadata.yaml       | redshiftdata.java2.data_add.record.main |
| `describeStatement`          | redshift_metadata.yaml       | redshiftdata.java2.checkstatement.main  |
| `modifyCluster `             | redshift_metadata.yaml       | redshift.java2.mod_cluster.main         |
| `querymoviesd`               | redshift_metadata.yaml       | redshiftdata.java2.mod_cluster.main     |
| `getStatementResult`         | redshift_metadata.yaml       | redshiftdata.java2.getresults.main      |
| `describeClusters`           | redshift_metadata.yaml       | redshift.java2.describe_cluster.main    |
| `createDatabase  `           | redshift_metadata.yaml       | redshiftdata.java2.create_database.main |
| `createTable `               | redshift_metadata.yaml       | redshiftdata.java2.create_table.main    |
| `createCluster `             | redshift_metadata.yaml       | redshift.java2.create_cluster.main      |
| `describeClustersPaginator ` | redshift_metadata.yaml       | redshift.java2.hello.main               |
| `scenario`                   | redshift_metadata.yaml       | redshift.java2.scenario.main            |
