// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_file_system.cpp demonstrates how to delete an Amazon Elastic File System.]
*/

//snippet-start:[efs.cpp.delete_file_system.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/DeleteFileSystemRequest.h>
#include <iostream>
//snippet-end:[efs.cpp.delete_file_system.inc]

/**
 * Deletes file system based on command line input
 */
int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_file_system <file_system_id>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String file_system_id(argv[1]);
    //snippet-start:[efs.cpp.delete_file_system]
    Aws::EFS::EFSClient efs;

    Aws::EFS::Model::DeleteFileSystemRequest dfs_req;

    dfs_req.SetFileSystemId(file_system_id);

    auto dfs_out = efs.DeleteFileSystem(dfs_req);

    if (dfs_out.IsSuccess())
    {
      std::cout << "Successfully deleted file system " << std::endl;
    }

    else
    {
      std::cout << "Error deleting file system " << dfs_out.GetError().GetMessage()
        << std::endl;
    }
        //snippet-end:[efs.cpp.delete_file_system]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
