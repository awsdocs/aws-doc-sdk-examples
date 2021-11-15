// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
list_receipt_filters.cpp demonstrates how to list the Amazon SES IP filters for an AWS account.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/ListReceiptFiltersRequest.h>
#include <aws/email/model/ListReceiptFiltersResult.h>
#include <aws/email/model/ReceiptFilterPolicy.h>
#include <iostream>

int main(int argc, char **argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::SES::SESClient ses;
        Aws::SES::Model::ListReceiptFiltersRequest  lrf_req;

        auto lrf_out = ses.ListReceiptFilters(lrf_req);

        if (!lrf_out.IsSuccess())
        {
              std::cout << "Error retrieving IP address filters: "
                  << lrf_out.GetError().GetMessage() << std::endl;
        }
        else
        {
            // Output filter details
            std::cout << "Amazon Simple Email Service\n";
            std::cout << "Email Receiving: IP Address Filters:\n\n";
            auto filters = lrf_out.GetResult().GetFilters();
            if (filters.empty())
            {
                std::cout << "No filters defined" << std::endl;
            }
            else
            {
                for (auto filter : filters)
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
