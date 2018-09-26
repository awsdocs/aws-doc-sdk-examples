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
#include <aws/email/model/CreateTemplateRequest.h>
#include <aws/email/model/CreateTemplateResult.h>
#include <aws/email/model/Template.h>
#include <iostream>

/**
 * Creates an ses template based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_template <template_name> <html_content> <subject_line> <text_content>";
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name = argv[1];
    Aws::String html_name = argv[2];
    Aws::String subject_line = argv[3];
    Aws::String text_content = argv[4];

    Aws::SES::SESClient ses;

    Aws::SES::Model::CreateTemplateRequest ct_req;
    Aws::SES::Model::Template template_var;

    template_var.SetTemplateName(template_name);
    template_var.SetHtmlPart(html_name);
    template_var.SetSubjectPart(subject_line);

    ct_req.SetTemplate(template_var);

    auto ct_out = ses.CreateTemplate(ct_req);

    if (ct_out.IsSuccess())
    {
      std::cout << "Successfully create template " << template_name << std::endl;
    }

    else
    {
      std::cout << "Error creating template " << ct_out.GetError().GetMessage() << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
