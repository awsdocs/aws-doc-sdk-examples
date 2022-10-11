global using Amazon;
global using Amazon.CognitoIdentityProvider;
global using Amazon.CognitoIdentityProvider.Model;
global using Cognito_MVP;
global using Microsoft.Extensions.Configuration;
global using Xunit;

// Optional.
[assembly: CollectionBehavior(DisableTestParallelization = true)]
// Optional.
[assembly: TestCaseOrderer("Xunit.Extensions.Ordering.TestCaseOrderer", "Xunit.Extensions.Ordering")]
// Optional.
[assembly: TestCollectionOrderer("Xunit.Extensions.Ordering.CollectionOrderer", "Xunit.Extensions.Ordering")]