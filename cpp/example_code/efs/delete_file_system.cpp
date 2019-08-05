 
//snippet-sourcedescription:[delete_file_system.cpp demonstrates how to delete an Amazon Elastic File System.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic File System]
//snippet-service:[elasticfilesystem]
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
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/DeleteFileSystemRequest.h>
#include <iostream>

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
  }

  Aws::ShutdownAPI(options);
  return 0;
}
