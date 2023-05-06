// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Amazon.ECS;
using Amazon.ECS.Model;
using Microsoft.Extensions.Logging;

namespace ECSActions;

public class ECSWrapper
{

    private readonly AmazonECSClient _ecsClient;
    private readonly ILogger<ECSWrapper> _logger;

    /// <summary>
    /// Constructor for the CloudWatch wrapper.
    /// </summary>
    /// <param name="ecsClient">The injected ECS client.</param>
    /// <param name="logger">The injected logger for the wrapper.</param>
    public ECSWrapper(AmazonECSClient ecsClient, ILogger<ECSWrapper> logger)

    {
        _logger = logger;
        _ecsClient = ecsClient;
    }

    /// <summary>
    /// List cluster ARNs available.
    /// </summary>
    /// <returns>The ARN list of clusters.</returns>
    public async Task<List<string>> GetClusterARNSAsync()
    {

        Console.WriteLine("Getting a list of all the clusters in your AWS account...");
        List<string> clusterArnList = new List<string>();
        // Get a list of all the clusters in your AWS account
        try
        {
            var listClustersResponse = await _ecsClient.ListClustersAsync(new ListClustersRequest
            {
            });

            var clusterArns = listClustersResponse.ClusterArns;

            // Print the ARNs of the clusters
            foreach (var clusterArn in clusterArns)
            {
                clusterArnList.Add(clusterArn);
            }

            if (clusterArns.Count == 0)
            {
                _logger.LogWarning("No clusters found in your AWS account.");
            }


            return clusterArnList;

        }
        catch (Exception e)
        {
            _logger.LogError($"An error occurred while getting a list of all the clusters in your AWS account. {e.InnerException}");
            throw new Exception($"An error occurred while getting a list of all the clusters in your AWS account. {e.InnerException}");
        }

    }

    /// <summary>
    /// List service ARNs available.
    /// </summary>
    /// <param name="clusterARN">The arn of the ECS cluster.</param>
    /// <returns>The ARN list of services in given cluster.</returns>
    public async Task<List<string>> GetServiceARNSAsync(string clusterARN)
    {
        List<string> serviceArns = new List<string>();

        var request = new ListServicesRequest
        {
            Cluster = clusterARN
        };

        string nextToken = null;
        do
        {
            request.NextToken = nextToken;
            var response = await _ecsClient.ListServicesAsync(request);
            serviceArns.AddRange(response.ServiceArns);
            nextToken = response.NextToken;
        } while (nextToken != null);


        return serviceArns;

    }

    /// <summary>
    /// List tasks ARNs available.
    /// </summary>
    /// <param name="clusterARN">The arn of the ECS cluster.</param>
    /// <returns>The ARN list of tasks in given cluster.</returns>
    public async Task<List<string>> GetTaskARNsAsync(string clusterARN)
    {
        // Set up the request to describe the tasks in the service
        var listTasksRequest = new ListTasksRequest
        {
            Cluster = clusterARN
        };

        // Call the ListTasks API operation and get the list of task ARNs
        List<string> taskArns = new List<string>();
        string nextToken = null;
        do
        {
            listTasksRequest.NextToken = nextToken;
            var listTasksResponse = await _ecsClient.ListTasksAsync(listTasksRequest);
            taskArns.AddRange(listTasksResponse.TaskArns);
            nextToken = listTasksResponse.NextToken;
        } while (nextToken != null);

        if (taskArns.Count == 0)
        {
            _logger.LogWarning("No tasks found in cluster: " + clusterARN);
        }


        return taskArns;
    }

}
