// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Net;
using Amazon.CognitoIdentityProvider;
using Amazon.CognitoIdentityProvider.Model;
using Amazon.Runtime;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Moq;

namespace CognitoWrapperTests;

/// <summary>
/// Tests for the Cognito scenario.
/// </summary>
public class CognitoBasicsTests
{
    private ILoggerFactory _loggerFactory = null!;

    [Trait("Category", "Unit")]
    [Fact]
    public async Task ScenarioTest()
    {
        // Arrange.
        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        var mockCognitoService = new Mock<IAmazonCognitoIdentityProvider>();

        mockCognitoService.Setup(client => client.Paginators.ListUserPools(
                It.IsAny<ListUserPoolsRequest>()))
            .Returns(new TestUserPoolPaginator() as IListUserPoolsPaginator);

        mockCognitoService.Setup(client => client.Paginators.ListUserPools(
                It.IsAny<ListUserPoolsRequest>()))
            .Returns(new TestUserPoolPaginator() as IListUserPoolsPaginator);

        mockCognitoService.Setup(client => client.AdminRespondToAuthChallengeAsync(
                It.IsAny<AdminRespondToAuthChallengeRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((AdminRespondToAuthChallengeRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new AdminRespondToAuthChallengeResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                    AuthenticationResult = new AuthenticationResultType()
                });
            });

        mockCognitoService.Setup(client => client.VerifySoftwareTokenAsync(
                It.IsAny<VerifySoftwareTokenRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((VerifySoftwareTokenRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new VerifySoftwareTokenResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockCognitoService.Setup(client => client.AssociateSoftwareTokenAsync(
                It.IsAny<AssociateSoftwareTokenRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((AssociateSoftwareTokenRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new AssociateSoftwareTokenResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockCognitoService.Setup(client => client.AdminInitiateAuthAsync(
                It.IsAny<AdminInitiateAuthRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((AdminInitiateAuthRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new AdminInitiateAuthResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockCognitoService.Setup(client => client.InitiateAuthAsync(
                It.IsAny<InitiateAuthRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((InitiateAuthRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new InitiateAuthResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockCognitoService.Setup(client => client.ConfirmSignUpAsync(
                It.IsAny<ConfirmSignUpRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((ConfirmSignUpRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new ConfirmSignUpResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        mockCognitoService.Setup(client => client.ResendConfirmationCodeAsync(
                It.IsAny<ResendConfirmationCodeRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((ResendConfirmationCodeRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new ResendConfirmationCodeResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                    CodeDeliveryDetails = new CodeDeliveryDetailsType()
                });
            });

        mockCognitoService.Setup(client => client.AdminGetUserAsync(
                It.IsAny<AdminGetUserRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((AdminGetUserRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new AdminGetUserResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                    UserStatus = UserStatusType.CONFIRMED
                });
            });

        mockCognitoService.Setup(client => client.SignUpAsync(
                It.IsAny<SignUpRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((SignUpRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new SignUpResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        var configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        var wrapper = new CognitoWrapper(mockCognitoService.Object);
        CognitoBasics.CognitoBasics._interactive = false;

        var success =
            await CognitoBasics.CognitoBasics.RunScenario(wrapper, configuration);
        Assert.True(success);
    }

}


/// <summary>
/// Mock Paginator for user pool response.
/// </summary>
public class TestUsersPaginator : IPaginator<ListUsersResponse>, IListUsersPaginator
{
    public IAsyncEnumerable<ListUsersResponse> PaginateAsync(
        CancellationToken cancellationToken = new CancellationToken())
    {
        throw new NotImplementedException();
    }

    public IPaginatedEnumerable<ListUsersResponse> Responses { get; } = null!;
    public IPaginatedEnumerable<UserType> Users { get; } = null!;
}

/// <summary>
/// Mock Paginator for user response.
/// </summary>
public class TestUserPoolPaginator : IPaginator<ListUserPoolsResponse>, IListUserPoolsPaginator
{
    public IAsyncEnumerable<ListUserPoolsResponse> PaginateAsync(
        CancellationToken cancellationToken = new CancellationToken())
    {
        throw new NotImplementedException();
    }

    public IPaginatedEnumerable<ListUserPoolsResponse> Responses { get; } = null!;
    public IPaginatedEnumerable<UserPoolDescriptionType> UserPools { get; } = null!;
}