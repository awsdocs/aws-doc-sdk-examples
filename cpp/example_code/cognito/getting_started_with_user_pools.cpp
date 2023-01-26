/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
* Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates adding a user to an Amazon Cognito user pool.
 *
 *  1. Add a user with a username, password, and email address. (SignUp)
 *  2. Confirm that the user was added to the user pool. (AdminGetUser)
 *  3. Request another confirmation code by email. (ResendConfirmationCode)
 *  4. Send the confirmation code that's received in the email. (ConfirmSignUp)
 *  5. Initiate authorization with a username and password. (AdminInitiateAuth)
 *  6. Request a setup key for one-time password (TOTP)
 *     multi-factor authentication (MFA). (AssociateSoftwareToken)
 *  7. Send the MFA code copied from an authenticator app. (VerifySoftwareToken)
 *  8. Initiate authorization again with username and password. (AdminInitiateAuth)
 *  9. Send a new MFA code copied from an authenticator app. (AdminRespondToAuthChallenge)
 *  10. Delete the user that you just added. (DeleteUser)
 *
 *
 *  This example requires a previously configured Amazon Cognito user pool.
 *
 *  To set up and run the example, refer to the instructions in the README.
 *
 */

#include <iostream>
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/cognito-idp//CognitoIdentityProviderClient.h>
#include <aws/cognito-idp/model/AdminGetUserRequest.h>
#include <aws/cognito-idp/model/AssociateSoftwareTokenRequest.h>
#include <aws/cognito-idp/model/ConfirmSignUpRequest.h>
#include <aws/cognito-idp/model/DeleteUserRequest.h>
#include <aws/cognito-idp/model/AdminInitiateAuthRequest.h>
#include <aws/cognito-idp/model/ResendConfirmationCodeRequest.h>
#include <aws/cognito-idp/model/AdminRespondToAuthChallengeRequest.h>
#include <aws/cognito-idp/model/SignUpRequest.h>
#include <aws/cognito-idp/model/VerifySoftwareTokenRequest.h>
#include "cognito_samples.h"

#ifdef USING_QR // Defined in CMakeLists.txt.

#include <qrcodegen/qrcodegen.hpp>

#endif // USING_QR

namespace AwsDoc {
    namespace Cognito {
        static const int ASTERISK_FILL_WIDTH = 88;

        //! Routine which checks the user status in an Amazon Cognito user pool.
        /*!
         \sa checkAdminUserStatus()
         \param userName: A username.
         \param userPoolID: An Amazon Cognito user pool ID.
         \return bool: Successful completion.
         */
        static bool checkAdminUserStatus(const Aws::String &userName,
                                         const Aws::String &userPoolID,
                                         const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client);

        //! Routine which starts authorization of an Amazon Cognito user.
        //! This routine requires administrator credentials.
        /*!
         \sa adminInitiateAuthorization()
         \param clientID: Client ID of tracked device.
         \param userPoolID: An Amazon Cognito user pool ID.
         \param userName: A username.
         \param password: A password.
         \param sessionResult: String to receive a session token.
         \return bool: Successful completion.
         */
        static bool adminInitiateAuthorization(const Aws::String &clientID,
                                               const Aws::String &userPoolID,
                                               const Aws::String &userName,
                                               const Aws::String &password,
                                               Aws::String &sessionResult,
                                               const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        static bool testForEmptyString(const Aws::String &string);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        static bool alwaysTrueTest(const Aws::String &) { return true; }

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        static Aws::String askQuestion(const Aws::String &string,
                                       const std::function<bool(
                                               Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        static bool askYesNoQuestion(const Aws::String &string);

        //! Utility routine to print a line of asterisks to standard out.
        /*!
         \\sa printAsterisksLine()
        \return void:
         */
        inline void printAsterisksLine() {
            std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " "
                      << std::endl;
        }

#ifdef USING_QR
        static const char QR_CODE_PATH[] = SOURCE_DIR "/QR_Code.bmp";

        //! Routine which converts a string to a QR code and writes it to a bmp file.
        /*!
         \sa saveQRCode()
         \param string: A string to convert to a QR code.
         \return void:
         */
        static void saveQRCode(const std::string &string);

#endif
    } // namespace Cognito
} // namespace AwsDoc

// snippet-start:[cpp.example_code.getting_started_with_user_pools]
//! Scenario that adds a user to an Amazon Cognito user pool.
/*!
  \sa gettingStartedWithUserPools()
  \param clientID: Client ID associated with an Amazon Cognito user pool.
  \param userPoolID: An Amazon Cognito user pool ID.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
 */
bool AwsDoc::Cognito::gettingStartedWithUserPools(const Aws::String &clientID,
                                                  const Aws::String &userPoolID,
                                                  const Aws::Client::ClientConfiguration &clientConfig) {
    printAsterisksLine();
    std::cout
            << "Welcome to the Amazon Cognito example scenario."
            << std::endl;
    printAsterisksLine();

    std::cout
            << "This scenario will add a user to an Amazon Cognito user pool."
            << std::endl;
    const Aws::String userName = askQuestion("Enter a new username: ");
    const Aws::String password = askQuestion("Enter a new password: ");
    const Aws::String email = askQuestion("Enter a valid email for the user: ");

    std::cout << "Signing up " << userName << std::endl;

    // snippet-start:[cpp.example_code.cognito.cognito_client]
    Aws::CognitoIdentityProvider::CognitoIdentityProviderClient client(clientConfig);
    // snippet-end:[cpp.example_code.cognito.cognito_client]
    bool userExists = false;
    do {
        // 1. Add a user with a username, password, and email address.
        // snippet-start:[cpp.example_code.cognito.signup]
        Aws::CognitoIdentityProvider::Model::SignUpRequest request;
        request.AddUserAttributes(
                Aws::CognitoIdentityProvider::Model::AttributeType().WithName(
                        "email").WithValue(email));
        request.SetUsername(userName);
        request.SetPassword(password);
        request.SetClientId(clientID);
        Aws::CognitoIdentityProvider::Model::SignUpOutcome outcome =
                client.SignUp(request);

        if (outcome.IsSuccess()) {
            std::cout << "The signup request for " << userName << " was successful."
                      << std::endl;
        }
        else if (outcome.GetError().GetErrorType() ==
                 Aws::CognitoIdentityProvider::CognitoIdentityProviderErrors::USERNAME_EXISTS) {
            std::cout
                    << "The username already exists. Please enter a different username."
                    << std::endl;
            userExists = true;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::SignUpRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.signup]
    } while (userExists);

    printAsterisksLine();
    std::cout << "Retrieving status of " << userName << " in the user pool."
              << std::endl;
    // 2. Confirm that the user was added to the user pool.
    if (!checkAdminUserStatus(userName, userPoolID, client)) {
        return false;
    }

    std::cout << "A confirmation code was sent to " << email << "." << std::endl;

    bool resend = askYesNoQuestion("Would you like to send a new code? (y/n) ");
    if (resend) {
        // Request a resend of the confirmation code to the email address. (ResendConfirmationCode)
        // snippet-start:[cpp.example_code.cognito.resend_confirmation]
        Aws::CognitoIdentityProvider::Model::ResendConfirmationCodeRequest request;
        request.SetUsername(userName);
        request.SetClientId(clientID);

        Aws::CognitoIdentityProvider::Model::ResendConfirmationCodeOutcome outcome =
                client.ResendConfirmationCode(request);

        if (outcome.IsSuccess()) {
            std::cout
                    << "CognitoIdentityProvider::ResendConfirmationCode was successful."
                    << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::ResendConfirmationCode. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.resend_confirmation]
    }

    printAsterisksLine();

    {
        // 4. Send the confirmation code that's received in the email. (ConfirmSignUp)
        const Aws::String confirmationCode = askQuestion(
                "Enter the confirmation code that was emailed: ");
        // snippet-start:[cpp.example_code.cognito.confirm_signup]
        Aws::CognitoIdentityProvider::Model::ConfirmSignUpRequest request;
        request.SetClientId(clientID);
        request.SetConfirmationCode(confirmationCode);
        request.SetUsername(userName);

        Aws::CognitoIdentityProvider::Model::ConfirmSignUpOutcome outcome =
                client.ConfirmSignUp(request);

        if (outcome.IsSuccess()) {
            std::cout << "ConfirmSignup was Successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::ConfirmSignUp. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.confirm_signup]
    }

    std::cout << "Rechecking the status of " << userName << " in the user pool."
              << std::endl;
    if (!checkAdminUserStatus(userName, userPoolID, client)) {
        return false;
    }

    printAsterisksLine();

    std::cout << "Initiating authorization using the username and password."
              << std::endl;

    Aws::String session;
    // 5. Initiate authorization with username and password. (AdminInitiateAuth)
    if (!adminInitiateAuthorization(clientID, userPoolID,  userName, password, session, client)) {
        return false;
    }

    printAsterisksLine();

    std::cout
            << "Starting setup of time-based one-time password (TOTP) multi-factor authentication (MFA)."
            << std::endl;

    {
        // 6. Request a setup key for one-time password (TOTP)
        //    multi-factor authentication (MFA). (AssociateSoftwareToken)
        // snippet-start:[cpp.example_code.cognito.associate_software_token]
        Aws::CognitoIdentityProvider::Model::AssociateSoftwareTokenRequest request;
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::AssociateSoftwareTokenOutcome outcome =
                client.AssociateSoftwareToken(request);

        if (outcome.IsSuccess()) {
            std::cout
                    << "Enter this setup key into an authenticator app, for example Google Authenticator."
                    << std::endl;
            std::cout << "Setup key: " << outcome.GetResult().GetSecretCode()
                      << std::endl;
#ifdef USING_QR
            printAsterisksLine();
            std::cout << "\nOr scan the QR code in the file '" << QR_CODE_PATH << "."
                      << std::endl;

            saveQRCode(std::string("otpauth://totp/") + userName + "?secret=" +
                       outcome.GetResult().GetSecretCode());
#endif // USING_QR
            session = outcome.GetResult().GetSession();
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::AssociateSoftwareToken. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.associate_software_token]
    }
    askQuestion("Type enter to continue...", alwaysTrueTest);

    printAsterisksLine();

    {
        Aws::String userCode = askQuestion(
                "Enter the 6 digit code displayed in the authenticator app: ");

        //  7. Send the MFA code copied from an authenticator app. (VerifySoftwareToken)
        // snippet-start:[cpp.example_code.cognito.verify_software_token]
        Aws::CognitoIdentityProvider::Model::VerifySoftwareTokenRequest request;
        request.SetUserCode(userCode);
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::VerifySoftwareTokenOutcome outcome =
                client.VerifySoftwareToken(request);

        if (outcome.IsSuccess()) {
            std::cout << "Verification of the code was successful."
                      << std::endl;
            session = outcome.GetResult().GetSession();
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::VerifySoftwareToken. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.verify_software_token]
    }

    printAsterisksLine();
    std::cout << "You have completed the MFA authentication setup." << std::endl;
    std::cout << "Now, sign in." << std::endl;

    // 8. Initiate authorization again with username and password. (AdminInitiateAuth)
    if (!adminInitiateAuthorization(clientID, userPoolID, userName, password, session, client)) {
        return false;
    }

    Aws::String accessToken;
    {
        Aws::String mfaCode = askQuestion(
                "Re-enter the 6 digit code displayed in the authenticator app: ");

        // 9. Send a new MFA code copied from an authenticator app. (AdminRespondToAuthChallenge)
        // snippet-start:[cpp.example_code.cognito.admin_respond_to_auth_challenge]
        Aws::CognitoIdentityProvider::Model::AdminRespondToAuthChallengeRequest request;
        request.AddChallengeResponses("USERNAME", userName);
        request.AddChallengeResponses("SOFTWARE_TOKEN_MFA_CODE", mfaCode);
        request.SetChallengeName(
                Aws::CognitoIdentityProvider::Model::ChallengeNameType::SOFTWARE_TOKEN_MFA);
        request.SetClientId(clientID);
        request.SetUserPoolId(userPoolID);
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::AdminRespondToAuthChallengeOutcome outcome =
                client.AdminRespondToAuthChallenge(request);

        if (outcome.IsSuccess()) {
            std::cout << "Here is the response to the challenge.\n" <<
                      outcome.GetResult().GetAuthenticationResult().Jsonize().View().WriteReadable()
                      << std::endl;

            accessToken = outcome.GetResult().GetAuthenticationResult().GetAccessToken();
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::AdminRespondToAuthChallenge. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
        // snippet-end:[cpp.example_code.cognito.admin_respond_to_auth_challenge]

        std::cout << "You have successfully added a user to Amazon Cognito."
                  << std::endl;
    }

    if (askYesNoQuestion("Would you like to delete the user that you just added? (y/n) ")) {
        // 10. Delete the user that you just added. (DeleteUser)
        // snippet-start:[cpp.example_code.cognito.delete_user]
        Aws::CognitoIdentityProvider::Model::DeleteUserRequest request;
        request.SetAccessToken(accessToken);

        Aws::CognitoIdentityProvider::Model::DeleteUserOutcome outcome =
                client.DeleteUser(request);

        if (outcome.IsSuccess()) {
            std::cout << "The user " << userName << " was deleted."
                      << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::DeleteUser. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
        // snippet-end:[cpp.example_code.cognito.delete_user]
    }

    return true;
}

//! Routine which checks the user status in an Amazon Cognito user pool.
/*!
 \sa checkAdminUserStatus()
 \param userName: A username.
 \param userPoolID: An Amazon Cognito user pool ID.
 \return bool: Successful completion.
 */
bool AwsDoc::Cognito::checkAdminUserStatus(const Aws::String &userName,
                                           const Aws::String &userPoolID,
                                           const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client) {
    // snippet-start:[cpp.example_code.cognito.admin_get_user]
    Aws::CognitoIdentityProvider::Model::AdminGetUserRequest request;
    request.SetUsername(userName);
    request.SetUserPoolId(userPoolID);

    Aws::CognitoIdentityProvider::Model::AdminGetUserOutcome outcome =
            client.AdminGetUser(request);

    if (outcome.IsSuccess()) {
        std::cout << "The status for " << userName << " is " <<
                  Aws::CognitoIdentityProvider::Model::UserStatusTypeMapper::GetNameForUserStatusType(
                          outcome.GetResult().GetUserStatus()) << std::endl;
        std::cout << "Enabled is " << outcome.GetResult().GetEnabled() << std::endl;
    }
    else {
        std::cerr << "Error with CognitoIdentityProvider::AdminGetUser. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }
    // snippet-end:[cpp.example_code.cognito.admin_get_user]

    return outcome.IsSuccess();
}

//! Routine which starts authorization of an Amazon Cognito user.
//! This routine requires administrator credentials.
/*!
 \sa adminInitiateAuthorization()
 \param clientID: Client ID of tracked device.
 \param userPoolID: An Amazon Cognito user pool ID.
 \param userName: A username.
 \param password: A password.
 \param sessionResult: String to receive a session token.
 \return bool: Successful completion.
 */
bool AwsDoc::Cognito::adminInitiateAuthorization(const Aws::String &clientID,
                                                 const Aws::String &userPoolID,
                                                 const Aws::String &userName,
                                                 const Aws::String &password,
                                                 Aws::String &sessionResult,
                                                 const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client) {
    // snippet-start:[cpp.example_code.cognito.admin_initiate_auth]
    Aws::CognitoIdentityProvider::Model::AdminInitiateAuthRequest request;
    request.SetClientId(clientID);
    request.SetUserPoolId(userPoolID);
    request.AddAuthParameters("USERNAME", userName);
    request.AddAuthParameters("PASSWORD", password);
    request.SetAuthFlow(
            Aws::CognitoIdentityProvider::Model::AuthFlowType::ADMIN_USER_PASSWORD_AUTH);


    Aws::CognitoIdentityProvider::Model::AdminInitiateAuthOutcome outcome =
            client.AdminInitiateAuth(request);

    if (outcome.IsSuccess()) {
        std::cout << "Call to AdminInitiateAuth was successful." << std::endl;
        sessionResult = outcome.GetResult().GetSession();
    }
    else {
        std::cerr << "Error with CognitoIdentityProvider::AdminInitiateAuth. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }
    // snippet-end:[cpp.example_code.cognito.admin_initiate_auth]

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.getting_started_with_user_pools]

#ifndef TESTING_BUILD

/*
 *  main function
 *
 *  Usage: 'run_getting_started_with_user_pools <clientID> <userPoolID>'
 *
 *  For instructions on setting up this example, see the accompanying README.
 *
 */

int main(int argc, const char *argv[]) {
    if (argc != 3) {
        std::cout << "Usage:\n" <<
                  "    run_getting_started_with_user_pools <clientID> <userPoolID>\n\n"
                  <<
                  "Where:\n" <<
                  "   clientID - A client ID associated with an Amazon Cognito user pool.\n"
                  <<
                  "   userPoolID - An Amazon Cognito user pool ID." << std::endl;
        return 1;
    }

    Aws::String clientID = argv[1];
    Aws::String userPoolID = argv[2];

    Aws::SDKOptions options;
    InitAPI(options);

    {
        // snippet-start:[cpp.example_code.cognito.client_configuration]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.cognito.client_configuration]

        AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userPoolID,
                                                     clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::Cognito::askQuestion(const Aws::String &string,
                                         const std::function<bool(
                                                 Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \\sa askYesNoQuestion()
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::Cognito::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Test routine passed as argument to askQuestion routine.
/*!
 \sa testForEmptyString()
 \param string: A string to test.
 \return bool: True if empty.
 */
bool AwsDoc::Cognito::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

#ifdef USING_QR
// The following routines support creating a QR code file for MFA.

//! Routine which writes a bitmap to file.
/*!
 \sa writeBitmap()
 \param fileName: A file name for the created bitmap.
 \param bytes: Pointer to bitmap bits.
 \param width: Bitmap width.
 \param height: Bitmap height.
 \return void:
 */
static void
writeBitmap(const std::string &fileName, const uint8_t *bytes, int width, int height) {
    char tag[] = {'B', 'M'};
    int header[] = {
            0x3a, 0x00, 0x36, 0x28, width, height, 0x200001,
            0, 0, 0x002e23, 0x002e23, 0, 0
    };

    FILE *fp = fopen(fileName.c_str(), "w+");
    fwrite(&tag, sizeof(tag), 1, fp);
    fwrite(&header, sizeof(header), 1, fp);
    fwrite(bytes, width * height * 4, 1, fp);
    fclose(fp);
}

//! Routine which writes a QR code to a bmp file.
/*!
 \sa writeQRCode()
 \param qr: A qrcodegen::QrCode struct.
 \return void:
 */
static void writeQRCode(const qrcodegen::QrCode &qr) {
    int border = 4;
    int width = +qr.getSize() + 2 * border;
    int height = width;

    std::vector<uint32_t> bitmap(width * height);
    int i = 0;
    for (int y = -border; y < qr.getSize() + border; y++) {
        for (int x = -border; x < qr.getSize() + border; x++) {
            bitmap[i++] = qr.getModule(x, y) ? 0 : 0xFFFFFFFF;
        }
    }

    writeBitmap(AwsDoc::Cognito::QR_CODE_PATH,
                reinterpret_cast<uint8_t *>(bitmap.data()), width, height);
}

//! Routine which converts a string to a QR code and writes it to a bmp file.
/*!
 \sa saveQRCode()
 \param string: A string to convert to a QR code.
 \return void:
 */
void AwsDoc::Cognito::saveQRCode(const std::string &string) {
    const qrcodegen::QrCode::Ecc errCorLvl = qrcodegen::QrCode::Ecc::LOW;  // Error correction level.

    const qrcodegen::QrCode qr = qrcodegen::QrCode::encodeText(string.c_str(),
                                                               errCorLvl);
    ::writeQRCode(qr);
}

#endif // USING_QR