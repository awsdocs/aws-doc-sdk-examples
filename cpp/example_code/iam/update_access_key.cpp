/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment,
 * including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates updating the status (active/inactive) of an IAM user's access key.
 *
 */

//snippet-start:[iam.cpp.update_access_key.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateAccessKeyRequest.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.update_access_key.inc]

static void printUsage();

//! Updates the status (active/inactive) of an IAM user's access key.
/*!
  \sa updateAccessKey()
  \param userName: The user's name.
  \param accessKeyID: The access key ID.
  \param status: The access key status.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

// snippet-start:[iam.cpp.update_access_key.code]
bool AwsDoc::IAM::updateAccessKey(const Aws::String &userName,
                                  const Aws::String &accessKeyID,
                                  Aws::IAM::Model::StatusType status,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::UpdateAccessKeyRequest request;
    request.SetUserName(userName);
    request.SetAccessKeyId(accessKeyID);
    request.SetStatus(status);

    auto outcome = iam.UpdateAccessKey(request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully updated status of access key "
                  << accessKeyID << " for user " << userName << std::endl;
    }
    else {
        std::cerr << "Error updated status of access key " << accessKeyID <<
                  " for user " << userName << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.update_access_key.code]

/*
 *
 *  main function
 *
 * Prerequisites: Existing access key.
 *
 * Usage: 'run_update_access_key  <user_name> <access_key_id> <Active|Inactive>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc != 4)
    {
        printUsage();
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String userName(argv[1]);
        Aws::String accessKeyId(argv[2]);
        Aws::String keyStatus(argv[3]);

        Aws::IAM::Model::StatusType status =
            Aws::IAM::Model::StatusTypeMapper::GetStatusTypeForName(argv[3]);

        if (status == Aws::IAM::Model::StatusType::NOT_SET)
        {
            printUsage();
            return 1;
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::updateAccessKey(userName, accessKeyId, status, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif


void printUsage() {
    std::cout <<
              "Usage: run_update_access_key <user_name> <access_key_id> <Active|Inactive>"
              << std::endl;
}
