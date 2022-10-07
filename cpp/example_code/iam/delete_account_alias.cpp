/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
* Before running this C++ code example, set up your development environment,
* including your credentials.
*
* For more information, see the following documentation topic:
*
* https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*
* Purpose
*
* Demonstrates deleting an alias from an AWS account.
*
*/

//snippet-start:[iam.cpp.delete_account_alias.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeleteAccountAliasRequest.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.delete_account_alias.inc]

// snippet-start:[iam.cpp.delete_account_alias.code]
//! Deletes an alias from an AWS account.
/*!
  \sa deleteAccountAlias()
  \param accountAlias: The account alias.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/
bool AwsDoc::IAM::deleteAccountAlias(const Aws::String &accountAlias,
                                     const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);

    Aws::IAM::Model::DeleteAccountAliasRequest request;
    request.SetAccountAlias(accountAlias);

    const auto outcome = iam.DeleteAccountAlias(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting account alias " << accountAlias << ": "
                  << outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully deleted account alias " << accountAlias <<
                  std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.delete_account_alias.code]


/*
 *
 *  main function
 *
 * Prerequisites: Existing access key.
 *
 * Usage: run_delete_access_key <user_name> <access_key_id>
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: delete_account_alias <account_alias>" <<
                  std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String accountAlias(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::IAM::deleteAccountAlias(accountAlias, clientConfig);

    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
