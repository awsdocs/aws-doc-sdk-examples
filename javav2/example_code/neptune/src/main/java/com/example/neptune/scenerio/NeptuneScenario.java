// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.scenerio;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class NeptuneScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    static Scanner scanner = new Scanner(System.in);
    static NeptuneActions neptuneActions = new NeptuneActions();

    public static void main(String[] args) {
        String subnetGroupName = "neptuneSubnetGroup28" ;
        String vpcId = "vpc-e97a4393" ;
        String clusterName = "neptuneCluster28" ;
        String dbInstanceId = "neptuneDB28" ;

        System.out.println("""
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
        runScenario(subnetGroupName,  vpcId, dbInstanceId, clusterName);
    }

    public static void runScenario(String subnetGroupName,  String vpcId, String dbInstanceId, String clusterName) {
        System.out.println(DASHES);
        System.out.println("1. Create a Neptune DB Subnet Group");
        System.out.println("The Neptune DB subnet group is used when launching a Neptune cluster");
        waitForInputToContinue(scanner);
        String groupARN = neptuneActions.createSubnetGroup(vpcId, subnetGroupName);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create a Neptune Cluster");
        System.out.println("A Neptune Cluster allows you to store and query highly connected datasets with low latency.");
        waitForInputToContinue(scanner);
        String dbClusterId = neptuneActions.createDBCluster(clusterName);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create a Neptune DB Instance");
        System.out.println("In this step, we add a new database instance to the Neptune cluster");
        waitForInputToContinue(scanner);
        neptuneActions.createDBInstance(dbInstanceId, dbClusterId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Check the status of the Neptune DB Instance");
        System.out.println("""
        In this step, we will wait until the DB instance 
        becomes available. This may take around 10 minutes.
        """);
        waitForInputToContinue(scanner);
        neptuneActions.isNeptuneInstanceReady(dbInstanceId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Tag the Amazon Neptune Resource");
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6.Show Neptune Cluster details");
        waitForInputToContinue(scanner);
        neptuneActions.describeDBClusters(clusterName);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7.Show Neptune Instance details");
        waitForInputToContinue(scanner);
        neptuneActions.describeDBInstances(dbInstanceId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Delete the Neptune Assets");
        System.out.println("Would you like to delete the Neptune Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete the Neptune assets.");
            neptuneActions.deleteDBInstance(dbInstanceId);
            neptuneActions.deleteDBCluster(dbClusterId);
            neptuneActions.deleteDBSubnetGroup(subnetGroupName);
        } else {
            System.out.println("You selected not to delete Neptune assets.");
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(
        """
        Thank you for checking out the Amazon Neptune Service Use demo. We hope you
        learned something new, or got some inspiration for your own apps today.
        For more AWS code examples, have a look at:
        https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
        """);
        System.out.println(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}