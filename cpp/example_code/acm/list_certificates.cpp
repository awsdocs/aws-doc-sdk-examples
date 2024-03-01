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
#include <aws/acm/model/ListCertificatesRequest.h>
#include "acm_samples.h"

bool AwsDoc::ACM::ListCertificates(const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::ACM::ACMClient acm_client(config);

    Aws::ACM::Model::ListCertificatesRequest request;

    Aws::ACM::Model::ListCertificatesOutcome outcome =
            acm_client.ListCertificates(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ListCertificates: " <<
                  outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Information about certificates: "
                  << std::endl << std::endl;

        auto result = outcome.GetResult();

        Aws::Vector<Aws::ACM::Model::CertificateSummary> certificates =
                result.GetCertificateSummaryList();

        if (certificates.size() > 0)
        {
            for (const Aws::ACM::Model::CertificateSummary& certificate : certificates)
            {
                std::cout << "Certificate ARN: " <<
                          certificate.GetCertificateArn() << std::endl;
                std::cout << "Domain name:     " <<
                          certificate.GetDomainName() << std::endl << std::endl;
            }
        }
        else
        {
            std::cout << "No available certificates found in AWS Region '" <<
                      region << "'." << std::endl;
        }

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

