/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "S3_GTests.h"
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/PutBucketPolicyRequest.h>
#include <aws/s3/model/PutBucketWebsiteRequest.h>
#include <aws/core/utils/UUID.h>
#include <fstream>

Aws::SDKOptions AwsDocTest::S3_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::S3_GTests::s_clientConfig;
std::vector<Aws::String> AwsDocTest::S3_GTests::s_cachedS3Buckets;
Aws::String AwsDocTest::S3_GTests::s_testFilePath;
Aws::String AwsDocTest::S3_GTests::s_canonicalUserID;
Aws::String AwsDocTest::S3_GTests::s_userArn;

void AwsDocTest::S3_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized after InitAPI
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::S3_GTests::TearDownTestSuite() {
    DeleteBuckets();
    if (!s_testFilePath.empty()) {
        remove(s_testFilePath.c_str());
    }

    ShutdownAPI(s_options);

}

std::vector<Aws::String> AwsDocTest::S3_GTests::GetCachedS3Buckets(size_t count) {
    for (size_t index = s_cachedS3Buckets.size(); index < count; ++index) {
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucketName = "doc-example-bucket-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());

        if (CreateBucket(bucketName)) {
            s_cachedS3Buckets.push_back(bucketName);
        }
    }

    return s_cachedS3Buckets;
}

Aws::String AwsDocTest::S3_GTests::GetTestFilePath() {
    if (s_testFilePath.empty()) {
        Aws::String filePath = "SampleGTest_test.file.txt";
        std::ofstream myFile(filePath);
        if (myFile) {
            myFile << "This file is part of unit testing";
            myFile.close();
            s_testFilePath = filePath;
        }
        else {
            std::cerr << "Error - S3_GTests::GetTestFilePath could not create file." << std::endl;
        }
    }
    return s_testFilePath;
}

Aws::String AwsDocTest::S3_GTests::PutTestFileInBucket(const Aws::String &bucketName) {
    Aws::String filePath = GetTestFilePath();
    if (!filePath.empty()) {
        Aws::S3::Model::PutObjectRequest putObjectRequest;
        putObjectRequest.SetBucket(bucketName);
        putObjectRequest.SetKey(filePath);

        std::shared_ptr<Aws::IOStream> fileBody =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag", filePath,
                                              std::ios_base::in | std::ios_base::binary);

        putObjectRequest.SetBody(fileBody);

        Aws::S3::S3Client client(*s_clientConfig);
        Aws::S3::Model::PutObjectOutcome outcome =
                client.PutObject(putObjectRequest);

        if (!outcome.IsSuccess()) {
            auto err = outcome.GetError();
            std::cerr << "Error: S3_GTests::PutTestFileInBucket Upload object '" << filePath << "' " <<
                      "to bucket '" << bucketName << "': " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

            filePath.clear();
        }
    }
    return filePath;
}

bool AwsDocTest::S3_GTests::DeleteObjectInBucket(const Aws::String &bucketName, const Aws::String &objectName) {
    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectName)
            .WithBucket(bucketName);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: S3_GTests::DeleteObjectInBucket: Delete Object '" <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::DeleteAllObjectsInBucket(const Aws::String &bucketName) {
    bool result = true;
    Aws::S3::S3Client client(*s_clientConfig);

    Aws::S3::Model::ListObjectsRequest request;
    request.WithBucket(bucketName);

    Aws::S3::Model::ListObjectsOutcome outcome = client.ListObjects(request);

    if (outcome.IsSuccess()) {
        for (auto object: outcome.GetResult().GetContents()) {
            if (!DeleteObjectInBucket(bucketName, object.GetKey())) {
                result = false;
            }
        }
    }
    else {
        auto err = outcome.GetError();
        std::cerr << "Error - S3_GTests::DeleteAllObjectsInBucket " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }
    return result;
}

void AwsDocTest::S3_GTests::DeleteBuckets() {
    for (Aws::String bucketName: s_cachedS3Buckets) {
        DeleteAllObjectsInBucket(bucketName);

        DeleteBucket(bucketName);
    }

    s_cachedS3Buckets.clear();
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::DeleteBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client client(*s_clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    bool result = true;
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cout << "S3_GTests::DeleteBucket Error: DeleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::CreateBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);
    if (s_clientConfig->region != Aws::Region::US_EAST_1) {
        Aws::S3::Model::CreateBucketConfiguration createBucketConfiguration;
        createBucketConfiguration.WithLocationConstraint(
                Aws::S3::Model::BucketLocationConstraintMapper::GetBucketLocationConstraintForName(
                        s_clientConfig->region));
        request.WithCreateBucketConfiguration(createBucketConfiguration);
    }

    Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);
    bool result = true;
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "S3_GTests::getCachedS3Bucket Error: CreateBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

Aws::String AwsDocTest::S3_GTests::GetArnForUser() {
    if (s_userArn.empty()) {
        Aws::IAM::IAMClient client(*s_clientConfig);

        Aws::IAM::Model::GetUserRequest request;
        Aws::IAM::Model::GetUserOutcome outcome = client.GetUser(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Error getting Iam user. " <<
                      outcome.GetError().GetMessage() << std::endl;
        }
        else {
            s_userArn = outcome.GetResult().GetUser().GetArn();
        }
    }

    return s_userArn;
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::AddPolicyToBucket(const Aws::String &bucketName) {
    Aws::String policyString = GetBucketPolicy(bucketName);
    if (policyString.empty()) {
        return false;
    }

    std::shared_ptr<Aws::StringStream> policyBody =
            Aws::MakeShared<Aws::StringStream>("");

    *policyBody << policyString;

    Aws::S3::Model::PutBucketPolicyRequest policyRequest;
    policyRequest.SetBucket(bucketName);
    policyRequest.SetBody(policyBody);

    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::PutBucketPolicyOutcome outcome =
            client.PutBucketPolicy(policyRequest);

    bool result = true;
    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cout << "Error: AddPolicyToBucket test setup: Add bucket policy '" <<
                  policyString << "' to bucket '" << bucketName << "': " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::PutWebsiteConfig(const Aws::String &bucketName) {
    Aws::S3::S3Client s3_client(*s_clientConfig);

    Aws::S3::Model::IndexDocument indexDocument;
    indexDocument.SetSuffix("index.html");

    Aws::S3::Model::ErrorDocument errorDocument;
    errorDocument.SetKey("error.html");

    Aws::S3::Model::WebsiteConfiguration websiteConfiguration;
    websiteConfiguration.SetIndexDocument(indexDocument);
    websiteConfiguration.SetErrorDocument(errorDocument);

    Aws::S3::Model::PutBucketWebsiteRequest request;
    request.SetBucket(bucketName);
    request.SetWebsiteConfiguration(websiteConfiguration);

    Aws::S3::Model::PutBucketWebsiteOutcome outcome =
            s3_client.PutBucketWebsite(request);

    bool result = true;
    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutBucketWebsite: "
                  << outcome.GetError().GetMessage() << std::endl;

        result = false;
    }

    return result;
}

Aws::String AwsDocTest::S3_GTests::GetCanonicalUserID() {
    if (s_canonicalUserID.empty()) {
        Aws::S3::S3Client client(*s_clientConfig);

        auto outcome = client.ListBuckets();
        if (outcome.IsSuccess()) {
            s_canonicalUserID = outcome.GetResult().GetOwner().GetID();
        }
        else {
            std::cout << "S3_GTests::GetCanonicalUserID Failed with error: " << outcome.GetError() << std::endl;
        }
    }
    return s_canonicalUserID;
}

Aws::String AwsDocTest::S3_GTests::GetBucketPolicy(const Aws::String &bucketName) {
    Aws::String userArn = GetArnForUser();
    if (userArn.empty()) {
        return "";
    }

    Aws::String policyString =
            "{\n"
            "  \"Version\":\"2012-10-17\",\n"
            "  \"Statement\":[\n"
            "   {\n"
            "     \"Sid\": \"1\",\n"
            "     \"Effect\": \"Allow\",\n"
            "     \"Principal\": {\n"
            "          \"AWS\": \""
            + userArn +
            "\"\n"
            "     },\n"
            "     \"Action\": [\"s3:GetObject\"],\n"
            "     \"Resource\": [\"arn:aws:s3:::"
            + bucketName +
            "/*\"]\n"
            "   }]\n"
            "}";

    return policyString;
}

void AwsDocTest::S3_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);
}

void AwsDocTest::S3_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }
}



