// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.ControlCatalog;
using Amazon.ControlCatalog.Model;
using Amazon.ControlTower;
using Amazon.ControlTower.Model;
using Amazon.Organizations;
using Amazon.Organizations.Model;
using Amazon.Runtime;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using ControlTowerActions;
using Microsoft.Extensions.Logging;
using Moq;

namespace ControlTowerTests;

/// <summary>
/// Integration tests for the AWS Control Tower Basics scenario.
/// </summary>
public class ControlTowerBasicsTests
{
    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenarioIntegration()
    {
        // Arrange
        ControlTowerBasics.ControlTowerBasics.isInteractive = false;
        var loggerScenarioMock = new Mock<ILogger<ControlTowerBasics.ControlTowerBasics>>();

        loggerScenarioMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        // Act
        ControlTowerBasics.ControlTowerBasics.logger = loggerScenarioMock.Object;

        ControlTowerBasics.ControlTowerBasics.wrapper = new ControlTowerWrapper(new AmazonControlTowerClient(), new AmazonControlCatalogClient());
        ControlTowerBasics.ControlTowerBasics.orgClient = new AmazonOrganizationsClient();
        ControlTowerBasics.ControlTowerBasics.stsClient = new AmazonSecurityTokenServiceClient();


        await ControlTowerBasics.ControlTowerBasics.RunScenario();

        // Assert no errors logged
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }

    /// <summary>
    /// Scenario test using mocked AWS service clients.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task TestScenarioLogic()
    {
        // Arrange
        var mockControlTower = new Mock<IAmazonControlTower>();
        var mockControlCatalog = new Mock<IAmazonControlCatalog>();
        var mockOrganizations = new Mock<IAmazonOrganizations>();
        var mockSts = new Mock<IAmazonSecurityTokenService>();

        SetupMocks(mockControlTower, mockControlCatalog, mockOrganizations, mockSts);

        ControlTowerBasics.ControlTowerBasics.isInteractive = false;
        ControlTowerBasics.ControlTowerBasics.wrapper = new ControlTowerWrapper(mockControlTower.Object, mockControlCatalog.Object);
        ControlTowerBasics.ControlTowerBasics.orgClient = mockOrganizations.Object;
        ControlTowerBasics.ControlTowerBasics.stsClient = mockSts.Object;

        var loggerScenarioMock = new Mock<ILogger<ControlTowerBasics.ControlTowerBasics>>();
        ControlTowerBasics.ControlTowerBasics.logger = loggerScenarioMock.Object;

        // Act
        await ControlTowerBasics.ControlTowerBasics.RunScenario();

        // Assert no errors logged
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }

    /// <summary>
    /// Test scenario with ControlTowerException.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task TestScenarioWithException()
    {
        // Arrange
        var mockControlTower = new Mock<IAmazonControlTower>();
        var mockControlCatalog = new Mock<IAmazonControlCatalog>();
        var mockOrganizations = new Mock<IAmazonOrganizations>();
        var mockSts = new Mock<IAmazonSecurityTokenService>();

        SetupMocks(mockControlTower, mockControlCatalog, mockOrganizations, mockSts, throwException: true);

        ControlTowerBasics.ControlTowerBasics.isInteractive = false;
        ControlTowerBasics.ControlTowerBasics.wrapper = new ControlTowerWrapper(mockControlTower.Object, mockControlCatalog.Object);
        ControlTowerBasics.ControlTowerBasics.orgClient = mockOrganizations.Object;
        ControlTowerBasics.ControlTowerBasics.stsClient = mockSts.Object;

        var loggerScenarioMock = new Mock<ILogger<ControlTowerBasics.ControlTowerBasics>>();
        ControlTowerBasics.ControlTowerBasics.logger = loggerScenarioMock.Object;

        // Act
        await ControlTowerBasics.ControlTowerBasics.RunScenario();

        // Assert the error is logged.
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Once);
    }

    /// <summary>
    /// Set up the mocks for testing.
    /// </summary>
    /// <param name="mockControlTower">Mock ControlTower client.</param>
    /// <param name="mockControlCatalog">Mock ControlCatalog client.</param>
    /// <param name="mockOrganizations">Mock Organizations client.</param>
    /// <param name="mockSts">Mock Sts client.</param>
    /// <param name="throwException">True to force an exception.</param>
    private void SetupMocks(Mock<IAmazonControlTower> mockControlTower, Mock<IAmazonControlCatalog> mockControlCatalog, Mock<IAmazonOrganizations> mockOrganizations, Mock<IAmazonSecurityTokenService> mockSts, bool throwException = false)
    {
        // Setup paginator mocks
        var mockLandingZonesPaginator = new Mock<IListLandingZonesPaginator>();
        var mockLandingZonesEnumerable = new Mock<IPaginatedEnumerable<ListLandingZonesResponse>>();

        mockLandingZonesEnumerable.Setup(x => x.GetAsyncEnumerator(CancellationToken.None))
            .Returns(new List<ListLandingZonesResponse>
            {
                new ListLandingZonesResponse { LandingZones =
                    new List<LandingZoneSummary>
                    {
                        new LandingZoneSummary { Arn = "arn:aws:controltower:us-east-1:123456789012:landingzone/test-lz" }
                    } }
            }.ToAsyncEnumerable().GetAsyncEnumerator());


        mockLandingZonesPaginator.Setup(x => x.Responses).Returns(mockLandingZonesEnumerable.Object);
        mockControlTower.Setup(x => x.Paginators.ListLandingZones(It.IsAny<ListLandingZonesRequest>()))
            .Returns(mockLandingZonesPaginator.Object);

        var mockBaselinesPaginator = new Mock<IListBaselinesPaginator>();
        var mockBaselinesEnumerable = new Mock<IPaginatedEnumerable<ListBaselinesResponse>>();
        mockBaselinesEnumerable.Setup(x => x.GetAsyncEnumerator(CancellationToken.None))
            .Returns(new List<ListBaselinesResponse>
            {
                new ListBaselinesResponse { Baselines =
                    new List<BaselineSummary>
                    {
                        new BaselineSummary { Arn = "arn:aws:controltower:us-east-1:123456789012:baseline/test-baseline", Name = "AWSControlTowerBaseline" }
                    } }
            }.ToAsyncEnumerable().GetAsyncEnumerator());
        mockBaselinesPaginator.Setup(x => x.Responses).Returns(mockBaselinesEnumerable.Object);
        mockControlTower.Setup(x => x.Paginators.ListBaselines(It.IsAny<ListBaselinesRequest>()))
            .Returns(mockBaselinesPaginator.Object);

        var mockEnabledBaselinesPaginator = new Mock<IListEnabledBaselinesPaginator>();
        var mockEnabledBaselinesEnumerable = new Mock<IPaginatedEnumerable<ListEnabledBaselinesResponse>>();
        mockEnabledBaselinesEnumerable.Setup(x => x.GetAsyncEnumerator(CancellationToken.None))
            .Returns(new List<ListEnabledBaselinesResponse>
            {
                new ListEnabledBaselinesResponse { EnabledBaselines =
                    new List<EnabledBaselineSummary>
                    {
                        new EnabledBaselineSummary { Arn = "arn:aws:controltower:us-east-1:123456789012:enabledbaseline/test-enabled", BaselineIdentifier = "baseline/LN25R72TTG6IGPTQ" }
                    } }
            }.ToAsyncEnumerable().GetAsyncEnumerator());
        mockEnabledBaselinesPaginator.Setup(x => x.Responses).Returns(mockEnabledBaselinesEnumerable.Object);
        mockControlTower.Setup(x => x.Paginators.ListEnabledBaselines(It.IsAny<ListEnabledBaselinesRequest>()))
            .Returns(mockEnabledBaselinesPaginator.Object);

        var mockEnabledControlsPaginator = new Mock<IListEnabledControlsPaginator>();
        var mockEnabledControlsEnumerable = new Mock<IPaginatedEnumerable<ListEnabledControlsResponse>>();
        mockEnabledControlsEnumerable.Setup(x => x.GetAsyncEnumerator(CancellationToken.None))
            .Returns(new List<ListEnabledControlsResponse>
            {
                new ListEnabledControlsResponse { EnabledControls =
                    new List<EnabledControlSummary>
                    {
                        new EnabledControlSummary { Arn = "arn:aws:controltower:us-east-1:123456789012:control/test-control", ControlIdentifier = "arn:aws:controltower:us-east-1:123456789012:control/test-control-identifier" }
                    } }
            }.ToAsyncEnumerable().GetAsyncEnumerator());
        mockEnabledControlsPaginator.Setup(x => x.Responses).Returns(mockEnabledControlsEnumerable.Object);
        mockControlTower.Setup(x => x.Paginators.ListEnabledControls(It.IsAny<ListEnabledControlsRequest>()))
            .Returns(mockEnabledControlsPaginator.Object);

        var mockControlsPaginator = new Mock<IListControlsPaginator>();
        var mockControlsEnumerable = new Mock<IPaginatedEnumerable<Amazon.ControlCatalog.Model.ListControlsResponse>>();
        mockControlsEnumerable.Setup(x => x.GetAsyncEnumerator(CancellationToken.None))
            .Returns(new List<Amazon.ControlCatalog.Model.ListControlsResponse>
            {
                new Amazon.ControlCatalog.Model.ListControlsResponse { Controls =
                    new List<Amazon.ControlCatalog.Model.ControlSummary>
                    {
                        new Amazon.ControlCatalog.Model.ControlSummary { Arn = "arn:aws:controlcatalog:us-east-1::control/ABCDEFG1234567", Name = "Test Control" }
                    } }
            }.ToAsyncEnumerable().GetAsyncEnumerator());
        mockControlsPaginator.Setup(x => x.Responses).Returns(mockControlsEnumerable.Object);
        mockControlCatalog.Setup(x => x.Paginators.ListControls(It.IsAny<Amazon.ControlCatalog.Model.ListControlsRequest>()))
            .Returns(mockControlsPaginator.Object);

        // Force an exception that should end the scenario.
        if (throwException)
        {
            mockControlTower.Setup(x =>
                x.DisableBaselineAsync(It.IsAny<DisableBaselineRequest>(), default))
                .Throws(new AmazonControlTowerException("Test exception"));
        }
        else
        {
            mockControlTower.Setup(x => x.DisableBaselineAsync(It.IsAny<DisableBaselineRequest>(), default))
                .ReturnsAsync(new DisableBaselineResponse { OperationIdentifier = "12345678-1234-1234-1234-123456789012" });
        }
        mockControlTower.Setup(x =>
                x.EnableBaselineAsync(It.IsAny<EnableBaselineRequest>(), default))
            .ReturnsAsync(new EnableBaselineResponse
            {
                Arn =
                    "arn:aws:controltower:us-east-1:123456789012:enabledbaseline/test-baseline",
                OperationIdentifier = "12345678-1234-1234-1234-123456789012"
            });

        mockControlTower.Setup(x => x.ResetEnabledBaselineAsync(It.IsAny<ResetEnabledBaselineRequest>(), default))
            .ReturnsAsync(new ResetEnabledBaselineResponse { OperationIdentifier = "12345678-1234-1234-1234-123456789012" });
        mockControlTower.Setup(x => x.GetBaselineOperationAsync(It.IsAny<GetBaselineOperationRequest>(), default))
            .ReturnsAsync(new GetBaselineOperationResponse { BaselineOperation = new BaselineOperation { Status = BaselineOperationStatus.SUCCEEDED } });
        mockControlTower.Setup(x => x.EnableControlAsync(It.IsAny<EnableControlRequest>(), default))
            .ReturnsAsync(new EnableControlResponse { OperationIdentifier = "12345678-1234-1234-1234-123456789012" });
        mockControlTower.Setup(x => x.DisableControlAsync(It.IsAny<DisableControlRequest>(), default))
            .ReturnsAsync(new DisableControlResponse { OperationIdentifier = "12345678-1234-1234-1234-123456789012" });
        mockControlTower.Setup(x => x.GetControlOperationAsync(It.IsAny<GetControlOperationRequest>(), default))
            .ReturnsAsync(new GetControlOperationResponse { ControlOperation = new ControlOperation { Status = ControlOperationStatus.SUCCEEDED } });
        mockControlTower.Setup(x => x.GetLandingZoneAsync(It.IsAny<GetLandingZoneRequest>(), default))
            .ReturnsAsync(new GetLandingZoneResponse { LandingZone = new LandingZoneDetail() });

        // Setup Organizations mocks
        mockOrganizations.Setup(x => x.DescribeOrganizationAsync(It.IsAny<DescribeOrganizationRequest>(), default))
            .ReturnsAsync(new DescribeOrganizationResponse { Organization = new Organization { Id = "o-test123456" } });
        mockOrganizations.Setup(x => x.ListRootsAsync(It.IsAny<ListRootsRequest>(), default))
            .ReturnsAsync(new ListRootsResponse { Roots = new List<Root> { new Root { Id = "r-test123", Arn = "arn:aws:organizations::123456789012:root/o-test123456/r-test123" } } });
        mockOrganizations.Setup(x => x.ListOrganizationalUnitsForParentAsync(It.IsAny<ListOrganizationalUnitsForParentRequest>(), default))
            .ReturnsAsync(new ListOrganizationalUnitsForParentResponse { OrganizationalUnits = new List<OrganizationalUnit> { new OrganizationalUnit { Id = "ou-test1234-abcd5678", Name = "Sandbox", Arn = "arn:aws:organizations::123456789012:ou/o-test123456/ou-test1234-abcd5678" } } });

        // Setup STS mocks
        mockSts.Setup(x => x.GetCallerIdentityAsync(It.IsAny<GetCallerIdentityRequest>(), default))
            .ReturnsAsync(new GetCallerIdentityResponse { Account = "123456789012" });
    }
}