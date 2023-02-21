// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[Cognito.dotnetv3.Main]
namespace CognitoBasics;

public class CognitoBasics
{
    private static ILogger logger = null!;

    static async Task Main(string[] args)
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
            .AddTransient<UiMethods>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<CognitoBasics>();

        var configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally load local settings.
            .Build();

        var cognitoWrapper = host.Services.GetRequiredService<CognitoWrapper>();
        var uiMethods = host.Services.GetRequiredService<UiMethods>();

        // clientId - The app client Id value that you get from the AWS CDK script.
        string clientId = configuration["ClientId"]; // "*** REPLACE WITH CLIENT ID VALUE FROM CDK SCRIPT";

        // poolId - The pool Id that you get from the AWS CDK script.
        string poolId = configuration["PoolId"]; // "*** REPLACE WITH POOL ID VALUE FROM CDK SCRIPT";
        var userName = configuration["UserName"];
        var password = configuration["Password"];
        var email = configuration["Email"];
        var userPoolId = configuration["UserPoolId"];

        // If the username wasn't set in the configuration file,
        // get it from the user now.
        if (userName is null)
        {
            do
            {
                Console.Write("Username: ");
                userName = Console.ReadLine();
            }
            while (string.IsNullOrEmpty(userName));
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
            }
            while (string.IsNullOrEmpty(password));
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

        uiMethods.DisplayTitle("Get confirmation code");
        Console.WriteLine($"Conformation code sent to {userName}.");
        Console.Write("Would you like to send a new code? (Yes/No) ");
        var answer = Console.ReadLine();

        if (answer.ToLower() == "YES")
        {
            await cognitoWrapper.ResendConfirmationCodeAsync(clientId, userName);
            Console.WriteLine("Sending a new confirmation code");
        }

        Console.Write("Enter confirmation code (from Email): ");
        string code = Console.ReadLine();

        await cognitoWrapper.ConfirmSignupAsync(clientId, code, userName);

        uiMethods.DisplayTitle("Checking status");
        Console.WriteLine($"Rechecking the status of {userName} in the user pool");
        await cognitoWrapper.GetAdminUserAsync(userName, poolId);

        var authResponse = await cognitoWrapper.InitiateAuthAsync(clientId, userName, password);
        var mySession = authResponse.Session;

        var newSession = await cognitoWrapper.AssociateSoftwareTokenAsync(mySession);

        Console.Write("Enter the 6-digit code displayed in Google Authenticator: ");
        string myCode = Console.ReadLine();

        // Verify the TOTP and register for MFA.
        await cognitoWrapper.GetAdminUserAsync(newSession, myCode);
        Console.Write("Re-enter the 6-digit code displayed in your authenticator");
        string mfaCode = Console.ReadLine();

        var session2 = await cognitoWrapper.AdminInitiateAuthAsync(clientId, userPoolId, userName, password);
        await cognitoWrapper.RespondToAuthChallengeAsync(userName, clientId, mfaCode, session2);
    }
}

// snippet-end:[Cognito.dotnetv3.Main]