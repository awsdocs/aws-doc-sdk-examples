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

static const char BUCKET[] = "tangandr-dev-test";
static const char KEY[] = "TestFile104MB";
static const char LOCAL_FILE[] = "/home/singku/aws-sdk-cpp-test/build/TestFile104MB";
static const char LOCAL_FILE_COPY[] = "/home/singku/aws-sdk-cpp-test/build/TestFile104MB_copy";
static const size_t BUFFER_SIZE = 512 * 1024 * 1024; // 512MB Buffer 

static size_t g_file_size = 0;

class MyUnderlyingStream : public Aws::IOStream
{
    public:
        using Base = Aws::IOStream;
        MyUnderlyingStream(std::streambuf* buf)
        	: Base(buf)
        {}

        virtual ~MyUnderlyingStream() = default;
};

int main()
{
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

        // verify upload expected length of data
        assert(uploadHandle->GetBytesTotalSize() == uploadHandle->GetBytesTransferred());
        g_file_size = uploadHandle->GetBytesTotalSize();

        Aws::Utils::Array<unsigned char> buffer(BUFFER_SIZE);
        auto downloadHandle = transfer_manager->DownloadFile(BUCKET,
                                                KEY, 
                                                [&]() { //create stream lambda fn
                                                    return Aws::New<MyUnderlyingStream>("TestTag", Aws::New<Stream::PreallocatedStreamBuf>("TestTag", buffer.GetUnderlyingData(), BUFFER_SIZE));
                                                }
        );
        downloadHandle->WaitUntilFinished();

        // verify download expected length of data
        assert(downloadHandle->GetBytesTotalSize() == downloadHandle->GetBytesTransferred());

        // verify length of upload equals download 
        assert(uploadHandle->GetBytesTotalSize() == downloadHandle->GetBytesTotalSize());


        // write buffered data to local file copy
        Aws::OFStream storeFile(LOCAL_FILE_COPY, Aws::OFStream::out | Aws::OFStream::trunc);
        storeFile.write((const char*)(buffer.GetUnderlyingData()), downloadHandle->GetBytesTransferred());
        storeFile.close();

        // verify upload file is the same as downloaded copy. I did that simply using `md5sum file file_copy`
    }
    Aws::ShutdownAPI(options);
}
