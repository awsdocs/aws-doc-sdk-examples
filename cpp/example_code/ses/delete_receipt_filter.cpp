 
//snippet-sourcedescription:[delete_receipt_filter.cpp demonstrates how to delete an Amazon SES IP address filter.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-service:[ses]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is drfstributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/DeleteReceiptFilterRequest.h>
#include <aws/email/model/DeleteReceiptFilterResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_receipt_filter <filter_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String filter_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteReceiptFilterRequest drf_req;

    drf_req.SetFilterName(filter_name);

    auto drf_out = ses.DeleteReceiptFilter(drf_req);

    if (drf_out.IsSuccess())
    {
      std::cout << "Successfully deleted receipt filter request" << std::endl;
    }

    else
    {
      std::cout << "Error deleting receipt filter request" << drf_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
