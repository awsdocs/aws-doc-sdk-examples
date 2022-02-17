//snippet-sourcedescription:[s3Encryption.cpp demonstrates how to perform client-side encryption on an Amazon S3 bucket object using AWS KMS.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#define AWS_DISABLE_DEPRECATION
#include <awsdoc/s3-encryption/s3_encryption_examples.h>
#include <aws/s3-encryption/S3EncryptionClient.h>
#include <aws/s3-encryption/CryptoConfiguration.h>
#include <aws/s3-encryption/materials/KMSEncryptionMaterials.h>
#include <aws/s3-encryption/materials/SimpleEncryptionMaterials.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/utils/crypto/Cipher.h>

using namespace Aws::S3;
using namespace Aws::S3::Model;
using namespace Aws::S3Encryption;
using namespace Aws::S3Encryption::Materials;

static const char ALLOCATION_TAG[] = "s3-encryption-examples";

bool AwsDoc::S3Encryption::KMSWithContextEncryptionMaterialsExample(const char* bucket, const char* objectKey, const char* masterKeyId)
{
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.region = Aws::Region::US_EAST_1;
    const auto kmsWithContextEncryptionMaterials = Aws::MakeShared<KMSWithContextEncryptionMaterials>(ALLOCATION_TAG, masterKeyId, clientConfig);
    CryptoConfigurationV2 cryptoConfig(kmsWithContextEncryptionMaterials);
    S3EncryptionClientV2 s3EncryptionClient(cryptoConfig, clientConfig);

    Aws::String content = "Hello from KMSWithContextEncryptionMaterials";
    auto contentStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
    *contentStream << content;

    // Put an encrypted object to S3
    PutObjectRequest putObjectRequest;
    putObjectRequest.WithBucket(bucket).WithKey(objectKey).SetBody(contentStream);

    Aws::Map<Aws::String, Aws::String> kmsContextMap;
    kmsContextMap.emplace("client", "aws-sdk-cpp");
    kmsContextMap.emplace("version", "1.8.0");
    auto putObjectOutcome = s3EncryptionClient.PutObject(putObjectRequest, kmsContextMap /* Or {} as an empty map */);

    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Put object with KMSWithContextEncryptionMaterials succeeded" << std::endl;
    } else {
        std::cout << "Error while putting object with KMSWithContextEncryptionMaterials: \n"
                  << putObjectOutcome.GetError() << std::endl;
        return false;
    }

    // Get an encrypted object from S3
    GetObjectRequest getRequest;
    getRequest.WithBucket(bucket).WithKey(objectKey);
    Aws::String decryptedContent;

    auto getObjectOutcome = s3EncryptionClient.GetObject(getRequest);
    if (getObjectOutcome.IsSuccess()) {
        Aws::StringStream ss;
        ss << getObjectOutcome.GetResult().GetBody().rdbuf();
        decryptedContent = ss.str();
        std::cout << "Successfully retrieved object with content: "
                  << decryptedContent << std::endl;;
    } else {
        std::cout << "Error while getting object: \n"
                  << getObjectOutcome.GetError() << std::endl;
        return false;
    }

    return decryptedContent == content;
}

bool AwsDoc::S3Encryption::SimpleEncryptionMaterialsWithGCMAADExample(const char* bucket, const char* objectKey)
{
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.region = Aws::Region::US_EAST_1;
    auto symmetricKey = Aws::Utils::Crypto::SymmetricCipher::GenerateKey();
    const auto simpleEncryptionMaterialsWithGCMAAD = Aws::MakeShared<SimpleEncryptionMaterialsWithGCMAAD>(ALLOCATION_TAG, symmetricKey);
    CryptoConfigurationV2 cryptoConfig(simpleEncryptionMaterialsWithGCMAAD);
    S3EncryptionClientV2 s3EncryptionClient(cryptoConfig, clientConfig);

    Aws::String content = "Hello from SimpleEncryptionMaterialsWithGCMAAD";
    auto contentStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
    *contentStream << content;

    // Put an encrypted object to S3
    PutObjectRequest putObjectRequest;
    putObjectRequest.WithBucket(bucket).WithKey(objectKey).SetBody(contentStream);

    // Has to specify an empty map here for SimpleEncryptionMaterialsWithGCMAAD.
    auto putObjectOutcome = s3EncryptionClient.PutObject(putObjectRequest, {});

    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Put object with SimpleEncryptionMaterialsWithGCMAAD succeeded" << std::endl;
    } else {
        std::cout << "Error while putting object with KMSWithContextEncryptionMaterials: \n"
                  << putObjectOutcome.GetError() << std::endl;
        return false;
    }

    //get an encrypted object from S3
    GetObjectRequest getRequest;
    getRequest.WithBucket(bucket).WithKey(objectKey);
    Aws::String decryptedContent;

    auto getObjectOutcome = s3EncryptionClient.GetObject(getRequest);
    if (getObjectOutcome.IsSuccess()) {
        Aws::StringStream ss;
        ss << getObjectOutcome.GetResult().GetBody().rdbuf();
        decryptedContent = ss.str();
        std::cout << "Successfully retrieved object with content: "
                  << decryptedContent << std::endl;
    } else {
        std::cout << "Error while getting object: \n"
                  << getObjectOutcome.GetError() << std::endl;
        return false;
    }

    return decryptedContent == content;
}

bool AwsDoc::S3Encryption::DecryptObjectsEncryptedWithLegacyEncryptionExample(const char* bucket, const char* objectKey)
{
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.region = Aws::Region::US_EAST_1;
    auto symmetricKey = Aws::Utils::Crypto::SymmetricCipher::GenerateKey();
    const auto simpleEncryptionMaterials = Aws::MakeShared<SimpleEncryptionMaterials>(ALLOCATION_TAG, symmetricKey);
    CryptoConfiguration cryptoConfig(CryptoMode::ENCRYPTION_ONLY);
    S3EncryptionClient s3EncryptionClient(simpleEncryptionMaterials, cryptoConfig, clientConfig);

    Aws::String content = "Hello from DecryptObjectsEncryptedWithLegacyEncryption";
    auto contentStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
    *contentStream << content;

    // Put an encrypted object to S3
    PutObjectRequest putObjectRequest;
    putObjectRequest.WithBucket(bucket).WithKey(objectKey).SetBody(contentStream);

    // Has to specify an empty map here for SimpleEncryptionMaterialsWithGCMAAD.
    auto putObjectOutcome = s3EncryptionClient.PutObject(putObjectRequest);

    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Put object with SimpleEncryptionMaterials and EncryptionOnly crypto mode succeeded" << std::endl;
    } else {
        std::cout << "Error while putting object with SimpleEncryptionMaterials and EncryptionOnly crypto mode: \n"
                  << putObjectOutcome.GetError() << std::endl;
        return false;
    }

    const auto simpleEncryptionMaterialsWithGCMAAD = Aws::MakeShared<SimpleEncryptionMaterialsWithGCMAAD>(ALLOCATION_TAG, symmetricKey);
    CryptoConfigurationV2 cryptoConfigV2(simpleEncryptionMaterialsWithGCMAAD);
    // By default, SecurityProfile is V2 for CryptoConfigurationV2. You need to specify V2_AND_LEGACY explicitly.
    // Otherwise S3EncryptionClientV2 is not able to decrypt objects encrypted with legacy (less secure) encryption.
    cryptoConfigV2.SetSecurityProfile(SecurityProfile::V2_AND_LEGACY);
    S3EncryptionClientV2 s3EncryptionClientV2(cryptoConfigV2, clientConfig);

    //get an encrypted object from S3
    GetObjectRequest getRequest;
    getRequest.WithBucket(bucket).WithKey(objectKey);
    Aws::String decryptedContent;

    auto getObjectOutcome = s3EncryptionClientV2.GetObject(getRequest);
    if (getObjectOutcome.IsSuccess()) {
        Aws::StringStream ss;
        ss << getObjectOutcome.GetResult().GetBody().rdbuf();
        decryptedContent = ss.str();
        std::cout << "Successfully retrieved object with content: "
                  << decryptedContent << std::endl;
    } else {
        std::cout << "Error while getting object: \n"
                  << getObjectOutcome.GetError() << std::endl;
        return false;
    }

    return decryptedContent == content;
}

bool AwsDoc::S3Encryption::DecryptObjectsWithRangeExample(const char* bucket, const char* objectKey)
{
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.region = Aws::Region::US_EAST_1;
    auto symmetricKey = Aws::Utils::Crypto::SymmetricCipher::GenerateKey();
    const auto simpleEncryptionMaterialsWithGCMAAD = Aws::MakeShared<SimpleEncryptionMaterialsWithGCMAAD>(ALLOCATION_TAG, symmetricKey);
    CryptoConfigurationV2 cryptoConfig(simpleEncryptionMaterialsWithGCMAAD);
    // By default, RangeGetMode is DISABLED for CryptoConfigurationV2. You need to specify RangeGetMode::ALL explicitly.
    // Otherwise S3EncryptionClientV2 is not able to decrypt objects with range.
    cryptoConfig.SetUnAuthenticatedRangeGet(RangeGetMode::ALL);
    S3EncryptionClientV2 s3EncryptionClient(cryptoConfig, clientConfig);

    Aws::String content = "Hello from DecryptObjectsWithRange";
    auto contentStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
    *contentStream << content;

    // Put an encrypted object to S3
    PutObjectRequest putObjectRequest;
    putObjectRequest.WithBucket(bucket).WithKey(objectKey).SetBody(contentStream);

    // Has to specify an empty map here for SimpleEncryptionMaterialsWithGCMAAD.
    auto putObjectOutcome = s3EncryptionClient.PutObject(putObjectRequest, {});

    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Put object with SimpleEncryptionMaterialsWithGCMAAD succeeded" << std::endl;
    } else {
        std::cout << "Error while putting object with KMSWithContextEncryptionMaterials: \n"
                  << putObjectOutcome.GetError() << std::endl;
        return false;
    }

    //get an encrypted object from S3
    GetObjectRequest getRequest;
    getRequest.WithBucket(bucket).WithKey(objectKey).WithRange("bytes=11-24");
    Aws::String decryptedContent;

    auto getObjectOutcome = s3EncryptionClient.GetObject(getRequest);
    if (getObjectOutcome.IsSuccess()) {
        Aws::StringStream ss;
        ss << getObjectOutcome.GetResult().GetBody().rdbuf();
        decryptedContent = ss.str();
        std::cout << "Successfully retrieved object with content range \"bytes=11-24\": "
                  << decryptedContent << std::endl;
    } else {
        std::cout << "Error while getting object: \n"
                  << getObjectOutcome.GetError() << std::endl;
        return false;
    }

    return decryptedContent == content.substr(11, 24-11+1);
}

bool AwsDoc::S3Encryption::DecryptObjectsWithAnyCMKExample(const char* bucket, const char* objectKey, const char* masterKeyId)
{
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.region = Aws::Region::US_EAST_1;
    const auto kmsWithContextEncryptionMaterials = Aws::MakeShared<KMSWithContextEncryptionMaterials>(ALLOCATION_TAG, masterKeyId, clientConfig);
    CryptoConfigurationV2 cryptoConfig(kmsWithContextEncryptionMaterials);
    S3EncryptionClientV2 s3EncryptionClient(cryptoConfig, clientConfig);

    Aws::String content = "Hello from KMSWithContextEncryptionMaterials";
    auto contentStream = Aws::MakeShared<Aws::StringStream>("s3Encryption");
    *contentStream << content;

    // Put an encrypted object to S3
    PutObjectRequest putObjectRequest;
    putObjectRequest.WithBucket(bucket).WithKey(objectKey).SetBody(contentStream);

    auto putObjectOutcome = s3EncryptionClient.PutObject(putObjectRequest, {});

    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Put object with KMSWithContextEncryptionMaterials succeeded" << std::endl;
    } else {
        std::cout << "Error while putting object with KMSWithContextEncryptionMaterials: \n"
                  << putObjectOutcome.GetError() << std::endl;
        return false;
    }

    const auto kmsWithContextEncryptionMaterialsWithAnyCMK = Aws::MakeShared<KMSWithContextEncryptionMaterials>(ALLOCATION_TAG, ""/* empty master key ID */, clientConfig);
    kmsWithContextEncryptionMaterialsWithAnyCMK->SetKMSDecryptWithAnyCMK(true);
    CryptoConfigurationV2 cryptoConfigWithAnyCMK(kmsWithContextEncryptionMaterialsWithAnyCMK);
    S3EncryptionClientV2 s3DecryptionClient(cryptoConfigWithAnyCMK, clientConfig);

    // Get an encrypted object from S3
    GetObjectRequest getRequest;
    getRequest.WithBucket(bucket).WithKey(objectKey);
    Aws::String decryptedContent;

    auto getObjectOutcome = s3DecryptionClient.GetObject(getRequest);
    if (getObjectOutcome.IsSuccess()) {
        Aws::StringStream ss;
        ss << getObjectOutcome.GetResult().GetBody().rdbuf();
        decryptedContent = ss.str();
        std::cout << "Successfully retrieved object with any CMK with content: "
                  << decryptedContent << std::endl;;
    } else {
        std::cout << "Error while getting object: \n"
                  << getObjectOutcome.GetError() << std::endl;
        return false;
    }

    return decryptedContent == content;
}

int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "\n" <<
            "To run this example, supply: \n"
            "(1) The name (key) prefix of an S3 object. The actual object key name will be generated based on the prefix, for example \"<key-prefix>-kms-with-context-encryption-materials\".\n"
            "(2) The bucket name that it's contained within.\n"
            "(3) And the KMS master key ID used for KMS encryption materials.\n\n"
            "Ex: run_s3Encryption my-key-prefix my-bucket 1234abcd-12ab-34cd-56ef-1234567890ab" << std::endl;
        exit(1);
    }

    const char* OBJECT_KEY_PREFIX = argv[1];
    const char* BUCKET = argv[2];
    const char* MASTER_KEY_ID = argv[3];

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    Aws::InitAPI(options);
    {
        // Create the specified bucket using vanilla S3 client first
        Aws::Client::ClientConfiguration config;
        config.region = Aws::Region::US_EAST_1;
        S3Client s3Client(config);
        CreateBucketRequest createBucketRequest;
        createBucketRequest.SetBucket(BUCKET);
        createBucketRequest.SetACL(BucketCannedACL::private_);
        CreateBucketOutcome createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

        if (!createBucketOutcome.IsSuccess()) {
            std::cout << "Bucket Creation failed: \n"
                      << createBucketOutcome.GetError() << std::endl;
            exit(-1);
        } else {
            std::cout << "Bucket Creation succeeded!\n" << std::endl;
        }

        Aws::String objectKey(OBJECT_KEY_PREFIX);
        AwsDoc::S3Encryption::KMSWithContextEncryptionMaterialsExample(BUCKET, (objectKey + "-kms-with-context-encryption-materials").c_str(), MASTER_KEY_ID);
        std::cout << std::endl;
        AwsDoc::S3Encryption::SimpleEncryptionMaterialsWithGCMAADExample(BUCKET, (objectKey + "-simple-encryption-materials-with-gcm-aad").c_str());
        std::cout << std::endl;
        AwsDoc::S3Encryption::DecryptObjectsEncryptedWithLegacyEncryptionExample(BUCKET, (objectKey + "-decrypt-objects-encrypted-with-legacy-encryption").c_str());
        std::cout << std::endl;
        AwsDoc::S3Encryption::DecryptObjectsWithRangeExample(BUCKET, (objectKey + "-decrypt-objects-with-range").c_str());
        std::cout << std::endl;
        AwsDoc::S3Encryption::DecryptObjectsWithAnyCMKExample(BUCKET, (objectKey + "-decrypt-objects-with-any-cmk").c_str(), MASTER_KEY_ID);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
