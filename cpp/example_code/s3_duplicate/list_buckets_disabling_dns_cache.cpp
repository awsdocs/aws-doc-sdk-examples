//NOTE!! This example only currently works on Linux.  If you are on a Windows environment, delete this .cpp file to build your solution.

//snippet-sourcedescription:[Upgrade AWS SDK for C++ to version 1.8 to build list_buckets_disabling_dns_cache.cpp. This example demonstrates how to replace the default HTTP client and override the default HTTP client configurations.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/core/http/standard/StandardHttpRequest.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/http/curl/CurlHttpClient.h>  //This example is for Linux only. See top.
#include <aws/s3/S3Client.h>

using namespace Aws;
using namespace Aws::Http;
using namespace Aws::Client;
using namespace Aws::S3;
using namespace Aws::S3::Model;

static const char ALLOCATION_TAG[] = "OverrideDefaultHttpClient";

/**
 * Extending Default CurlHttpClient, and override OverrideOptionsOnConnectionHandle() to disable DNS caching with CURLOPT_DNS_CACHE_TIMEOUT setting to 0.
 */
class MyCurlHttpClient : public Aws::Http::CurlHttpClient
{
public:
    MyCurlHttpClient(const Aws::Client::ClientConfiguration& clientConfig) : Aws::Http::CurlHttpClient(clientConfig) {}

protected:
    void OverrideOptionsOnConnectionHandle(CURL* connectionHandle) const override
    {
        std::cout << "Disable DNS caching completely." << std::endl;
        curl_easy_setopt(connectionHandle, CURLOPT_DNS_CACHE_TIMEOUT, 0L);
    }
};

/**
 * Extending the default HttpClientFactory to return the custom HttpClient we just created.
 */
class MyHttpClientFactory : public Aws::Http::HttpClientFactory
{
    std::shared_ptr<Aws::Http::HttpClient> CreateHttpClient(const Aws::Client::ClientConfiguration& clientConfiguration) const override
    {
        return Aws::MakeShared<MyCurlHttpClient>(ALLOCATION_TAG, clientConfiguration);
    }

    std::shared_ptr<Aws::Http::HttpRequest> CreateHttpRequest(const Aws::String &uri, Aws::Http::HttpMethod method,
                                                    const Aws::IOStreamFactory &streamFactory) const override
    {
        return CreateHttpRequest(Aws::Http::URI(uri), method, streamFactory);
    }

    std::shared_ptr<Aws::Http::HttpRequest> CreateHttpRequest(const Aws::Http::URI& uri, Aws::Http::HttpMethod method, const Aws::IOStreamFactory& streamFactory) const override
    {
        auto request = Aws::MakeShared<Aws::Http::Standard::StandardHttpRequest>(ALLOCATION_TAG, uri, method);
        request->SetResponseStreamFactory(streamFactory);

        return request;
    }

    void InitStaticState() override
    {
        MyCurlHttpClient::InitGlobalState();
    }

    void CleanupStaticState() override
    {
        MyCurlHttpClient::CleanupGlobalState();
    }
};

/**
 * With AWS SDK for C++ version 1.8, it's much easier to override the default HTTP client configuration with the virtual functions: OverrideOptionsOn*Handle()
 * In this example, we override the default HTTP client and disable DNS caching with some low level Curl APIs.
 */
int main(int argc, char *argv[])
{
    SDKOptions options;
    options.loggingOptions.logLevel = Utils::Logging::LogLevel::Trace;
    InitAPI(options);
    {
        SetHttpClientFactory(Aws::MakeShared<MyHttpClientFactory>(ALLOCATION_TAG));

        Aws::S3::S3Client s3Client;
        auto listBucketsOutcome = s3Client.ListBuckets();
        if (listBucketsOutcome.IsSuccess())
        {
            std::cout << "Found " << listBucketsOutcome.GetResult().GetBuckets().size() << " buckets" << std::endl;
            for (auto&& bucket : listBucketsOutcome.GetResult().GetBuckets())
            {
                std::cout << "  " << bucket.GetName() << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to list buckets. Error details:" << std::endl;
            std::cout << listBucketsOutcome.GetError() << std::endl;
        }
    }

    ShutdownAPI(options);
    return 0;
}