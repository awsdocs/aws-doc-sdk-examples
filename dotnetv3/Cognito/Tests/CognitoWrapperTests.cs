// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.CognitoIdentityProvider;
using Microsoft.Extensions.Configuration;

namespace CognitoWrapperTests
{
    public class CognitoWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonCognitoIdentityProvider _client;
        private readonly CognitoWrapper _wrapper;
        private readonly string _userName;
        private readonly string _password;
        private readonly string _email;
        private readonly string _clientId;
        private readonly string _userPoolId;
        private static string _mfaToken;
        private static string _session;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public CognitoWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _client = new AmazonCognitoIdentityProviderClient();
            _wrapper = new CognitoWrapper(_client);

            _userName = _configuration["UserName"];
            _email = _configuration["Email"];
            _password = _configuration["Password"];
            _clientId = _configuration["ClientId"];
            _userPoolId = _configuration["UserPoolId"];
        }

        [Fact(Skip = "Requires user intervention.")]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task SignUpAsyncTest()
        {
            var success = await _wrapper.SignUpAsync(_clientId, _userName, _password, _email);
            Assert.True(success);
        }

        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task ListUserPoolsAsyncTest()
        {
            var userPools = await _wrapper.ListUserPoolsAsync();
            Assert.NotNull(userPools);
        }

        [Fact(Skip = "Requires user intervention.")]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task GetAdminUserAsyncTest()
        {
            var userStatus = await _wrapper.GetAdminUserAsync(_userName, _userPoolId);
            Assert.Equal(userStatus, UserStatusType.CONFIRMED);
        }

        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task ResendConfirmationCodeAsyncTest()
        {
            var codeDeliveryDetails = await _wrapper.ResendConfirmationCodeAsync(_clientId, _userName);
            Assert.Equal(DeliveryMediumType.EMAIL, codeDeliveryDetails.DeliveryMedium);
        }

        [Fact(Skip = "Requires confirmation code.")]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task ConfirmSignupAsyncTest()
        {
            var success = await _wrapper.ConfirmSignupAsync(_clientId, _mfaToken, _userName);
            Assert.True(success);
        }

        [Fact(Skip = "Requires user intervention.")]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task InitiateAuthAsyncTest()
        {
            var response = await _wrapper.InitiateAuthAsync(_clientId, _userName, _password);
            _session = response.Session;
            Assert.NotNull(_session);
        }

        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task ListUsersAsyncTest()
        {
            var users = await _wrapper.ListUsersAsync(_userPoolId);
            Assert.NotNull(users);
        }

        [Fact(Skip = "Requires token.")]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task VerifySoftwareTokenAsyncTest()
        {
            var verifyTokenResponse = await _wrapper.VerifySoftwareTokenAsync(_session, _mfaToken);
            Assert.Equal(verifyTokenResponse, VerifySoftwareTokenResponseType.SUCCESS);
        }

        [Fact(Skip = "Requires token.")]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task AssociateSoftwareTokenAsyncTest()
        {
            var newSession = await _wrapper.AssociateSoftwareTokenAsync(_session);
            Assert.NotNull(newSession);
        }

    }
}