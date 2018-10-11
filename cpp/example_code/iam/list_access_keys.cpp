 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/iam/model/ListAccessKeysRequest.h>
#include <aws/iam/model/ListAccessKeysResult.h>
#include <iomanip>
#include <iostream>
#include <iomanip> 

static const char* DATE_FORMAT = "%Y-%m-%d";

/**
 * Lists all access keys associated with an IAM user
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: list_access_keys <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String userName(argv[1]);

        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::ListAccessKeysRequest request;
        request.SetUserName(userName);

        bool done = false;
        bool header = false;
        while (!done)
        {
            auto outcome = iam.ListAccessKeys(request);
            if (!outcome.IsSuccess())
            {
                std::cout << "Failed to list access keys for user " << userName
                    << ": " << outcome.GetError().GetMessage() << std::endl;
                break;
            }

            if (!header)
            {
                std::cout << std::left << std::setw(32) << "UserName" <<
                    std::setw(30) << "KeyID" << std::setw(20) << "Status" <<
                    std::setw(20) << "CreateDate" << std::endl;
                header = true;
            }

            const auto &keys = outcome.GetResult().GetAccessKeyMetadata();
            for (const auto &key : keys)
            {
                Aws::String statusString =
                    Aws::IAM::Model::StatusTypeMapper::GetNameForStatusType(
                        key.GetStatus());
                std::cout << std::left << std::setw(32) << key.GetUserName() <<
                    std::setw(30) << key.GetAccessKeyId() << std::setw(20) <<
                    statusString << std::setw(20) <<
                    key.GetCreateDate().ToGmtString(DATE_FORMAT) << std::endl;
            }

            if (outcome.GetResult().GetIsTruncated())
            {
                request.SetMarker(outcome.GetResult().GetMarker());
            }
            else
            {
                done = true;
            }
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

