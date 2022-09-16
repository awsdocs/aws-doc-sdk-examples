// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Cognito_MVP.Tests
{
    public class CognitoMethodsTests
    {
        private static AmazonCognitoIdentityProviderClient _Client = new AmazonCognitoIdentityProviderClient();
        private static readonly string _UserName = "test-user";
        private static readonly string _ClientId = "some-client-id";
        private static string _Session = "";
        private static string _MfaCode = "";

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
            
        }

        [Fact]
        [Order(5)]
        public void InitiateAuthTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact]
        [Order(4)]
        public void ConfirmSignUpTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact]
        [Order(3)]
        public void ResendConfirmationCodeTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact]
        [Order(2)]
        public void GetAdminUserTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact]
        [Order(1)]
        public void SignUpTest()
        {
            Assert.True(false, "This test needs an implementation");
        }
    }
}