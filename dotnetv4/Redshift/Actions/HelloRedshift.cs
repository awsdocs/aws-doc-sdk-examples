// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.Redshift;
using Amazon.Redshift.Model;
using Microsoft.Extensions.Logging;

namespace RedshiftActions;

/// <summary>
/// Hello Amazon Redshift example.
/// </summary>
public class HelloRedshift
{
    private static ILogger logger = null!;

    // snippet-start:[Redshift.dotnetv4.Hello]
    /// <summary>
    /// Main method to run the Hello Amazon Redshift example.
    /// </summary>
    /// <param name="args">Command line arguments (not used).</param>
    public static async Task Main(string[] args)
    {
        var redshiftClient = new AmazonRedshiftClient();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<HelloRedshift>();

        Console.Clear();
        Console.WriteLine("Hello, Amazon Redshift! Let's list available clusters:");
        Console.WriteLine();

        var clusters = new List<Cluster>();

        try
        {
            // Use pagination to retrieve all clusters.
            var clustersPaginator = redshiftClient.Paginators.DescribeClusters(new DescribeClustersRequest());

            await foreach (var response in clustersPaginator.Responses)
            {
                if (response.Clusters != null)
                    clusters.AddRange(response.Clusters);
            }

            Console.WriteLine($"{clusters.Count} cluster(s) retrieved.");

            foreach (var cluster in clusters)
            {
                Console.WriteLine($"\t{cluster.ClusterIdentifier} (Status: {cluster.ClusterStatus})");
            }
        }
        catch (AmazonRedshiftException ex)
        {
            logger.LogError("Error listing clusters: {Message}", ex.Message);
            Console.WriteLine($"Couldn't list clusters. Error: {ex.Message}");
        }
        catch (Exception ex)
        {
            logger.LogError("An error occurred: {Message}", ex.Message);
            Console.WriteLine($"An error occurred: {ex.Message}");
        }
    }
    // snippet-end:[Redshift.dotnetv4.Hello]
}