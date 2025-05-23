// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.scenerio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

// snippet-start:[neptune.java2.scenario.main]
public class NeptuneScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final Logger logger = LoggerFactory.getLogger(NeptuneScenario.class);
    static Scanner scanner = new Scanner(System.in);
    static NeptuneActions neptuneActions = new NeptuneActions();

    public static void main(String[] args) {
        final String usage =
                """
                Usage:
                    <subnetGroupName> <clusterName> <dbInstanceId>
                
                Where:
                    subnetGroupName - The name of an existing Neptune DB subnet group that includes subnets in at least two Availability Zones.
                    vpcId           - The ID of the Amazon Virtual Private Cloud (VPC) where the Neptune cluster and resources will be created.
                    clusterName     - The unique identifier for the Neptune DB cluster.
                    dbInstanceId    - The identifier for a specific Neptune DB instance within the cluster.
                """;
        String subnetGroupName = "neptuneSubnetGroup56";
        String clusterName = "neptuneCluster56";
        String dbInstanceId = "neptuneDB56";

        logger.info("""
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
                    """);
        waitForInputToContinue(scanner);
        runScenario(subnetGroupName, vpcId, dbInstanceId, clusterName);
    }

    public static void runScenario(String subnetGroupName, String vpcId, String dbInstanceId, String clusterName) {
        logger.info(DASHES);
        logger.info("1. Create a Neptune DB Subnet Group");
        logger.info("The Neptune DB subnet group is used when launching a Neptune cluster");
        waitForInputToContinue(scanner);
        neptuneActions.createSubnetGroupAsync(vpcId, subnetGroupName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Create a Neptune Cluster");
        logger.info("A Neptune Cluster allows you to store and query highly connected datasets with low latency.");
        waitForInputToContinue(scanner);
        String dbClusterId = neptuneActions.createDBClusterAsync(clusterName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("3. Create a Neptune DB Instance");
        logger.info("In this step, we add a new database instance to the Neptune cluster");
        waitForInputToContinue(scanner);
        neptuneActions.createDBInstanceAsync(dbInstanceId, dbClusterId).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Check the status of the Neptune DB Instance");
        logger.info("""
                    In this step, we will wait until the DB instance 
                    becomes available. This may take around 10 minutes.
                    """);
        waitForInputToContinue(scanner);
        neptuneActions.checkInstanceStatus(dbInstanceId, "available").join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5.Show Neptune Cluster details");
        waitForInputToContinue(scanner);
        neptuneActions.describeDBClustersAsync(clusterName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. Stop the Amazon Neptune cluster");
        logger.info("""
                    Once stopped, this step polls the status 
                    until the cluster is in a stopped state.
                    """);
        waitForInputToContinue(scanner);
        neptuneActions.stopDBCluster(dbClusterId);
        neptuneActions.waitForClusterStatus(dbClusterId, "stopped");
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Start the Amazon Neptune cluster");
        logger.info("""
                    Once started, this step polls the clusters 
                    status until it's in an available state.
                    We will also poll the instance status.
                    """);
        waitForInputToContinue(scanner);
        neptuneActions.startDBCluster(dbClusterId);
        neptuneActions.waitForClusterStatus(dbClusterId, "available");
        neptuneActions.checkInstanceStatus(dbInstanceId, "available").join();
        logger.info(DASHES);
        logger.info(DASHES);
        logger.info("8. Delete the Neptune Assets");
        logger.info("Would you like to delete the Neptune Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            logger.info("You selected to delete the Neptune assets.");
            neptuneActions.deleteNeptuneResourcesAsync(dbInstanceId, clusterName, subnetGroupName);

        } else {
            logger.info("You selected not to delete Neptune assets.");
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info(
                """
                Thank you for checking out the Amazon Neptune Service Use demo. We hope you
                learned something new, or got some inspiration for your own apps today.
                For more AWS code examples, have a look at:
                https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
                """);
        logger.info(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            logger.info("");
            logger.info("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                logger.info("Continuing with the program...");
                logger.info("");
                break;
            } else {
                logger.info("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[neptune.java2.scenario.main]