global using Xunit;
global using Xunit.Extensions.Ordering;
global using Microsoft.Extensions.Configuration;
global using IAMActions;
global using IamScenariosCommon;

// Optional.
[assembly: CollectionBehavior(DisableTestParallelization = true)]
// Optional.
[assembly: TestCaseOrderer("Xunit.Extensions.Ordering.TestCaseOrderer", "Xunit.Extensions.Ordering")]
// Optional.
[assembly: TestCollectionOrderer("Xunit.Extensions.Ordering.CollectionOrderer", "Xunit.Extensions.Ordering")]