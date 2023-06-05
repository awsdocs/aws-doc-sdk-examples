/*
  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
  SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment,
 *including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and
 *run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include "mediaconvert_samples.h"
#include <aws/core/Aws.h>
#include <aws/mediaconvert/MediaConvertClient.h>
#include <aws/mediaconvert/model/DescribeEndpointsRequest.h>

//! Retrieve the account API endpoint.
/*!
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::MediaConvert::describeEndpoints(
    const Aws::Client::ClientConfiguration &clientConfiguration) {
  Aws::MediaConvert::MediaConvertClient endpointClient(clientConfiguration);

  bool result = true;
  Aws::String nextToken; // Used to handle paginated results.
  do {
    Aws::MediaConvert::Model::DescribeEndpointsRequest request;
    if (!nextToken.empty()) {
      request.SetNextToken(nextToken);
    }
    const Aws::MediaConvert::Model::DescribeEndpointsOutcome outcome =
        endpointClient.DescribeEndpoints(request);
    if (outcome.IsSuccess()) {
      const Aws::Vector<Aws::MediaConvert::Model::Endpoint> &endpoints =
          outcome.GetResult().GetEndpoints();
      std::cout << endpoints.size() << " endpoints retrieved." << std::endl;
      for (const Aws::MediaConvert::Model::Endpoint &endpoint : endpoints) {
        std::cout << "  " << endpoint.GetUrl() << std::endl;
      }

      nextToken = outcome.GetResult().GetNextToken();
    } else {
      std::cerr << "DescribeEndpoints error - "
                << outcome.GetError().GetMessage() << std::endl;
      result = false;
      break;
    }
  } while (!nextToken.empty());

  return result;
}

/*
 *
 *  main function
 *
 *  Usage: 'run_describe_endpoints'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
  Aws::SDKOptions options;
  options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
  Aws::InitAPI(options);
  {
    Aws::Client::ClientConfiguration clientConfig;
    // Optional: Set to the AWS Region (overrides config file).
    // clientConfig.region = "us-east-1";

    AwsDoc::MediaConvert::describeEndpoints(clientConfig);
  }
  Aws::ShutdownAPI(options);
  return 0;
}

#endif // TESTING_BUILD