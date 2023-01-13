/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
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
#include <aws/cognito-idp/model/InitiateAuthRequest.h>
#include <aws/cognito-idp/model/ResendConfirmationCodeRequest.h>
#include <aws/cognito-idp/model/RespondToAuthChallengeRequest.h>
#include <aws/cognito-idp/model/SignUpRequest.h>
#include <aws/cognito-idp/model/VerifySoftwareTokenRequest.h>
#include "cognito_samples.h"

#ifdef USING_QR // Defined in CMakeLists.txt.

#include <qrcodegen/qrcodegen.hpp>

#endif // USING_QR

namespace AwsDoc {
    namespace Cognito {
        static const int ASTERISK_FILL_WIDTH = 88;

        static bool checkAdminUserStatus(const Aws::String &userName,
                                         const Aws::String &userPoolID,
                                         const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client);

        static bool initiateAuthorization(const Aws::String &clientID,
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

        inline void printAsterisksLine() {
            std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " "
                      << std::endl;
        }

#ifdef USING_QR
        static const char QR_CODE_PATH[] = SOURCE_DIR "/QR_Code.bmp";

        static void saveQRCode(const std::string &string);

#endif
    } // namespace Cognito
} // namespace AwsDoc


//! Scenario to create, copy, and delete S3 buckets and objects.
/*!
  \sa S3_GettingStartedScenario()
  \param uploadFilePath Path to file to upload to an Amazon S3 bucket.
  \param saveFilePath Path for saving a downloaded S3 object.
  \param clientConfig Aws client configuration.
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
            << "This scenario will add a user to a Cognito user pool."
            << std::endl;
    Aws::CognitoIdentityProvider::CognitoIdentityProviderClient client(clientConfig);

    const Aws::String userName = askQuestion("Enter a new user name: ");
    const Aws::String password = askQuestion("Enter a new password: ");
    const Aws::String email = askQuestion("Enter a valid email for the user: ");

    std::cout << "Signing up " << userName << std::endl;

    bool userExists = false;
    do {
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
                    << "The username already exists. Please enter a different user name."
                    << std::endl;
            userExists = true;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::SignUpRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (userExists);

    printAsterisksLine();
    std::cout << "Retrieving status of " << userName << " in the user pool."
              << std::endl;

    if (!checkAdminUserStatus(userName, userPoolID, client)) {
        return false;
    }

    std::cout << "A confirmation code was sent to " << email << "." << std::endl;

    bool resend = askYesNoQuestion("Would you like to send a new code? (y/n) ");
    if (resend) {
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
    }

    printAsterisksLine();

    {
        const Aws::String confirmationCode = askQuestion(
                "Enter the confirmation code that was emailed: ");
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
    }

    std::cout << "Rechecking the status of " << userName << " in the user pool."
              << std::endl;
    if (!checkAdminUserStatus(userName, userPoolID, client)) {
        return false;
    }

    printAsterisksLine();

    std::cout << "Initiating authorization using the user name and password."
              << std::endl;

    Aws::String session;
    if (!initiateAuthorization(clientID, userName, password, session, client)) {
        return false;
    }

    printAsterisksLine();

    std::cout
            << "Starting setup of time-based one-time password (TOTP) multi-factor authentication (MFA)."
            << std::endl;

    {
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
            std::cout << "\nOr scan the QR code in the file '" << QR_CODE_PATH << "." << std::endl;

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
    }
    askQuestion("Type enter to continue...", alwaysTrueTest);

    printAsterisksLine();

    bool codeMatch = true;
    do {
        Aws::String userCode = askQuestion(
                "Enter the 6 digit code displayed in the authenticator app: ");

        Aws::CognitoIdentityProvider::Model::VerifySoftwareTokenRequest request;
        request.SetUserCode(userCode);
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::VerifySoftwareTokenOutcome outcome =
                client.VerifySoftwareToken(request);

        if (outcome.IsSuccess()) {
            std::cout << "Verification of the code was successful."
                      << std::endl;
            session = outcome.GetResult().GetSession();
            codeMatch = true;
        }
        else if ((outcome.GetError().GetErrorType() ==
                  Aws::CognitoIdentityProvider::CognitoIdentityProviderErrors::ENABLE_SOFTWARE_TOKEN_M_F_A) &&
                 (outcome.GetError().GetMessage() == "Code mismatch")) {
            std::cout << "The code did not match." << std::endl;
            codeMatch = false;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::VerifySoftwareToken. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!codeMatch);

    printAsterisksLine();
    std::cout << "You have completed the MFA authentication setup." << std::endl;
    std::cout << "Now you will do a normal login." << std::endl;

    if (!initiateAuthorization(clientID, userName, password, session, client)) {
        return false;
    }

    Aws::String accessToken;
    codeMatch = true;
    do {
        Aws::String mfaCode = askQuestion(
                "Re-enter the 6 digit code displayed in the authenticator app: ");

        Aws::CognitoIdentityProvider::Model::RespondToAuthChallengeRequest request;
        request.AddChallengeResponses("USERNAME", userName);
        request.AddChallengeResponses("SOFTWARE_TOKEN_MFA_CODE", mfaCode);
        request.SetChallengeName(
                Aws::CognitoIdentityProvider::Model::ChallengeNameType::SOFTWARE_TOKEN_MFA);
        request.SetClientId(clientID);
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::RespondToAuthChallengeOutcome outcome =
                client.RespondToAuthChallenge(request);

        if (outcome.IsSuccess()) {
            std::cout << "Here is the response to the challenge.\n" <<
                      outcome.GetResult().GetAuthenticationResult().Jsonize().View().WriteReadable()
                      << std::endl;

            accessToken = outcome.GetResult().GetAuthenticationResult().GetAccessToken();
            codeMatch = true;
        }
        else if (outcome.GetError().GetErrorType() ==
                 Aws::CognitoIdentityProvider::CognitoIdentityProviderErrors::CODE_MISMATCH) {
            std::cout << "The code did not match." << std::endl;
            codeMatch = false;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::RespondToAuthChallenge. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }

        std::cout << "You have successfully added a user to Amazon Cognito."
                  << std::endl;
    } while (!codeMatch);

    if (askYesNoQuestion("Would you like to delete the user you created? (y/n) ")) {
        Aws::CognitoIdentityProvider::Model::DeleteUserRequest request;
        request.SetAccessToken(accessToken);

        Aws::CognitoIdentityProvider::Model::DeleteUserOutcome outcome = client.DeleteUser(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "The user " << userName << " was deleted."
                      << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::DeleteUser. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return true;
}

bool AwsDoc::Cognito::checkAdminUserStatus(const Aws::String &userName,
                                           const Aws::String &userPoolID,
                                           const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client) {
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

    return outcome.IsSuccess();
}

bool AwsDoc::Cognito::initiateAuthorization(const Aws::String &clientID,
                                            const Aws::String &userName,
                                            const Aws::String &password,
                                            Aws::String &sessionResult,
                                            const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client) {
    Aws::CognitoIdentityProvider::Model::InitiateAuthRequest request;
    request.SetClientId(clientID);
    request.AddAuthParameters("USERNAME", userName);
    request.AddAuthParameters("PASSWORD", password);
    request.SetAuthFlow(
            Aws::CognitoIdentityProvider::Model::AuthFlowType::USER_PASSWORD_AUTH);

    Aws::CognitoIdentityProvider::Model::InitiateAuthOutcome outcome =
            client.InitiateAuth(request);

    if (outcome.IsSuccess()) {
        std::cout << "Call to InitiateAuth was successful." << std::endl;
        sessionResult = outcome.GetResult().GetSession();
    }
    else {
        std::cerr << "Error with CognitoIdentityProvider::InitiateAuth. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
#if 0
    AwsDoc::Cognito::saveQRCode("HKUIJOJNHBIL4QZH4YNGZMWD7365G664RANOTTWNCLAIX5CBEAA");
#else
    if (argc != 3) {
        std::cout << "Usage:\n" <<
                  "    <clientID> <userPathID>\n\n" <<
                  "Where:\n" << // TODO: update this.
                  "   uploadFilePath - The path where the file is located (for example, C:/AWS/book2.pdf).\n"
                  <<
                  "   saveFilePath - The path where the file is saved after it's " <<
                  "downloaded (for example, C:/AWS/book2.pdf). " << std::endl;
        return 1;
    }

    Aws::String clientID = argv[1];
    Aws::String userPoolID = argv[2];

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userPoolID,
                                                     clientConfig);
    }

    ShutdownAPI(options);
#endif
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
                std::cout << "Please answer 'y' or 'n'." << std::endl;
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
        std::cout << "Please enter some text." << std::endl;
        return false;
    }

    return true;
}

#ifdef USING_QR
// Prints the given QrCode object to the console.

void writeBitmap(const std::string fileName, const uint8_t* bytes, int width, int height)
{
    char tag[] = { 'B', 'M' };
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

static void printQr(const qrcodegen::QrCode &qr) {
    int border = 4;
    int width = + qr.getSize() + 2 * border;
    int height = width;

    std::vector<uint32_t> bitmap(width * height);
    int i = 0;
    for (int y = -border; y < qr.getSize() + border; y++) {
        for (int x = -border; x < qr.getSize() + border; x++) {
            bitmap[i++] = qr.getModule(x, y) ? 0 : 0xFFFFFFFF;
        }
    }

    writeBitmap(AwsDoc::Cognito::QR_CODE_PATH, reinterpret_cast<uint8_t*>(bitmap.data()), width, height);
}

void AwsDoc::Cognito::saveQRCode(const std::string &string)
{
    const qrcodegen::QrCode::Ecc errCorLvl = qrcodegen::QrCode::Ecc::LOW;  // Error correction level

    // Make and print the QR Code symbol
    const qrcodegen::QrCode qr = qrcodegen::QrCode::encodeText(string.c_str(), errCorLvl);
    printQr(qr);
}

#endif // USING_QR