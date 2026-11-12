// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.athena.basics.AthenaActions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.WorkGroup;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for AthenaActions against real AWS.
 */
@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(value = 5, unit = TimeUnit.MINUTES)
class AthenaIntegrationTest {

    private final AthenaActions actions = new AthenaActions();
    private S3AsyncClient s3Client;

    private final String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    // Workgroup names: pattern [a-zA-Z0-9._-]{1,128} - hyphens are fine
    private final String workGroupName = "wt-test-wg-" + uniqueId;
    // S3 bucket names: lowercase, hyphens ok, 3-63 chars
    private final String bucketName = "wt-test-athena-" + uniqueId;
    private final String s3OutputLocation = "s3://" + bucketName + "/results/";

    private String namedQueryId;
    private String queryExecutionId;
    // Track which resources still exist so @AfterAll only cleans up what
    // testCleanup didn't already delete (for example, after an earlier failure).
    private boolean bucketCreated;
    private boolean workGroupCreated;

    @BeforeAll
    void setup() {
        // Reset to use the real client (not the mock)
        AthenaActions.setAsyncClient(null);
        s3Client = S3AsyncClient.builder().build();
    }

    @AfterAll
    void cleanup() {
        // Delete named query
        if (namedQueryId != null) {
            try {
                actions.deleteNamedQueryAsync(namedQueryId).join();
            } catch (Exception e) {
                System.err.println("Cleanup: Could not delete named query: " + e.getMessage());
            }
        }

        // Delete workgroup (recursive)
        if (workGroupCreated) {
            try {
                actions.deleteWorkGroupAsync(workGroupName).join();
            } catch (Exception e) {
                System.err.println("Cleanup: Could not delete workgroup: " + e.getMessage());
            }
        }

        // Empty and delete the S3 bucket
        if (bucketCreated) {
            try {
                emptyBucket(bucketName);
                s3Client.deleteBucket(DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build()).join();
            } catch (Exception e) {
                System.err.println("Cleanup: Could not delete bucket: " + e.getMessage());
            }
        }

        if (s3Client != null) {
            s3Client.close();
        }
        AthenaActions.closeClient();
    }

    private void emptyBucket(String bucket) {
        try {
            ListObjectsV2Response listResp = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .build()).join();
            for (S3Object obj : listResp.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(obj.key())
                    .build()).join();
            }
            String token = listResp.nextContinuationToken();
            while (token != null) {
                listResp = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .continuationToken(token)
                    .build()).join();
                for (S3Object obj : listResp.contents()) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(obj.key())
                        .build()).join();
                }
                token = listResp.nextContinuationToken();
            }
        } catch (Exception e) {
            System.err.println("Cleanup: Could not empty bucket: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testCreateS3Bucket() {
        s3Client.createBucket(CreateBucketRequest.builder()
            .bucket(bucketName)
            .build()).join();
        bucketCreated = true;
    }

    @Test
    @Order(2)
    void testCreateWorkGroup() {
        actions.createWorkGroupAsync(workGroupName,
            "Integration test workgroup", s3OutputLocation).join();
        workGroupCreated = true;
    }

    @Test
    @Order(3)
    void testGetWorkGroup() {
        GetWorkGroupResponse response = actions.getWorkGroupAsync(workGroupName).join();
        WorkGroup wg = response.workGroup();
        assertNotNull(wg);
        assertEquals(workGroupName, wg.name());
        assertNotNull(wg.configuration().resultConfiguration().outputLocation());
    }

    @Test
    @Order(4)
    void testCreateNamedQuery() {
        CreateNamedQueryResponse response = actions.createNamedQueryAsync(
            "wt-test-query-" + uniqueId,
            "Integration test query",
            "default",
            "SELECT 1 AS test_column",
            workGroupName
        ).join();
        namedQueryId = response.namedQueryId();
        assertNotNull(namedQueryId);
    }

    @Test
    @Order(5)
    void testCreatePreparedStatement() {
        actions.createPreparedStatementAsync(
            "wt_test_stmt_" + uniqueId.replace("-", ""),
            "SELECT ? AS greeting",
            workGroupName
        ).join();
    }

    @Test
    @Order(6)
    void testStartQueryExecution() {
        StartQueryExecutionResponse response = actions.startQueryExecutionAsync(
            "SELECT 1 AS id, 'Hello Athena' AS message",
            null,
            workGroupName
        ).join();
        queryExecutionId = response.queryExecutionId();
        assertNotNull(queryExecutionId);
    }

    @Test
    @Order(7)
    void testWaitForQueryExecution() {
        assertNotNull(queryExecutionId, "queryExecutionId must be set by previous test");
        GetQueryExecutionResponse response = actions.waitForQueryExecutionAsync(queryExecutionId).join();
        assertEquals(QueryExecutionState.SUCCEEDED,
            response.queryExecution().status().state());
    }

    @Test
    @Order(8)
    void testGetQueryResults() {
        assertNotNull(queryExecutionId, "queryExecutionId must be set by previous test");
        List<Row> rows = actions.getQueryResultsAsync(queryExecutionId).join();
        assertNotNull(rows);
        assertTrue(rows.size() >= 2, "Expected at least header + 1 data row");
    }

    @Test
    @Order(9)
    void testListQueryExecutions() {
        List<String> ids = actions.listQueryExecutionsAsync(workGroupName).join();
        assertNotNull(ids);
        assertFalse(ids.isEmpty(), "Expected at least one query execution");
        assertTrue(ids.contains(queryExecutionId));
    }

    @Test
    @Order(10)
    void testCleanup() {
        // Delete named query
        if (namedQueryId != null) {
            actions.deleteNamedQueryAsync(namedQueryId).join();
            namedQueryId = null; // Prevent double-delete in @AfterAll
        }

        // Delete workgroup
        actions.deleteWorkGroupAsync(workGroupName).join();
        workGroupCreated = false; // Prevent double-delete in @AfterAll

        // Clean S3
        emptyBucket(bucketName);
        s3Client.deleteBucket(DeleteBucketRequest.builder()
            .bucket(bucketName)
            .build()).join();
        bucketCreated = false; // Prevent double-delete in @AfterAll
    }
}
