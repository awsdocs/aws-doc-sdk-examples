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
#include <aws/mediaconvert/Model/GetJobRequest.h>
#include "mediaconvert_samples.h"

//! Retrieve the information for a specific completed transcoding job.
/*!
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::MediaConvert::getJob(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::MediaConvert::MediaConvertClient endpointClient(clientConfiguration);

    Aws::MediaConvert::Model::GetJobRequest request;
         const Aws::MediaConvert::Model::GetJobOutcome outcome = endpointClient.GetJob(
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

/*
 *
 *  main function
 *
 *  Usage: 'run_get_job'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::MediaConvert::getJob(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD