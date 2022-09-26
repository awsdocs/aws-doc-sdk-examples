//NOTE!! This example only works on Linux and Mac. It does not work on Windows.

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/core/http/standard/StandardHttpRequest.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/http/curl/CurlHttpClient.h>  //This example is for Linux only. See top.
#include <aws/s3/S3Client.h>
#include "awsdoc/s3/s3_examples.h"

using namespace Aws;
using namespace Aws::Http;
using namespace Aws::Client;
using namespace Aws::S3;
using namespace Aws::S3::Model;

static const char ALLOCATION_TAG[] = "OverrideDefaultHttpClient";

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates subclassing CurlHttpClient and HttpClientFactory to disable the DNS cache.
 *
 * With AWS SDK for C++ version 1.8, it's much easier to override the default HTTP client configuration with the virtual functions: OverrideOptionsOn*Handle()
 * In this example, we override the default HTTP client and disable DNS caching with some low level Curl APIs.
 *
*/

/**
 * Extending Default CurlHttpClient, and override OverrideOptionsOnConnectionHandle() to
 * disable DNS caching with CURLOPT_DNS_CACHE_TIMEOUT setting to 0.
 */

class MyCurlHttpClient : public Aws::Http::CurlHttpClient {
public:
    explicit MyCurlHttpClient(const Aws::Client::ClientConfiguration &clientConfig) : Aws::Http::CurlHttpClient(
            clientConfig) {}

protected:
    // NOLINTNEXTLINE(readability-convert-member-functions-to-static)
    void OverrideOptionsOnConnectionHandle(CURL *connectionHandle) const override {
        std::cout << "Disable DNS caching completely." << std::endl;
        curl_easy_setopt(connectionHandle, CURLOPT_DNS_CACHE_TIMEOUT, 0L);
    }
};

/**
 * Extending the default HttpClientFactory to return the custom HttpClient we just created.
 */

class MyHttpClientFactory : public Aws::Http::HttpClientFactory {
    std::shared_ptr<Aws::Http::HttpClient>
    CreateHttpClient(const Aws::Client::ClientConfiguration &clientConfiguration) const override {
        return Aws::MakeShared<MyCurlHttpClient>(ALLOCATION_TAG, clientConfiguration);
    }

    std::shared_ptr<Aws::Http::HttpRequest> CreateHttpRequest(const Aws::String &uri, Aws::Http::HttpMethod method,
                                                              const Aws::IOStreamFactory &streamFactory) const override {
        return CreateHttpRequest(Aws::Http::URI(uri), method, streamFactory);
    }

    std::shared_ptr<Aws::Http::HttpRequest> CreateHttpRequest(const Aws::Http::URI &uri, Aws::Http::HttpMethod method,
                                                              const Aws::IOStreamFactory &streamFactory) const override {
        auto request = Aws::MakeShared<Aws::Http::Standard::StandardHttpRequest>(ALLOCATION_TAG, uri, method);
        request->SetResponseStreamFactory(streamFactory);

        return request;
    }

    void InitStaticState() override {
        MyCurlHttpClient::InitGlobalState();
    }

    void CleanupStaticState() override {
        MyCurlHttpClient::CleanupGlobalState();
    }
};

//! Routine which demonstrates configuring a website for an S3 bucket.
/*!
  \sa ListBucketDisablingDnsCache()
  \param clientConfig Aws client configuration.
*/

bool AwsDoc::S3::ListBucketDisablingDnsCache(const Aws::Client::ClientConfiguration &clientConfig) {
    SetHttpClientFactory(Aws::MakeShared<MyHttpClientFactory>(ALLOCATION_TAG));

    Aws::S3::S3Client s3Client(clientConfig);
    auto listBucketsOutcome = s3Client.ListBuckets();
    if (!listBucketsOutcome.IsSuccess()) {
        std::cerr << "Failed to list buckets. Error details:" << std::endl;
        std::cerr << listBucketsOutcome.GetError() << std::endl;
    }
    else {
        std::cout << "Found " << listBucketsOutcome.GetResult().GetBuckets().size() << " buckets" << std::endl;
        for (auto &&bucket: listBucketsOutcome.GetResult().GetBuckets()) {
            std::cout << "  " << bucket.GetName() << std::endl;
        }
    }

    CleanupHttp();

    return listBucketsOutcome.IsSuccess();
}

/*
 *
 *  main function
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char *argv[]) {
    SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::ListBucketDisablingDnsCache(clientConfig);
    }

    ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
