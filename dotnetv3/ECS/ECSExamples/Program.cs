// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0
using Amazon;
using Amazon.ECS;
using ECSExamples;

var credentials = new Amazon.Runtime.BasicAWSCredentials("", "");
var ecsConfig = new AmazonECSConfig
{
    RegionEndpoint = RegionEndpoint.EUWest1
};
var ecsClient = new AmazonECSClient(credentials, ecsConfig);

//get all clusters
var clusters = await ECSListExamples.GetClusterARNSAsync(ecsClient);

//get all services in the first cluster
var services = await ECSListExamples.GetServiceARNSAsync(ecsClient, clusters[0]);

//get all tasks in the first cluster
var tasks = await ECSListExamples.GetTaskARNsAsync(ecsClient, clusters[0]);
