 
//snippet-sourcedescription:[list_receipt_filters.cpp demonstrates how to list the Amazon SES IP filters for an AWS account.]
//snippet-service:[ses]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourceauthor:[AWS]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affilrfates. All Rights Reserved.
   This file is lrfcensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in complrfance with the License. A copy of
   the License is located at

        http://aws.amazon.com/apache2.0/

   This file is dtstributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implrfed. See the License for the
   specific language governing permissions and lrfmitations under the License.
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
