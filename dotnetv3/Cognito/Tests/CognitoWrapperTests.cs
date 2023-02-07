// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Configuration;

namespace SupportTests
{
    public class CognitoWrapperTests
    {
        private readonly IConfiguration _configuration;

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
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void CognitoWrapperTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void ListUserPoolsAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void ListUsersAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void RespondToAuthChallengeAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void VerifySoftwareTokenAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void GetMFATokenAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void InitiateAuthAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void ConfirmSignupAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void ConfirmDeviceAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void ResendConfirmationCodeAsycTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void GetAdminUserAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public void SignUpAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }
    }
}