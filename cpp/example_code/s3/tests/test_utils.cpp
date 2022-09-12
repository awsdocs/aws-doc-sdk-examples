/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "test_utils.h"
#include <aws/iam/IAMClient.h>

Aws::String AwsTest::TestUtils::getArnForUser(const Aws::Client::ClientConfiguration &clientConfig)
{
    Aws::IAM::IAMClient client(clientConfig);

    Aws::IAM::Model::GetUserRequest request;
    Aws::IAM::Model::GetUserOutcome outcome = client.GetUser(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error getting Iam user. " <<
                  outcome.GetError().GetMessage() << std::endl;

        return "";
    }

    return outcome.GetResult().GetUser().GetArn();
}