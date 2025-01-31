// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include "rekognition_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/PutObjectRequest.h>


Aws::SDKOptions AwsDocTest::Rekognition_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::Rekognition_GTests::s_clientConfig;
Aws::String AwsDocTest::Rekognition_GTests::s_bucketName;

void AwsDocTest::Rekognition_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::Rekognition_GTests::TearDownTestSuite() {
    if (!s_bucketName.empty()) {
        deleteBucket(s_bucketName);
    }

    ShutdownAPI(s_options);

}

void AwsDocTest::Rekognition_GTests::SetUp() {
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

void AwsDocTest::Rekognition_GTests::TearDown() {
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

Aws::String AwsDocTest::Rekognition_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::Rekognition_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::Rekognition_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}


Aws::String AwsDocTest::Rekognition_GTests::getImageBucket() {
    if (s_bucketName.empty()) {
        Aws::String bucketName = uuidName("rekognition-test-cpp-");
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
        if (!outcome.IsSuccess()) {
            const Aws::S3::S3Error &err = outcome.GetError();
            std::cerr << "Rekognition_GTests::getImageBucket Error: CreateBucket: " <<
                      err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        } else {
            s_bucketName = bucketName;
        }
    }

    return s_bucketName;
}


Aws::String AwsDocTest::Rekognition_GTests::uuidName(const Aws::String &prefix) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return prefix + Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

Aws::String AwsDocTest::Rekognition_GTests::getImageFileName() {
    return TESTS_DIR"/../../../../resources/sample_files/.sample_media/market_2.jpg";
}

bool AwsDocTest::Rekognition_GTests::uploadImage(const Aws::String &bucketName, const Aws::String &imageFileName,
                                                 const Aws::String &keyName) {
    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(keyName);
    std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                          imageFileName.c_str(),
                                          std::ios_base::in | std::ios_base::binary);

    if (!*inputData) {
        std::cerr << "Error unable to read file " << imageFileName << std::endl;
        return false;
    }
    request.SetBody(inputData);
    Aws::S3::Model::PutObjectOutcome outcome = client.PutObject(request);
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Rekognition_GTests::uploadImage Error: PutObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

void AwsDocTest::Rekognition_GTests::deleteBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client s3_client(*s_clientConfig);

    Aws::S3::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);

    auto listObjectsOutcome = s3_client.ListObjects(listObjectsRequest);
    if (listObjectsOutcome.IsSuccess()) {
        Aws::Vector<Aws::S3::Model::Object> objects = listObjectsOutcome.GetResult().GetContents();
        for (const auto &object: objects) {
            Aws::S3::Model::DeleteObjectRequest deleteObjectRequest;
            deleteObjectRequest.SetBucket(bucketName);
            deleteObjectRequest.SetKey(object.GetKey());

            auto deleteObjectOutcome = s3_client.DeleteObject(deleteObjectRequest);
            if (!deleteObjectOutcome.IsSuccess()) {
                auto &err = deleteObjectOutcome.GetError();
                std::cerr << "Error: DeleteObject: " << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
                break;
            }
        }
    } else {
        auto &err = listObjectsOutcome.GetError();
        std::cerr << "Error: ListObjects: " << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }

    // Delete the bucket
    Aws::S3::Model::DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(bucketName);

    auto deleteBucketOutcome = s3_client.DeleteBucket(deleteBucketRequest);
    if (!deleteBucketOutcome.IsSuccess()) {
        auto &err = deleteBucketOutcome.GetError();
        std::cerr << "Error: DeleteBucket: " << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
}


int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}
