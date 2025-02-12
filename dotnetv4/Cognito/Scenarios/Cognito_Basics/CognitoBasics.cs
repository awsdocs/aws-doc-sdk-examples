// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[Cognito.dotnetv4.Main]

using LogLevel = Microsoft.Extensions.Logging.LogLevel;

namespace CognitoBasics;

public static class CognitoBasics
{
    public static bool _interactive = true;

    public static async Task Main(string[] args)
    {
        // Set up dependency injection for Amazon Cognito.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonCognitoIdentityProvider>()
                    .AddTransient<CognitoWrapper>()
            )
            .Build(); ;

        var configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally load local settings.
            .Build();

        var cognitoWrapper = host.Services.GetRequiredService<CognitoWrapper>();

        await RunScenario(cognitoWrapper, configuration);
    }

    /// <summary>
    /// Run the example scenario.
    /// </summary>
    /// <param name="cognitoWrapper">Wrapper for service actions.</param>
    /// <param name="configuration">Scenario configuration.</param>
    /// <returns></returns>
    public static async Task<bool> RunScenario(CognitoWrapper cognitoWrapper, IConfigurationRoot configuration)
    {
        Console.WriteLine(new string('-', 80));
        UiMethods.DisplayOverview();
        Console.WriteLine(new string('-', 80));

        // clientId - The app client Id value that you get from the AWS CDK script.
        var clientId =
            configuration[
                "ClientId"]; // "*** REPLACE WITH CLIENT ID VALUE FROM CDK SCRIPT";

        // poolId - The pool Id that you get from the AWS CDK script.
        var poolId =
            configuration["PoolId"]!; // "*** REPLACE WITH POOL ID VALUE FROM CDK SCRIPT";
        var userName = configuration["UserName"];
        var password = configuration["Password"];
        var email = configuration["Email"];

        // If the username wasn't set in the configuration file,
        // get it from the user now.
        if (userName is null)
        {
            do
            {
                Console.Write("Username: ");
                userName = Console.ReadLine();
            } while (string.IsNullOrEmpty(userName));
        }

        Console.WriteLine($"\nUsername: {userName}");

        // If the password wasn't set in the configuration file,
        // get it from the user now.
        if (password is null)
        {
            do
            {
                Console.Write("Password: ");
                password = Console.ReadLine();
            } while (string.IsNullOrEmpty(password));
        }

        // If the email address wasn't set in the configuration file,
        // get it from the user now.
        if (email is null)
        {
            do
            {
                Console.Write("Email: ");
                email = Console.ReadLine();
            } while (string.IsNullOrEmpty(email));
        }

        // Now sign up the user.
        Console.WriteLine($"\nSigning up {userName} with email address: {email}");
        await cognitoWrapper.SignUpAsync(clientId, userName, password, email);

        // Add the user to the user pool.
        Console.WriteLine($"Adding {userName} to the user pool");
        await cognitoWrapper.GetAdminUserAsync(userName, poolId);

        UiMethods.DisplayTitle("Get confirmation code");
        Console.WriteLine($"Conformation code sent to {userName}.");

        Console.Write("Would you like to send a new code? (Y/N) ");
        var answer = _interactive ? Console.ReadLine() : "y";

        if (answer!.ToLower() == "y")
        {
            await cognitoWrapper.ResendConfirmationCodeAsync(clientId, userName);
            Console.WriteLine("Sending a new confirmation code");
        }

        Console.Write("Enter confirmation code (from Email): ");
        var code = _interactive ? Console.ReadLine() : "-";

        await cognitoWrapper.ConfirmSignupAsync(clientId, code, userName);


        UiMethods.DisplayTitle("Checking status");
        Console.WriteLine($"Rechecking the status of {userName} in the user pool");
        await cognitoWrapper.GetAdminUserAsync(userName, poolId);

        Console.WriteLine($"Setting up authenticator for {userName} in the user pool");
        var setupResponse = await cognitoWrapper.InitiateAuthAsync(clientId, userName, password);

        var setupSession = await cognitoWrapper.AssociateSoftwareTokenAsync(setupResponse.Session);
        Console.Write("Enter the 6-digit code displayed in Google Authenticator: ");
        var setupCode = _interactive ? Console.ReadLine() : "-";
        var setupResult =
            await cognitoWrapper.VerifySoftwareTokenAsync(setupSession, setupCode);
        Console.WriteLine($"Setup status: {setupResult}");

        Console.WriteLine($"Now logging in {userName} in the user pool");
        var authSession =
            await cognitoWrapper.AdminInitiateAuthAsync(clientId, poolId, userName,
                password);

        Console.Write("Enter a new 6-digit code displayed in Google Authenticator: ");
        var authCode = _interactive ? Console.ReadLine() : "-";
        var authResult =
            await cognitoWrapper.AdminRespondToAuthChallengeAsync(userName, clientId,
                authCode, authSession, poolId);
        Console.WriteLine(
            $"Authenticated and received access token: {authResult.AccessToken}");


        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Cognito scenario is complete.");
        Console.WriteLine(new string('-', 80));
        return true;
    }
}

// snippet-end:[Cognito.dotnetv4.Main]