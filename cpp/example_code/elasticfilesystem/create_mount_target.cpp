// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_mount_target.cpp demonstrates how to create a mount target for an Amazon Elastic File System.]
*/
//snippet-start:[efs.cpp.create_mount_target.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/CreateMountTargetRequest.h>
#include <aws/elasticfilesystem/model/CreateMountTargetResult.h>
#include <iostream>
//snippet-end:[efs.cpp.create_mount_target.inc]

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

    //snippet-start:[efs.cpp.create_mount_target]
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
     //snippet-end:[efs.cpp.create_mount_target]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
