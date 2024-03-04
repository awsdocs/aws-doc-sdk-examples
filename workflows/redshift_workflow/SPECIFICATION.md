# Redshift Scenario Java Specification

## Overview
This Java program serves as an example workflow for interacting with Amazon Redshift using the AWS SDK for Java (v2). It demonstrates various tasks such as creating a Redshift cluster, verifying its readiness, establishing a database, creating a table, populating data, executing SQL queries, and finally, cleaning up resources. Its purpose is to demonstrate how to get up and running with Amazon Redshift and the AWS SDK for Java V2.

## Resources and User Input
The only required resource for this Java program is the Movie.json file. This can be located in the `aws-doc-sdk-examples\resources\sample_files` folder.

User input is also required for this program. The user is asked to input the following values:
- A cluster id value.
- A database name (default is dev if the user does not enter a value).
- A value between 50 and 200 that represents the number of records to add to the table.
- The year to use to query records from the database.
- Whether or not to delete the Amazon Redshift cluster.

## Program Flow
The program executes the following steps:
1. Retrieves database credentials from AWS Secrets Manager using a specified secret name.
2. Prompts the user for a unique cluster ID or use the default value.
3. Creates a Redshift cluster with the specified or default cluster ID using `redshiftClient.createCluster()`.
4. Waits until the Redshift cluster is available for use using `redshiftClient.describeClusters`.
5. Prompts the user for a database name or use the default value.
6. Creates a Redshift database within the specified cluster using `redshiftDataClient.executeStatement()`.
7. Creates a table named "Movies" with fields ID, title, and year using `redshiftDataClient.executeStatement()`.
8. Inserts a specified number of records into the "Movies" table by reading the Movies JSON file. Then it uses `redshiftDataClient.executeStatement()` to insert movie data.
9. Prompts the user for a movie release year.
10. Execute a SQL query, using `redshiftDataClient.executeStatement()`, to retrieve movies released in the specified year. Then displays the result set.
11. Prompts the user for confirmation to delete the Redshift cluster.
12. If confirmed, deletes the specified Redshift cluster using `redshiftClient.deleteCluster()`.

## Program execution
```java
Welcome to the Amazon Redshift example workflow.
This Java program demonstrates how to interact with Amazon Redshift by using the AWS SDK for Java (v2).
Amazon Redshift is a fully managed, petabyte-scale data warehouse service hosted in the cloud.

...

## Metadata
Action / Scenario | Metadata File | Metadata Key
--- | --- | ---
Delete a cluster | redshift_metadata.yaml | redshift.java2.delete_cluster.main
