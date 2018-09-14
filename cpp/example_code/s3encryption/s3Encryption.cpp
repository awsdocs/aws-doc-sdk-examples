/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    specific language governing permissions and limitations under the License.
*/
#include <aws/core/auth/AWSCredentialsProviderChain.h>
#include <aws/core/http/Scheme.h>
#include <aws/s3-encryption/S3EncryptionClient.h>
#include <aws/s3-encryption/CryptoConfiguration.h>
#include <aws/s3-encryption/materials/KMSEncryptionMaterials.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/S3Client.h>

using namespace Aws::S3;
using namespace Aws::S3::Model;
using namespace Aws::S3Encryption;
using namespace Aws::S3Encryption::Materials;


int main(int argc, char** argv)
{
    if (argc < 4) {
        std::cout << "\n" <<
            "To run this example, supply the name (key) of an S3 object,\n"
            "the bucket name that it's contained within,\n"
            "and the master key id created from IAM\n\n"
            "Ex: s3Encryption <objectname> <bucketname> <master_key_id>\n";
        exit(1);
    }

    const char* KEY = argv[1];
    const char* BUCKET = argv[2];
    const char* MASTER_KEY_ID = argv[3];

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    Aws::InitAPI(options);
    {
        // Create the specified bucket using vanilla S3 client first
        S3Client s3Client;
        CreateBucketRequest createBucketRequest;
        createBucketRequest.SetBucket(BUCKET);
        createBucketRequest.SetACL(BucketCannedACL::private_);
        CreateBucketOutcome createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

        if (!createBucketOutcome.IsSuccess()) {
            std::cout << "Bucket Creation failed: " << createBucketOutcome.GetError() << "\n";
            exit(-1);
        } else {
            std::cout << "Bucket Creation succeeded!\n";
        }

        const auto kmsMaterials = Aws::MakeShared<KMSEncryptionMaterials>("", MASTER_KEY_ID);
        const auto credentials = Aws::MakeShared<Aws::Auth::DefaultAWSCredentialsProviderChain>("");

#ifdef UNDER_MACOS
        CryptoConfiguration cryptoConfiguration(StorageMethod::INSTRUCTION_FILE, CryptoMode::ENCRYPTION_ONLY);
#else
        CryptoConfiguration cryptoConfiguration(StorageMethod::INSTRUCTION_FILE,
                CryptoMode::STRICT_AUTHENTICATED_ENCRYPTION);
#endif

        //construct S3 encryption client
        S3EncryptionClient encryptionClient(kmsMaterials, cryptoConfiguration, credentials);

        auto requestStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
        *requestStream << "Hello from the S3 Encryption Client!";

        //put an encrypted object to S3
        PutObjectRequest putObjectRequest;
        putObjectRequest.WithBucket(BUCKET).WithKey(KEY).SetBody(requestStream);

        auto putObjectOutcome = encryptionClient.PutObject(putObjectRequest);

        if (putObjectOutcome.IsSuccess()) {
            std::cout << "Put object succeeded\n";
        } else {
            std::cout << "Error while putting Object " << putObjectOutcome.GetError() << "\n";
        }

        //get an encrypted object from S3
        GetObjectRequest getRequest;
        getRequest.WithBucket(BUCKET).WithKey(KEY);

        auto getObjectOutcome = encryptionClient.GetObject(getRequest);
        if (getObjectOutcome.IsSuccess()) {
            std::cout << "Successfully retrieved object with avalue: \n";
            std::cout << getObjectOutcome.GetResult().GetBody().rdbuf() << "\n";
        } else {
            std::cout << "Error while getting object " << getObjectOutcome.GetError() << "\n";
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
