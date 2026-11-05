// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.athena.basics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.athena.AthenaAsyncClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.CreatePreparedStatementRequest;
import software.amazon.awssdk.services.athena.model.CreatePreparedStatementResponse;
import software.amazon.awssdk.services.athena.model.CreateWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.CreateWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.Datum;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.DeleteWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.DeleteWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.GetWorkGroupRequest;
import software.amazon.awssdk.services.athena.model.GetWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.InvalidRequestException;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsRequest;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsResponse;
import software.amazon.awssdk.services.athena.model.QueryExecution;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.QueryExecutionStatus;
import software.amazon.awssdk.services.athena.model.ResultSet;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.WorkGroup;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsPublisher;
import software.amazon.awssdk.services.athena.paginators.ListQueryExecutionsPublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AthenaActions that use a mocked AthenaAsyncClient,
 * so no AWS credentials or resources are needed.
 */
@Tag("UnitTest")
class AthenaActionsTest {

    private static final String WORKGROUP = "test-workgroup";
    private static final String QUERY_ID = "test-query-execution-id";

    private AthenaAsyncClient mockClient;
    private AthenaActions actions;

    @BeforeEach
    void setUp() {
        mockClient = mock(AthenaAsyncClient.class);
        AthenaActions.setAsyncClient(mockClient);
        actions = new AthenaActions();
    }

    @AfterEach
    void tearDown() {
        AthenaActions.setAsyncClient(null);
    }

    private static InvalidRequestException invalidRequest() {
        return (InvalidRequestException) InvalidRequestException.builder()
            .message("invalid request")
            .build();
    }

    @Test
    void createWorkGroupSendsOutputLocation() {
        when(mockClient.createWorkGroup(any(CreateWorkGroupRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(CreateWorkGroupResponse.builder().build()));

        actions.createWorkGroupAsync(WORKGROUP, "desc", "s3://bucket/results/").join();

        ArgumentCaptor<CreateWorkGroupRequest> captor = ArgumentCaptor.forClass(CreateWorkGroupRequest.class);
        verify(mockClient).createWorkGroup(captor.capture());
        assertEquals(WORKGROUP, captor.getValue().name());
        assertEquals("s3://bucket/results/",
            captor.getValue().configuration().resultConfiguration().outputLocation());
    }

    @Test
    void createWorkGroupPropagatesInvalidRequest() {
        when(mockClient.createWorkGroup(any(CreateWorkGroupRequest.class)))
            .thenReturn(CompletableFuture.failedFuture(invalidRequest()));

        CompletionException ex = assertThrows(CompletionException.class,
            () -> actions.createWorkGroupAsync(WORKGROUP, "desc", "s3://bucket/").join());
        assertInstanceOf(InvalidRequestException.class, ex.getCause());
    }

    @Test
    void getWorkGroupReturnsWorkGroup() {
        when(mockClient.getWorkGroup(any(GetWorkGroupRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(GetWorkGroupResponse.builder()
                .workGroup(WorkGroup.builder().name(WORKGROUP).build())
                .build()));

        GetWorkGroupResponse response = actions.getWorkGroupAsync(WORKGROUP).join();
        assertEquals(WORKGROUP, response.workGroup().name());
    }

    @Test
    void createNamedQueryReturnsId() {
        when(mockClient.createNamedQuery(any(CreateNamedQueryRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(CreateNamedQueryResponse.builder()
                .namedQueryId("nq-123")
                .build()));

        CreateNamedQueryResponse response = actions.createNamedQueryAsync(
            "name", "desc", "default", "SELECT 1", WORKGROUP).join();
        assertEquals("nq-123", response.namedQueryId());
    }

    @Test
    void createPreparedStatementSendsStatement() {
        when(mockClient.createPreparedStatement(any(CreatePreparedStatementRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(CreatePreparedStatementResponse.builder().build()));

        actions.createPreparedStatementAsync("stmt", "SELECT ? AS greeting", WORKGROUP).join();

        ArgumentCaptor<CreatePreparedStatementRequest> captor =
            ArgumentCaptor.forClass(CreatePreparedStatementRequest.class);
        verify(mockClient).createPreparedStatement(captor.capture());
        assertEquals("stmt", captor.getValue().statementName());
        assertEquals(WORKGROUP, captor.getValue().workGroup());
    }

    @Test
    void startQueryExecutionSetsDatabaseWhenProvided() {
        when(mockClient.startQueryExecution(any(StartQueryExecutionRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(StartQueryExecutionResponse.builder()
                .queryExecutionId(QUERY_ID)
                .build()));

        StartQueryExecutionResponse response =
            actions.startQueryExecutionAsync("SELECT 1", "mydb", WORKGROUP).join();

        ArgumentCaptor<StartQueryExecutionRequest> captor =
            ArgumentCaptor.forClass(StartQueryExecutionRequest.class);
        verify(mockClient).startQueryExecution(captor.capture());
        assertEquals(QUERY_ID, response.queryExecutionId());
        assertEquals("mydb", captor.getValue().queryExecutionContext().database());
    }

    @Test
    void startQueryExecutionOmitsContextWhenDatabaseIsNull() {
        when(mockClient.startQueryExecution(any(StartQueryExecutionRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(StartQueryExecutionResponse.builder()
                .queryExecutionId(QUERY_ID)
                .build()));

        actions.startQueryExecutionAsync("SELECT 1", null, WORKGROUP).join();

        ArgumentCaptor<StartQueryExecutionRequest> captor =
            ArgumentCaptor.forClass(StartQueryExecutionRequest.class);
        verify(mockClient).startQueryExecution(captor.capture());
        assertNull(captor.getValue().queryExecutionContext());
    }

    private static GetQueryExecutionResponse executionInState(QueryExecutionState state) {
        return GetQueryExecutionResponse.builder()
            .queryExecution(QueryExecution.builder()
                .queryExecutionId(QUERY_ID)
                .status(QueryExecutionStatus.builder()
                    .state(state)
                    .stateChangeReason("reason")
                    .build())
                .build())
            .build();
    }

    @Test
    void waitForQueryExecutionReturnsOnSuccess() {
        when(mockClient.getQueryExecution(any(GetQueryExecutionRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(executionInState(QueryExecutionState.SUCCEEDED)));

        GetQueryExecutionResponse response = actions.waitForQueryExecutionAsync(QUERY_ID).join();
        assertEquals(QueryExecutionState.SUCCEEDED, response.queryExecution().status().state());
    }

    @Test
    void waitForQueryExecutionFailsOnFailedQuery() {
        when(mockClient.getQueryExecution(any(GetQueryExecutionRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(executionInState(QueryExecutionState.FAILED)));

        CompletionException ex = assertThrows(CompletionException.class,
            () -> actions.waitForQueryExecutionAsync(QUERY_ID).join());
        assertTrue(ex.getCause().getMessage().contains("FAILED"));
    }

    @Test
    void getQueryResultsCollectsAllRows() {
        Row header = Row.builder().data(Datum.builder().varCharValue("id").build()).build();
        Row data = Row.builder().data(Datum.builder().varCharValue("1").build()).build();
        GetQueryResultsRequest request = GetQueryResultsRequest.builder().queryExecutionId(QUERY_ID).build();

        // Drive the real paginator against the mocked client.
        when(mockClient.getQueryResultsPaginator(any(GetQueryResultsRequest.class)))
            .thenReturn(new GetQueryResultsPublisher(mockClient, request));
        when(mockClient.getQueryResults(any(GetQueryResultsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(GetQueryResultsResponse.builder()
                .resultSet(ResultSet.builder().rows(header, data).build())
                .build()));

        List<Row> rows = actions.getQueryResultsAsync(QUERY_ID).join();
        assertEquals(2, rows.size());
        assertEquals("1", rows.get(1).data().get(0).varCharValue());
    }

    @Test
    void listQueryExecutionsReturnsIds() {
        ListQueryExecutionsRequest request = ListQueryExecutionsRequest.builder().workGroup(WORKGROUP).build();

        when(mockClient.listQueryExecutionsPaginator(any(ListQueryExecutionsRequest.class)))
            .thenReturn(new ListQueryExecutionsPublisher(mockClient, request));
        when(mockClient.listQueryExecutions(any(ListQueryExecutionsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(ListQueryExecutionsResponse.builder()
                .queryExecutionIds("id-1", "id-2")
                .build()));

        List<String> ids = actions.listQueryExecutionsAsync(WORKGROUP).join();
        assertEquals(List.of("id-1", "id-2"), ids);
    }

    @Test
    void deleteNamedQuerySendsId() {
        when(mockClient.deleteNamedQuery(any(DeleteNamedQueryRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(DeleteNamedQueryResponse.builder().build()));

        actions.deleteNamedQueryAsync("nq-123").join();

        ArgumentCaptor<DeleteNamedQueryRequest> captor = ArgumentCaptor.forClass(DeleteNamedQueryRequest.class);
        verify(mockClient).deleteNamedQuery(captor.capture());
        assertEquals("nq-123", captor.getValue().namedQueryId());
    }

    @Test
    void deleteWorkGroupIsRecursive() {
        when(mockClient.deleteWorkGroup(any(DeleteWorkGroupRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(DeleteWorkGroupResponse.builder().build()));

        actions.deleteWorkGroupAsync(WORKGROUP).join();

        ArgumentCaptor<DeleteWorkGroupRequest> captor = ArgumentCaptor.forClass(DeleteWorkGroupRequest.class);
        verify(mockClient).deleteWorkGroup(captor.capture());
        assertEquals(WORKGROUP, captor.getValue().workGroup());
        assertTrue(captor.getValue().recursiveDeleteOption());
    }

    @Test
    void closeClientClosesAndResetsClient() {
        AthenaActions.closeClient();
        verify(mockClient).close();
    }
}
