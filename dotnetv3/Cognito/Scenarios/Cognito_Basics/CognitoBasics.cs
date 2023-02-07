// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[Cognito.dotnetv3.Main]
namespace CognitoBasics;

public class CognitoBasics
{
    private static ILogger logger = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
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
            .AddJsonFile("settings.json") // Load test settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally load local settings.
            .Build();

        var cognitoWrapper = host.Services.GetRequiredService<CognitoWrapper>();
        var uiMethods = host.Services.GetRequiredService<UiMethods>();

        // clientId - The app client Id value that you can get from the AWS CDK script.
        string clientId = "29g6vl2hkmlo91ni1rfvm9ggqd"; // "*** REPLACE WITH POOL ID VALUE FROM CDK SCRIPT";

        // poolId - The pool Id that you can get from the AWS CDK script.
        string poolId = "us-west-2_zffFiT8i6"; // "*** REPLACE WITH POOL ID VALUE FROM CDK SCRIPT";
        var userName = string.Empty;
        var password = string.Empty;
        var email = string.Empty;

        do
        {
            Console.Write("User name: ");
            userName = Console.ReadLine();
        }
        while (userName == string.Empty);

        Console.WriteLine($"\nUser name: {userName}");

        do
        {
            Console.Write("Password: ");
            password = Console.ReadLine();
        }
        while (password == string.Empty);
        Console.WriteLine($"\bSigning up {userName}...");

        do
        {
            Console.Write("Email: ");
            email = Console.ReadLine();
        } while (email == string.Empty);

        await cognitoWrapper.SignUpAsync(clientId, userName, password, email);

        Console.WriteLine($"Adding {userName} to the user pool");
        await cognitoWrapper.GetAdminUserAsync(userName, poolId);

        uiMethods.DisplayTitle("Get confirmation code");
        Console.WriteLine($"Conformation code sent to {userName}.");
        Console.Write("Would you like to send a new code? (Yes/No) ");
        var answer = Console.ReadLine();

        if (answer.ToLower() == "YES")
        {
            await cognitoWrapper.ResendConfirmationCodeAsyc(clientId, userName);
            Console.WriteLine("Sending a new confirmation code");
        }

        Console.Write("Confirmation code (from Email): ");
        string code = Console.ReadLine();

        await cognitoWrapper.ConfirmSignupAsync(clientId, code, userName);

        uiMethods.DisplayTitle("Checking status");
        Console.WriteLine($"Rechecking the status of {userName} in the user pool");
        await cognitoWrapper.GetAdminUserAsync(userName, poolId);

        var authResponse = await cognitoWrapper.InitiateAuthAsync(clientId, userName, password);
        var mySession = authResponse.Session;

        var newSession = await cognitoWrapper.GetMFATokenAsync(mySession);

        Console.Write("Enter the 6-digit code displayed in Google Authenticator: ");
        string myCode = Console.ReadLine();

        // Verify the TOTP and register for MFA.
        await cognitoWrapper.GetAdminUserAsync(newSession, myCode);
        Console.Write("Re-enter the 6-digit code displayed in Google Authenticator");
        string mfaCode = Console.ReadLine();

        var authResponse1 = await cognitoWrapper.InitiateAuthAsync(clientId, userName, password);
        var session2 = authResponse1.Session;
        await cognitoWrapper.RespondToAuthChallengeAsync(userName, clientId, mfaCode, session2);
    }
}

// snippet-end:[Cognito.dotnetv3.Main]