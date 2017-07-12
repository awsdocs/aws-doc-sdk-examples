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
#include <iomanip>
#include <iostream>

static const char* DATE_FORMAT = "%Y-%m-%d";

/**
 * Lists all iam users
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::ListUsersRequest request;

        bool done = false;
        bool header = false;
        while (!done)
        {
            auto outcome = iam.ListUsers(request);
            if (!outcome.IsSuccess()) {
                std::cout << "Failed to list iam users:" <<
                    outcome.GetError().GetMessage() << std::endl;
                break;
            }

            if (!header) {
                std::cout << std::left << std::setw(32) << "Name" <<
                    std::setw(30) << "ID" << std::setw(64) << "Arn" <<
                    std::setw(20) << "CreateDate" << std::endl;
                header = true;
            }

            const auto &users = outcome.GetResult().GetUsers();
            for (const auto &user : users) {
                std::cout << std::left << std::setw(32) << user.GetUserName() <<
                    std::setw(30) << user.GetUserId() << std::setw(64) <<
                    user.GetArn() << std::setw(20) <<
                    user.GetCreateDate().ToGmtString(DATE_FORMAT) << std::endl;
            }

            if (outcome.GetResult().GetIsTruncated()) {
                request.SetMarker(outcome.GetResult().GetMarker());
            } else {
                done = true;
            }
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

