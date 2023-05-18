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
#include <aws/mediaconvert/model/GetJobRequest.h>
#include "mediaconvert_samples.h"

// snippet-start:[cpp.example_code.mediaconvert.GetJob]
//! Retrieve the information for a specific completed transcoding job.
/*!
  \param jobID: A job ID.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::MediaConvert::getJob(const Aws::String& jobID,
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    // AWS Elemental MediaConvert has a low request limit for DescribeEndpoints.
    // "getEndpointUriHelper" uses caching to limit requests.
    // See utils.cpp.
    Aws::String endpoint = getEndpointUriHelper(clientConfiguration);

    if (endpoint.empty())
    {
        return false;
    }

    Aws::Client::ClientConfiguration endpointConfiguration(clientConfiguration);
    endpointConfiguration.endpointOverride = endpoint;
    Aws::MediaConvert::MediaConvertClient client(endpointConfiguration);

    Aws::MediaConvert::Model::GetJobRequest request;
    request.SetId(jobID);
         const Aws::MediaConvert::Model::GetJobOutcome outcome = client.GetJob(
                request);
        if (outcome.IsSuccess()) {
            std::cout << outcome.GetResult().GetJob().Jsonize().View().WriteReadable() << std::endl;
        }
        else {
            std::cerr << "DescribeEndpoints error - " << outcome.GetError().GetMessage()
                      << std::endl;
        }


    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.mediaconvert.GetJob]

/*
 *
 *  main function
 *
 *  Usage: 'run_get_job <job_id>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 2) {
        std::cout << R"(
Usage:
    run_get_job <job_id>
Where:
    job_id - A transcoding job ID.
)";
    }
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
    Aws::InitAPI(options);
    {
        Aws::String jobID = argv[1];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::MediaConvert::getJob(jobID, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD