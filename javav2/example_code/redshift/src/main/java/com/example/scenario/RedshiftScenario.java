// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.scenario;

// snippet-start:[redshift.java2.scenario.main]
import com.example.redshift.User;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.CreateClusterRequest;
import software.amazon.awssdk.services.redshift.model.CreateClusterResponse;
import software.amazon.awssdk.services.redshift.model.DeleteClusterRequest;
import software.amazon.awssdk.services.redshift.model.DeleteClusterResponse;
import software.amazon.awssdk.services.redshift.model.DescribeClustersRequest;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterRequest;
import software.amazon.awssdk.services.redshift.model.ModifyClusterResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.Field;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.core.JsonParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *
 This Java example performs these tasks:
 *
 * 1. Reads an AWS Secret Manager secret to get database credentials.
 * 2. Creates an Amazon Redshift Cluster.
 * 3. Waits for the Cluster to be available.
 * 4. Creates a Redshift database.
 * 5. Creates a Movie table.
 * 6. Populates the Movies table using the Movies.json file.
 * 7. Queries the Movies table by year.
 * 8. Deletes the Amazon Redshift cluster.
 */

public class RedshiftScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    public static void main(String[] args) throws Exception {
        final String usage = """

            Usage:
                <secretName> <jsonFilePath>\s

            Where:
                secretName - The name of the AWS Secrets Manager secret that contains the database credentials
                jsonFilePath - The path to the Movies JSON file (you can locate that file in resources/sample_files)
            """;

      //  if (args.length != 2) {
      //      System.out.println(usage);
      //      System.exit(1);
     //   }

        String secretName = "redshift/work" ; //args[0];
        String jsonFilePath = "C:\\Users\\scmacdon\\Test_Git\\aws-doc-sdk-examples-main\\resources\\sample_files\\Movies.json" ;
        String databaseName ;
        Scanner scanner = new Scanner(System.in);

        Region region = Region.US_EAST_1;
        RedshiftClient redshiftClient = RedshiftClient.builder()
            .region(region)
            .build();

        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
            .region(region)
            .build();

        System.out.println(DASHES);
        System.out.println("Welcome to the Amazon Redshift example workflow.");
        System.out.println("""
        This Java program demonstrates how to interact with Amazon Redshift by using the AWS SDK for Java (v2). 
        Amazon Redshift is a fully managed, petabyte-scale data warehouse service hosted in the cloud.
                                                                            
        The program's primary functionalities includes cluster creation, verification of cluster readiness, database establishment, 
        table creation, data population within the table, and execution of SQL statements. Furthermore, it demonstrates 
        the process of retrieving data from the Movie table. Upon completion of the program, all AWS resources are cleaned up.
        """);

        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Get Amazon Redshift credentials from AWS Secret Manager");
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        Gson gson = new Gson();
        User user = gson.fromJson(String.valueOf(getSecretValues(secretName)), User.class);

        System.out.println(DASHES);
        System.out.println("A Redshift cluster refers to the collection of computing resources and storage that work together to process and analyze large volumes of data.");
        System.out.println("Enter a cluster id value (default is redshift-cluster-200): ");
        String userInput = scanner.nextLine();

        // Use a default value if the user input is empty.
        String clusterId = userInput.isEmpty() ? "redshift-cluster-200" : userInput;
        createCluster(redshiftClient, clusterId, user.getMasterUsername(), user.getMasterUserPassword());
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Wait until the Redshift cluster ia available.");
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        waitForClusterReady(redshiftClient, clusterId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Create a Redshift database");
        System.out.println("Enter a database name (default is dev): ");
        String name = scanner.nextLine();
        databaseName = name.isEmpty() ? "dev" : name;
        createDatabase(redshiftDataClient, clusterId, databaseName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Now you will create a table named Movie.");
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        createTable(redshiftDataClient, clusterId, databaseName);

        System.out.println(DASHES);
        System.out.println("Populate the Movies table using the Movies.json file.");
        System.out.println("Specify the number of records you would like to add to the Movies Table.");
        System.out.println("Please enter a value between 50 and 200.");
        int numRecords;
        do {
            System.out.print("Enter a value: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a value between 50 and 200.");
                System.out.print("Enter a year: ");
                scanner.next();
            }
            numRecords = scanner.nextInt();
        } while (numRecords < 50 || numRecords > 200);
        popTable(redshiftDataClient, clusterId, databaseName, jsonFilePath, numRecords);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Query the Movies table by year. Enter a value between 2010-2014.");
        int movieYear;
        do {
            System.out.print("Enter a year: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid year between 2010 and 2014.");
                System.out.print("Enter a year: ");
                scanner.next();
            }
            movieYear = scanner.nextInt();
            scanner.nextLine();
        } while (movieYear < 2010 || movieYear > 2014);

        String sqlYear = " SELECT * FROM Movies WHERE year = "+movieYear;
        String id = queryMoviesByYear(redshiftDataClient, databaseName, user.getMasterUsername(), sqlYear, clusterId);
        System.out.println("The identifier of the statement is " + id);
        checkStatement(redshiftDataClient, id);
        getResults(redshiftDataClient, id);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Would you like to delete the Amazon Redshift cluster? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete " +clusterId);
            System. out.print("Press Enter to continue...");
            scanner.nextLine();
            deleteRedshiftCluster(redshiftClient, clusterId);
        } else {
            System.out.println("The "+clusterId +" was not deleted");
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("This concludes the Amazon Redshift example workflow.");
        System.out.println(DASHES);
     }

    // snippet-start:[redshift.java2.delete_cluster.main]
    public static void deleteRedshiftCluster(RedshiftClient redshiftClient, String clusterId) {
        try {
            DeleteClusterRequest deleteClusterRequest = DeleteClusterRequest.builder()
                .clusterIdentifier(clusterId)
                .skipFinalClusterSnapshot(true)
                .build();

            DeleteClusterResponse response = redshiftClient.deleteCluster(deleteClusterRequest);
            System.out.println("The status is " + response.cluster().clusterStatus());

        } catch (RedshiftException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[redshift.java2.delete_cluster.main]

    // snippet-start:[redshift.java2.data_addrecord.main]
    public static void popTable(RedshiftDataClient redshiftDataClient, String clusterId, String databaseName, String fileName, int number) throws IOException {
        JsonParser parser = new JsonFactory().createParser(new File(fileName));
        com.fasterxml.jackson.databind.JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();
        ObjectNode currentNode;
        int t = 0;
        while (iter.hasNext()) {
            if (t == number)
                break;
            currentNode = (ObjectNode) iter.next();
            int year = currentNode.get("year").asInt();
            String title = currentNode.get("title").asText();

            // Use SqlParameter to avoid SQL injection.
            List<SqlParameter> parameterList = new ArrayList<>();
            String sqlStatement = "INSERT INTO Movies VALUES( :id , :title, :year);";

            // Create the parameters.
            SqlParameter idParam = SqlParameter.builder()
                .name("id")
                .value(String.valueOf(t))
                .build();

            SqlParameter titleParam= SqlParameter.builder()
                .name("title")
                .value(title)
                .build();

            SqlParameter yearParam = SqlParameter.builder()
                .name("year")
                .value(String.valueOf(year))
                .build();
            parameterList.add(idParam);
            parameterList.add(titleParam);
            parameterList.add(yearParam);

            try {
                ExecuteStatementRequest insertStatementRequest = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterId)
                    .sql(sqlStatement)
                    .database(databaseName)
                    .parameters(parameterList)
                    .build();

                redshiftDataClient.executeStatement(insertStatementRequest);
                System.out.println("Inserted: " + title + " (" + year + ")");
                t++;

            } catch (RedshiftDataException e) {
                System.err.println("Error inserting data: " + e.getMessage());
                System.exit(1);
            }
        }
        System.out.println(t + " records were added to the Movies table. ");
    }
    // snippet-end:[redshift.java2.data_addrecord.main]

    public static void checkStatement(RedshiftDataClient redshiftDataClient, String sqlId) {
        try {
            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                .id(sqlId)
                .build();

            String status;
            while (true) {
                DescribeStatementResponse response = redshiftDataClient.describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("..." + status);

                if (status.compareTo("FINISHED") == 0) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }

            System.out.println("The statement is finished!");

        } catch (RedshiftDataException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // snippet-start:[redshift.java2.mod_cluster.main]
    public static void modifyCluster(RedshiftClient redshiftClient, String clusterId) {

        try {
            ModifyClusterRequest modifyClusterRequest = ModifyClusterRequest.builder()
                .clusterIdentifier(clusterId)
                .preferredMaintenanceWindow("wed:07:30-wed:08:00")
                .build();

            ModifyClusterResponse clusterResponse = redshiftClient.modifyCluster(modifyClusterRequest);
            System.out.println("The modified cluster was successfully modified and has "
                + clusterResponse.cluster().preferredMaintenanceWindow() + " as the maintenance window");

        } catch (RedshiftException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[redshift.java2.mod_cluster.main]


    public static String queryMoviesByYear(RedshiftDataClient redshiftDataClient,
                                             String database,
                                             String dbUser,
                                             String sqlStatement,
                                             String clusterId) {

        try {
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .database(database)
                .dbUser(dbUser)
                .sql(sqlStatement)
                .build();

            ExecuteStatementResponse response = redshiftDataClient.executeStatement(statementRequest);
            return response.id();

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static void getResults(RedshiftDataClient redshiftDataClient, String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            // Extract and print the field values using streams.
            GetStatementResultResponse response = redshiftDataClient.getStatementResult(resultRequest);
            response.records().stream()
                .flatMap(List::stream)
                .map(Field::stringValue)
                .filter(value -> value != null)
                .forEach(value -> System.out.println("The Movie title field is " + value));

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // snippet-start:[redshift.java2.describe_cluster.main]
    public static void waitForClusterReady(RedshiftClient redshiftClient, String clusterId) {
        Boolean clusterReady = false;
        String clusterReadyStr;
        System.out.println("Waiting for cluster to become available. This may take a few mins.");

        try {
            DescribeClustersRequest clustersRequest = DescribeClustersRequest.builder()
                .clusterIdentifier(clusterId)
                .build();

            long startTime = System.currentTimeMillis();

            // Loop until the cluster is ready.
            while (!clusterReady) {
                DescribeClustersResponse clusterResponse = redshiftClient.describeClusters(clustersRequest);
                List<Cluster> clusterList = clusterResponse.clusters();
                for (Cluster cluster : clusterList) {
                    clusterReadyStr = cluster.clusterStatus();
                    if (clusterReadyStr.contains("available"))
                        clusterReady = true;
                    else {
                        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
                        long elapsedSeconds = elapsedTimeMillis / 1000;
                        long minutes = elapsedSeconds / 60;
                        long seconds = elapsedSeconds % 60;

                        System.out.println(String.format("Elapsed Time: %02d:%02d - Waiting for cluster... ", minutes, seconds));
                        TimeUnit.SECONDS.sleep(5);
                    }
                }
            }

            long elapsedTimeMillis = System.currentTimeMillis() - startTime;
            long elapsedSeconds = elapsedTimeMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;

            System.out.println(String.format("Cluster is available! Total Elapsed Time: %02d:%02d", minutes, seconds));

        } catch (RedshiftException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[redshift.java2.describe_cluster.main]

    public static void createDatabase(RedshiftDataClient redshiftDataClient, String clusterId, String databaseName) {
        try {
            ExecuteStatementRequest createDatabaseRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .sql("CREATE DATABASE " + databaseName)
                .database(databaseName)
                .build();

            redshiftDataClient.executeStatement(createDatabaseRequest);
            System.out.println("Database created: " + databaseName);

        } catch (RedshiftDataException e) {
            System.err.println("Error creating database: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void createTable(RedshiftDataClient redshiftDataClient, String clusterId, String databaseName) {
        try {
            ExecuteStatementRequest createTableRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .database(databaseName)
                .sql("CREATE TABLE Movies ("
                    + "id INT PRIMARY KEY, "
                    + "title VARCHAR(100), "
                    + "year INT)")
                .build();

            redshiftDataClient.executeStatement(createTableRequest);
            System.out.println("Table created: Movies");

        } catch (RedshiftDataException e) {
            System.err.println("Error creating table: " + e.getMessage());
            System.exit(1);
        }
    }

    // snippet-start:[redshift.java2.create_cluster.main]
    public static void createCluster(RedshiftClient redshiftClient, String clusterId, String masterUsername,
                                     String masterUserPassword) {
        try {
            CreateClusterRequest clusterRequest = CreateClusterRequest.builder()
                .clusterIdentifier(clusterId)
                .masterUsername(masterUsername)
                .masterUserPassword(masterUserPassword)
                .nodeType("dc2.large")
                .publiclyAccessible(true)
                .numberOfNodes(2)
                .build();

            CreateClusterResponse clusterResponse = redshiftClient.createCluster(clusterRequest);
            System.out.println("Created cluster " + clusterResponse.cluster().clusterIdentifier());

        } catch (RedshiftException e) {

            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[redshift.java2.create_cluster.main]

    private static SecretsManagerClient getSecretClient() {
        Region region = Region.US_WEST_2;
        return SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    public static String getSecretValues(String secretName) {
        SecretsManagerClient secretClient = getSecretClient();
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }
}
// snippet-end:[redshift.java2.scenario.main]