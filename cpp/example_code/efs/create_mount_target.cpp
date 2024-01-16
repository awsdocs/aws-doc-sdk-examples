// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/CreateMountTargetRequest.h>
#include <aws/elasticfilesystem/model/CreateMountTargetResult.h>
#include <iostream>

/**
 * Creates mount target based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: create_mount_target <file_system_id> <subnet_id>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String file_system_id(argv[1]);
    Aws::String subnet_id(argv[2]);
    Aws::EFS::EFSClient efs;

    Aws::EFS::Model::CreateMountTargetRequest cmt_req;

    cmt_req.SetFileSystemId(file_system_id);
    cmt_req.SetSubnetId(subnet_id);

    auto cmt_out = efs.CreateMountTarget(cmt_req);

    if (cmt_out.IsSuccess())
    {
      std::cout << "Successfully created mount target " << std::endl;
    }

    else
    {
      std::cout << "Error creating mount target" << cmt_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
