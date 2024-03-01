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

#include <aws/acm/ACMClient.h>
#include <aws/acm/model/RequestCertificateRequest.h>
#include "acm_samples.h"

bool AwsDoc::ACM::RequestCertificate(const Aws::String& domainName,
                                     const Aws::String& idempotencyToken,
                                     const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::RequestCertificateRequest request;
    request.WithDomainName(domainName)
            .WithIdempotencyToken(idempotencyToken);

    Aws::ACM::Model::RequestCertificateOutcome outcome =
            acm_client.RequestCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "RequestCertificate error: " <<
                  outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: The newly requested certificate's "
                     "ARN is '" <<
                  outcome.GetResult().GetCertificateArn() <<
                  "'." << std::endl;

        return true;
    }
}

/*
*
*  main function
*
*  Usage: 'run_'
*
*  Prerequisites: .
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

