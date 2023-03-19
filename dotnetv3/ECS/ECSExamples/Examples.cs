// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0
using Amazon.ECS;
using Amazon.ECS.Model;

namespace ECSExamples;


public class ECSListExamples
{
    public static async Task<List<string>> GetClusterARNSAsync(AmazonECSClient ecsClient)
    {

        Console.WriteLine("Getting a list of all the clusters in your AWS account...");
        List<string> clusterArnList = new List<string>();
        // Get a list of all the clusters in your AWS account
        try
        {
            var listClustersResponse = await ecsClient.ListClustersAsync(new ListClustersRequest
            {
            });

            var clusterArns = listClustersResponse.ClusterArns;

            // Print the ARNs of the clusters
            foreach (var clusterArn in clusterArns)
            {
                clusterArnList.Add(clusterArn);

                Console.WriteLine($"Cluster ARN: {clusterArn}");
                Console.WriteLine($"Cluster Name: {clusterArn.Split("/").Last()}");
            }

            if (clusterArns.Count == 0)
            {
                Console.WriteLine("No clusters found in your AWS account.");
            }


            return clusterArnList;

        }
        catch (Exception e)
        {
            throw new Exception($"An error occurred while getting a list of all the clusters in your AWS account. {e.InnerException}");
        }

    }


    public static async Task<List<string>> GetServiceARNSAsync(AmazonECSClient ecsClient, string clusterARN)
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
            var response = await ecsClient.ListServicesAsync(request);
            serviceArns.AddRange(response.ServiceArns);
            nextToken = response.NextToken;
        } while (nextToken != null);


        foreach (string arn in serviceArns)
        {
            Console.WriteLine($"Service ARN: {arn}");
        }

        return serviceArns;

    }

    public static async Task<List<string>> GetTaskARNsAsync(AmazonECSClient ecsClient, string clusterARN)
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
            var listTasksResponse = await ecsClient.ListTasksAsync(listTasksRequest);
            taskArns.AddRange(listTasksResponse.TaskArns);
            nextToken = listTasksResponse.NextToken;
        } while (nextToken != null);

        // Print the ARNs of the tasks
        foreach (var taskArn in taskArns)
        {
            Console.WriteLine($"Task ARN: {taskArn}");
        }

        if (taskArns.Count == 0)
        {
            Console.WriteLine("No tasks found in cluster: " + clusterARN);
        }


        return taskArns;
    }

}