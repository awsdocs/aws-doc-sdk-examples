/*
Purpose:
delete_template.cpp demonstrates how to delete an Amazon SES email template.




// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/DeleteTemplateRequest.h>
#include <aws/email/model/DeleteTemplateResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_template_request <template_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteTemplateRequest dt_req;

    dt_req.SetTemplateName(template_name);

    auto dt_out = ses.DeleteTemplate(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully deleted template" << std::endl;
    }

    else
    {
      std::cout << "Error deleting template" << dt_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
