// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
#include <cstdlib>
#include <fstream>

Aws::SDKOptions AwsDocTest::S3_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::S3_GTests::s_clientConfig;
std::vector<Aws::String> AwsDocTest::S3_GTests::s_cachedS3Buckets;
Aws::String AwsDocTest::S3_GTests::s_testFilePath;
Aws::String AwsDocTest::S3_GTests::s_canonicalUserID;
Aws::String AwsDocTest::S3_GTests::s_userArn;
static const char ALLOCATION_TAG[] = "S3_GTEST";

/*
 * Subclass MockHTTPCLient to respond to credential requests.
 * Otherwise, the stored responses are returned for credential requests
 * and not the service API calls.
 */
class CustomMockHTTPClient : public MockHttpClient {
public:
    explicit CustomMockHTTPClient(
            const std::shared_ptr<Aws::Http::HttpRequest> &requestTmp) {
        std::shared_ptr<Aws::Http::Standard::StandardHttpResponse> goodResponse = Aws::MakeShared<Aws::Http::Standard::StandardHttpResponse>(
                ALLOCATION_TAG, requestTmp);
        goodResponse->AddHeader("Content-Type", "text/json");
        goodResponse->SetResponseCode(Aws::Http::HttpResponseCode::OK);
        Aws::Utils::DateTime expiration =
                Aws::Utils::DateTime::Now() + std::chrono::milliseconds(60000);

        goodResponse->GetResponseBody() << "{"
                                        << R"("RoleArn":"arn:aws:iam::123456789012:role/MockRole",)"
                                        << R"("AccessKeyId":"ABCDEFGHIJK",)"
                                        << R"("SecretAccessKey":"ABCDEFGHIJK",)"
                                        << R"(Token":"ABCDEFGHIJK==","Expiration":")"
                                        << expiration.ToGmtString(Aws::Utils::DateFormat::ISO_8601) << "\""
                                        << "}";
        this->AddResponseToReturn(goodResponse);

        mCredentialsResponse = MockHttpClient::MakeRequest(requestTmp);
    }

    std::shared_ptr<Aws::Http::HttpResponse>
    MakeRequest(const std::shared_ptr<Aws::Http::HttpRequest> &request,
                Aws::Utils::RateLimits::RateLimiterInterface *readLimiter,
                Aws::Utils::RateLimits::RateLimiterInterface *writeLimiter) const override {

        // Do not use stored responses for a credentials request.
        if (request->GetURIString().find("/credentials/") != std::string::npos) {
            std::cout << "CustomMockHTTPClient returning credentials request."
                      << std::endl;
            return mCredentialsResponse;
        } else {
            return MockHttpClient::MakeRequest(request, readLimiter, writeLimiter);;
        }
    }

private:
    std::shared_ptr<Aws::Http::HttpResponse> mCredentialsResponse;
};

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
        Aws::String bucketName = GetUniqueBucketName();

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
        if (myFile) { // NOLINT(readability-implicit-bool-conversion)
            myFile << "This file is part of unit testing";
            myFile.close();
            s_testFilePath = filePath;
        } else {
            std::cerr << "Error - S3_GTests::GetTestFilePath could not create file." << std::endl;
        }
    }
    return s_testFilePath;
}


Aws::String AwsDocTest::S3_GTests::PutTestFileInBucket(const Aws::String &bucketName,
                                                       const Aws::String &key) {
    Aws::String filePath = GetTestFilePath();
    Aws::String objectKey = key.empty() ? filePath : key;

    if (!filePath.empty()) {
        Aws::S3::Model::PutObjectRequest putObjectRequest;
        putObjectRequest.SetBucket(bucketName);
        putObjectRequest.SetKey(objectKey);

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
    return objectKey;
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
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
    } else {
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
    request.SetObjectOwnership(Aws::S3::Model::ObjectOwnership::BucketOwnerPreferred); // Needed for the ACL tests.
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

Aws::String AwsDocTest::S3_GTests::GetArnForUser() {
    if (s_userArn.empty()) {
        Aws::Client::ClientConfiguration config(*s_clientConfig);
        config.region = "us-east-1"; // Ensures a valid IAM region.
        Aws::IAM::IAMClient client(config);

        Aws::IAM::Model::GetUserRequest request;
        Aws::IAM::Model::GetUserOutcome outcome = client.GetUser(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Error getting Iam user. " <<
                      outcome.GetError().GetMessage() << std::endl;
        } else {
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
        std::cerr << "Error: AddPolicyToBucket test setup: Add bucket policy '" <<
                  policyString << "' to bucket '" << bucketName << "': " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

// NOLINTNEXTLINE(readability-convert-member-functions-to-static)
bool AwsDocTest::S3_GTests::PutWebsiteConfig(const Aws::String &bucketName) {
    Aws::S3::S3Client s3Client(*s_clientConfig);

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
            s3Client.PutBucketWebsite(request);

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
        } else {
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
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, we get an infinite loop when the buffer is empty.
    std::cin.exceptions(std::ios_base::badbit);
}

void AwsDocTest::S3_GTests::TearDown() {
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

Aws::String AwsDocTest::S3_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

bool AwsDocTest::S3_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

void AwsDocTest::S3_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

Aws::String AwsDocTest::S3_GTests::GetUniqueBucketName() {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return  GetBucketNamePrefix() + Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

Aws::String AwsDocTest::S3_GTests::GetBucketNamePrefix() {
    Aws::String bucketNamePrefix;
    const char* envPrefix = std::getenv("S3TestsBucketPrefix");
    if (envPrefix != nullptr) {
        bucketNamePrefix = Aws::String(envPrefix) + "-";
    }
    return bucketNamePrefix;
}

AwsDocTest::MockHTTP::MockHTTP() {
    requestTmp = CreateHttpRequest(Aws::Http::URI("https://test.com/"),
                                   Aws::Http::HttpMethod::HTTP_GET,
                                   Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);
    mockHttpClient = Aws::MakeShared<CustomMockHTTPClient>(
            ALLOCATION_TAG, requestTmp);
    mockHttpClientFactory = Aws::MakeShared<MockHttpClientFactory>(ALLOCATION_TAG);
    mockHttpClientFactory->SetClient(mockHttpClient);
    SetHttpClientFactory(mockHttpClientFactory);
}

AwsDocTest::MockHTTP::~MockHTTP() {
    Aws::Http::CleanupHttp();
    Aws::Http::InitHttp();
}

bool AwsDocTest::MockHTTP::addResponseWithBody(const std::string &fileName,
                                               Aws::Http::HttpResponseCode httpResponseCode) {
    std::string filePath = std::string(SRC_DIR) + "/" + fileName;

    std::ifstream inStream(filePath);
    if (inStream) {
        std::shared_ptr<Aws::Http::Standard::StandardHttpResponse> goodResponse = Aws::MakeShared<Aws::Http::Standard::StandardHttpResponse>(
                ALLOCATION_TAG, requestTmp);
        goodResponse->AddHeader("Content-Type", "text/json");
        goodResponse->SetResponseCode(httpResponseCode);
        goodResponse->GetResponseBody() << inStream.rdbuf();
        mockHttpClient->AddResponseToReturn(goodResponse);

        return true;
    }

    std::cerr << "MockHTTP::addResponseWithBody open file error '" << filePath << "'."
              << std::endl;

    return false;
}

int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}
