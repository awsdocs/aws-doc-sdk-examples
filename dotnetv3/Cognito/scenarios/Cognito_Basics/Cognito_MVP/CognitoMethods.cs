// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Cognito_MVP
{
    public class CognitoMethods
    {
        /// <summary>
        /// Responds to an authentication challenge for an Amazon Cognito user.
        /// </summary>
        /// <param name="identityProviderClient">The Amazon Cognito client object.</param>
        /// <param name="userName">The user name of the user to authenticate.</param>
        /// <param name="clientId">The client Id of the Amazon Cognito user pool.</param>
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

            var challengeResponses = new Dictionary<string, string>();
            challengeResponses.Add("USERNAME", userName);
            challengeResponses.Add("SOFTWARE_TOKEN_MFA_CODE", mfaCode);

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

        public static async Task<string> GetSecretForAppMFA(AmazonCognitoIdentityProviderClient identityProviderClient, string session)
        {
            var softwareTokenRequest = new AssociateSoftwareTokenRequest
            {
                Session = session,
            };

            var tokenResponse = await identityProviderClient.AssociateSoftwareTokenAsync(softwareTokenRequest);
            var secretCode = tokenResponse.SecretCode;

            Console.WriteLine("Enter the following token into Google Authenticator");
            Console.WriteLine(secretCode);

            return tokenResponse.Session;
        }

        public static async Task<InitiateAuthResponse> InitiateAuth(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string userName, string password)
        {
            var authParameters = new Dictionary<string, string>();
            authParameters.Add("USERNAME", userName);
            authParameters.Add("PASSWORD", password);

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

        public static async Task<bool> ConfirmSignUp(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string code, string userName)
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

        public static async Task resendConfirmationCode(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string userName)
        {
            var codeRequest = new ResendConfirmationCodeRequest
            {
                ClientId = clientId,
                Username = userName,
            };

            var response = await identityProviderClient.ResendConfirmationCodeAsync(codeRequest);

            Console.WriteLine($"Method of delivery is {response.CodeDeliveryDetails.DeliveryMedium}");
        }

        public static async Task GetAdminUser(AmazonCognitoIdentityProviderClient identityProviderClient, string userName, string poolId)
        {
            AdminGetUserRequest userRequest = new AdminGetUserRequest
            {
                Username = userName,
                UserPoolId = poolId,
            };

            var response = await identityProviderClient.AdminGetUserAsync(userRequest);

            Console.WriteLine($"User status {response.UserStatus}");
        }

        public static async Task SignUp(AmazonCognitoIdentityProviderClient identityProviderClient, string clientId, string userName, string password, String email)
        {
            var userAttrs = new AttributeType
            {
                Name = "email",
                Value = email,
            };

            var userAttrsList = new List<AttributeType>();

            userAttrsList.Add(userAttrs);

            var signUpRequest = new SignUpRequest
            {
                UserAttributes = userAttrsList,
                Username = userName,
                ClientId = clientId,
                Password = password
            };

            await identityProviderClient.SignUpAsync(signUpRequest);
            Console.WriteLine("User has been signed up ");
        }
    }
}
