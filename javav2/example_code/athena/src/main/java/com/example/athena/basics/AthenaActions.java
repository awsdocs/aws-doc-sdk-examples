// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.athena.basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.athena.AthenaAsyncClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.CreatePreparedStatementRequest;
import software.amazon.awssdk.services.athena.model.CreatePreparedStatementResponse;
import software.amazon.awssdk.services.athena.model.CreateWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.CreateWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.DeleteWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.DeleteWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.GetWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.InvalidRequestException;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsRequest;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.WorkGroupConfiguration;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsPublisher;
import software.amazon.awssdk.services.athena.paginators.ListQueryExecutionsPublisher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Wrapper class for Amazon Athena async operations.
 */
// snippet-start:[athena.java2.actions.main]
public class AthenaActions {
    private static final Logger logger = LoggerFactory.getLogger(AthenaActions.class);
    private static volatile AthenaAsyncClient athenaAsyncClient;

    /**
     * Returns the singleton AthenaAsyncClient, creating it lazily if needed.
     */
    private static AthenaAsyncClient getAsyncClient() {
        if (athenaAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryStrategy(RetryMode.STANDARD)
                .build();

            athenaAsyncClient = AthenaAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return athenaAsyncClient;
    }

    /**
     * Sets the async client (for tests only).
     *
     * @param client the AthenaAsyncClient to inject, or null to reset
     */
    public static void setAsyncClient(AthenaAsyncClient client) {
        athenaAsyncClient = client;
    }

    // snippet-start:[athena.java2.CreateWorkGroup.main]
    /**
     * Creates an Athena workgroup with the given S3 output location.
     *
     * @param workGroupName   the name of the workgroup
     * @param description     the description
     * @param s3OutputLocation the S3 output location for query results
     * @return a CompletableFuture with the CreateWorkGroupResponse
     */
    public CompletableFuture<CreateWorkGroupResponse> createWorkGroupAsync(
            String workGroupName, String description, String s3OutputLocation) {

        CreateWorkGroupRequest request = CreateWorkGroupRequest.builder()
            .name(workGroupName)
            .description(description)
            .configuration(WorkGroupConfiguration.builder()
                .resultConfiguration(ResultConfiguration.builder()
                    .outputLocation(s3OutputLocation)
                    .build())
                .enforceWorkGroupConfiguration(true)
                .build())
            .build();

        return getAsyncClient().createWorkGroup(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request creating workgroup: {}", cause.getMessage());
                    } else {
                        logger.error("Error creating workgroup: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully created workgroup: {}", workGroupName);
            });
    }
    // snippet-end:[athena.java2.CreateWorkGroup.main]

    // snippet-start:[athena.java2.GetWorkGroup.main]
    /**
     * Gets details of an Athena workgroup.
     *
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with the GetWorkGroupResponse
     */
    public CompletableFuture<GetWorkGroupResponse> getWorkGroupAsync(String workGroupName) {
        GetWorkGroupRequest request = GetWorkGroupRequest.builder()
            .workGroup(workGroupName)
            .build();

        return getAsyncClient().getWorkGroup(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request getting workgroup: {}", cause.getMessage());
                    } else {
                        logger.error("Error getting workgroup: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully retrieved workgroup: {}", workGroupName);
            });
    }
    // snippet-end:[athena.java2.GetWorkGroup.main]

    // snippet-start:[athena.java2.CreateNamedQuery.main]
    /**
     * Creates a named query in a workgroup.
     *
     * @param queryName     the name of the query
     * @param description   the description
     * @param database      the database
     * @param queryString   the SQL query string
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with the CreateNamedQueryResponse
     */
    public CompletableFuture<CreateNamedQueryResponse> createNamedQueryAsync(
            String queryName, String description, String database,
            String queryString, String workGroupName) {

        CreateNamedQueryRequest request = CreateNamedQueryRequest.builder()
            .name(queryName)
            .description(description)
            .database(database)
            .queryString(queryString)
            .workGroup(workGroupName)
            .build();

        return getAsyncClient().createNamedQuery(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request creating named query: {}", cause.getMessage());
                    } else {
                        logger.error("Error creating named query: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully created named query: {} (ID: {})", queryName, response.namedQueryId());
            });
    }
    // snippet-end:[athena.java2.CreateNamedQuery.main]

    // snippet-start:[athena.java2.CreatePreparedStatement.main]
    /**
     * Creates a prepared statement in a workgroup.
     *
     * @param statementName the name of the prepared statement
     * @param queryStatement the parameterized SQL
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with the CreatePreparedStatementResponse
     */
    public CompletableFuture<CreatePreparedStatementResponse> createPreparedStatementAsync(
            String statementName, String queryStatement, String workGroupName) {

        CreatePreparedStatementRequest request = CreatePreparedStatementRequest.builder()
            .statementName(statementName)
            .queryStatement(queryStatement)
            .workGroup(workGroupName)
            .description("A sample prepared statement")
            .build();

        return getAsyncClient().createPreparedStatement(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request creating prepared statement: {}", cause.getMessage());
                    } else {
                        logger.error("Error creating prepared statement: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully created prepared statement: {}", statementName);
            });
    }
    // snippet-end:[athena.java2.CreatePreparedStatement.main]

    // snippet-start:[athena.java2.StartQueryExecution.main]
    /**
     * Starts a query execution in a workgroup.
     *
     * @param queryString   the SQL query
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with the StartQueryExecutionResponse
     */
    public CompletableFuture<StartQueryExecutionResponse> startQueryExecutionAsync(
            String queryString, String workGroupName) {

        StartQueryExecutionRequest request = StartQueryExecutionRequest.builder()
            .queryString(queryString)
            .workGroup(workGroupName)
            .queryExecutionContext(QueryExecutionContext.builder()
                .database("default")
                .build())
            .build();

        return getAsyncClient().startQueryExecution(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request starting query execution: {}", cause.getMessage());
                    } else {
                        logger.error("Error starting query execution: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Started query execution with ID: {}", response.queryExecutionId());
            });
    }
    // snippet-end:[athena.java2.StartQueryExecution.main]

    // snippet-start:[athena.java2.GetQueryExecution.main]
    /**
     * Waits for a query execution to complete by polling GetQueryExecution.
     *
     * @param queryExecutionId the query execution ID
     * @return a CompletableFuture with the final GetQueryExecutionResponse
     */
    public CompletableFuture<GetQueryExecutionResponse> waitForQueryExecutionAsync(String queryExecutionId) {
        return CompletableFuture.supplyAsync(() -> {
            int maxAttempts = 60;
            int sleepMillis = 2000;

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                try {
                    GetQueryExecutionRequest request = GetQueryExecutionRequest.builder()
                        .queryExecutionId(queryExecutionId)
                        .build();

                    GetQueryExecutionResponse response = getAsyncClient().getQueryExecution(request).join();
                    QueryExecutionState state = response.queryExecution().status().state();

                    if (state == QueryExecutionState.SUCCEEDED) {
                        logger.info("Query {} succeeded.", queryExecutionId);
                        return response;
                    } else if (state == QueryExecutionState.FAILED || state == QueryExecutionState.CANCELLED) {
                        String reason = response.queryExecution().status().stateChangeReason();
                        throw new RuntimeException("Query " + state + ": " + reason);
                    }

                    logger.info("Query state: {}. Waiting...", state);
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for query", e);
                } catch (CompletionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request polling query execution: {}", cause.getMessage());
                    }
                    throw e;
                }
            }
            throw new RuntimeException("Query timed out after " + maxAttempts + " attempts.");
        });
    }
    // snippet-end:[athena.java2.GetQueryExecution.main]

    // snippet-start:[athena.java2.GetQueryResults.main]
    /**
     * Gets all query result rows using the async paginator.
     *
     * @param queryExecutionId the query execution ID
     * @return a CompletableFuture with all result rows
     */
    public CompletableFuture<List<Row>> getQueryResultsAsync(String queryExecutionId) {
        GetQueryResultsRequest request = GetQueryResultsRequest.builder()
            .queryExecutionId(queryExecutionId)
            .build();

        GetQueryResultsPublisher paginator = getAsyncClient().getQueryResultsPaginator(request);

        List<Row> allRows = new ArrayList<>();
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            allRows.addAll(response.resultSet().rows());
        });

        return future.thenApply(v -> allRows)
            .whenComplete((rows, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request getting query results: {}", cause.getMessage());
                    } else {
                        logger.error("Error getting query results: {}", cause.getMessage());
                    }
                }
                if (rows != null) {
                    logger.info("Retrieved {} rows from query results.", rows.size());
                }
            });
    }
    // snippet-end:[athena.java2.GetQueryResults.main]

    // snippet-start:[athena.java2.ListQueryExecutions.main]
    /**
     * Lists all query execution IDs for a workgroup using the async paginator.
     *
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with all query execution IDs
     */
    public CompletableFuture<List<String>> listQueryExecutionsAsync(String workGroupName) {
        ListQueryExecutionsRequest request = ListQueryExecutionsRequest.builder()
            .workGroup(workGroupName)
            .build();

        ListQueryExecutionsPublisher paginator = getAsyncClient().listQueryExecutionsPaginator(request);

        List<String> allExecutionIds = new ArrayList<>();
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            allExecutionIds.addAll(response.queryExecutionIds());
        });

        return future.thenApply(v -> allExecutionIds)
            .whenComplete((ids, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request listing query executions: {}", cause.getMessage());
                    } else {
                        logger.error("Error listing query executions: {}", cause.getMessage());
                    }
                }
                if (ids != null) {
                    logger.info("Found {} query execution IDs.", ids.size());
                }
            });
    }
    // snippet-end:[athena.java2.ListQueryExecutions.main]

    // snippet-start:[athena.java2.DeleteNamedQuery.main]
    /**
     * Deletes a named query.
     *
     * @param namedQueryId the named query ID
     * @return a CompletableFuture with the DeleteNamedQueryResponse
     */
    public CompletableFuture<DeleteNamedQueryResponse> deleteNamedQueryAsync(String namedQueryId) {
        DeleteNamedQueryRequest request = DeleteNamedQueryRequest.builder()
            .namedQueryId(namedQueryId)
            .build();

        return getAsyncClient().deleteNamedQuery(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request deleting named query: {}", cause.getMessage());
                    } else {
                        logger.error("Error deleting named query: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully deleted named query: {}", namedQueryId);
            });
    }
    // snippet-end:[athena.java2.DeleteNamedQuery.main]

    // snippet-start:[athena.java2.DeleteWorkGroup.main]
    /**
     * Deletes an Athena workgroup.
     *
     * @param workGroupName the workgroup name
     * @return a CompletableFuture with the DeleteWorkGroupResponse
     */
    public CompletableFuture<DeleteWorkGroupResponse> deleteWorkGroupAsync(String workGroupName) {
        DeleteWorkGroupRequest request = DeleteWorkGroupRequest.builder()
            .workGroup(workGroupName)
            .recursiveDeleteOption(true)
            .build();

        return getAsyncClient().deleteWorkGroup(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof InvalidRequestException) {
                        logger.error("Invalid request deleting workgroup: {}", cause.getMessage());
                    } else {
                        logger.error("Error deleting workgroup: {}", cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                logger.info("Successfully deleted workgroup: {}", workGroupName);
            });
    }
    // snippet-end:[athena.java2.DeleteWorkGroup.main]
}
// snippet-end:[athena.java2.actions.main]
