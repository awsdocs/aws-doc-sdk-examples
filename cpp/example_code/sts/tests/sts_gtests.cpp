// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include "sts_gtests.h"
#include <fstream>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateRoleRequest.h>
#include <aws/iam/model/DeleteRoleRequest.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/core/utils/UUID.h>

Aws::SDKOptions AwsDocTest::STS_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::STS_GTests::s_clientConfig;
Aws::IAM::Model::Role AwsDocTest::STS_GTests::s_role;
Aws::String AwsDocTest::STS_GTests::s_userArn;
static const char ALLOCATION_TAG[] = "IAM_GTEST";

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
            return MockHttpClient::MakeRequest(request, readLimiter, writeLimiter);;
        }
    }

private:
    std::shared_ptr<Aws::Http::HttpResponse> mCredentialsResponse;
};


void AwsDocTest::STS_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized after InitAPI
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::STS_GTests::TearDownTestSuite() {
    if (s_role.ArnHasBeenSet())
    {
        deleteRole(s_role.GetRoleName());
    }

     ShutdownAPI(s_options);
}

void AwsDocTest::STS_GTests::SetUp() {
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }
}

void AwsDocTest::STS_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }
}

Aws::String AwsDocTest::STS_GTests::getRoleArn() {
    if (!s_role.ArnHasBeenSet()) {
        s_role = createRole();
    }

    return s_role.GetArn();
}

Aws::IAM::Model::Role AwsDocTest::STS_GTests::createRole() {
    Aws::IAM::Model::Role result;
    auto policyDocument = getAssumeRolePolicyJSON();
    if (!policyDocument.empty()) {
        Aws::IAM::IAMClient client(*s_clientConfig);
        Aws::IAM::Model::CreateRoleRequest request;

        Aws::String roleName = uuidName("role");

        request.SetRoleName(roleName);
        request.SetAssumeRolePolicyDocument(policyDocument);

        Aws::IAM::Model::CreateRoleOutcome outcome = client.CreateRole(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Error creating role. " <<
                      outcome.GetError().GetMessage() << std::endl;
        }
        else {
            result = outcome.GetResult().GetRole();
        }
    }


    return result;
}

void AwsDocTest::STS_GTests::deleteRole(const Aws::String &role) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::DeleteRoleRequest request;
    request.SetRoleName(role);
    auto outcome = iam.DeleteRole(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleteRole " << outcome.GetError().GetMessage()
                  << std::endl;
    }
}

Aws::String AwsDocTest::STS_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return "doc-example-tests-" + name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

Aws::String AwsDocTest::STS_GTests::getAssumeRolePolicyJSON() {
    auto userArn = getUserArn();
    Aws::String result;
    if (!userArn.empty()) {
        result = Aws::String(R"({
            "Version": "2012-10-17",
            "Statement": {
                "Effect": "Allow",
                "Principal": {"AWS": ")")
                 + userArn
                 + Aws::String(R"("},
                "Action": "sts:AssumeRole"
            }
        })");
    }

    return result;
 }

Aws::String AwsDocTest::STS_GTests::getUserArn() {
    if (s_userArn.empty())
    {
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

Aws::String AwsDocTest::STS_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

bool AwsDocTest::STS_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
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




