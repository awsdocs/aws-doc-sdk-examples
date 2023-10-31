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
#include <aws/email/model/ListIdentitiesRequest.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.ListIdentities]
//! List the identities associated with this account.
/*!
  \param identityType: The identity type enum. "NOT_SET" is a valid option.
  \param identities; A vector to receive the retrieved identities.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::listIdentities(Aws::SES::Model::IdentityType identityType,
                                 Aws::Vector<Aws::String> &identities,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::ListIdentitiesRequest listIdentitiesRequest;

    if (identityType != Aws::SES::Model::IdentityType::NOT_SET) {
        listIdentitiesRequest.SetIdentityType(identityType);
    }

    Aws::String nextToken; // Used for paginated results.
    do {
        if (!nextToken.empty()) {
            listIdentitiesRequest.SetNextToken(nextToken);
        }
        Aws::SES::Model::ListIdentitiesOutcome outcome = sesClient.ListIdentities(
                listIdentitiesRequest);

        if (outcome.IsSuccess()) {
            const auto &retrievedIdentities = outcome.GetResult().GetIdentities();
            if (!retrievedIdentities.empty()) {
                identities.insert(identities.cend(), retrievedIdentities.cbegin(),
                                  retrievedIdentities.cend());
            }
            nextToken = outcome.GetResult().GetNextToken();
        }
        else {
            std::cout << "Error listing identities. " << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!nextToken.empty());

    return true;
}

// snippet-end:[cpp.example_code.ses.ListIdentities]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_list_identities [optional identity_type]'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv)
{
  Aws::SDKOptions options;
    Aws::InitAPI(options);
  {
    Aws::SES::SESClient ses;

    Aws::SES::Model::ListIdentitiesRequest li_req;

      Aws::SES::Model::IdentityType identityType = Aws::SES::Model::IdentityType::NOT_SET;

    if (argc == 2) {
        std::string identityTypeAsString = argv[1];
        if (identityTypeAsString == "EmailAddress") {
            identityType = Aws::SES::Model::IdentityType::EmailAddress;
        } else if (identityTypeAsString == "Domain") {
            identityType = Aws::SES::Model::IdentityType::Domain;
        } else {
            std::cout << "Invalid identity type. Must be either 'EmailAddress' or 'Domain'" << std::endl;
        }
    }

      Aws::Client::ClientConfiguration clientConfig;
      // Optional: Set to the AWS Region (overrides config file).
      // clientConfig.region = "us-east-1";

      Aws::Vector<Aws::String> identities;
      if (AwsDoc::SES::listIdentities(identityType, identities, clientConfig))
      {
          std::cout << identities.size() << " identities retrieved." << std::endl;
          for (auto &identity : identities) {
              std::cout << "   " << identity << std::endl;
          }
      }
  }

  Aws::ShutdownAPI(options);
  return 0;
}

#endif // TESTING_BUILD