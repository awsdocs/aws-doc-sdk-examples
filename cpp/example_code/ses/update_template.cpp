// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
update_template.cpp demonstrates how to update an Amazon SES email template.]
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/UpdateTemplateRequest.h>
#include <aws/email/model/UpdateTemplateResult.h>
#include <aws/email/model/Template.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 6)
  {
    std::cout << "Usage: update_template <template_name> <html_content>"
      "<subject_line> <text_content>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name(argv[1]);
    Aws::String html_content(argv[2]);
    Aws::String subject_line(argv[3]);
    Aws::String text_content(argv[4]);
    Aws::SES::SESClient ses;

    Aws::SES::Model::Template template_values;
    Aws::SES::Model::UpdateTemplateRequest ut_req;

    template_values.SetTemplateName(template_name);
    template_values.SetSubjectPart(subject_line);
    template_values.SetHtmlPart(html_content);
    template_values.SetTextPart(text_content);

    ut_req.SetTemplate(template_values);

    auto ut_out = ses.UpdateTemplate(ut_req);

    if (ut_out.IsSuccess())
    {
      std::cout << "Successfully updated template" << std::endl;
    }

    else
    {
      std::cout << "Error updating template" << ut_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
