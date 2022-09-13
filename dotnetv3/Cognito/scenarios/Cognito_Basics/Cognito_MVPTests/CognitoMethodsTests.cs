using Microsoft.VisualStudio.TestTools.UnitTesting;
using Cognito_MVP;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.CognitoIdentityProvider;

namespace Cognito_MVP.Tests
{
    [TestClass()]
    public class CognitoMethodsTests
    {
        private static AmazonCognitoIdentityProviderClient _CognitoClient;
        private static readonly string _UserName = "test-user";
        private static readonly string _ClientId = "test-client";

        CognitoMethodsTests()
        {
            _CognitoClient = new AmazonCognitoIdentityProviderClient();
        }

        [TestMethod()]
        public void AdminRespondToAuthChallengeTest()
        {
            
        }

        [TestMethod()]
        public void VerifyTOTPTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void GetSecretForAppMFATest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void InitiateAuthTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void ConfirmSignUpTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void resendConfirmationCodeTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void GetAdminUserTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void SignUpTest()
        {
            Assert.Fail();
        }
    }
}