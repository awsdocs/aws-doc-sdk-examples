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
#include <aws/cognito-idp/model/InitiateAuthRequest.h>
#include <aws/cognito-idp/model/ResendConfirmationCodeRequest.h>
#include <aws/cognito-idp/model/SignUpRequest.h>

namespace AwsDoc {
    namespace Cognito {
        static const int ASTERISK_FILL_WIDTH = 88;

        bool gettingStartedWithUserPools(const Aws::String &clientID,
                                         const Aws::String &userPoolID,
                                         const Aws::Client::ClientConfiguration &clientConfig);

        bool checkAdminUserStatus(const Aws::String &userName,
                                  const Aws::String &userPoolID,
                                  const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        bool testForEmptyString(const Aws::String &string);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        bool alwaysTrueTest(const Aws::String &) { return true; }

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        bool askYesNoQuestion(const Aws::String &string);

        inline void printAsterisksLine() {
            std::cout << std::setfill('*') << std::setw(ASTERISK_FILL_WIDTH) << " "
                      << std::endl;
        }

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

    Aws::CognitoIdentityProvider::CognitoIdentityProviderClient client(clientConfig);

    const Aws::String userName = askQuestion("Enter your user name: ");
    const Aws::String password = askQuestion("Enter your password: ");
    const Aws::String email = askQuestion("Enter your email: ");

    std::cout << "Signing up " << userName << std::endl;

    {
        Aws::CognitoIdentityProvider::Model::SignUpRequest request;
        request.AddUserAttributes(
                Aws::CognitoIdentityProvider::Model::AttributeType().WithName(
                        "email").WithValue(email));
        request.SetUsername(userName);
        request.SetPassword(password);
        request.SetClientId(clientID);
        Aws::CognitoIdentityProvider::Model::SignUpOutcome outcome = client.SignUp(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "CognitoIdentityProvider::SignUpRequest was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::SignUpRequest. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    printAsterisksLine();
    std::cout << "Getting " << userName << " in the user pool." << std::endl;

    if (!checkAdminUserStatus(userName, userPoolID, client))
    {
        return false;
    }

    std::cout << "A confirmation code was sent to " << userName << "." << std::endl;

    bool resend = askYesNoQuestion("Would you like to send a new code? (y/n) ");
    if (resend)
    {
        Aws::CognitoIdentityProvider::Model::ResendConfirmationCodeRequest request;
        request.SetUsername(userName);
        request.SetClientId(clientID);

        Aws::CognitoIdentityProvider::Model::ResendConfirmationCodeOutcome outcome = client.ResendConfirmationCode(
                request);

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
    const Aws::String confirmationCode = askQuestion("Enter the confirmation code that was emailed. ");

    {
        Aws::CognitoIdentityProvider::Model::ConfirmSignUpRequest request;
        request.SetClientId(clientID);
        request.SetConfirmationCode(confirmationCode);
        request.SetUsername(userName);

        Aws::CognitoIdentityProvider::Model::ConfirmSignUpOutcome outcome = client.ConfirmSignUp(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "CognitoIdentityProvider::ConfirmSignUp was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::ConfirmSignUp. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    std::cout << "Rechecking the status of " << userName << " in the user pool." << std::endl;
    if (!checkAdminUserStatus(userName, userPoolID, client))
    {
        return false;
    }

    printAsterisksLine();

    std::cout << "Initiating authorization using the user name and password." << std::endl;

    Aws::String session;
    {
        Aws::CognitoIdentityProvider::Model::InitiateAuthRequest request;
        request.SetClientId(clientID);
        request.AddAuthParameters("USERNAME", userName);
        request.AddAuthParameters("PASSWORD", password);
        request.SetAuthFlow(Aws::CognitoIdentityProvider::Model::AuthFlowType::USER_PASSWORD_AUTH);

        Aws::CognitoIdentityProvider::Model::InitiateAuthOutcome outcome = client.InitiateAuth(request);

        if (outcome.IsSuccess()) {
            std::cout << "CognitoIdentityProvider::InitiateAuth was successful." << std::endl;
            session = outcome.GetResult().GetSession();
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::InitiateAuth. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    printAsterisksLine();

    std::cout << "Starting setup of time-based one-time password (TOTP) multi-factor authentication (MFA)." << std::endl;

    {
        Aws::CognitoIdentityProvider::Model::AssociateSoftwareTokenRequest request;
        request.SetSession(session);

        Aws::CognitoIdentityProvider::Model::AssociateSoftwareTokenOutcome outcome = client.AssociateSoftwareToken(request);

        if (outcome.IsSuccess()) {
            std::cout << "Enter this token into an authenticator app, for example Google Authenticator." << std::endl;
            std::cout << "Secret code: " << outcome.GetResult().GetSecretCode() << std::endl;
        }
        else {
            std::cerr << "Error with CognitoIdentityProvider::AssociateSoftwareToken. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    printAsterisksLine();

    return true;
}

bool AwsDoc::Cognito::checkAdminUserStatus(const Aws::String &userName,
                                           const Aws::String &userPoolID,
                                           const Aws::CognitoIdentityProvider::CognitoIdentityProviderClient &client) {
    Aws::CognitoIdentityProvider::Model::AdminGetUserRequest request;
    request.SetUsername(userName);
    request.SetUserPoolId(userPoolID);

    Aws::CognitoIdentityProvider::Model::AdminGetUserOutcome outcome = client.AdminGetUser(request);

    if (outcome.IsSuccess()) {
        std::cout << "CognitoIdentityProvider::AdminGetUser was successful." << std::endl;
    }
    else {
        std::cerr << "Error with CognitoIdentityProvider::AdminGetUser. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {

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
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userPoolID,
                                                     clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


