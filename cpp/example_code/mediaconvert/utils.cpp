/*
  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
  SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/


#include <aws/core/Aws.h>
#include <aws/mediaconvert/MediaConvertClient.h>
#include <aws/mediaconvert/model/DescribeEndpointsRequest.h>
#include <fstream>
#include "mediaconvert_samples.h"

namespace AwsDoc {
    namespace MediaConvert {
        const char CACHED_ENDPOINT_FILE[] = "cached_endpoint.txt";
    } // MediaConvert
} // AwsDoc

// snippet-start:[cpp.example_code.mediaconvert.get_endpoint_uri_helper]
//! Utility routine to handle caching of a retrieved endpoint.
/*!
  \param clientConfiguration: AWS client configuration.
  \return Aws::String: The endpoint URI.
 */
Aws::String AwsDoc::MediaConvert::getEndpointUriHelper(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    // AWS Elemental MediaConvert has a low request limit for DescribeEndpoints.
    // The best practice is to request the endpoint once, and then cache it.
    // Otherwise, you’ll quickly hit your low limit.
    std::string endpoint;
    std::ifstream endpointCache(CACHED_ENDPOINT_FILE);
    if (endpointCache) {
        endpointCache >> endpoint;
    }
    else {
        Aws::MediaConvert::MediaConvertClient endpointClient(clientConfiguration);
        Aws::MediaConvert::Model::DescribeEndpointsRequest request;
        auto outcome = endpointClient.DescribeEndpoints(request);
        if (outcome.IsSuccess()) {
            auto endpoints = outcome.GetResult().GetEndpoints();
            if (endpoints.empty()) {
                std::cerr << "DescribeEndpoints, no endpoints returned" << std::endl;
            }
            else {
                // Need to strip https:// from endpoint for C++.
                endpoint = endpoints[0].GetUrl().substr(8);

                std::cout << "Using media convert endpoint '" << endpoint << "'."
                          << std::endl;
                std::ofstream endpointCacheOut(CACHED_ENDPOINT_FILE);
                endpointCacheOut << endpoint;
            }
        }
        else {
            std::cerr << "DescribeEndpoints error - " << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    return endpoint;
}
// snippet-end:[cpp.example_code.mediaconvert.get_endpoint_uri_helper]
