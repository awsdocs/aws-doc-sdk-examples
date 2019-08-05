 
//snippet-sourcedescription:[create_vault.cpp demonstrates how to create an Amazon Glacier vault.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Glacier]
//snippet-service:[glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


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

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/glacier/GlacierClient.h>
#include <aws/glacier/model/CreateVaultRequest.h>
#include <aws/glacier/model/CreateVaultResult.h>
#include <iostream>

/**
 * Creates a glacier vault based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_vault <vault_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String vault_name(argv[1]);
    Aws::Glacier::GlacierClient glacier;

    Aws::Glacier::Model::CreateVaultRequest cv_req;
    cv_req.SetVaultName(vault_name);

    auto cv_out = glacier.CreateVault(cv_req);

    if (cv_out.IsSuccess())
    {
      std::cout << "Successfully created vault" << std::endl;
    }

    else
    {
      std::cout << "Error creating vault" << cv_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
