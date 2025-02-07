// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[Cognito.dotnetv4.UIMethods]
namespace CognitoBasics;

/// <summary>
/// Some useful methods to make screen display easier.
/// </summary>
public static class UiMethods
{
    /// <summary>
    /// Show information about the scenario.
    /// </summary>
    public static void DisplayOverview()
    {
        DisplayTitle("Welcome to the Amazon Cognito Demo");

        Console.WriteLine("This example application does the following:");
        Console.WriteLine("\t 1. Signs up a user.");
        Console.WriteLine("\t 2. Gets the user's confirmation status.");
        Console.WriteLine("\t 3. Resends the confirmation code if the user requested another code.");
        Console.WriteLine("\t 4. Confirms that the user signed up.");
        Console.WriteLine("\t 5. Invokes the initiateAuth to sign in. This results in being prompted to set up TOTP (time-based one-time password). (The response is “ChallengeName”: “MFA_SETUP”).");
        Console.WriteLine("\t 6. Invokes the AssociateSoftwareToken method to generate a TOTP MFA private key. This can be used with Google Authenticator.");
        Console.WriteLine("\t 7. Invokes the VerifySoftwareToken method to verify the TOTP and register for MFA.");
        Console.WriteLine("\t 8. Invokes the AdminInitiateAuth to sign in again. This results in being prompted to submit a TOTP (Response: “ChallengeName”: “SOFTWARE_TOKEN_MFA”).");
        Console.WriteLine("\t 9. Invokes the AdminRespondToAuthChallenge to get back a token.");
    }

    /// <summary>
    /// Display a line of hyphens, the centered text of the title and another
    /// line of hyphens.
    /// </summary>
    /// <param name="strTitle">The string to be displayed.</param>
    public static void DisplayTitle(string strTitle)
    {
        Console.WriteLine();
        Console.WriteLine(strTitle);
        Console.WriteLine();
    }
}

// snippet-end:[Cognito.dotnetv4.UIMethods]