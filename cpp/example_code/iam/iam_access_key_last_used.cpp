/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetAccessKeyLastUsedRequest.h>
#include <aws/iam/model/GetAccessKeyLastUsedResult.h>

#include <iostream>

/**
 * Displays the time an access key was last used, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: iam_access_key_last_used <access_key_id>" << std::endl;
        return 1;
    }

    Aws::String accessKeyId(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iamClient;

        Aws::IAM::Model::GetAccessKeyLastUsedRequest getAccessKeyLastUsedRequest;
        getAccessKeyLastUsedRequest.SetAccessKeyId(accessKeyId);

        auto getAccessKeyLastUsedOutcome = iamClient.GetAccessKeyLastUsed(getAccessKeyLastUsedRequest);
        if (!getAccessKeyLastUsedOutcome.IsSuccess())
        {
            std::cout << "Error querying last used time for access key " << accessKeyId << ":" <<
            getAccessKeyLastUsedOutcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            auto lastUsedTimeString = getAccessKeyLastUsedOutcome.GetResult().GetAccessKeyLastUsed().GetLastUsedDate().ToGmtString(
                    Aws::Utils::DateFormat::ISO_8601);
            std::cout << "Access key " << accessKeyId << " last used at time " << lastUsedTimeString << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



