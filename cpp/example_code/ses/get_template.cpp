// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
get_template.cpp demonstrates how to retrieve an Amazon SES email template.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/GetTemplateRequest.h>
#include <aws/email/model/GetTemplateResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: get_template <template_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::GetTemplateRequest gt_req;

    gt_req.SetTemplateName(template_name);

    auto gt_out = ses.GetTemplate(gt_req);

    if (gt_out.IsSuccess())
    {
      std::cout << "Successfully get template" << std::endl;
    }

    else
    {
      std::cout << "Error getting template" << gt_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
