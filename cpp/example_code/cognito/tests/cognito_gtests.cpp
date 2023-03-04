/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "cognito_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/testing/mocks/http/MockHttpClient.h>
#include <aws/core/http/standard/StandardHttpRequest.h>

Aws::SDKOptions AwsDocTest::Cognito_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::Cognito_GTests::s_clientConfig;
static const char ALLOCATION_TAG[] = "COGNITO_GTEST";

void AwsDocTest::Cognito_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::Cognito_GTests::TearDownTestSuite() {
    ShutdownAPI(s_options);
}

void AwsDocTest::Cognito_GTests::SetUp() {
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

void AwsDocTest::Cognito_GTests::TearDown() {
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

void AwsDocTest::Cognito_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


Aws::String AwsDocTest::Cognito_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

bool AwsDocTest::Cognito_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}


AwsDocTest::MockHTTP::MockHTTP() {
    mockHttpClient = Aws::MakeShared<MockHttpClient>(ALLOCATION_TAG);
    mockHttpClientFactory = Aws::MakeShared<MockHttpClientFactory>(ALLOCATION_TAG);
    mockHttpClientFactory->SetClient(mockHttpClient);
    SetHttpClientFactory(mockHttpClientFactory);
    requestTmp = CreateHttpRequest(Aws::Http::URI("https://test.com/"),
                                   Aws::Http::HttpMethod::HTTP_GET,
                                   Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);
}

AwsDocTest::MockHTTP::~MockHTTP() {
    Aws::Http::CleanupHttp();
    Aws::Http::InitHttp();
}

void AwsDocTest::MockHTTP::addResponseWithBody(const std::string &body) {
    std::shared_ptr<Aws::Http::Standard::StandardHttpResponse> goodResponse = Aws::MakeShared<Aws::Http::Standard::StandardHttpResponse>(
            ALLOCATION_TAG, requestTmp);
    goodResponse->AddHeader("Content-Type", "text/json");
    goodResponse->SetResponseCode(Aws::Http::HttpResponseCode::OK);
    goodResponse->GetResponseBody() << body;
    mockHttpClient->AddResponseToReturn(goodResponse);
}


int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}

