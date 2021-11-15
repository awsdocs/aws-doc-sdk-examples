// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_file_system.cpp demonstrates how to create an Amazon Elastic File System.]

*/
//snippet-start:[efs.cpp.create_file_system.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/CreateFileSystemRequest.h>
#include <aws/elasticfilesystem/model/CreateFileSystemResult.h>
#include <iostream>
//snippet-end:[efs.cpp.create_file_system.inc]

/**
 * Creates file system based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_file_system <creation_token>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String creation_token(argv[1]);
    //snippet-start:[efs.cpp.create_file_system]
    Aws::EFS::EFSClient efs;

    Aws::EFS::Model::CreateFileSystemRequest cfs_req;

    cfs_req.SetCreationToken(creation_token);
    auto cfs_out = efs.CreateFileSystem(cfs_req);

    if (cfs_out.IsSuccess())
    {
      std::cout << "Successfully created file system " << std::endl;
    }

    else
    {
      std::cout << "Error creating file system " << cfs_out.GetError().GetMessage()
        << std::endl;
    }
        //snippet-end:[efs.cpp.create_file_system]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
