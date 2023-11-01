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
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/VerifyEmailIdentityRequest.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.VerifyEmailIdentity]
//! Add an email address to the list of identities associated with this account and
//! initiate verification.
/*!
  \param emailAddress; The email address to add.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::verifyEmailIdentity(const Aws::String &emailAddress,
                         const Aws::Client::ClientConfiguration &clientConfiguration)
{
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::VerifyEmailIdentityRequest verifyEmailIdentityRequest;

    verifyEmailIdentityRequest.SetEmailAddress(emailAddress);

    Aws::SES::Model::VerifyEmailIdentityOutcome outcome = sesClient.VerifyEmailIdentity(verifyEmailIdentityRequest);

    if (outcome.IsSuccess())
    {
        std::cout << "Email verification initiated." << std::endl;
    }

    else
    {
        std::cerr << "Error initiating email verification. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.VerifyEmailIdentity]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_verify_email_address <email_address>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: run_verify_email_address <email_address>";
    return 1;
  }
  Aws::SDKOptions options;
  options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
  Aws::InitAPI(options);
  {
    Aws::String emailAddress(argv[1]);

      Aws::Client::ClientConfiguration clientConfig;
      // Optional: Set to the AWS Region (overrides config file).
      // clientConfig.region = "us-east-1";

      AwsDoc::SES::verifyEmailIdentity(emailAddress, clientConfig);
  }

  Aws::ShutdownAPI(options);
  return 0;
}

#endif // TESTING_BUILD
