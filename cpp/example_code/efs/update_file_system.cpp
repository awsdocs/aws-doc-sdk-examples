//snippet-sourcedescription:[update_file_system.cpp demonstrates how to update the configuration of an Amazon Elastic File System.]
//snippet-service:[elasticfilesystem]
//snippet-keyword:[Amazon Elastic File System]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
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
