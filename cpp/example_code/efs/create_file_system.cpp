 
//snippet-sourcedescription:[create_file_system.cpp demonstrates how to create an Amazon Elastic File System.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
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
#include <aws/elasticfilesystem/model/CreateFileSystemRequest.h>
#include <aws/elasticfilesystem/model/CreateFileSystemResult.h>
#include <iostream>

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
  }

  Aws::ShutdownAPI(options);
  return 0;
}
