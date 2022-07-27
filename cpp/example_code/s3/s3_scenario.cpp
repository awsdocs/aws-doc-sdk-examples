//snippet-sourcedescription:[S3Scenario.cpp demonstrates how to perform various Amazon Simple Storage Service (Amazon S3) operations.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.s3_scenario.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/s3/model/CreateBucketConfiguration.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/core/utils/memory/stl/AWSAllocator.h>
#include <aws/core/utils/memory/stl/AWSStreamFwd.h>
#include <fstream>
// snippet-end:[s3.cpp.s3_scenario.inc]

// snippet-start:[s3.cpp.s3_scenario.main]
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * 1. Create a bucket.
 * 2. Upload a local file to the bucket.
 * 3. Download the object to a local file.
 * 4. Copy the object to a different "folder" in the bucket.
 * 5. List objects in the bucket.
 * 6. Delete all objects in the bucket.
 * 7. Delete the bucket.
 *
 */

static bool DeleteTempBucket(const Aws::String& bucketName, Aws::S3::S3Client &client);
static bool DeleteObjectFromBucket(const Aws::String& bucketName, const Aws::String& key, Aws::S3::S3Client &client);
bool S3Scenario(const Aws::String& key, const Aws::String& objectPath,
                const Aws::String& savePath, const Aws::String& toFolder,
                const Aws::Client::ClientConfiguration &clientConfig);

#ifndef TESTING_BUILD
int main(int argc, char* argv[])  {

    if (argc != 5) {
         std::cout << "Usage:\n" <<
              "    <key> <objectPath> <savePath> <toFolder>\n\n" <<
              "Where:\n" <<
              "    key - The key to use.\n\n" <<
              "    objectPath - The path where the file is located (for example, C:/AWS/book2.pdf). " <<
              "    savePath - The path where the file is saved after it's downloaded (for example, C:/AWS/book2.pdf). " <<
              "    toFolder - A folder where the file will be copied to in the bucket (for example, myFolder). ";
         return 1;
    }

    Aws::String key = argv[1];
    Aws::String objectPath = argv[2];
    Aws::String savePath = argv[3];
    Aws::String toFolder = argv[4] ;

    Aws::SDKOptions options;
    InitAPI(options);

    Aws::Client::ClientConfiguration clientConfig;
    S3Scenario(key, objectPath, savePath, toFolder, clientConfig);

    ShutdownAPI(options);

    return 0;
}
#endif // TESTING_BUILD

bool S3Scenario(const Aws::String& key, const Aws::String& objectPath,
                              const Aws::String& savePath, const Aws::String& toFolder,
                              const Aws::Client::ClientConfiguration &clientConfig) {

    Aws::S3::S3Client client(clientConfig);

    // Create a unique bucket name which is only temporary and will be deleted.
    // Format: "temporary-" + lowercase UUID.
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    Aws::String bucketName = "temporary-" +
                             Aws::Utils::StringUtils::ToLower(uuid.c_str());

    // 1. Create a bucket.
    {
        Aws::S3::Model::CreateBucketRequest request;
        request.SetBucket(bucketName);

        if (clientConfig.region != Aws::Region::US_EAST_1)
        {
            Aws::S3::Model::CreateBucketConfiguration createBucketConfiguration;
            createBucketConfiguration.WithLocationConstraint(
                    Aws::S3::Model::BucketLocationConstraintMapper::GetBucketLocationConstraintForName(
                            clientConfig.region));
            request.WithCreateBucketConfiguration(createBucketConfiguration);
        }

        Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);

        if (!outcome.IsSuccess()) {
            Aws::S3::S3Error err = outcome.GetError();
            std::cerr << "Error: CreateBucket: " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
            return false;
        } else {
            std::cout << "Created the bucket, '" << bucketName <<
                      "', in the region, '" << clientConfig.region << "'." << std::endl;
        }
    }

    // 2. Upload a local file to the bucket.
    {
        Aws::S3::Model::PutObjectRequest request;
        request.SetBucket(bucketName);
        request.SetKey(key);

        std::shared_ptr<Aws::FStream> input_data =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              objectPath,
                                              std::ios_base::in | std::ios_base::binary);

        if (!input_data->is_open()) {
            std::cout << "Error: unable to open file, '" << objectPath << "'." << std::endl;
            DeleteTempBucket(bucketName, client);
            return false;
        }

          request.SetBody(input_data);

        Aws::S3::Model::PutObjectOutcome outcome =
                client.PutObject(request);

        if (outcome.IsSuccess()) {
            std::cout << "Added the object with the key, '" << key << "', to the bucket, '"
                      << bucketName << "'." << std::endl;
        } else {
            std::cout << "Error: PutObject: " <<
                      outcome.GetError().GetMessage() << std::endl;
            DeleteObjectFromBucket(bucketName, key, client);
            DeleteTempBucket(bucketName, client);
            return false;
        }
    }

    // 3. Download the object to a local file.
    {
        Aws::S3::Model::GetObjectRequest request;
        request.SetBucket(bucketName);
        request.SetKey(key);

        Aws::S3::Model::GetObjectOutcome get_object_outcome =
                client.GetObject(request);

        if (get_object_outcome.IsSuccess())
        {
            std::cout << "Downloaded the object with the key, '" << key << "', in the bucket, '"
                      << bucketName << "'." << std::endl;
            Aws::IOStream &ioStream = get_object_outcome.GetResultWithOwnership().
                    GetBody();
            Aws::OFStream outStream(savePath);
            if (!outStream.is_open()) {
                std::cout << "Error: unable to open file, '" << savePath << "'." << std::endl;
             } else {
                outStream << ioStream.rdbuf();
                std::cout << "Wrote the downloaded object to the file '"
                        << savePath << "'."  << std::endl;
            }
        }
        else
        {
            Aws::S3::S3Error err = get_object_outcome.GetError();
            std::cout << "Error: GetObject: " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        }
    }

    // 4. Copy the object to a different "folder" in the bucket.
    Aws::String copiedToKey = toFolder + "/" + key;
    {
        Aws::S3::Model::CopyObjectRequest request;
        request.WithBucket(bucketName)
                .WithKey(copiedToKey)
                .WithCopySource(bucketName + "/" + key);

        Aws::S3::Model::CopyObjectOutcome outcome =
                client.CopyObject(request);
        if (outcome.IsSuccess()) {
            std::cout << "Copied the object with the key, '" << key << "', to the key, '" << copiedToKey
                        << ", in the bucket, '" << bucketName << "'." << std::endl;
        } else {
            std::cout << "Error: CopyObject: " <<
                      outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 5. List objects in the bucket.
    {
        Aws::S3::Model::ListObjectsRequest request;
        request.WithBucket(bucketName);

        auto outcome = client.ListObjects(request);

        if (outcome.IsSuccess()) {

            Aws::Vector<Aws::S3::Model::Object> objects =
                    outcome.GetResult().GetContents();

            std::cout << objects.size() << " objects in the bucket, '" << bucketName << "':" << std::endl;

            for (Aws::S3::Model::Object &object: objects) {
                std::cout << "     '"<< object.GetKey() << "'" << std::endl;
            }
        } else {
            std::cout << "Error: ListObjects: " <<
                      outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 6. Delete all objects in the bucket.
    // All objects in the bucket must be deleted before deleting the bucket.
    DeleteObjectFromBucket(bucketName, copiedToKey, client);
    DeleteObjectFromBucket(bucketName, key, client);

    // 7. Delete the bucket.
    return DeleteTempBucket(bucketName, client);
}

bool DeleteObjectFromBucket(const Aws::String& bucketName, const Aws::String& key, Aws::S3::S3Client &client) {
    Aws::S3::Model::DeleteObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(key);

    Aws::S3::Model::DeleteObjectOutcome  outcome =
            client.DeleteObject(request);

    if (outcome.IsSuccess()) {
        std::cout << "Deleted the object with the key, '" << key << "', from the bucket, '"
                  << bucketName << "'." << std::endl;
    } else {
        std::cout << "Error: DeleteObject: " <<
                  outcome.GetError().GetMessage() << std::endl;
        DeleteTempBucket(bucketName, client);
        return false;
    }

    return true;
}


bool DeleteTempBucket(const Aws::String& bucketName, Aws::S3::S3Client &client)
{
    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    if (!outcome.IsSuccess())
    {
        Aws::S3::S3Error err = outcome.GetError();
        std::cerr << "Error: DeleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }
    else
    {
        std::cout << "Deleted the bucket, '" << bucketName << "'." << std::endl;
        return true;
    }
}

// snippet-end:[s3.cpp.s3_scenario.main]

