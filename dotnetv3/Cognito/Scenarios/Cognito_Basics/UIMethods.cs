// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[Cognito.dotnetv3.UIMethods]
namespace CognitoBasics;

/// <summary>
/// Some useful methods to make screen display easier.
/// </summary>
public class UiMethods
{
    public readonly string SepBar = new string('-', Console.WindowWidth);

    /// <summary>
    /// Show information about the scenario.
    /// </summary>
    public void DisplayOverview()
    {
        Console.Clear();
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
    /// Display a message and wait until the user presses enter.
    /// </summary>
    public void PressEnter()
    {
        Console.Write("\nPress <Enter> to continue.");
        _ = Console.ReadLine();
    }

    /// <summary>
    /// Pad a string with spaces to center it on the console display.
    /// </summary>
    /// <param name="strToCenter">The string to pad with spaces.</param>
    /// <returns>The padded string.</returns>
    public string CenterString(string strToCenter)
    {
        var padAmount = (Console.WindowWidth - strToCenter.Length) / 2;
        var leftPad = new string(' ', padAmount);
        return $"{leftPad}{strToCenter}";
    }

    /// <summary>
    /// Display a line of hyphens, the centered text of the title and another
    /// line of hyphens.
    /// </summary>
    /// <param name="strTitle">The string to be displayed.</param>
    public void DisplayTitle(string strTitle)
    {
        Console.WriteLine(SepBar);
        Console.WriteLine(CenterString(strTitle));
        Console.WriteLine(SepBar);
    }
}

// snippet-end:[Cognito.dotnetv3.UIMethods]