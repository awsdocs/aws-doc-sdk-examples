#include <aws/core/auth/AWSCredentialsProviderChain.h>
#include <aws/s3-encryption/S3EncryptionClient.h>
#include <aws/s3-encryption/CryptoConfiguration.h>
#include <aws/s3-encryption/materials/KMSEncryptionMaterials.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/core/http/Scheme.h>

using namespace Aws::S3;
using namespace Aws::S3::Model;
using namespace Aws::S3Encryption;
using namespace Aws::S3Encryption::Materials;

static const char* const KEY = "<your_key_here>";
static const char* const BUCKET = "<your_bucket_here>";
static const char* const CUSTOMER_MASTER_KEY_ID = "<your_master_key_id_here>";

int main()
{
    Aws::SDKOptions options;
   	options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    Aws::InitAPI(options);
    {
        auto kmsMaterials = Aws::MakeShared<KMSEncryptionMaterials>("s3Encryption", CUSTOMER_MASTER_KEY_ID);
		#ifdef UNDER_MACOS
            CryptoConfiguration cryptoConfiguration(StorageMethod::INSTRUCTION_FILE, CryptoMode::ENCRYPTION_ONLY);
        #else
            CryptoConfiguration cryptoConfiguration(StorageMethod::INSTRUCTION_FILE, CryptoMode::STRICT_AUTHENTICATED_ENCRYPTION);
        #endif

        auto credentials = Aws::MakeShared<Aws::Auth::DefaultAWSCredentialsProviderChain>("s3Encryption");

        //construct S3 encryption client
        S3EncryptionClient encryptionClient(kmsMaterials, cryptoConfiguration, credentials);

        auto requestStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
        *requestStream << "Hello from the S3 Encryption Client!";

        CreateBucketRequest createBucketRequest;
        createBucketRequest.SetBucket(BUCKET);
        createBucketRequest.SetACL(BucketCannedACL::private_);
        CreateBucketOutcome createBucketOutcome = encryptionClient.CreateBucket(createBucketRequest);
        if (!createBucketOutcome.IsSuccess())
        {
            std::cout << "Bucket Creation failed\n";
            exit(-1);
        }
        else
        {
            std::cout << "Bucket Creation succ!\n";
        }

        //put an encrypted object to S3
        PutObjectRequest putObjectRequest;
        putObjectRequest.WithBucket(BUCKET)
            .WithKey(KEY).SetBody(requestStream);

        auto putObjectOutcome = encryptionClient.PutObject(putObjectRequest);

        if (putObjectOutcome.IsSuccess())
        {
            std::cout << "Put object succeeded" << std::endl;
        }
        else
        {
            std::cout << "Error while putting Object " << putObjectOutcome.GetError().GetExceptionName() <<
                " " << putObjectOutcome.GetError().GetMessage() << std::endl;
        }

        //get an encrypted object from S3
        GetObjectRequest getRequest;
        getRequest.WithBucket(BUCKET)
            .WithKey(KEY);

        auto getObjectOutcome = encryptionClient.GetObject(getRequest);
        if (getObjectOutcome.IsSuccess())
        {
            std::cout << "Successfully retrieved object from s3 with value: " << std::endl;
            std::cout << getObjectOutcome.GetResult().GetBody().rdbuf() << std::endl << std::endl;;
        }
        else
        {
            std::cout << "Error while getting object " << getObjectOutcome.GetError().GetExceptionName() <<
                " " << getObjectOutcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}
