// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.java.codeexample.DynamoDBLocalFixture] 

package com.amazonaws.services.dynamodbv2;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;

/**
 * This class demonstrates how to use DynamoDB Local as a test fixture.
 * 
 * @author Alexander Patrikalakis
 */
public class DynamoDBLocalFixture {
    public static void main(String[] args) throws Exception {
        AmazonDynamoDB dynamodb = null;
        try {
            // Create an in-memory and in-process instance of DynamoDB Local
            // that skips HTTP
            dynamodb = DynamoDBEmbedded.create();
            // use the DynamoDB API with DynamoDBEmbedded
            listTables(dynamodb.listTables(), "DynamoDB Embedded");
        } finally {
            // Shutdown the thread pools in DynamoDB Local / Embedded
            if (dynamodb != null) {
                dynamodb.shutdown();
            }
        }

        // Create an in-memory and in-process instance of DynamoDB Local that
        // runs over HTTP
        final String[] localArgs = { "-inMemory" };
        DynamoDBProxyServer server = null;
        try {
            server = ServerRunner.createServerFromCommandLineArgs(localArgs);
            server.start();
            dynamodb = new AmazonDynamoDBClient();
            dynamodb.setEndpoint("http://localhost:8000");

            // use the DynamoDB API over HTTP
            listTables(dynamodb.listTables(), "DynamoDB Local over HTTP");
        } finally {
            // Stop the DynamoDB Local endpoint
            if (server != null) {
                server.stop();
            }
        }
    }

    public static void listTables(ListTablesResult result, String method) {
        System.out.println("found " + Integer.toString(result.getTableNames().size()) + " tables with " + method);
        for (String table : result.getTableNames()) {
            System.out.println(table);
        }
    }
}

// snippet-end:[dynamodb.java.codeexample.DynamoDBLocalFixture]