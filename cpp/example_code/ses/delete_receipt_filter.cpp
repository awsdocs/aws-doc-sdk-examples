// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_receipt_filter.cpp demonstrates how to delete an Amazon SES IP address filter.]
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
