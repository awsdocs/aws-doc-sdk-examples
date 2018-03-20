/*
 * Copyright 2011-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDBAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DynamoDBAsync {

    public static void main(String[] args) {
    	// Creates a default async client with credentials and regions loaded from the environment
    	DynamoDBAsyncClient client = DynamoDBAsyncClient.create();
    	CompletableFuture<ListTablesResponse> response = client.listTables(ListTablesRequest.builder()
    	                                                                                    .limit(5)
    	                                                                                    .build());
    	// Map the response to another CompletableFuture containing just the table names
    	CompletableFuture<List<String>> tableNames = response.thenApply(ListTablesResponse::tableNames);
    	// When future is complete (either successfully or in error) handle the response
    	tableNames.whenComplete((tables, err) -> {
    	    if (tables != null) {
    	        tables.forEach(System.out::println);
    	    } else {
    	        // Handle error
    	        err.printStackTrace();
    	    }
    	});
    }
}
