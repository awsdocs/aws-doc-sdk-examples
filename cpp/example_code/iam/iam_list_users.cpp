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
#include <aws/iam/model/ListUsersRequest.h>
#include <aws/iam/model/ListUsersResult.h>

#include <iostream>

static const char* SIMPLE_DATE_FORMAT_STR = "%Y-%m-%d";

/**
 * Lists all iam users
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iam_client;

    Aws::IAM::Model::ListUsersRequest listUsersRequest;

    bool done = false;
    bool header = false;
    while(!done)
    {
        auto listUsersOutcome = iam_client.ListUsers(listUsersRequest);
        if(!listUsersOutcome.IsSuccess())
        {
            std::cout << "Failed to list iam users:" << listUsersOutcome.GetError().GetMessage() << std::endl;
            break;
        }

        if(!header)
        {
            std::cout << std::left << std::setw(32) << "Name" 
                                   << std::setw(30) << "ID" 
                                   << std::setw(64) << "Arn" 
                                   << std::setw(20) << "CreateDate" << std::endl;
            header = true;
        }

        const auto& users = listUsersOutcome.GetResult().GetUsers();
        for (const auto& user : users)
        {
            std::cout << std::left << std::setw(32) << user.GetUserName() 
                                   << std::setw(30) << user.GetUserId()
                                   << std::setw(64) << user.GetArn()
                                   << std::setw(20) << user.GetCreateDate().ToGmtString(SIMPLE_DATE_FORMAT_STR) << std::endl;
        }

        if(listUsersOutcome.GetResult().GetIsTruncated())
        {
            listUsersRequest.SetMarker(listUsersOutcome.GetResult().GetMarker());
        }
        else
        {
            done = true;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



