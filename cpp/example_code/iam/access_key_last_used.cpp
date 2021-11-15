 
//snippet-sourcedescription:[access_key_last_used.cpp demonstrates how to retrieve information about the last time an IAM access key was used.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
//snippet-start:[iam.cpp.access_key_last_used.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetAccessKeyLastUsedRequest.h>
#include <aws/iam/model/GetAccessKeyLastUsedResult.h>
#include <iostream>
//snippet-end:[iam.cpp.access_key_last_used.inc]

/**
 * Displays the time an access key was last used, based on command line input
 */
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
        Aws::String key_id(argv[1]);

        // snippet-start:[iam.cpp.access_key_last_used.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::GetAccessKeyLastUsedRequest request;

        request.SetAccessKeyId(key_id);

        auto outcome = iam.GetAccessKeyLastUsed(request);

        if (!outcome.IsSuccess())
        {
            std::cout << "Error querying last used time for access key " <<
                key_id << ":" << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            auto lastUsedTimeString =
                outcome.GetResult()
                .GetAccessKeyLastUsed()
                .GetLastUsedDate()
                .ToGmtString(Aws::Utils::DateFormat::ISO_8601);
            std::cout << "Access key " << key_id << " last used at time " <<
                lastUsedTimeString << std::endl;
        }
        // snippet-end:[iam.cpp.access_key_last_used.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

