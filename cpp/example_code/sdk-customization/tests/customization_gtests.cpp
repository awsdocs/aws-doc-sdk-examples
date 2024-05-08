// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include "customization_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>

Aws::SDKOptions AwsDocTest::SdkCustomization_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::SdkCustomization_GTests::s_clientConfig;

void AwsDocTest::SdkCustomization_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::SdkCustomization_GTests::TearDownTestSuite() {
    ShutdownAPI(s_options);

}

void AwsDocTest::SdkCustomization_GTests::SetUp() {
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, an infinite loop occurs when looping for a result on an empty buffer.
    std::cin.exceptions(std::ios_base::badbit);
}

void AwsDocTest::SdkCustomization_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        std::cin.exceptions(std::ios_base::goodbit);
        m_savedInBuffer = nullptr;
    }
}

Aws::String AwsDocTest::SdkCustomization_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::SdkCustomization_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::SdkCustomization_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}


int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}

bool AwsDocTest::SdkCustomization_GTests::deleteBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client client(*s_clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    bool result = true;
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cout << "S3_GTests::DeleteBucket Error: deleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

bool AwsDocTest::SdkCustomization_GTests::createBucket(const Aws::String &bucketName) {
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
        std::cerr << "S3_GTests::getCachedS3Bucket Error: createBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

Aws::String AwsDocTest::SdkCustomization_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

bool
AwsDocTest::SdkCustomization_GTests::deleteObjectInBucket(const Aws::String &bucketName,
                                                          const Aws::String &key) {
    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(key)
            .WithBucket(bucketName);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: S3_GTests::deleteObjectInBucket: Delete Object '" <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::SdkCustomization_GTests::putFileInBucket(const Aws::String &bucketName,
                                                          const Aws::String &key,
                                                          const Aws::String &filePath) {

    Aws::S3::Model::PutObjectRequest putObjectRequest;
    putObjectRequest.SetBucket(bucketName);
    putObjectRequest.SetKey(key);

    std::shared_ptr<Aws::IOStream> fileBody =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag", filePath,
                                          std::ios_base::in | std::ios_base::binary);

    putObjectRequest.SetBody(fileBody);

    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::PutObjectOutcome outcome =
            client.PutObject(putObjectRequest);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: S3_GTests::PutTestFileInBucket Upload object '" << filePath
                  << "' " <<
                  "to bucket '" << bucketName << "': " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

    }

    return outcome.IsSuccess();

}

Aws::String AwsDocTest::SdkCustomization_GTests::getTestFilePath() {
    return SRC_DIR"/customization_gtests.cpp";
}


