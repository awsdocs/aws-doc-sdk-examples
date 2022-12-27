// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

global using Amazon.EC2;
global using Amazon.EC2.Model;
global using Amazon.SimpleSystemsManagement;
global using EC2Actions;
global using Microsoft.Extensions.Configuration;
global using Xunit;
global using Xunit.Extensions.Ordering;

// Optional.
[assembly: CollectionBehavior(DisableTestParallelization = true)]
// Optional.
[assembly: TestCaseOrderer("Xunit.Extensions.Ordering.TestCaseOrderer", "Xunit.Extensions.Ordering")]
// Optional.
[assembly: TestCollectionOrderer("Xunit.Extensions.Ordering.CollectionOrderer", "Xunit.Extensions.Ordering")]