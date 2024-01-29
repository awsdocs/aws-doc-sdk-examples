// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/UpdateFileSystemRequest.h>
#include <aws/elasticfilesystem/model/UpdateFileSystemResult.h>
#include <iostream>

/**
 * Update file system based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: update_file_system <file_system_id> <throughput_mode>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String file_system_id(argv[1]);
    Aws::String throughput_mode(argv[2]);
    Aws::EFS::EFSClient efs;

    Aws::EFS::Model::UpdateFileSystemRequest ufs_req;

    if (throughput_mode == "bursting")
    {
      ufs_req.SetThroughputMode(Aws::EFS::Model::ThroughputMode::bursting);
    }
    else if (throughput_mode == "provisioned")
    {
      ufs_req.SetThroughputMode(Aws::EFS::Model::ThroughputMode::provisioned);
    }
    else
    {
      ufs_req.SetThroughputMode(Aws::EFS::Model::ThroughputMode::NOT_SET);
    }

    ufs_req.SetFileSystemId(file_system_id);

    auto ufs_out = efs.UpdateFileSystem(ufs_req);

    if (ufs_out.IsSuccess())
    {
      std::cout << "Successfully updated file system " << std::endl;
    }

    else
    {
      std::cout << "Error updating file system " << ufs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
