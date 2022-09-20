// snippet-start:[cognito.dotnetv3.CognitoBasics.Main]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Before running this SDK for .NET (v3) code example, set up your development environment, including your credentials.
// For more information, see the following documentation:
// https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html
// TIP: To set up the required user pool, run the AWS CDK script provided in this GitHub repo at:
// resources/cdk/cognito_scenario_user_pool_with_mfa.
// This code example performs the following operations:
// 1. Invokes the signUp method to sign up a user.
// 2. Invokes the adminGetUser method to get the user's confirmation status.
// 3. Invokes the ResendConfirmationCode method if the user requested another code.
// 4. Invokes the confirmSignUp method.
// 5. Invokes the initiateAuth to sign in. This results in being prompted to set up TOTP (time-based one-time password). (The response is “ChallengeName”: “MFA_SETUP”).
// 6. Invokes the AssociateSoftwareToken method to generate a TOTP MFA private key. This can be used with Google Authenticator.
// 7. Invokes the VerifySoftwareToken method to verify the TOTP and register for MFA.
// 8. Invokes the AdminInitiateAuth to sign in again. This results in being prompted to submit a TOTP (Response: “ChallengeName”: “SOFTWARE_TOKEN_MFA”).
// 9. Invokes the AdminRespondToAuthChallenge to get back a token.

// Set the following variables:
// clientId - The app client Id value that you can get from the AWS CDK script.
string clientId = "1rq6e6s148h8loarorm5abmlr";

// poolId - The pool Id that you can get from the AWS CDK script.
string poolId = "us-west-2_uBJIO18xA";
var userName = string.Empty;
var password = string.Empty;
var email = string.Empty;

string sepBar = new string('-', 80);
var identityProviderClient = new AmazonCognitoIdentityProviderClient(RegionEndpoint.USWest2);

do
{
    Console.Write("Enter your user name: ");
    userName = Console.ReadLine();
}
while (userName == string.Empty);

Console.WriteLine($"User name: {userName}");

do
{
    Console.Write("Enter your password: ");
    password = Console.ReadLine();
}
while (password == string.Empty);
Console.WriteLine($"Signing up {userName}");

do
{
    Console.WriteLine("Enter your email");
    email = Console.ReadLine();
} while (email == string.Empty);

await CognitoMethods.SignUp(identityProviderClient, clientId, userName, password, email);

Console.WriteLine(sepBar);
Console.WriteLine($"Getting {userName} status from the user pool");
await CognitoMethods.GetAdminUser(identityProviderClient, userName, poolId);

Console.WriteLine(sepBar);
Console.WriteLine($"Conformation code sent to {userName}. Would you like to send a new code? (Yes/No)");
var ans = Console.ReadLine();

if (ans.ToUpper() == "YES")
{
    await CognitoMethods.ResendConfirmationCode(identityProviderClient, clientId, userName);
    Console.WriteLine("Sending a new confirmation code");
}

Console.WriteLine(sepBar);
Console.WriteLine("*** Enter confirmation code that was emailed");
string code = Console.ReadLine();

await CognitoMethods.ConfirmSignUp(identityProviderClient, clientId, code, userName);

Console.WriteLine($"Rechecking the status of {userName} in the user pool");
await CognitoMethods.GetAdminUser(identityProviderClient, userName, poolId);

var authResponse = await CognitoMethods.InitiateAuth(identityProviderClient, clientId, userName, password);
var mySession = authResponse.Session;

var newSession = await CognitoMethods.GetSecretForAppMFA(identityProviderClient, mySession);

Console.WriteLine("Enter the 6-digit code displayed in Google Authenticator");
string myCode = Console.ReadLine();

// Verify the TOTP and register for MFA.
await CognitoMethods.VerifyTOTP(identityProviderClient, newSession, myCode);
Console.WriteLine("Enter the new 6-digit code displayed in Google Authenticator");
string mfaCode = Console.ReadLine();

Console.WriteLine(sepBar);
var authResponse1 = await CognitoMethods.InitiateAuth(identityProviderClient, clientId, userName, password);
var session2 = authResponse1.Session;
await CognitoMethods.AdminRespondToAuthChallenge(identityProviderClient, userName, clientId, mfaCode, session2);

Console.WriteLine("The Cognito MVP application has completed.");

// snippet-end:[cognito.dotnetv3.CognitoBasics.Main]