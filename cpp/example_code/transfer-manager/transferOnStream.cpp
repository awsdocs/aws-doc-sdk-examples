//snippet-sourcedescription:[transferOnStream.cpp demonstrates how to transfer an S3 object using stream into local memory and verify the correctness of the transfer]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourceauthor:[AWS]

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
static size_t g_file_size = 0;

/**
 * In memory stream implementation
 */
class MyUnderlyingStream : public Aws::IOStream
{
    public:
        using Base = Aws::IOStream;
        // provide a customer controlled streambuf, so as to put all transfered data into this in memory buffer.
        MyUnderlyingStream(std::streambuf* buf)
            : Base(buf)
        {}

        virtual ~MyUnderlyingStream() = default;
};

int main(int argc, char** argv)
{
    if (argc < 4) 
    {
        std::cout << "This program is used to demostrate how transfer manager transfers large object in memory without copying it to a local file." << std::endl
            << "It first uploads [LocalFilePath] to your S3 [Bucket] with object name [Key], then downloads the object to memory." << std::endl
            << "To verify the correctness of the file content in memory, we will dump the data to a local file [LocalFilePath]_copy." << std::endl
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
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    Aws::InitAPI(options);
    {
        auto s3_client = Aws::MakeShared<Aws::S3::S3Client>("S3Client");
        auto executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>("executor", 25);
        Aws::Transfer::TransferManagerConfiguration transfer_config(executor.get());
        transfer_config.s3Client = s3_client;

        auto transfer_manager = Aws::Transfer::TransferManager::Create(transfer_config);

        auto uploadHandle = transfer_manager->UploadFile(LOCAL_FILE, BUCKET, KEY, "text/plain", Aws::Map<Aws::String, Aws::String>());
        uploadHandle->WaitUntilFinished();
        std::cout << "File upload finished!" << std::endl;

        // verify upload expected length of data
        assert(uploadHandle->GetBytesTotalSize() == uploadHandle->GetBytesTransferred());
        g_file_size = uploadHandle->GetBytesTotalSize();

        // This buffer is what we used to initialize streambuf and is in memory
        Aws::Utils::Array<unsigned char> buffer(BUFFER_SIZE);
        auto downloadHandle = transfer_manager->DownloadFile(BUCKET,
                KEY, 
                [&]() { //create stream lambda fn
                    return Aws::New<MyUnderlyingStream>("TestTag", Aws::New<Stream::PreallocatedStreamBuf>("TestTag", buffer.GetUnderlyingData(), BUFFER_SIZE));
                });
        downloadHandle->WaitUntilFinished();
        std::cout << "File download to memory finished!" << std::endl;

        // verify download expected length of data
        assert(downloadHandle->GetBytesTotalSize() == downloadHandle->GetBytesTransferred());

        // verify length of upload equals download 
        assert(uploadHandle->GetBytesTotalSize() == downloadHandle->GetBytesTotalSize());

        // write buffered data to local file copy
        Aws::OFStream storeFile(LOCAL_FILE_COPY.c_str(), Aws::OFStream::out | Aws::OFStream::trunc);
        storeFile.write((const char*)(buffer.GetUnderlyingData()), downloadHandle->GetBytesTransferred());
        storeFile.close();
        std::cout << "File dumpped to local file finished! You can verify the two files' content using md5sum." << std::endl;

        // verify upload file is the same as downloaded copy. I did that simply using `md5sum file file_copy`
    }
    Aws::ShutdownAPI(options);
}
