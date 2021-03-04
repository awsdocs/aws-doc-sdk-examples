// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#define AWS_DISABLE_DEPRECATION
#include <awsdoc/s3-encryption/s3_encryption_examples.h>
#include <aws/core/Aws.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/kms/KMSClient.h>
#include <aws/kms/model/CreateKeyRequest.h>
#include <aws/kms/model/ScheduleKeyDeletionRequest.h>

static const char AWSDOC_S3ENCRYPTION_BUCKET_PREFIX[] = "awsdoc-s3encryption-bucket-";
static const char AWSDOC_S3ENCRYPTION_OBJECT_KEY_PREFIX[] = "awsdoc-s3encryption-key-";
static const int TIMEOUT_MAX = 20;

bool CreateBucket(const Aws::S3::S3Client& s3Client, const Aws::String& bucketName)
{
    Aws::S3::Model::CreateBucketRequest createBucketRequest;
    createBucketRequest.SetBucket(bucketName);

    auto createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

    if (!createBucketOutcome.IsSuccess())
    {
        std::cout << "Failed to create bucket: " << bucketName << "\n" << createBucketOutcome.GetError() << std::endl;
        return false;
    }

    // Wait for bucket to propagate
    unsigned timeoutCount = 0;
    while (timeoutCount++ < TIMEOUT_MAX)
    {
        Aws::S3::Model::ListObjectsRequest listObjectsRequest;
        listObjectsRequest.SetBucket(bucketName);
        auto listObjectsOutcome = s3Client.ListObjects(listObjectsRequest);
        if (listObjectsOutcome.IsSuccess())
        {
            return true;
        }

        std::this_thread::sleep_for(std::chrono::seconds(10));
    }

    return false;
}

bool DeleteBucket(const Aws::S3::S3Client& s3Client, const Aws::String& bucketName)
{
    // Empty bucket
    Aws::S3::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);

    auto listObjectsOutcome = s3Client.ListObjects(listObjectsRequest);
    if (!listObjectsOutcome.IsSuccess())
    {
        std::cout << "Failed to list objects in bucket: " << bucketName << "\n" << listObjectsOutcome.GetError() << std::endl;
        return false;
    }

    for (const auto& object : listObjectsOutcome.GetResult().GetContents())
    {
        Aws::S3::Model::DeleteObjectRequest deleteObjectRequest;
        deleteObjectRequest.SetBucket(bucketName);
        deleteObjectRequest.SetKey(object.GetKey());
        auto deleteObjectOutcome = s3Client.DeleteObject(deleteObjectRequest);
        if (!deleteObjectOutcome.IsSuccess())
        {
            std::cout << "Failed to delete object with key name: " << object.GetKey() << "\n" << deleteObjectOutcome.GetError() << std::endl;
            return false;
        }
    }

    // Then delete bucket
    Aws::S3::Model::DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(bucketName);

    auto deleteBucketOutcome = s3Client.DeleteBucket(deleteBucketRequest);

    if (!deleteBucketOutcome.IsSuccess())
    {
        std::cout << "Failed to delete bucket: " << bucketName << "\n" << deleteBucketOutcome.GetError() << std::endl;
        return false;
    }

    return true;
}

Aws::String CreateKMSMasterKey(const Aws::KMS::KMSClient& kmsClient)
{
    Aws::KMS::Model::CreateKeyRequest createKeyRequest;
    auto createKeyOutcome = kmsClient.CreateKey(createKeyRequest);
    if (!createKeyOutcome.IsSuccess())
    {
        std::cout << "Failed to create KMS master key.\n" << createKeyOutcome.GetError() << std::endl;
        return {};
    }
    else
    {
        const Aws::String& masterKeyId = createKeyOutcome.GetResult().GetKeyMetadata().GetKeyId();
        return masterKeyId;
    }

}

bool DeleteKMSMasterKey(const Aws::KMS::KMSClient& kmsClient, const Aws::String& kmsMasterKeyId)
{
    Aws::KMS::Model::ScheduleKeyDeletionRequest scheduleKeyDeletationRequest;
    scheduleKeyDeletationRequest.SetKeyId(kmsMasterKeyId);
    scheduleKeyDeletationRequest.SetPendingWindowInDays(7);
    auto scheduleKeyDeletationOutcome = kmsClient.ScheduleKeyDeletion(scheduleKeyDeletationRequest);
    if (!scheduleKeyDeletationOutcome.IsSuccess())
    {
        std::cout << "Failed to schedule KMS master key deletation with ID: " << kmsMasterKeyId << std::endl;
        return false;
    }
    return true;
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Tests setup
        Aws::Client::ClientConfiguration clientConfig;
        clientConfig.region = Aws::Region::US_EAST_1;
        Aws::S3::S3Client s3Client(clientConfig);

        Aws::String bucketName(AWSDOC_S3ENCRYPTION_BUCKET_PREFIX);
        bucketName += Aws::Utils::StringUtils::ToLower(static_cast<Aws::String>(Aws::Utils::UUID::RandomUUID()).c_str());
        if (CreateBucket(s3Client, bucketName))
        {
            std::cout << "Succeeded to create bucket:" << bucketName << std::endl;
        }
        else
        {
            return 1;
        }

        Aws::KMS::KMSClient kmsClient(clientConfig);
        Aws::String masterKeyId = CreateKMSMasterKey(kmsClient);
        if (!masterKeyId.empty())
        {
            std::cout << "Succeeded to create KMS master key: " << masterKeyId << std::endl;
            std::cout << std::endl;
        }
        else
        {
            return 1;
        }

        Aws::String objectKey(AWSDOC_S3ENCRYPTION_OBJECT_KEY_PREFIX);

        if (!AwsDoc::S3Encryption::KMSWithContextEncryptionMaterialsExample(bucketName.c_str(), (objectKey + "kms-with-context-encryption-materials").c_str(), masterKeyId.c_str()))
        {
            return 1;
        }
        std::cout << std::endl;
        if (!AwsDoc::S3Encryption::SimpleEncryptionMaterialsWithGCMAADExample(bucketName.c_str(), (objectKey + "simple-encryption-materials-with-gcm-aad").c_str()))
        {
            return 1;
        }
        std::cout << std::endl;
        if (!AwsDoc::S3Encryption::DecryptObjectsEncryptedWithLegacyEncryptionExample(bucketName.c_str(), (objectKey + "decrypt-objects-encrypted-with-legacy-encryption").c_str()))
        {
            return 1;
        }
        std::cout << std::endl;
        if (!AwsDoc::S3Encryption::DecryptObjectsWithRangeExample(bucketName.c_str(), (objectKey + "decrypt-objects-with-range").c_str()))
        {
            return 1;
        }
        std::cout << std::endl;
        if (!AwsDoc::S3Encryption::DecryptObjectsWithAnyCMKExample(bucketName.c_str(), (objectKey + "decrypt-objects-with-any-cmk").c_str(), masterKeyId.c_str()))
        {
            return 1;
        }
        std::cout << std::endl;

        // Tests cleanup
        if (DeleteBucket(s3Client, bucketName))
        {
            std::cout << "Succeeded to delete bucket: " << bucketName << std::endl;
        }
        else
        {
            return 1;
        }

        if (DeleteKMSMasterKey(kmsClient, masterKeyId))
        {
            std::cout << "Succeeded to schedule KMS master key deletation with ID: " << masterKeyId << std::endl;
        }
        else
        {
            return 1;
        }


    }
    ShutdownAPI(options);

    return 0;
}
