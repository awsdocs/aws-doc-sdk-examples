/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetAccessKeyLastUsedRequest.h>
#include <aws/iam/model/GetAccessKeyLastUsedResult.h>
#include <iostream>
#include "iam_samples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Displays the time an access key was last used.
 *
 */

// snippet-start:[iam.cpp.access_key_last_used.code]
//! Displays the time an access key was last used.
/*!
  \sa accessKeyLastUsed()
  \param secretKeyID: The secret key ID.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::accessKeyLastUsed(const Aws::String& secretKeyID,
                       const Aws::Client::ClientConfiguration &clientConfig)
{
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::GetAccessKeyLastUsedRequest request;

    request.SetAccessKeyId(secretKeyID);

    Aws::IAM::Model::GetAccessKeyLastUsedOutcome outcome = iam.GetAccessKeyLastUsed(request);

    if (!outcome.IsSuccess())
    {
        std::cerr << "Error querying last used time for access key " <<
                                                                     secretKeyID << ":" << outcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        Aws::String lastUsedTimeString =
                outcome.GetResult()
                        .GetAccessKeyLastUsed()
                        .GetLastUsedDate()
                        .ToGmtString(Aws::Utils::DateFormat::ISO_8601);
        std::cout << "Access key " << secretKeyID << " last used at time " <<
                  lastUsedTimeString << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.access_key_last_used.code]

/*
 *
 *  main function
 *
 * Prerequisites: Existing key in Secrets Manager.
 *
 * Usage: 'run_access_key_last_used <access_key_id>'
 *
 */

#ifndef TESTING_BUILD
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: access_key_last_used <access_key_id>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        Aws::String keyId(argv[1]);
        AwsDoc::IAM::accessKeyLastUsed(keyId, clientConfig);
     }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif // TESTING_BUILD
