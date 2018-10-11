 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/CreateReceiptFilterRequest.h>
#include <aws/email/model/CreateReceiptFilterResult.h>
#include <aws/email/model/ReceiptFilterPolicy.h>
#include <aws/email/model/ReceiptFilter.h>
#include <aws/email/model/ReceiptIpFilter.h>
#include <iostream>

/**
 * Creates an ses receipt filter based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: create_template <receipt_filter_name> <cidr_value> <receipt_filter_policy_val>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String receipt_filter_name = argv[1];
    Aws::String cidr_value = argv[2];
    Aws::SES::SESClient ses;

    Aws::SES::Model::CreateReceiptFilterRequest crf_req;
    Aws::SES::Model::ReceiptFilter receipt_filter;
    Aws::SES::Model::ReceiptIpFilter receipt_ip_filter;

    receipt_ip_filter.SetCidr(cidr_value);

    receipt_filter.SetName(receipt_filter_name);
    receipt_filter.SetIpFilter(receipt_ip_filter);

    crf_req.SetFilter(receipt_filter);

    auto ct_out = ses.CreateReceiptFilter(crf_req);

    if (ct_out.IsSuccess())
    {
      std::cout << "Successfully created receipt filter " << std::endl;
    }

    else
    {
      std::cout << "Error creating receipt filter " << ct_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
