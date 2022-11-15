// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

global using Microsoft.Extensions.Configuration;
global using Xunit;
global using Xunit.Extensions.Ordering;
global using System.Threading.Tasks;
global using Amazon.IdentityManagement;
global using Amazon;
global using Amazon.IdentityManagement.Model;
global using IAM_Basics_Scenario;

// Optional.
[assembly: CollectionBehavior(DisableTestParallelization = true)]
// Optional.
[assembly: TestCaseOrderer("Xunit.Extensions.Ordering.TestCaseOrderer", "Xunit.Extensions.Ordering")]
// Optional.
[assembly: TestCollectionOrderer("Xunit.Extensions.Ordering.CollectionOrderer", "Xunit.Extensions.Ordering")]