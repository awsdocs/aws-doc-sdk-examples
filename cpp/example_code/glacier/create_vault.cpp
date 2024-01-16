// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
