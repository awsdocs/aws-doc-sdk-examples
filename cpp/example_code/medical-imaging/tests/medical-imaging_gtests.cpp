// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


#include "medical-imaging_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>

Aws::SDKOptions AwsDocTest::MedicalImaging_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::MedicalImaging_GTests::s_clientConfig;
static const char ALLOCATION_TAG[] = "Medical_Inmging_GTEST";

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
                                        << R"(Token":"ABCDEFGHIJK==","Expiration":")" << expiration.ToGmtString(Aws::Utils::DateFormat::ISO_8601) << "\""
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
        }
        else {
            return MockHttpClient::MakeRequest(request, readLimiter, writeLimiter);
        }
    }

private:
    std::shared_ptr<Aws::Http::HttpResponse> mCredentialsResponse;
};

void AwsDocTest::MedicalImaging_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::MedicalImaging_GTests::TearDownTestSuite() {
    ShutdownAPI(s_options);

}

void AwsDocTest::MedicalImaging_GTests::SetUp() {
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

void AwsDocTest::MedicalImaging_GTests::TearDown() {
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

Aws::String AwsDocTest::MedicalImaging_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::MedicalImaging_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::MedicalImaging_GTests::suppressStdOut() {
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
