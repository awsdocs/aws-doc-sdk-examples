// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CognitoActions;

/// <summary>
/// Methods to perform Amazon Cognito Identity Provider actions.
/// </summary>
public class CognitoWrapper
{
    private readonly IAmazonCognitoIdentityProvider _cognitoService;

    /// <summary>
    /// Constructor for the wrapper class containing Amazon Cognito actions.
    /// </summary>
    /// <param name="cognitoService"></param>
    public CognitoWrapper(IAmazonCognitoIdentityProvider cognitoService)
    {
        _cognitoService = cognitoService;
    }

    // snippet-start:[Cognito.dotnetv3.ListUserPools]
    /// <summary>
    /// List the Amazon Cognito user pools for an account.
    /// </summary>
    /// <returns>A list of UserPoolDescriptionType objects.</returns>
    public async Task<List<UserPoolDescriptionType>> ListUserPoolsAsync()
    {
        var response = new ListUserPoolsResponse();
        var request = new ListUserPoolsRequest { MaxResults = 10 };
        var userPools = new List<UserPoolDescriptionType>();

        do
        {
            response = await _cognitoService.ListUserPoolsAsync(request);

            if (response.UserPools.Count > 0)
            {
                userPools.AddRange(response.UserPools);
            }

            request.NextToken = response.NextToken;
        }
        while (response.NextToken is not null);

        return userPools;
    }

    // snippet-end:[Cognito.dotnetv3.ListUserPools]

    // snippet-start:[Cognito.dotnetv3.ListUsers]
    /// <summary>
    /// Get a list of users for the Amazon Cognito user pool.
    /// </summary>
    /// <param name="userPoolId">The user pool Id.</param>
    /// <returns>A list of users.</returns>
    public async Task<List<UserType>> ListUsersAsync(string userPoolId)
    {
        var request = new ListUsersRequest
        {
            UserPoolId = userPoolId
        };

        var response = new ListUsersResponse();
        var users = new List<UserType>();

        do
        {
            response = await _cognitoService.ListUsersAsync(request);
            users.AddRange(response.Users);
            request.PaginationToken = response.PaginationToken;
        }
        while (response.PaginationToken is not null);

        return users;
    }

    // snippet-end:[Cognito.dotnetv3.ListUsers]

    // snippet-start:[Cognito.dotnetv3.AdminRespondToAuthChallenge]
    public async Task<AuthenticationResultType> AdminRespondToAuthChallengeAsync(string userPoolId, string userName, string clientId, string mfaCode, string session)
    {
        var challengeResponses = new Dictionary<string, string>();
        challengeResponses.Add("USERNAME", userName);
        challengeResponses.Add("SOFTWARE_TOKEN_MFA_CODE", mfaCode);

        var request = new AdminRespondToAuthChallengeRequest
        {
            ClientId = clientId,
            UserPoolId = userPoolId,
            ChallengeResponses = challengeResponses,
            Session = session
        };

        var response = await _cognitoService.AdminRespondToAuthChallengeAsync(request);
        return response.AuthenticationResult;
    }

    // snippet-end:[Cognito.dotnetv3.AdminRespondToAuthChallenge]

    // snippet-start:[Cognito.dotnetv3.RespondToAuthChallenge]
    /// <summary>
    /// Respond to an authentication challenge.
    /// </summary>
    /// <param name="userName">The name of the user.</param>
    /// <param name="clientId">The client Id</param>
    /// <param name="mfaCode">The multi-factor authentication code.</param>
    /// <param name="session">The current application session.</param>
    /// <returns>An async Task</returns>
    public async Task RespondToAuthChallengeAsync(string userName, string clientId, string mfaCode, string session)
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
            Session = session
        };

        var respondToAuthChallengeResult = await _cognitoService.RespondToAuthChallengeAsync(respondToAuthChallengeRequest);
        Console.WriteLine("respondToAuthChallengeResult.getAuthenticationResult()" + respondToAuthChallengeResult.AuthenticationResult);
    }

    // snippet-end:[Cognito.dotnetv3.RespondToAuthChallenge]

    // snippet-start:[Cognito.dotnetv3.VerifySoftwareToken]
    /// <summary>
    /// Verify the TOTP and register for MFA.
    /// </summary>
    /// <param name="session"></param>
    /// <param name="code"></param>
    /// <returns>The status of the software token.</returns>
    public async Task<VerifySoftwareTokenResponseType> VerifySoftwareTokenAsync(string session, string code)
    {
        var tokenRequest = new VerifySoftwareTokenRequest
        {
            UserCode = code,
            Session = session,
        };

        var verifyResponse = await _cognitoService.VerifySoftwareTokenAsync(tokenRequest);

        return verifyResponse.Status;
    }

    // snippet-end:[Cognito.dotnetv3.VerifySoftwareToken]

    // snippet-start:[Cognito.dotnetv3.AssociateSoftwareToken]
    /// <summary>
    /// Get an MFA token to authenticate the user with the Google Authenticator.
    /// </summary>
    /// <param name="session"></param>
    /// <returns></returns>
    public async Task<string> GetMFATokenAsync(string session)
    {
        var softwareTokenRequest = new AssociateSoftwareTokenRequest
        {
            Session = session,
        };

        var tokenResponse = await _cognitoService.AssociateSoftwareTokenAsync(softwareTokenRequest);
        var secretCode = tokenResponse.SecretCode;

        Console.Write("Enter the following token into Google Authenticator: {secretCode}");

        return tokenResponse.Session;
    }

    // snippet-end:[Cognito.dotnetv3.AssociateSoftwareToken]

    // snippet-start:[Cognito.dotnetv3.AdminInitiateAuth]
    public async Task<string> AdminInitiateAuthAsync(string clientId, string userPoolId, string userName, string password)
    {
        var authParameters = new Dictionary<string, string>();
        authParameters.Add("USERNAME", userName);
        authParameters.Add("PASSWORD", password);

        var request = new AdminInitiateAuthRequest
        {
            ClientId = clientId,
            UserPoolId = userPoolId,
            AuthParameters = authParameters,
            AuthFlow = AuthFlowType.USER_PASSWORD_AUTH,
        };

        var response = await _cognitoService.AdminInitiateAuthAsync(request);
        return response.Session;
    }
    // snippet-end:[Cognito.dotnetv3.AdminInitiateAuth]

    // snippet-start:[Cognito.dotnetv3.InitiateAuth]
    /// <summary>
    /// Initiate authorization.
    /// </summary>
    /// <param name="clientId">The client Id of the application.</param>
    /// <param name="userName">The name of the user who is authenticating.</param>
    /// <param name="password">The password for the user who is authenticating.</param>
    /// <returns>The response from the call to InitiateAuthAsync</returns>
    public async Task<InitiateAuthResponse> InitiateAuthAsync(string clientId, string userName, string password)
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

        var response = await _cognitoService.InitiateAuthAsync(authRequest);
        Console.WriteLine($"Result Challenge is : {response.ChallengeName}");

        return response;
    }

    // snippet-end:[Cognito.dotnetv3.InitiateAuth]

    // snippet-start:[Cognito.dotnetv3.ConfirmSignUp]
    /// <summary>
    /// Confirm that the user has signed up.
    /// </summary>
    /// <param name="clientId">The Id of this application.</param>
    /// <param name="code">The confirmation code sent to the user.</param>
    /// <param name="userName">The user name.</param>
    /// <returns></returns>
    public async Task ConfirmSignupAsync(string clientId, string code, string userName)
    {
        var signUpRequest = new ConfirmSignUpRequest
        {
            ClientId = clientId,
            ConfirmationCode = code,
            Username = userName,
        };

        await _cognitoService.ConfirmSignUpAsync(signUpRequest);
        Console.WriteLine($"{userName} was confirmed");
    }

    // snippet-end:[Cognito.dotnetv3.ConfirmSignUp]

    // snippet-start:[Cognito.dotnetv3.ConfirmDevice]
    /// <summary>
    /// Initiates and confirms tracking of the device.
    /// </summary>
    /// <param name="accessToken">The user's access token.</param>
    /// <param name="deviceKey">The key of the device from Amazon Cognito.</param>
    /// <param name="deviceName">The device name.</param>
    /// <returns></returns>
    public async Task<bool> ConfirmDeviceAsync(string accessToken, string deviceKey, string deviceName)
    {
        var request = new ConfirmDeviceRequest
        {
            AccessToken = accessToken,
            DeviceKey = deviceKey,
            DeviceName = deviceName
        };

        var response = await _cognitoService.ConfirmDeviceAsync(request);
        return response.UserConfirmationNecessary;
    }

    // snippet-end:[Cognito.dotnetv3.ConfirmDevice]

    // snippet-start:[Cognito.dotnetv3.ResendConfirmationCode]
    /// <summary>
    /// Send a new confirmation code to a user.
    /// </summary>
    /// <param name="clientId">The Id of the client application.</param>
    /// <param name="userName">The user name of user who will receive the code.</param>
    /// <returns></returns>
    public async Task ResendConfirmationCodeAsyc(string clientId, string userName)
    {
        var codeRequest = new ResendConfirmationCodeRequest
        {
            ClientId = clientId,
            Username = userName,
        };

        var response = await _cognitoService.ResendConfirmationCodeAsync(codeRequest);

        Console.WriteLine($"Method of delivery is {response.CodeDeliveryDetails.DeliveryMedium}");
    }

    // snippet-end:[Cognito.dotnetv3.ResendConfirmationCode]

    // snippet-start:[Cognito.dotnetv3.GetAdminUser]
    /// <summary>
    /// Get the specified user from an Amazon Cognito user pool with administrator access.
    /// </summary>
    /// <param name="userName">The name of the user.</param>
    /// <param name="poolId">The Id of the Amazon Cognito user pool.</param>
    /// <returns></returns>
    public async Task GetAdminUserAsync(string userName, string poolId)
    {
        AdminGetUserRequest userRequest = new AdminGetUserRequest
        {
            Username = userName,
            UserPoolId = poolId,
        };

        var response = await _cognitoService.AdminGetUserAsync(userRequest);

        Console.WriteLine($"User status {response.UserStatus}");
    }

    // snippet-end:[Cognito.dotnetv3.GetAdminUser]

    // snippet-start:[Cognito.dotnetv3.SignUp]
    /// <summary>
    /// Sign up a new user.
    /// </summary>
    /// <param name="clientId">The client Id of the application.</param>
    /// <param name="userName">The user name to use.</param>
    /// <param name="password">The user's password.</param>
    /// <param name="email">The email address of the user.</param>
    /// <returns>A Boolean value indicating whether the user was confirmed.</returns>
    public async Task<bool> SignUpAsync(string clientId, string userName, string password, String email)
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

        var response = await _cognitoService.SignUpAsync(signUpRequest);
        return response.UserConfirmed;
    }

    // snippet-end:[Cognito.dotnetv3.SignUp]
}