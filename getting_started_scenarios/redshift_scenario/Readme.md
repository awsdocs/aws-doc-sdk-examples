# Amazon Redshift Getting Started Scenario

## Introduction
This Java V2 Redshift getting started scenario demonstrates how to interact with Amazon Redshift using the AWS SDK for Java (v2). This Java V2 Redshift getting started scenario is a good starting point for new users who want to interact with Amazon Redshift using the AWS SDK for Java (v2). The code provides essential operations, such as cluster management, database and table creation, data population, and SQL querying.

## Service Operations Invoked
The program performs the following tasks:

1. Creates an Amazon Redshift cluster.
2. Waits for the cluster to become available.
3. Uses the default dev database.
4. Creates a "Movies" table.
5. Populates the "Movies" table using a JSON file.
6. Queries the "Movies" table by year.
7. Deletes the Amazon Redshift cluster.


## Usage
1. Clone the repository or download the Java source code file.
2. Open the code in your preferred Java IDE.
3. Update the following variables in the `main()` method:
   - `userName`: The username for the Redshift cluster.
   - `userPassword`: The password for the Redshift cluster.
   - `jsonFilePath`: The file path to the "Movies.json" file.
   - `databaseName`: The name of the database to use ("dev").
4. Run the `RedshiftScenario` class.

The program will guide you through the scenario, prompting you to enter a cluster ID and the number of records to add to the "Movies" table. The program will also display the progress and results of the various operations.

## Code Explanation
The provided code demonstrates the following key features of the AWS SDK for Java (v2) and the Amazon Redshift service:

1. **Redshift Cluster Management**: The code uses the `RedshiftClient` to create, modify, and delete an Amazon Redshift cluster.
2. **Redshift Data API**: The `RedshiftDataClient` is used to execute SQL statements, create a database, create a table, and query the "Movies" table.
3. **Error Handling**: The code includes exception handling for various Redshift-related exceptions.
4. **User Interaction**: The code prompts the user for input, such as the cluster ID and the number of records to add to the "Movies" table.

Overall, this Java V2 Redshift code example is a valuable resource for developers new to Amazon Redshift and the AWS SDK for Java (v2). It provides a solid foundation for understanding and building Redshift-powered applications.