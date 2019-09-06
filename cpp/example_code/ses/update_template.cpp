//snippet-sourcedescription:[update_template.cpp demonstrates how to update an Amazon SES email template.]
//snippet-service:[ses]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
