# .NET Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "dotnet-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("DotNet-premium-KB", ".NET implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate integration test suites using xUnit framework to validate complete scenario workflows against real AWS services.

## Requirements
- **Integration Tests Only**: Focus on end-to-end scenario testing, not unit tests
- **Real AWS Services**: Tests run against actual AWS infrastructure
- **Proper Attributes**: Use xUnit attributes for test categorization
- **Async Testing**: Use async Task for async test methods
- **Resource Cleanup**: Ensure proper cleanup of AWS resources after tests

## File Structure
```
dotnetv4/{Service}/Tests/
├── {Service}Tests.csproj              # Test project file
├── {Service}IntegrationTests.cs       # Integration tests
```

**CRITICAL:**
- ✅ **Integration tests ONLY** - no separate unit tests for wrapper methods
- ✅ **All tests in one project** - no separate IntegrationTests project
- ✅ **Use xUnit framework** - not MSTest
- ✅ **Test against real AWS** - not mocks

## Test Project Setup

### Step 1: Create Test Project File
```xml
<!-- dotnetv4/{Service}/Tests/{Service}Tests.csproj -->
<?xml version="1.0" encoding="utf-8"?>
<Project Sdk="Microsoft.NET.Sdk">
  <PropertyGroup>
    <TargetFramework>net8.0</TargetFramework>
    <IsPackable>false</IsPackable>
    <Nullable>enable</Nullable>
    <LangVersion>latest</LangVersion>
    <AssemblyName>{Service}Tests</AssemblyName>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.12.0" />
    <PackageReference Include="xunit" Version="2.9.3" />
    <PackageReference Include="xunit.runner.visualstudio" Version="2.4.5">
      <PrivateAssets>all</PrivateAssets>
      <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
    </PackageReference>
    <PackageReference Include="coverlet.collector" Version="6.0.4">
      <PrivateAssets>all</PrivateAssets>
      <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
    </PackageReference>
    <PackageReference Include="AWSSDK.{Service}" Version="3.7.401" />
    <PackageReference Include="AWSSDK.{ServiceDataAPI}" Version="3.7.401" />
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\Actions\{Service}Actions.csproj" />
  </ItemGroup>

  <ItemGroup>
    <Content Include="settings.*.json">
      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
      <DependentUpon>settings.json</DependentUpon>
    </Content>
  </ItemGroup>
</Project>
```

**Key Changes:**
- ✅ Use xUnit packages (not MSTest)
- ✅ Target .NET 8.0 with latest language version
- ✅ Use latest AWS SDK package versions
- ✅ Reference Actions project only (no Scenarios reference needed)

### Step 2: Create Integration Test Class Structure
```csharp
// dotnetv4/{Service}/Tests/{Service}IntegrationTests.cs
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Threading.Tasks;
using Amazon.{Service};
using Amazon.{ServiceDataAPI};
using {Service}Actions;
using Xunit;

namespace {Service}Tests;

/// <summary>
/// Integration tests for Amazon {Service} operations.
/// These tests require actual AWS credentials and will create real AWS resources.
/// </summary>
public class {Service}IntegrationTests
{
    /// <summary>
    /// Verifies the scenario with an integration test. No exceptions should be thrown.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenarioIntegration()
    {
        // Arrange
        {Service}Basics.{Service}Basics.IsInteractive = false;

        // Act
        {Service}Basics.{Service}Basics.Wrapper = new {Service}Wrapper(
            new Amazon{Service}Client(),
            new Amazon{ServiceDataAPI}Client());

        await {Service}Basics.{Service}Basics.RunScenarioAsync();

        // Assert - if we get here without exceptions, the test passes
    }
}
```

**Key Changes:**
- ✅ Use file-scoped namespaces
- ✅ Use xUnit attributes ([Fact], [Trait])
- ✅ No mocking - test against real AWS services
- ✅ Test runs the complete scenario end-to-end
- ✅ Set IsInteractive = false for non-interactive test execution

## Integration Test Patterns

### Integration Test Pattern
The integration test should run the complete scenario end-to-end:

```csharp
/// <summary>
/// Verifies the scenario with an integration test. No exceptions should be thrown.
/// </summary>
/// <returns>Async task.</returns>
[Fact]
[Trait("Category", "Integration")]
public async Task TestScenarioIntegration()
{
    // Arrange
    {Service}Basics.{Service}Basics.IsInteractive = false;

    // Act
    {Service}Basics.{Service}Basics.Wrapper = new {Service}Wrapper(
        new Amazon{Service}Client(),
        new Amazon{ServiceDataAPI}Client());

    await {Service}Basics.{Service}Basics.RunScenarioAsync();

    // Assert - if we get here without exceptions, the test passes
}
```

**Key Points:**
- ✅ Use `[Fact]` attribute for test methods
- ✅ Use `[Trait("Category", "Integration")]` for categorization
- ✅ Set `IsInteractive = false` to run without user input
- ✅ Create real AWS clients (not mocked)
- ✅ Call the scenario's `RunScenarioAsync()` method
- ✅ Test passes if no exceptions are thrown

## Test Execution Commands

### All Tests
```bash
dotnet test dotnetv4/{Service}/{Service}Examples.sln
```

### Integration Tests Only
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter TestCategory=Integration
```

### Exclude Long-Running Tests
```bash
dotnet test dotnetv4/{Service}/Tests/{Service}Tests.csproj --filter "TestCategory=Integration&TestCategory!=LongRunning"
```

## Test Requirements Checklist
- ✅ **Test project file created** with proper dependencies
- ✅ **xUnit framework** (not MSTest)
- ✅ **Integration tests only** (no unit tests with mocks)
- ✅ **Proper xUnit attributes** (`[Fact]`, `[Trait("Category", "Integration")]`)
- ✅ **Test against real AWS services** (not mocks)
- ✅ **Test runs complete scenario** via `RunScenarioAsync()`
- ✅ **Async test methods** using `async Task`
- ✅ **File-scoped namespaces** for modern C# style

## Integration Test Focus

### Primary Test: Complete Scenario Integration
The main integration test should:
- ✅ **Run the entire scenario** from start to finish via `RunScenarioAsync()`
- ✅ **Test against real AWS services** (not mocks)
- ✅ **Set IsInteractive = false** for non-interactive execution
- ✅ **Create real AWS clients** (not mocked)
- ✅ **Use xUnit attributes** (`[Fact]`, `[Trait]`) for test organization
- ✅ **Pass if no exceptions** are thrown during execution

### Test Structure
- **Single Integration Test** - Tests the complete scenario workflow
- **No separate unit tests** - Focus on end-to-end integration only
- **No mocking** - Use real AWS clients and services

## Common Test Failures to Avoid
- ❌ **Using mocks instead of real AWS services** in integration tests
- ❌ **Missing xUnit attributes** (`[Fact]`, `[Trait]`) for integration tests
- ❌ **Not setting IsInteractive = false** for non-interactive execution
- ❌ **Forgetting to set AWS region** in test clients
- ❌ **Missing proper async/await patterns** in test methods
- ❌ **Using MSTest instead of xUnit** framework
- ❌ **Creating separate IntegrationTests project** instead of using Tests project
- ❌ **Not referencing the Scenarios project** when testing scenarios