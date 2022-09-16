// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Cognito_MVP
{
    /// <summary>
    /// Class whose members perform Amazon Cognit methods for the scenario.
    /// </summary>
    public class CognitoMethods
    {
        /// <summary>
        /// Responds to an authentication challenge for an Amazon Cognito user.
        /// </summary>
        /// <param name="identityProviderClient">The Amazon Cognito client object.</param>
        /// <param name="userName">The user name of the user to authenticate.</param>
        /// <param name="clientId">The client Id of the application associated
        /// with the user pool.</param>
        /// <param name="mfaCode">The MFA code suppplied by the user.</param>
        /// <param name="session">The session for which the user will be authenticated.</param>
        /// <returns>A Boolean value that indicates the success of the authentication.</returns>
        public static async Task<bool> AdminRespondToAuthChallenge(
            AmazonCognitoIdentityProviderClient identityProviderClient,
            string userName,
            string clientId,
            string mfaCode,
            string session)
        {
            Console.WriteLine("SOFTWARE_TOKEN_MFA challenge is generated");

            var challengeResponses = new Dictionary<string, string>
            {
                { "USERNAME", userName },
                { "SOFTWARE_TOKEN_MFA_CODE", mfaCode },
            };

            var respondToAuthChallengeRequest = new RespondToAuthChallengeRequest
            {
                ChallengeName = ChallengeNameType.SOFTWARE_TOKEN_MFA,
                ClientId = clientId,
                ChallengeResponses = challengeResponses,
                Session = session,
            };

            var response = await identityProviderClient.RespondToAuthChallengeAsync(respondToAuthChallengeRequest);
            Console.WriteLine($"response.getAuthenticationResult() {response.AuthenticationResult}");

            if (response.AuthenticationResult is not null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Verifies that the user has supplied the correct one-time password
        /// and registers for multi-factor authentication (MFA).
        /// </summary>
        /// <param name="identityProviderClient">The name of the identity
        /// provider used to authenticate the user.</param>
        /// <param name="session">The ssession for which the user will be
        /// authenticated.</param>
        /// <param name="code">The code provided by the user.</param>
        /// <returns>A Boolean value that indicates the success of the authentication.</returns>
        public static async Task<bool> VerifyTOTP(
            AmazonCognitoIdentityProviderClient identityProviderClient,
            string session,
            string code)
        {
            var tokenRequest = new VerifySoftwareTokenRequest
            {
                UserCode = code,
                Session = session,
            };

            var response = await identityProviderClient.VerifySoftwareTokenAsync(tokenRequest);

            Console.WriteLine($"The status of the token is {response.Status}");

            if (response.Status == VerifySoftwareTokenResponseType.SUCCESS)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Gets the secret token that will enable MFA (Multi-Factor
        /// Authentication) for the user.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="session">The currently active session.</param>
        /// <returns>Returns a string representing the currently active
        /// session.</returns>
        public static async Task<string> GetSecretForAppMFA(
            AmazonCognitoIdentityProviderClient identityProviderClient,
            string session)
        {
            var softwareTokenRequest = new AssociateSoftwareTokenRequest
            {
                Session = session,
            };

            var tokenResponse = await identityProviderClient.AssociateSoftwareTokenAsync(softwareTokenRequest);
            var secretCode = tokenResponse.SecretCode;

            Console.WriteLine($"Enter the following token into Google Authenticator: {secretCode}");

            return tokenResponse.Session;
        }

        /// <summary>
        /// Initiates the authorization process.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="clientId">The client Id of the application associated
        /// with the user pool.</param>
        /// <param name="userName">The user name to be authorized.</param>
        /// <param name="password">The password of the user.</param>
        /// <returns>The responsse from the client from the InitiateAuthAsync
        /// call.</returns>
        public static async Task<InitiateAuthResponse> InitiateAuth(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string userName, string password)
        {
            var authParameters = new Dictionary<string, string>
            {
                { "USERNAME", userName },
                { "PASSWORD", password },
            };

            var authRequest = new InitiateAuthRequest
            {
                ClientId = clientId,
                AuthParameters = authParameters,
                AuthFlow = AuthFlowType.USER_PASSWORD_AUTH,
            };

            var response = await identityProviderClient.InitiateAuthAsync(authRequest);
            Console.WriteLine($"Result Challenge is : {response.ChallengeName}");

            return response;
        }

        /// <summary>
        /// Confirms that a user has been signed up successfully.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="clientId">The client Id of the application associated
        /// with the user pool.</param>
        /// <param name="code">The code sent by the authentication provider
        /// to confirm a user's membership in the pool.</param>
        /// <param name="userName">The user to confirm.</param>
        /// <returns>A Boolean value indicating the success of the confirmation
        /// operation.</returns>
        public static async Task<bool> ConfirmSignUp(
            AmazonCognitoIdentityProviderClient identityProviderClient,
            string clientId,
            string code,
            string userName)
        {
            var signUpRequest = new ConfirmSignUpRequest
            {
                ClientId = clientId,
                ConfirmationCode = code,
                Username = userName,
            };

            var response = await identityProviderClient.ConfirmSignUpAsync(signUpRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{userName} was confirmed");
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Causes the confirmation code for user registration to be sent
        /// again.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="clientId">The client Id of the application associated
        /// with the user pool.</param>
        /// <param name="userName">The user name to be confirmed.</param>
        /// <returns></returns>
        public static async Task ResendConfirmationCode(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string userName)
        {
            var codeRequest = new ResendConfirmationCodeRequest
            {
                ClientId = clientId,
                Username = userName,
            };

            var response = await identityProviderClient.ResendConfirmationCodeAsync(codeRequest);

            Console.WriteLine($"Method of delivery is {response.CodeDeliveryDetails.DeliveryMedium}");
        }

        /// <summary>
        /// Checks the status of a user for a paarticular Amazon Cognito user
        /// pool.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="userName">The user name for which we want to check
        /// the status.</param>
        /// <param name="poolId">The user pool for which we want to check the
        /// user's status.</param>
        /// <returns></returns>
        public static async Task GetAdminUser(AmazonCognitoIdentityProviderClient identityProviderClient, string userName, string poolId)
        {
            var userRequest = new AdminGetUserRequest
            {
                Username = userName,
                UserPoolId = poolId,
            };

            var response = await identityProviderClient.AdminGetUserAsync(userRequest);

            Console.WriteLine($"User status {response.UserStatus}");
        }

        /// <summary>
        /// Add a new user to an Amazon Cognito user pool.
        /// </summary>
        /// <param name="identityProviderClient">An initialized Identity
        /// Provider client object.</param>
        /// <param name="clientId">The client Id of the application associated
        /// with the user pool.</param>
        /// <param name="userName">The user name of the user to sign up.</param>
        /// <param name="password">The password for the user.</param>
        /// <param name="email">The user's email address.</param>
        /// <returns></returns>
        public static async Task SignUp(
            AmazonCognitoIdentityProviderClient identityProviderClient,
            string clientId,
            string userName,
            string password,
            string email)
        {
            var userAttrs = new AttributeType
            {
                Name = "email",
                Value = email,
            };

            var userAttrsList = new List<AttributeType>
            {
                userAttrs,
            };

            var signUpRequest = new SignUpRequest
            {
                UserAttributes = userAttrsList,
                Username = userName,
                ClientId = clientId,
                Password = password,
            };

            await identityProviderClient.SignUpAsync(signUpRequest);
            Console.WriteLine("User has been signed up.");
        }
    }
}
