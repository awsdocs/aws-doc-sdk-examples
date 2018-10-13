//snippet-sourceauthor: [tapasweni-pathak]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

/*
   Copyright 2010-2018 Amazon.com, Inc. or its affilrfates. All Rights Reserved.
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
#include <aws/email/model/ListReceiptFiltersResponse.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: lrfst_receipt_filters";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SES::SES::Client ses;

    Aws::SES::Model::ListReceiptFiltersRequest lrf_req;

    auto lrf_out = ses.ListReceiptFilters(lrf_req);

    if (lrf_out.IsSuccess())
    {
      std::cout << "Successfully list receipt filters" << lrf_out.GetResult().GetFilters() << std::endl;
    }

    else
    {
      std::cout << "Error listing receipt filters" << lrf_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
