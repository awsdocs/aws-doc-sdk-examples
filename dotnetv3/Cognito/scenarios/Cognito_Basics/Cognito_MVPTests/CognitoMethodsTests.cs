// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Cognito_MVP.Tests
{
    public class CognitoMethodsTests
    {
        private readonly IConfiguration _configuration;
        private readonly CognitoMethods _methods;
        private static readonly AmazonCognitoIdentityProviderClient _client = new AmazonCognitoIdentityProviderClient();

        public CognitoMethodsTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from JSON file.
                .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

            _methods = new CognitoMethods();
        }

        [Fact]
        public async Task AdminRespondToAuthChallengeTest_BadMFA_ShouldRaiseError()
        {
            var userName = _configuration["UserName"];
            var clientId = _configuration["ClientId"];
            var mfaCode = "abcdef0123"; // Incorrect MFA code.
            var session = _configuration["Session"];

            await Assert.ThrowsAsync<InvalidParameterException>(async () =>
            {
                var success = await CognitoMethods.AdminRespondToAuthChallenge(
                    _client,
                    userName,
                    clientId,
                    mfaCode,
                    session);
            });
        }

        [Fact]
        public async Task VerifyTOTPTest_Nonsense_ShouldRaiseError()
        {
            await Assert.ThrowsAsync<NotAuthorizedException>(async () =>
            {
                var session = _configuration["Session"];
                var code = "081394"; // Incorrect one-time password.
                var success = await CognitoMethods.VerifyTOTP(_client, session, code);
            });
        }

        [Fact]
        public async Task GetSecretForAppMFATest()
        {
            await Assert.ThrowsAsync<NotAuthorizedException>(async () =>
            {
                var session = await CognitoMethods.GetSecretForAppMFA(
                    _client,
                    _configuration["Session"]);
            });
        }

        [Fact]
        public async Task InitiateAuthTest_MadeUpValues_ShouldFail()
        {
            await Assert.ThrowsAsync<UserNotConfirmedException>(async () =>
            {
                var response = await CognitoMethods.InitiateAuth(
                    _client,
                    _configuration["ClientId"],
                    _configuration["UserName"],
                    _configuration["Password"]);
            });
        }

        [Fact]
        public async Task ConfirmSignUpTest_NonExistentUserName_ShouldRaiseError()
        {
            await Assert.ThrowsAsync<CodeMismatchException>(async () =>
            {
                var success = await CognitoMethods.ConfirmSignUp(
                    _client,
                    _configuration["ClientId"],
                    "abcde813", // incorrect confirmation code
                    _configuration["UserName"]);
            });
        }

        [Fact]
        public async Task GetAdminUserTest_ValidUser_ShouldSucceed()
        {
            var status = await CognitoMethods.GetAdminUser(_client,
                _configuration["UserName"],
                _configuration["PoolId"]);
            Assert.NotNull(status);

        }

        [Fact]
        public async Task GetAdminUserTest_UnknownUser_ShouldRaiseError()
        {
            await Assert.ThrowsAsync<UserNotFoundException>(async () =>
            {
                var status = await CognitoMethods.GetAdminUser(_client,
                    _configuration["UserName"],
                    _configuration["PoolId"]);
            });
        }

        [Fact]
        public async Task SignUpTest_NewUser_ShouldSucceed()
        {
            // Adding the millisecond value for the current time
            // to ensure that the user does not already exist.
            var userName = $"{_configuration["userName"]}{DateTime.Now.Millisecond}";
            var userSub = await CognitoMethods.SignUp(
                _client,
                _configuration["ClientId"],
                userName,
                _configuration["Password"],
                _configuration["UserEmail"]);
            Assert.NotNull(userSub);
        }
    }
}