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
#include <aws/email/SESClient.h>
#include <aws/email/model/ListReceiptFiltersRequest.h>
#include <aws/email/model/ListReceiptFiltersResult.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.ListReceiptFilters]
//! List the receipt filters associated with this account.
/*!
  \param filters; A vector of "ReceiptFilter" to receive the retrieved filters.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool
AwsDoc::SES::listReceiptFilters(Aws::Vector<Aws::SES::Model::ReceiptFilter> &filters,
                                const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);
    Aws::SES::Model::ListReceiptFiltersRequest listReceiptFiltersRequest;

    Aws::SES::Model::ListReceiptFiltersOutcome outcome = sesClient.ListReceiptFilters(
            listReceiptFiltersRequest);
    if (outcome.IsSuccess()) {
        auto &retrievedFilters = outcome.GetResult().GetFilters();
        if (!retrievedFilters.empty()) {
            filters.insert(filters.cend(), retrievedFilters.cbegin(),
                           retrievedFilters.cend());
        }
    }
    else {
        std::cerr << "Error retrieving IP address filters: "
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.ListReceiptFilters]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_list_receipt_filters'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv)
{
    (void)argc; // Unused.
    (void)argv; // Unused.

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Vector<Aws::SES::Model::ReceiptFilter> filters;

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        if (AwsDoc::SES::listReceiptFilters(filters, clientConfig))
        {
            if (filters.empty())
            {
                std::cout << "No filters defined" << std::endl;
            }
            else
            {
                for (auto& filter : filters)
                {
                    std::cout << "Name: " << filter.GetName() << "\n";
                    std::cout << "  Policy: ";
                    auto ip_filter = filter.GetIpFilter();
                    switch (ip_filter.GetPolicy())
                    {
                        case Aws::SES::Model::ReceiptFilterPolicy::Block:
                            std::cout << "Block\n";
                            break;
                        case Aws::SES::Model::ReceiptFilterPolicy::Allow:
                            std::cout << "Allow\n";
                            break;
                        default:
                            std::cout << "NOT SET\n";
                            break;
                    }
                    std::cout << "  CIDR: " << ip_filter.GetCidr() << std::endl;
                }
            }
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD