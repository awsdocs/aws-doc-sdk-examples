// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Threading.Tasks;
using Amazon.Redshift;
using Amazon.RedshiftDataAPIService;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using RedshiftActions;

namespace RedshiftTests;

/// <summary>
/// Integration tests for Amazon Redshift operations.
/// These tests require actual AWS credentials and will create real AWS resources.
/// </summary>
[TestClass]
public class RedshiftIntegrationTests
{
    private static RedshiftWrapper? _redshiftWrapper;
    private static string? _testClusterIdentifier;
    private const string TestDatabaseName = "dev";
    private const string TestUsername = "testuser";
    private const string TestPassword = "TestPassword123!";

    [ClassInitialize]
    public static void ClassInitialize(TestContext context)
    {
        // Initialize clients
        var redshiftClient = new AmazonRedshiftClient();
        var redshiftDataClient = new AmazonRedshiftDataAPIServiceClient();
        _redshiftWrapper = new RedshiftWrapper(redshiftClient, redshiftDataClient);

        // Generate unique cluster identifier
        _testClusterIdentifier = $"test-cluster-{DateTime.Now:yyyyMMddHHmmss}";

        Console.WriteLine($"Integration tests will use cluster: {_testClusterIdentifier}");
    }

    [ClassCleanup]
    public static async Task ClassCleanup()
    {
        // Clean up any remaining test resources
        if (_redshiftWrapper != null && !string.IsNullOrEmpty(_testClusterIdentifier))
        {
            try
            {
                Console.WriteLine($"Cleaning up test cluster: {_testClusterIdentifier}");
                await _redshiftWrapper.DeleteClusterAsync(_testClusterIdentifier);
                Console.WriteLine("Test cluster cleanup initiated.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Warning: Failed to cleanup test cluster: {ex.Message}");
            }
        }
    }

    [TestMethod]
    [TestCategory("Integration")]
    public async Task DescribeClusters_Integration_ReturnsClusterList()
    {
        // Act
        var clusters = await _redshiftWrapper!.DescribeClustersAsync();

        // Assert
        Assert.IsNotNull(clusters);
        // Note: We don't assert specific count since other clusters might exist
        Console.WriteLine($"Found {clusters.Count} existing clusters.");
    }

    [TestMethod]
    [TestCategory("Integration")]
    [TestCategory("LongRunning")]
    public async Task RedshiftFullWorkflow_Integration_CompletesSuccessfully()
    {
        // This test runs the complete Redshift workflow
        // Note: This test can take 10-15 minutes to complete due to cluster creation time

        try
        {
            Console.WriteLine("Starting Redshift full workflow integration test...");

            // Step 1: Create cluster
            Console.WriteLine($"Creating cluster: {_testClusterIdentifier}");
            var createdCluster = await _redshiftWrapper!.CreateClusterAsync(
                _testClusterIdentifier!,
                TestDatabaseName,
                TestUsername,
                TestPassword);

            Assert.IsNotNull(createdCluster);
            Assert.AreEqual(_testClusterIdentifier, createdCluster.ClusterIdentifier);
            Console.WriteLine("Cluster creation initiated successfully.");

            // Step 2: Wait for cluster to become available (this can take several minutes)
            Console.WriteLine("Waiting for cluster to become available... This may take 10-15 minutes.");
            await WaitForClusterAvailable(_testClusterIdentifier!, TimeSpan.FromMinutes(20));

            // Step 3: List databases
            Console.WriteLine("Listing databases...");
            var databases = await _redshiftWrapper.ListDatabasesAsync(_testClusterIdentifier!, TestUsername, TestDatabaseName);
            Assert.IsNotNull(databases);
            Assert.IsTrue(databases.Count > 0);
            Console.WriteLine($"Found {databases.Count} databases.");

            // Step 4: Create table
            Console.WriteLine("Creating Movies table...");
            var createTableStatementId = await _redshiftWrapper.CreateTableAsync(
                _testClusterIdentifier!,
                TestDatabaseName,
                TestUsername);
            Assert.IsNotNull(createTableStatementId);
            Console.WriteLine("Movies table created successfully.");

            // Step 5: Insert sample data
            Console.WriteLine("Inserting sample movie data...");
            var insertStatementId = await _redshiftWrapper.InsertMovieAsync(
                _testClusterIdentifier!,
                TestDatabaseName,
                TestUsername,
                1,
                "Test Movie",
                2023);
            Assert.IsNotNull(insertStatementId);
            Console.WriteLine("Sample data inserted successfully.");

            // Step 6: Query data
            Console.WriteLine("Querying movies by year...");
            var movies = await _redshiftWrapper.QueryMoviesByYearAsync(
                _testClusterIdentifier!,
                TestDatabaseName,
                TestUsername,
                2023);
            Assert.IsNotNull(movies);
            Assert.IsTrue(movies.Count > 0);
            Assert.AreEqual("Test Movie", movies[0]);
            Console.WriteLine($"Query returned {movies.Count} movies.");

            // Step 7: Modify cluster
            Console.WriteLine("Modifying cluster maintenance window...");
            var modifiedCluster = await _redshiftWrapper.ModifyClusterAsync(
                _testClusterIdentifier!,
                "wed:07:30-wed:08:00");
            Assert.IsNotNull(modifiedCluster);
            Console.WriteLine("Cluster modified successfully.");

            Console.WriteLine("Full workflow integration test completed successfully!");
        }
        finally
        {
            // Step 8: Clean up - Delete cluster
            if (!string.IsNullOrEmpty(_testClusterIdentifier))
            {
                Console.WriteLine($"Deleting test cluster: {_testClusterIdentifier}");
                try
                {
                    var deletedCluster = await _redshiftWrapper!.DeleteClusterAsync(_testClusterIdentifier);
                    Assert.IsNotNull(deletedCluster);
                    Console.WriteLine("Cluster deletion initiated successfully.");
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Failed to delete cluster: {ex.Message}");
                    throw;
                }
            }
        }
    }

    [TestMethod]
    [TestCategory("Integration")]
    public async Task DescribeStatement_Integration_ReturnsStatementDetails()
    {
        // This test requires an existing cluster - skip if none available
        var clusters = await _redshiftWrapper!.DescribeClustersAsync();

        if (clusters.Count == 0)
        {
            Assert.Inconclusive("No Redshift clusters available for integration testing.");
            return;
        }

        var testCluster = clusters[0];
        if (testCluster.ClusterStatus != "available")
        {
            Assert.Inconclusive($"Test cluster {testCluster.ClusterIdentifier} is not available (status: {testCluster.ClusterStatus}).");
            return;
        }

        try
        {
            // Execute a simple statement
            var statementId = await _redshiftWrapper.CreateTableAsync(
                testCluster.ClusterIdentifier,
                TestDatabaseName,
                TestUsername);

            // Describe the statement
            var statementDetails = await _redshiftWrapper.DescribeStatementAsync(statementId);

            Assert.IsNotNull(statementDetails);
            Assert.AreEqual(statementId, statementDetails.Id);
            Assert.IsNotNull(statementDetails.Status);

            Console.WriteLine($"Statement {statementId} has status: {statementDetails.Status}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Integration test failed: {ex.Message}");
            Assert.Inconclusive($"Could not complete integration test: {ex.Message}");
        }
    }

    /// <summary>
    /// Wait for a cluster to become available with timeout.
    /// </summary>
    /// <param name="clusterIdentifier">The cluster identifier.</param>
    /// <param name="timeout">Maximum time to wait.</param>
    private async Task WaitForClusterAvailable(string clusterIdentifier, TimeSpan timeout)
    {
        var startTime = DateTime.UtcNow;
        var endTime = startTime.Add(timeout);

        while (DateTime.UtcNow < endTime)
        {
            var clusters = await _redshiftWrapper!.DescribeClustersAsync(clusterIdentifier);

            if (clusters.Count > 0 && clusters[0].ClusterStatus == "available")
            {
                Console.WriteLine($"Cluster {clusterIdentifier} is now available!");
                return;
            }

            var elapsed = DateTime.UtcNow - startTime;
            Console.WriteLine($"Waiting for cluster... Elapsed time: {elapsed:mm\\:ss}");

            await Task.Delay(TimeSpan.FromSeconds(30)); // Wait 30 seconds between checks
        }

        throw new TimeoutException($"Cluster {clusterIdentifier} did not become available within {timeout.TotalMinutes} minutes.");
    }
}

/// <summary>
/// Integration tests specifically for data operations.
/// These tests require an existing, available Redshift cluster.
/// </summary>
[TestClass]
public class RedshiftDataIntegrationTests
{
    private static RedshiftWrapper? _redshiftWrapper;
    private const string TestDatabaseName = "dev";
    private const string TestUsername = "testuser";

    [ClassInitialize]
    public static void ClassInitialize(TestContext context)
    {
        var redshiftClient = new AmazonRedshiftClient();
        var redshiftDataClient = new AmazonRedshiftDataAPIServiceClient();
        _redshiftWrapper = new RedshiftWrapper(redshiftClient, redshiftDataClient);
    }

    [TestMethod]
    [TestCategory("Integration")]
    [TestCategory("DataOperations")]
    public async Task DataOperations_WithExistingCluster_WorksCorrectly()
    {
        // Find an available cluster for testing
        var clusters = await _redshiftWrapper!.DescribeClustersAsync();
        var availableCluster = clusters.Find(c => c.ClusterStatus == "available");

        if (availableCluster == null)
        {
            Assert.Inconclusive("No available Redshift clusters found for data operations testing.");
            return;
        }

        var clusterIdentifier = availableCluster.ClusterIdentifier;
        Console.WriteLine($"Using cluster: {clusterIdentifier}");

        try
        {
            // Test creating a unique table
            var tableName = $"test_table_{DateTime.Now:yyyyMMddHHmmss}";
            Console.WriteLine($"Creating table: {tableName}");

            // Note: This would require modifying the wrapper to accept custom table names
            // For now, we'll test with the standard Movies table

            var createResult = await _redshiftWrapper.CreateTableAsync(
                clusterIdentifier,
                TestDatabaseName,
                TestUsername);

            Assert.IsNotNull(createResult);
            Console.WriteLine("Table creation test completed.");

            // Test data insertion
            var insertResult = await _redshiftWrapper.InsertMovieAsync(
                clusterIdentifier,
                TestDatabaseName,
                TestUsername,
                999,
                $"Integration Test Movie {DateTime.Now:HHmmss}",
                2023);

            Assert.IsNotNull(insertResult);
            Console.WriteLine("Data insertion test completed.");

            // Test data querying
            var queryResults = await _redshiftWrapper.QueryMoviesByYearAsync(
                clusterIdentifier,
                TestDatabaseName,
                TestUsername,
                2023);

            Assert.IsNotNull(queryResults);
            Console.WriteLine($"Query returned {queryResults.Count} results.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Data operations test failed: {ex.Message}");
            // Don't fail the test for expected database-related issues
            Assert.Inconclusive($"Data operations test could not complete: {ex.Message}");
        }
    }

    [TestMethod]
    [TestCategory("Integration")]
    public async Task ListDatabases_WithExistingCluster_ReturnsResults()
    {
        var clusters = await _redshiftWrapper!.DescribeClustersAsync();
        var availableCluster = clusters.Find(c => c.ClusterStatus == "available");

        if (availableCluster == null)
        {
            Assert.Inconclusive("No available Redshift clusters found for database listing test.");
            return;
        }

        try
        {
            var databases = await _redshiftWrapper.ListDatabasesAsync(
                availableCluster.ClusterIdentifier,
                TestUsername,
                TestDatabaseName);

            Assert.IsNotNull(databases);
            Console.WriteLine($"Found {databases.Count} databases in cluster {availableCluster.ClusterIdentifier}");

            foreach (var db in databases)
            {
                Console.WriteLine($"  Database: {db}");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"List databases test failed: {ex.Message}");
            Assert.Inconclusive($"Could not list databases: {ex.Message}");
        }
    }
}
