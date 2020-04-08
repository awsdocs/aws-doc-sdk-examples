
//snippet-sourcedescription:[create_file_system_with_modeled_exception.cpp demonstrates how to get modeled exception from CreateFileSystem operation outout.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic File System]
//snippet-service:[elasticfilesystem]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/elasticfilesystem/EFSClient.h>
#include <aws/elasticfilesystem/model/CreateFileSystemRequest.h>
#include <aws/elasticfilesystem/model/DescribeFileSystemsRequest.h>
#include <aws/elasticfilesystem/model/DeleteFileSystemRequest.h>
#include <aws/elasticfilesystem/model/FileSystemAlreadyExists.h>

using namespace Aws;
using namespace Aws::Http;
using namespace Aws::Client;
using namespace Aws::EFS;
using namespace Aws::EFS::Model;

static const char FILE_SYSTEM_CREATION_TOKEN[] = "file-system-creation-token-test";

Aws::String CreateFileSystem(const Aws::EFS::EFSClient efsClient)
{
    Aws::String fileSystemId = "";
    CreateFileSystemRequest createFileSystemRequest;
    createFileSystemRequest.SetCreationToken(FILE_SYSTEM_CREATION_TOKEN);
    auto createFileSystemOutcome = efsClient.CreateFileSystem(createFileSystemRequest);

    if (createFileSystemOutcome.IsSuccess())
    {
        fileSystemId = createFileSystemOutcome.GetResult().GetFileSystemId();
        std::cout << "Succeeded to create file system with ID: " << createFileSystemOutcome.GetResult().GetFileSystemId() << std::endl;
    }
    else if (createFileSystemOutcome.GetError().GetErrorType() == EFSErrors::FILE_SYSTEM_ALREADY_EXISTS)
    {
        std::cout << "File system with ID: " << createFileSystemOutcome.GetError<FileSystemAlreadyExists>().GetFileSystemId() << " already exists." << std::endl;
        std::cout << "Failed to create file system. Error details:" << std::endl;
        std::cout << createFileSystemOutcome.GetError() << std::endl;
    }
    else
    {
        std::cout << "Failed to create file system. Error details:" << std::endl;
        std::cout << createFileSystemOutcome.GetError() << std::endl;
    }

    return fileSystemId;
}

int main(int argc, char *argv[])
{
    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Trace;
    InitAPI(options);
    {
        Aws::EFS::EFSClient efsClient;
        std::cout << "The first request to create file system:" << std::endl;
        Aws::String fileSystemId = CreateFileSystem(efsClient);
        if (fileSystemId.empty())
        {
            ShutdownAPI(options);
            return 1;
        }

        // Wait for the new file system to be propagated and visible.
        unsigned timeoutCount = 0;
        while (timeoutCount++ < 30)
        {
            DescribeFileSystemsRequest describeFileSystemsRequest;
            describeFileSystemsRequest.SetFileSystemId(fileSystemId);
            auto describeFileSystemsOutcome = efsClient.DescribeFileSystems(describeFileSystemsRequest);
            if (describeFileSystemsOutcome.IsSuccess())
            {
                break;
            }

            std::this_thread::sleep_for(std::chrono::seconds(1));
        }

        std::cout << "\n" << "The second request to create file system with the same token:" << std::endl;
        CreateFileSystem(efsClient);

        std::cout << "\n" << "The third request to delete file system:" << std::endl;
        DeleteFileSystemRequest deleteFileSystemRequest;
        deleteFileSystemRequest.SetFileSystemId(fileSystemId);
        auto deleteFileSystemOutcome = efsClient.DeleteFileSystem(deleteFileSystemRequest);
        if (deleteFileSystemOutcome.IsSuccess())
        {
            std::cout << "Succeeded to delete file system" << std::endl;
        }
        else
        {
            std::cout << "Failed to delete file system. Error details:" << std::endl;
            std::cout << deleteFileSystemOutcome.GetError() << std::endl;
        }
    }

    ShutdownAPI(options);
    return 0;
}