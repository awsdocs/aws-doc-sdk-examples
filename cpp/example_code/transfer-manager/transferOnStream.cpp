// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
  Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

  This file is licensed under the Apache License, Version 2.0 (the "License").
  You may not use this file except in compliance with the License. A copy of
  the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
 **/

#include <aws/core/Aws.h>
#include <aws/core/utils/threading/Executor.h>
#include <aws/transfer/TransferManager.h>
#include <aws/transfer/TransferHandle.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/memory/AWSMemory.h>
#include <aws/core/utils/memory/stl/AWSStreamFwd.h>
#include <aws/core/utils/stream/PreallocatedStreamBuf.h>
#include <aws/core/utils/StringUtils.h>
#include <fstream>

using namespace std;
using namespace Aws;
using namespace Aws::Utils;
using namespace Aws::S3;

static const size_t BUFFER_SIZE = 512 * 1024 * 1024; // 512MB Buffer 

/**
 * In-memory stream implementation
 */
class MyUnderlyingStream : public Aws::IOStream
{
    public:
        using Base = Aws::IOStream;
        // Provide a customer-controlled streambuf to hold data from the bucket.
        explicit MyUnderlyingStream(std::streambuf* buf)
            : Base(buf)
        {}

        ~MyUnderlyingStream() override = default;
};

int main(int argc, char** argv)
{
    if (argc < 4) 
    {
        std::cout << "This program is used to demonstrate how transfer manager transfers large object in memory without copying it to a local file." << std::endl
            << "It first uploads [LocalFilePath] to your Amazon S3 [Bucket] with object name [Key], then downloads the object to memory." << std::endl
            << "To verify the correctness of the file content in memory, the program will dump the data to a local file [LocalFilePath]_copy." << std::endl
            << "You can use md5sum [LocalFilePath] [LocalFilePath]_copy to verify they have the same content." << std::endl
            << "\tUsage: " << argv[0] << " [Bucket] [Key] [LocalFilePath]" << std::endl;
        return -1;
    }

    const char* BUCKET = argv[1];
    const char* KEY = argv[2];
    const char* LOCAL_FILE = argv[3];
    Aws::String LOCAL_FILE_COPY(LOCAL_FILE);
    LOCAL_FILE_COPY += "_copy";

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        // snippet-start:[transfer-manager.cpp.transferOnStream.code]
        auto s3_client = Aws::MakeShared<Aws::S3::S3Client>("S3Client");
        auto executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>("executor", 25);
        Aws::Transfer::TransferManagerConfiguration transfer_config(executor.get());
        transfer_config.s3Client = s3_client;

        // Create buffer to hold data received by the data stream.
        Aws::Utils::Array<unsigned char> buffer(BUFFER_SIZE);

        // The local variable 'streamBuffer' is captured by reference in a lambda.
        // It must persist until all downloading by the 'transfer_manager' is complete.
        Stream::PreallocatedStreamBuf streamBuffer(buffer.GetUnderlyingData(), buffer.GetLength());

        auto transfer_manager = Aws::Transfer::TransferManager::Create(transfer_config);

        auto uploadHandle = transfer_manager->UploadFile(LOCAL_FILE, BUCKET, KEY, "text/plain", Aws::Map<Aws::String, Aws::String>());
        uploadHandle->WaitUntilFinished();
        bool success = uploadHandle->GetStatus() == Transfer::TransferStatus::COMPLETED; 
      
        if (!success)
        {
            auto err = uploadHandle->GetLastError();           
            std::cout << "File upload failed:  "<< err.GetMessage() << std::endl;
        }
        else
        {
            std::cout << "File upload finished." << std::endl;

            auto downloadHandle = transfer_manager->DownloadFile(BUCKET,
                KEY,
                [&]() { //Define a lambda expression for the callback method parameter to stream back the data.
                    return Aws::New<MyUnderlyingStream>("TestTag", &streamBuffer);
                });
            downloadHandle->WaitUntilFinished();// Block calling thread until download is complete.
            auto downStat = downloadHandle->GetStatus();
            if (downStat != Transfer::TransferStatus::COMPLETED)
            {
                auto err = downloadHandle->GetLastError();
                std::cout << "File download failed:  " << err.GetMessage() << std::endl;
            }
            std::cout << "File download to memory finished."  << std::endl;
            // snippet-end:[transfer-manager.cpp.transferOnStream.code]
             
            
            // Verify the download retrieved the expected length of data.
            assert(downloadHandle->GetBytesTotalSize() == downloadHandle->GetBytesTransferred());

            // Write the buffered data to local file copy.
            Aws::OFStream storeFile(LOCAL_FILE_COPY.c_str(), Aws::OFStream::out | Aws::OFStream::trunc);
            storeFile.write((const char*)(buffer.GetUnderlyingData()),
                            static_cast<std::streamsize>(downloadHandle->GetBytesTransferred()));
            storeFile.close();

            std::cout << "File dumped to local file finished. You can verify the two files' content using md5sum." << std::endl;
            // Verify the upload file is the same as the downloaded copy. This can be done using 'md5sum' or any other file compare tool.
        }
    }
    Aws::ShutdownAPI(options);
}
