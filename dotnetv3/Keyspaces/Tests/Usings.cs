// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

global using KeyspacesScenario;
global using Amazon.Keyspaces;
global using Amazon.Keyspaces.Model;
global using Microsoft.Extensions.Configuration;
global using KeyspacesActions;
global using Xunit;
global using Xunit.Extensions.Ordering;

// Optional.
[assembly: CollectionBehavior(DisableTestParallelization = true)]
// Optional.
[assembly: TestCaseOrderer("Xunit.Extensions.Ordering.TestCaseOrderer", "Xunit.Extensions.Ordering")]
// Optional.
[assembly: TestCollectionOrderer("Xunit.Extensions.Ordering.CollectionOrderer", "Xunit.Extensions.Ordering")]