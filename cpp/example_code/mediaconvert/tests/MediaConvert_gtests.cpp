/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "MediaConvert_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/mediaconvert/MediaConvertClient.h>
#include <aws/mediaconvert/model/DescribeEndpointsRequest.h>
#include <aws/testing/mocks/http/MockHttpClient.h>
#include "mediaconvert_samples.h"


Aws::SDKOptions AwsDocTest::MediaConvert_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::MediaConvert_GTests::s_clientConfig;
static const char ALLOCATION_TAG[] = "RDS_GTEST";

void AwsDocTest::MediaConvert_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::MediaConvert_GTests::TearDownTestSuite() {
    ShutdownAPI(s_options);

}

void AwsDocTest::MediaConvert_GTests::SetUp() {
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, an infinite loop occurs when looping for a result on an empty buffer.
    std::cin.exceptions(std::ios_base::badbit);

    createEndpointCache();
}

void AwsDocTest::MediaConvert_GTests::TearDown() {
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

Aws::String AwsDocTest::MediaConvert_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::MediaConvert_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::MediaConvert_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

void AwsDocTest::MediaConvert_GTests::createEndpointCache() {
    Aws::MediaConvert::MediaConvertClient endpointClient(*s_clientConfig);
    Aws::MediaConvert::Model::DescribeEndpointsRequest request;
    auto outcome = endpointClient.DescribeEndpoints(request);
    Aws::String endpoint = "aaaaaaa.mediaconvert.us-test.amazonaws.com";
    if (outcome.IsSuccess()) {
        auto endpoints = outcome.GetResult().GetEndpoints();
        if (!endpoints.empty()) {
            // Need to strip https:// from endpoint for C++.
            endpoint = endpoints[0].GetUrl().substr(8);
        }
    }
    else {
        std::cerr << "DescribeEndpoints error - " << outcome.GetError().GetMessage()
                  << std::endl;
    }
    std::ofstream endpointCacheOut(AwsDoc::MediaConvert::CACHED_ENDPOINT_FILE);
    endpointCacheOut << endpoint;
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

bool AwsDocTest::MockHTTP::addResponseWithBody(const std::string &fileName,
                                               Aws::Http::HttpResponseCode httpResponseCode) {

    std::ifstream inStream(std::string(SRC_DIR) + "/" + fileName);
    if (inStream) {
        std::shared_ptr<Aws::Http::Standard::StandardHttpResponse> goodResponse = Aws::MakeShared<Aws::Http::Standard::StandardHttpResponse>(
                ALLOCATION_TAG, requestTmp);
        goodResponse->AddHeader("Content-Type", "text/json");
        goodResponse->SetResponseCode(httpResponseCode);
        goodResponse->GetResponseBody() << inStream.rdbuf();
        mockHttpClient->AddResponseToReturn(goodResponse);
        return true;
    }

    std::cerr << "MockHTTP::addResponseWithBody open file error '" << fileName << "'."
              << std::endl;

    return false;
}
