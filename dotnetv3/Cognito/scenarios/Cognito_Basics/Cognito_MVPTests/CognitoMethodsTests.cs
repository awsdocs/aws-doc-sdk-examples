// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Cognito_MVP.Tests
{
    public class CognitoMethodsTests
    {
        private static readonly AmazonCognitoIdentityProviderClient _Client = new AmazonCognitoIdentityProviderClient();
        private static readonly string _UserName = "test-user";
        private static readonly string _Password = "really-bad-password";
        private static readonly string _UserEmail = "someone@example.com";
        private static readonly string _ClientId = "some-client-id";

        private static readonly string _PoolId = "";
        private static string _Session = string.Empty;
        private static string _MfaCode = string.Empty;

        [Fact]
        [Order(1)]
        public static async Task AdminRespondToAuthChallengeTest()
        {
            var success = await CognitoMethods.AdminRespondToAuthChallenge(_Client, _UserName, _ClientId, _MfaCode, _Session);
            Assert.True(success, "Challenge failed to authenticate user.");
        }

        [Fact]
        [Order(1)]
        public static async Task AdminRespondToAuthChallengeTest_WithNonsense_ShouldFail()
        {
            var userName = "test-user";
            var clientId = "some-client-id";
            var mfaCode = "abcdefg-etc";
            var session = "not a session";
            var success = await CognitoMethods.AdminRespondToAuthChallenge(_Client, userName, clientId, mfaCode, session);
            Assert.False(success, "Challenge should fail with bad information.");
        }

        [Fact]
        [Order(7)]
        public static async Task VerifyTOTPTest()
        {
            var code = "";
            var success = await CognitoMethods.VerifyTOTP(_Client, _Session, code);
            Assert.True(success, "Could not verify the one-time password.");
        }

        [Fact]
        [Order(6)]
        public static async Task GetSecretForAppMFATest()
        {
            var session = await CognitoMethods.GetSecretForAppMFA(_Client, _Session);
            Assert.Equal(_Session, session, true);
        }

        [Fact]
        [Order(5)]
        public static async Task InitiateAuthTest()
        {
            var response = await CognitoMethods.InitiateAuth(_Client, _ClientId, _UserName, _Password);
            Assert.NotNull(response.Session);
        }

        [Fact]
        [Order(4)]
        public static async Task ConfirmSignUpTest()
        {
            var success = await CognitoMethods.ConfirmSignUp(_Client, _ClientId, _UserName, _Password);
            Assert.True(success, "Couldn't confirm the user's signup status.");
        }

        [Fact]
        [Order(3)]
        public static async Task ResendConfirmationCodeTest()
        {
            await CognitoMethods.ResendConfirmationCode(_Client, _ClientId, _UserName);
        }

        [Fact]
        [Order(2)]
        public static async Task GetAdminUserTest()
        {
            await CognitoMethods.GetAdminUser(_Client, _UserName, _PoolId);
        }

        [Fact]
        [Order(1)]
        public static async Task SignUpTest()
        {
            await CognitoMethods.SignUp(_Client, _ClientId, _UserName, _Password, _UserEmail);
        }
    }
}